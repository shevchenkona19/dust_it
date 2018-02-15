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
import dustit.clientapp.mvp.presenters.fragments.HotFragmentPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.interfaces.IHotFragmentView;
import dustit.clientapp.utils.AlertBuilder;


public class HotFragment extends Fragment implements IHotFragmentView, FeedRecyclerViewAdapter.IFeedInteractionListener {

    private IHotFragmentInteractionListener interactionListener;
    private Unbinder unbinder;
    private FeedRecyclerViewAdapter adapter;
    private final HotFragmentPresenter presenter = new HotFragmentPresenter();
    private boolean isFirstTimeVisible = true;

    @BindView(R.id.rvHot)
    RecyclerView rvHot;
    @BindView(R.id.srlHotRefresh)
    SwipeRefreshLayout srlRefresh;

    public HotFragment() {
    }

    public static HotFragment newInstance() {
        return new HotFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hot, container, false);
        unbinder = ButterKnife.bind(this, v);
        adapter = new FeedRecyclerViewAdapter(getContext(), this);
        rvHot.setLayoutManager(new WrapperLinearLayoutManager(getContext()));
        rvHot.setAdapter(adapter);
        presenter.bind(this);
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
        if (context instanceof IHotFragmentInteractionListener) {
            interactionListener = (IHotFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDestroyView() {
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
    public void onMemSelected(MemEntity mem) {
        interactionListener.onMemSelected(mem);
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
