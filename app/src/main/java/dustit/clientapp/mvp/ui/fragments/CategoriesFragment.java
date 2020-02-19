package dustit.clientapp.mvp.ui.fragments;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;
import dustit.clientapp.mvp.presenters.fragments.CategoriesFragmentPresenter;
import dustit.clientapp.mvp.ui.activities.AccountActivity;
import dustit.clientapp.mvp.ui.activities.NewFeedActivity;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.base.BaseFeedFragment;
import dustit.clientapp.mvp.ui.dialog.AchievementUnlockedDialog;
import dustit.clientapp.mvp.ui.interfaces.ICategoriesFragmentView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.GlideApp;
import dustit.clientapp.utils.IConstants;

public class CategoriesFragment extends BaseFeedFragment implements ICategoriesFragmentView,
        FeedRecyclerViewAdapter.IFeedInteractionListener {

    private static final String HEIGHT_APPBAR = "HEIGHT";
    private static final String IS_CATEGORIES_LOADED = "ISCATLOAD";
    private final CategoriesFragmentPresenter presenter = new CategoriesFragmentPresenter();
    @BindView(R.id.srlCategoriesRefresh)
    SwipeRefreshLayout srlRefresh;
    @BindView(R.id.rvCategoriesFeed)
    RecyclerView rvFeed;
    private Unbinder unbinder;
    private ICategoriesFragmentInteractionListener listener;
    private LinearLayoutManager linearLayoutManager;
    private int appBarHeight;
    private Category currentCategory;
    private boolean isCategoriesLoaded = false;
    private int myId;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance(int appBarHeight, boolean isCategoriesLoaded, int myId) {
        Bundle args = new Bundle();
        args.putInt(HEIGHT_APPBAR, appBarHeight);
        args.putInt(IConstants.IBundle.MY_ID, myId);
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
            myId = args.getInt(IConstants.IBundle.MY_ID);
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
        bindFeedback(this);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvFeed.setLayoutManager(linearLayoutManager);
        adapter = new FeedRecyclerViewAdapter(getContext(), this, rvFeed);
        adapter.setHasStableIds(true);
        adapter.setGlideLoader(GlideApp.with(this));
        rvFeed.setAdapter(adapter);
        rvFeed.setHasFixedSize(true);
        rvFeed.setRecycledViewPool(getFeedPool());
        srlRefresh.setProgressViewOffset(false, appBarHeight - 100, appBarHeight + 100);
        srlRefresh.setOnRefreshListener(() -> {
            if (currentCategory != null) {
                srlRefresh.setRefreshing(true);
                presenter.loadBase(currentCategory.getId());
            } else {
                srlRefresh.setRefreshing(true);
                listener.reloadCategories();
            }
        });

        listener.onAttachToActivity(new NewFeedActivity.ICategoriesSpinnerInteractionListener() {
            @Override
            public void onCategoriesFailed() {
                srlRefresh.setRefreshing(false);
                adapter.onFailedToLoad();
            }

            @Override
            public void onCategoriesArrived() {
            }

            @Override
            public void onCategorySelected(@NonNull Category category) {
                scrollToTop();
                currentCategory = category;
                srlRefresh.setEnabled(true);
                presenter.loadBase(category.getId());
            }
        });
        ((SimpleItemAnimator) rvFeed.getItemAnimator()).setSupportsChangeAnimations(false);

        subscribeToFeedbackChanges();
        if (!isCategoriesLoaded) {
            adapter.onFailedToLoad();
        }
        return v;
    }

    @Override
    public void onDestroyView() {
        unsubscribeFromFeedbackChanges();
        unbinder.unbind();
        presenter.unbind();
        listener.onDetachFromActivity();
        super.onDestroyView();
    }

    public void scrollToTop() {
        rvFeed.scrollToPosition(0);
    }

    public void onCategoriesLoaded(boolean isCategoriesLoaded) {
        this.isCategoriesLoaded = isCategoriesLoaded;
    }

    @Override
    public void onBaseUpdated(List<MemEntity> list) {
        srlRefresh.setRefreshing(false);
        adapter.updateWhole(list);
        rvFeed.scheduleLayoutAnimation();
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
        showErrorToast();
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
        if (currentCategory == null) {
            listener.reloadCategories();
            return;
        }
        presenter.loadBase(currentCategory.getId());
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
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(getContext());
    }

    @Override
    public void loadMore(int offset) {
        if (currentCategory == null) {
            listener.reloadCategories();
            return;
        }
        presenter.loadWithOffset(currentCategory.getId(), offset);
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

    public interface ICategoriesFragmentInteractionListener {
        void onAttachToActivity(NewFeedActivity.ICategoriesSpinnerInteractionListener listener);

        void reloadCategories();

        void onDetachFromActivity();
    }
}
