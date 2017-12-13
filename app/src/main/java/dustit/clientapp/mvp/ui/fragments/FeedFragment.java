package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapperLinearLayoutManager;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.fragments.FeedFragmentPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.interfaces.IFeedFragmentView;


public class FeedFragment extends Fragment implements IFeedFragmentView, FeedRecyclerViewAdapter.IFeedInteractionListener {

    @BindView(R.id.rvFeed)
    RecyclerView rvFeed;
    @BindView(R.id.srlFeedRefresh)
    SwipeRefreshLayout srlRefresh;

    private Unbinder unbinder;

    private FeedRecyclerViewAdapter adapter;

    private FeedFragmentPresenter presenter;


    private IFeedFragmentInteractionListener interactionListener;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        unbinder = ButterKnife.bind(this, v);
        rvFeed.setLayoutManager(new WrapperLinearLayoutManager(getContext()));
        adapter = new FeedRecyclerViewAdapter(getContext(), this);
        rvFeed.setAdapter(adapter);
        presenter = new FeedFragmentPresenter();
        presenter.bind(this);
        presenter.loadBase();
        srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srlRefresh.setRefreshing(true);
                presenter.loadBase();
            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        presenter.unbind();
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IFeedFragmentInteractionListener) {
            interactionListener = (IFeedFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
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
        Toast.makeText(getContext(), "Ошибка", Toast.LENGTH_SHORT).show();
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
    public void onMemSelected(MemEntity mem) {
        interactionListener.onFeedMemSelected(mem);
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

    public interface IFeedFragmentInteractionListener {
        void onFeedMemSelected(MemEntity memEntity);
    }
}
