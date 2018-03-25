package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
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
import dustit.clientapp.mvp.presenters.fragments.HotFragmentPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.base.BaseFeedFragment;
import dustit.clientapp.mvp.ui.interfaces.IHotFragmentView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.managers.ThemeManager;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;


public class HotFragment extends BaseFeedFragment implements IHotFragmentView, FeedRecyclerViewAdapter.IFeedInteractionListener {

    private static final String HEIGHT_APPBAR = "HEIGHT";
    private IHotFragmentInteractionListener interactionListener;
    private Unbinder unbinder;
    private FeedRecyclerViewAdapter adapter;
    private final HotFragmentPresenter presenter = new HotFragmentPresenter();
    private boolean isFirstTimeVisible = true;
    private RecyclerView.OnScrollListener scrollListener;
    private WrapperLinearLayoutManager linearLayoutManager;

    @BindView(R.id.rvHot)
    RecyclerView rvHot;
    @BindView(R.id.srlHotRefresh)
    SwipeRefreshLayout srlRefresh;
    private int appBarHeight;

    @Inject
    ThemeManager themeManager;

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
        appBarHeight = args.getInt(HEIGHT_APPBAR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hot, container, false);
        unbinder = ButterKnife.bind(this, v);
        adapter = new FeedRecyclerViewAdapter(getContext(), this, appBarHeight);
        linearLayoutManager = new WrapperLinearLayoutManager(getContext());
        rvHot.setLayoutManager(linearLayoutManager);
        rvHot.setAdapter(adapter);
        presenter.bind(this);
        srlRefresh.setProgressViewOffset(false, appBarHeight, appBarHeight + 100);
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
        return v;
    }

    public void setFavoritesList(List<FavoriteEntity> list) {
        adapter.setFavoritesList(list);
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
        if (context instanceof IHotFragmentInteractionListener) {
            interactionListener = (IHotFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
        rvHot.removeOnScrollListener(scrollListener);
        unbinder.unbind();
        presenter.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    @Override
    public void onBaseUpdated(List<MemEntity> list) {
        adapter.updateListWhole(list);
        srlRefresh.setRefreshing(false);
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
        /*adapter.onLikePostError(id);*/
        showErrorToast();
    }

    @Override
    public void onLikeDeletingError(String id) {
        /*adapter.onLikeDeletingError(id);*/
        showErrorToast();
    }

    @Override
    public void onDislikePostError(String id) {
        /*adapter.onDislikePostError(id);*/
        showErrorToast();
    }

    @Override
    public void onDislikeDeletingError(String id) {
        /*adapter.onDislikeDeletingError(id);*/
        showErrorToast();
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
    public void onRemovedFromFavorites(String id) {
        adapter.onDeletedFromFavorites(id);
    }

    @Override
    public void onErrorInRemovingFromFavorites(String id) {
        showErrorToast();
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
    public void onMemSelected(View animStart, MemEntity mem) {
        launchMemView(animStart, mem);
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
        presenter.deleteFromFavorites(id);
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

    //Select mem
    public interface IHotFragmentInteractionListener {
        void onMemSelected(MemEntity memEntity);
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(getContext());
    }
}
