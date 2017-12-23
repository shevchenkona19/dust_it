package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapperLinearLayoutManager;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.fragments.CategoriesFragmentPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.interfaces.ICategoriesFragmentView;

public class CategoriesFragment extends Fragment implements ICategoriesFragmentView, FeedRecyclerViewAdapter.IFeedInteractionListener {

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

    private Unbinder unbinder;
    private ICategoriesFragmentInteractionListener listener;
    private final CategoriesFragmentPresenter presenter = new CategoriesFragmentPresenter();
    private FeedRecyclerViewAdapter adapter;
    private Category currentCategory;
    private boolean isFirstTimeVisible = true;
    private boolean isLoaded = false;

    public interface ICategoriesFragmentInteractionListener {
        void onMemCategorySelected(MemEntity memEntity);

        void setScrollFlags();

        void resetScrollFlags();
    }

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ICategoriesFragmentInteractionListener) {
            listener = (ICategoriesFragmentInteractionListener) context;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_categories, container, false);
        unbinder = ButterKnife.bind(this, v);
        presenter.bind(this);
        adapter = new FeedRecyclerViewAdapter(getContext(), this);
        rvFeed.setLayoutManager(new WrapperLinearLayoutManager(getContext()));
        rvFeed.setAdapter(adapter);
        pbLoading.getIndeterminateDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        srlRefresh.setEnabled(false);
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlRefresh.setRefreshing(true);
                presenter.loadBase(currentCategory.getId());
            }
        });
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.getCategories();
            }
        });
        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isLoaded) {
            listener.setScrollFlags();
        }
        if (isVisibleToUser && isFirstTimeVisible && !isLoaded) {
            presenter.getCategories();
            isFirstTimeVisible = false;
        } else if (!isVisibleToUser) {
            if (listener != null) {
                listener.resetScrollFlags();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        presenter.unbind();
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
                presenter.loadBase(currentCategory.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
    }

    @Override
    public void onLikeDeletingError(String id) {

    }

    @Override
    public void onDislikePostError(String id) {

    }

    @Override
    public void onDislikeDeletingError(String id) {

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
        adapter.addedToFavorites(id);
    }

    @Override
    public void onErrorInAddingToFavorites(String id) {
        Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
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
}
