package dustit.clientapp.mvp.ui.fragments;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;
import dustit.clientapp.mvp.presenters.fragments.HotFragmentPresenter;
import dustit.clientapp.mvp.ui.activities.AccountActivity;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.base.BaseFeedFragment;
import dustit.clientapp.mvp.ui.dialog.AchievementUnlockedDialog;
import dustit.clientapp.mvp.ui.interfaces.IHotFragmentView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.GlideApp;
import dustit.clientapp.utils.IConstants;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;


public class HotFragment extends BaseFeedFragment implements IHotFragmentView,
        FeedRecyclerViewAdapter.IFeedInteractionListener {

    private static final String HEIGHT_APPBAR = "HEIGHT";
    private final HotFragmentPresenter presenter = new HotFragmentPresenter();
    @BindView(R.id.rvHot)
    RecyclerView rvHot;
    @BindView(R.id.srlHotRefresh)
    SwipeRefreshLayout srlRefresh;
    @BindView(R.id.hotEmpty)
    ViewGroup empty;
    @BindView(R.id.btnEmptyHot)
    Button emptyHotRetry;
    private Unbinder unbinder;
    private boolean isFirstTimeVisible = true;
    private int myId;
    private int appBarHeight;


    public HotFragment() {
    }

    public static HotFragment newInstance(int appBarHeight, int myId) {
        Bundle args = new Bundle();
        args.putInt(HEIGHT_APPBAR, appBarHeight);
        args.putInt(IConstants.IBundle.MY_ID, myId);
        final HotFragment fragment = new HotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            appBarHeight = args.getInt(HEIGHT_APPBAR);
            myId = args.getInt(IConstants.IBundle.MY_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hot, container, false);
        unbinder = ButterKnife.bind(this, v);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvHot.setLayoutManager(linearLayoutManager);
        bindFeedback(this);
        adapter = new FeedRecyclerViewAdapter(getContext(), this, rvHot);
        adapter.setIsHot();
        adapter.setHasStableIds(true);
        adapter.setGlideLoader(GlideApp.with(this));
        rvHot.setAdapter(adapter);
        rvHot.setHasFixedSize(true);
        presenter.bind(this);
        rvHot.setRecycledViewPool(getFeedPool());
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
        ((SimpleItemAnimator) rvHot.getItemAnimator()).setSupportsChangeAnimations(false);

        subscribeToFeedbackChanges();
        return v;
    }

    public void scrollToTop() {
        rvHot.scrollToPosition(0);
    }

    @Override
    public void onResume() {
        if (isFirstTimeVisible) {
            presenter.loadBase();
            isFirstTimeVisible = false;
        }
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bindWithBase(context);
    }

    @Override
    public void onDestroyView() {
        unsubscribeFromFeedbackChanges();
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
    public void gotoAccount(MemEntity mem) {
        Intent intent = new Intent(getContext(), AccountActivity.class);
        intent.putExtra(IConstants.IBundle.IS_ME, mem.getUserId() == myId);
        intent.putExtra(IConstants.IBundle.USER_ID, mem.getUserId());
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(getContext());
    }
}
