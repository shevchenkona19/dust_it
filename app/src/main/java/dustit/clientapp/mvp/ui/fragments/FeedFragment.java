package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapperLinearLayoutManager;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.fragments.FeedFragmentPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.base.BaseFeedFragment;
import dustit.clientapp.mvp.ui.interfaces.IFeedFragmentView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.managers.ThemeManager;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


public class FeedFragment extends BaseFeedFragment implements IFeedFragmentView, FeedRecyclerViewAdapter.IFeedInteractionListener {

    public static final String HEIGHT_APPBAR = "HEIGHT";
    private int appBarHeight;

    @BindView(R.id.rvFeed)
    RecyclerView rvFeed;
    @BindView(R.id.srlFeedRefresh)
    SwipeRefreshLayout srlRefresh;

    private Unbinder unbinder;

    private FeedRecyclerViewAdapter adapter;

    private FeedFragmentPresenter presenter;

    private RecyclerView.OnScrollListener scrollListener;

    private WrapperLinearLayoutManager linearLayoutManager;

    @Inject
    ThemeManager themeManager;

    public FeedFragment() {
        // Required empty public constructor
    }

    public void setFavoritesList(List<FavoriteEntity> list) {
        adapter.setFavoritesList(list);
    }

    public static FeedFragment newInstance(int appBarHeight) {
        Bundle args = new Bundle();
        args.putInt(HEIGHT_APPBAR, appBarHeight);
        final FeedFragment fragment = new FeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            appBarHeight = args.getInt(HEIGHT_APPBAR);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bindWithBase(context);
        L.print("onAttach");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        unbinder = ButterKnife.bind(this, v);
        linearLayoutManager = new WrapperLinearLayoutManager(getContext());
        rvFeed.setLayoutManager(linearLayoutManager);
        adapter = new FeedRecyclerViewAdapter(getContext(), this, appBarHeight);
        rvFeed.setAdapter(adapter);
        presenter = new FeedFragmentPresenter();
        presenter.bind(this);
        srlRefresh.setProgressViewOffset(false, appBarHeight, appBarHeight + 100);
        presenter.loadBase();
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlRefresh.setRefreshing(true);
                presenter.loadBase();
            }
        });
        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    notifyFeedScrollIdle(true);
                    if (rvFeed.getChildAt(0) != null)
                        if (rvFeed.getChildAt(0).getTop() == appBarHeight && linearLayoutManager.findFirstVisibleItemPosition() == 0) {
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
        rvFeed.addOnScrollListener(scrollListener);
        return v;
    }

    @Override
    public void onDestroyView() {
        rvFeed.removeOnScrollListener(scrollListener);
        unbinder.unbind();
        presenter.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onBaseUpdated(List<MemEntity> list) {
        srlRefresh.setRefreshing(false);
        adapter.updateListWhole(list);
    }

    @Override
    public void onPartialUpdate(List<MemEntity> list) {
        adapter.updateListAtEnding(list);
    }

    @Override
    public void onErrorInLoading() {
        srlRefresh.setRefreshing(false);
        adapter.onFailedToLoad();
    }

    @Override
    public void onStartLoading() {
        adapter.onStartLoading();
    }

    @Override
    public void onLikePostError(String id) {
        showErrorToast();
    }

    @Override
    public void onLikeDeletingError(String id) {
        showErrorToast();
    }

    @Override
    public void onDislikePostError(String id) {
        showErrorToast();
    }

    @Override
    public void onDislikeDeletingError(String id) {
        showErrorToast();
    }

    @Override
    public void onLikePostedSuccessfully(String id) {
        adapter.onLikePostedSuccesfully(id);
    }

    @Override
    public void onLikeDeletedSuccessfully(String id) {
        adapter.onLikeDeletedSuccesfully(id);
    }

    @Override
    public void onDislikePostedSuccessfully(String id) {
        adapter.onDislikePostedSuccesfully(id);
    }

    @Override
    public void onDislikeDeletedSuccessfully(String id) {
        adapter.onDislikeDeletedSuccesfully(id);
    }

    @Override
    public void onAddedToFavorites(String id) {
        notifyBase(id);
        adapter.addedToFavorites(id);
    }

    @Override
    public void onErrorInAddingToFavorites(String id) {
        showErrorToast();
    }

    @Override
    public void onErrorInRemovingFromFavorites(String s) {
        showErrorToast();
    }

    @Override
    public void onRemovedFromFavorites(String s) {
        adapter.onDeletedFromFavorites(s);
    }

    @Override
    public void reloadFeedPartial(int offset) {
        presenter.loadWithOffset(offset);
    }

    @Override
    public void reloadFeedBase() {
        presenter.loadBase();
    }

    @Override
    public void onMemSelected(View view, MemEntity mem) {
        launchMemView(view, mem);
    }

    @Override
    public void postLike(String id) {
        presenter.postLike(id);
    }

    @Override
    public void deleteLike(String id) {
        presenter.deleteLike(id);
    }

    @Override
    public void postDislike(String id) {
        presenter.postDislike(id);
    }

    @Override
    public void deleteDislike(String id) {
        presenter.deleteDislike(id);
    }

    @Override
    public void addToFavorites(String id) {
        presenter.addToFavorites(id);
    }

    @Override
    public void deleteFromFavorites(String id) {
        presenter.removeFromFavorites(id);
    }

    @Override
    public void showErrorToast() {
        Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    public void passPostLike(String id) {
        adapter.onLikePostedSuccesfully(id);
    }

    public void passDeleteLike(String id) {
        adapter.onLikeDeletedSuccesfully(id);
    }

    public void passPostDislike(String id) {
        adapter.onDislikePostedSuccesfully(id);
    }

    public void passDeleteDislike(String id) {
        adapter.onDislikeDeletedSuccesfully(id);
    }
    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(getContext());
    }
}
