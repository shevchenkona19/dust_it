package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapperLinearLayoutManager;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.FavoriteEntity;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.fragments.CategoriesFragmentPresenter;
import dustit.clientapp.mvp.ui.activities.FeedActivity;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.base.BaseFeedFragment;
import dustit.clientapp.mvp.ui.interfaces.ICategoriesFragmentView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.managers.ThemeManager;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class CategoriesFragment extends BaseFeedFragment implements ICategoriesFragmentView, FeedRecyclerViewAdapter.IFeedInteractionListener {

    private static final String HEIGHT_APPBAR = "HEIGHT";
    private static final String IS_CATEGORIES_LOADED = "ISCATLOAD";

    @BindView(R.id.rlCategoriesFeed)
    RelativeLayout rlFeed;
    @BindView(R.id.rlCategoriesLoading)
    RelativeLayout rlLoadingLayout;
    @BindView(R.id.pbCategoriesLoading)
    ProgressBar pbLoading;
    @BindView(R.id.srlCategoriesRefresh)
    SwipeRefreshLayout srlRefresh;
    @BindView(R.id.rvCategoriesFeed)
    RecyclerView rvFeed;

    @Inject
    ThemeManager themeManager;

    private Unbinder unbinder;
    private ICategoriesFragmentInteractionListener listener;
    private final CategoriesFragmentPresenter presenter = new CategoriesFragmentPresenter();
    private FeedRecyclerViewAdapter adapter;
    private RecyclerView.OnScrollListener scrollListener;
    private WrapperLinearLayoutManager linearLayoutManager;
    private int appBarHeight;
    private Category currentCategory;
    private boolean isCategoriesLoaded;


    public interface ICategoriesFragmentInteractionListener {
        void onAttachToActivity(FeedActivity.ICategoriesSpinnerInteractionListener listener);

        void onDetachFromActivity();
    }

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance(int appBarHeight, boolean isCategoriesLoaded) {
        Bundle args = new Bundle();
        args.putInt(HEIGHT_APPBAR, appBarHeight);
        args.putBoolean(IS_CATEGORIES_LOADED, isCategoriesLoaded);
        final CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            appBarHeight = args.getInt(HEIGHT_APPBAR);
            isCategoriesLoaded = args.getBoolean(IS_CATEGORIES_LOADED);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bindWithBase(context);
        if (context instanceof ICategoriesFragmentInteractionListener) {
            listener = (ICategoriesFragmentInteractionListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_categories, container, false);
        unbinder = ButterKnife.bind(this, v);
        presenter.bind(this);
        linearLayoutManager = new WrapperLinearLayoutManager(getContext());
        adapter = new FeedRecyclerViewAdapter(getContext(), this, appBarHeight
        );
        rvFeed.setLayoutManager(linearLayoutManager);
        rvFeed.setAdapter(adapter);
        srlRefresh.setProgressViewOffset(false, appBarHeight, appBarHeight + 100);
        pbLoading.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        srlRefresh.setEnabled(false);
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlRefresh.setRefreshing(true);
                presenter.loadBase(currentCategory.getName());
            }
        });
        if (isCategoriesLoaded) {
            rlLoadingLayout.setVisibility(View.GONE);
            rlFeed.setVisibility(View.VISIBLE);
        }
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
        listener.onAttachToActivity(new FeedActivity.ICategoriesSpinnerInteractionListener() {
            @Override
            public void onCategoriesArrived() {
                rlLoadingLayout.setVisibility(View.GONE);
                rlFeed.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCategorySelected(Category category) {
                currentCategory = category;
                srlRefresh.setEnabled(true);
                presenter.loadBase(category.getName());
            }
        });
        rvFeed.addOnScrollListener(scrollListener);
        return v;
    }

    @Override
    public void onDestroyView() {
        rvFeed.removeOnScrollListener(scrollListener);
        unbinder.unbind();
        presenter.unbind();
        listener.onDetachFromActivity();
        super.onDestroyView();
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


    public void setFavoritesList(List<FavoriteEntity> list) {
        adapter.setFavoritesList(list);
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
        Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorInRemovingFromFavorites(String id) {
        showErrorToast();
    }

    @Override
    public void onRemovedFromFavorites(String id) {
        adapter.onDeletedFromFavorites(id);
    }

    @Override
    public void reloadFeedPartial(int offset) {
        presenter.loadWithOffset(currentCategory.getId(), offset);
    }

    @Override
    public void reloadFeedBase() {
        presenter.loadBase(currentCategory.getId());
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
