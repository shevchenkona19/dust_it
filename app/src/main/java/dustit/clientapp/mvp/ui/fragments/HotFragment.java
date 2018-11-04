package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapperLinearLayoutManager;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;
import dustit.clientapp.mvp.presenters.fragments.HotFragmentPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.base.BaseFeedFragment;
import dustit.clientapp.mvp.ui.dialog.AchievementUnlockedDialog;
import dustit.clientapp.mvp.ui.interfaces.IHotFragmentView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.managers.ThemeManager;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


public class HotFragment extends BaseFeedFragment implements IHotFragmentView,
        FeedRecyclerViewAdapter.IFeedInteractionListener {

    private static final String HEIGHT_APPBAR = "HEIGHT";
    private Unbinder unbinder;
    private final HotFragmentPresenter presenter = new HotFragmentPresenter();
    private boolean isFirstTimeVisible = true;
    private RecyclerView.OnScrollListener scrollListener;
    private WrapperLinearLayoutManager linearLayoutManager;

    @BindView(R.id.rvHot)
    RecyclerView rvHot;
    @BindView(R.id.srlHotRefresh)
    SwipeRefreshLayout srlRefresh;
    @BindView(R.id.hotEmpty)
    ViewGroup empty;
    @BindView(R.id.btnEmptyHot)
    Button emptyHotRetry;

    private int appBarHeight;


    public HotFragment() {
    }

    public static HotFragment newInstance(int appBarHeight) {
        Bundle args = new Bundle();
        args.putInt(HEIGHT_APPBAR, appBarHeight);
        final HotFragment fragment = new HotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null)
            appBarHeight = args.getInt(HEIGHT_APPBAR);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hot, container, false);
        unbinder = ButterKnife.bind(this, v);
        linearLayoutManager = new WrapperLinearLayoutManager(getContext());
        rvHot.setLayoutManager(linearLayoutManager);
        bindFeedback(this);
        adapter = new FeedRecyclerViewAdapter(getContext(), this, appBarHeight);
        adapter.setIsHot();
        adapter.setHasStableIds(true);
        rvHot.setAdapter(adapter);
        presenter.bind(this);
        srlRefresh.setProgressViewOffset(false, appBarHeight - 100, appBarHeight + 100);
        srlRefresh.setOnRefreshListener(() -> {
            srlRefresh.setRefreshing(true);
            presenter.loadBase();
        });
        emptyHotRetry.setOnClickListener(view -> {
            empty.setVisibility(View.INVISIBLE);
            srlRefresh.setVisibility(View.VISIBLE);
            srlRefresh.setRefreshing(true);
            presenter.loadBase();
        });
        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    notifyFeedScrollIdle(true);
                    if (rvHot.getChildAt(0) != null)
                        if (rvHot.getChildAt(0).getTop() == appBarHeight && linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                            if (!srlRefresh.isRefreshing())
                                notifyFeedOnTop();
                        }
                } else {
                    notifyFeedScrollIdle(false);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                notifyFeedScrollChanged(dy);
            }
        };
        rvHot.addOnScrollListener(scrollListener);
        ((SimpleItemAnimator) rvHot.getItemAnimator()).setSupportsChangeAnimations(false);

        subscribeToFeedbackChanges();
        return v;
    }

    public void scrollToTop() {
        rvHot.scrollToPosition(0);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isFirstTimeVisible) {
            presenter.loadBase();
            isFirstTimeVisible = false;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bindWithBase(context);
    }

    @Override
    public void onDestroyView() {
        unsubscribeFromFeedbackChanges();
        rvHot.removeOnScrollListener(scrollListener);
        unbinder.unbind();
        presenter.unbind();
        super.onDestroyView();
    }

    @Override
    public void onBaseUpdated(List<MemEntity> list) {
        if (list.isEmpty()) {
            showEmpty();
            return;
        }
        adapter.updateWhole(list);
        rvHot.scheduleLayoutAnimation();
        srlRefresh.setRefreshing(false);
    }

    private void showEmpty() {
        empty.setVisibility(View.VISIBLE);
        srlRefresh.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPartialUpdate(List<MemEntity> list) {
        if (list.isEmpty()) {
            adapter.onMemesEnded();
            return;
        }
        adapter.updateAtEnding(list);
    }

    @Override
    public void onErrorInLoading() {
        srlRefresh.setRefreshing(false);
        adapter.onFailedToLoad();
    }

    @Override
    public void onAchievementUpdate(NewAchievementEntity achievementEntity) {
        if (getContext() != null) {
            if (presenter.isRegistered())
                new AchievementUnlockedDialog(getContext(), achievementEntity.isFinalLevel()).bind(achievementEntity).show();
        }
    }

    @Override
    public void reloadFeedBase() {
        presenter.loadBase();
    }

    @Override
    public boolean isRegistered() {
        return isUserRegistered();
    }

    @Override
    public void showErrorToast() {
        Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadMore(int offset) {
        presenter.loadWithOffset(offset);
    }

    @Override
    public void gotoHot() {
        gotoFragment((byte) 1);
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(getContext());
    }
}
