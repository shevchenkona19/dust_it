package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.base.BaseFeedFragment;
import dustit.clientapp.mvp.ui.interfaces.ICategoriesFragmentView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.managers.ThemeManager;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class CategoriesFragment extends BaseFeedFragment implements ICategoriesFragmentView, FeedRecyclerViewAdapter.IFeedInteractionListener {

    private static final String HEIGHT_APPBAR = "HEIGHT";
    @BindView(R.id.rlCategoriesFeed)
    RelativeLayout rlFeed;
    @BindView(R.id.rlCategoriesLoading)
    RelativeLayout rlLoadingLayout;
    @BindView(R.id.pbCategoriesLoading)
    ProgressBar pbLoading;
    @BindView(R.id.tvCategoriesFailedToLoad)
    TextView tvFailedToLoad;
    @BindView(R.id.btnCategoriesReload)
    Button btnReload;
    @BindView(R.id.spCategoriesChooser)
    Spinner spChooser;
    @BindView(R.id.srlCategoriesRefresh)
    SwipeRefreshLayout srlRefresh;
    @BindView(R.id.rvCategoriesFeed)
    RecyclerView rvFeed;
    @BindView(R.id.tbCategoriesToolbar)
    Toolbar tbPlank;

    @Inject
    ThemeManager themeManager;

    private Unbinder unbinder;
    private ICategoriesFragmentInteractionListener listener;
    private final CategoriesFragmentPresenter presenter = new CategoriesFragmentPresenter();
    private FeedRecyclerViewAdapter adapter;
    private Category currentCategory;
    private boolean isFirstTimeVisible = true;
    private boolean isLoaded = false;
    private String themeId;
    private RecyclerView.OnScrollListener scrollListener;
    private WrapperLinearLayoutManager linearLayoutManager;
    private int appBarHeight;


    public interface ICategoriesFragmentInteractionListener {
        void onMemCategorySelected(MemEntity memEntity);

        void setScrollFlags();

        void resetScrollFlags();
    }

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance(int appBarHeight) {
        Bundle args = new Bundle();
        args.putInt(HEIGHT_APPBAR, appBarHeight);
        final CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        appBarHeight = args.getInt(HEIGHT_APPBAR);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tbPlank.setElevation(9);
        }
        adapter = new FeedRecyclerViewAdapter(getContext(), this, 0);
        spChooser.setTop(appBarHeight);
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
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.getCategories();
            }
        });
        themeId = themeManager.subscribeToThemeChanges(new ThemeManager.IThemable() {
            @Override
            public void notifyThemeChanged(ThemeManager.Theme t) {
                setColors();
            }
        });
        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    notifyFeedScrollIdle(true);
                    if (rvFeed.getChildAt(0) != null)
                        if (rvFeed.getChildAt(0).getTop() == 0 && linearLayoutManager.findFirstVisibleItemPosition() == 0) {
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
        setColors();
        return v;
    }

    private void setColors() {
        tbPlank.setBackgroundColor(getColorFromResources(themeManager.getPrimaryColor()));
    }

    private int getColorFromResources(int c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(c);
        } else {
            return getResources().getColor(c);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isLoaded) {
            listener.setScrollFlags();
            final Animation slideFromUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
            slideFromUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    tbPlank.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            tbPlank.startAnimation(slideFromUp);
        }
        if (isVisibleToUser && isFirstTimeVisible && !isLoaded) {
            presenter.getCategories();
            isFirstTimeVisible = false;
        }
        if (!isVisibleToUser) {
            if (tbPlank != null) {
                tbPlank.setVisibility(View.GONE);
            }
            if (listener != null) {
                listener.resetScrollFlags();
            }
        }

        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView() {
        rvFeed.removeOnScrollListener(scrollListener);
        unbinder.unbind();
        presenter.unbind();
        themeManager.unsubscribe(themeId);
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
        isLoaded = false;
    }

    @Override
    public void onStartLoading() {
        adapter.onStartLoading();
    }

    @Override
    public void onCategoriesLoaded(final List<Category> categoryList) {
        isLoaded = true;
        listener.setScrollFlags();
        rlLoadingLayout.setVisibility(View.GONE);
        rlFeed.setVisibility(View.VISIBLE);
        srlRefresh.setEnabled(true);
        String categories[] = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            categories[i] = categoryList.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spChooser.setAdapter(adapter);
        spChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentCategory = categoryList.get(i);
                presenter.loadBase(currentCategory.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setFavoritesList(List<FavoriteEntity> list) {
        adapter.setFavoritesList(list);
    }

    @Override
    public void onCategoriesFailedToLoad() {
        srlRefresh.setEnabled(false);
        pbLoading.setVisibility(View.GONE);
        tvFailedToLoad.setVisibility(View.VISIBLE);
        btnReload.setVisibility(View.VISIBLE);
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
    public void onMemSelected(View animStart, String transitionName, MemEntity mem) {
        launchMemView(animStart, transitionName, mem);
    }

    @Override
    public void onMemSelected(MemEntity mem) {
        listener.onMemCategorySelected(mem);
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
