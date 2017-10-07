package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.presenters.fragments.FeedFragmentPresenter;
import dustit.clientapp.mvp.ui.adapters.FeedRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.interfaces.IFeedFragmentView;


public class FeedFragment extends Fragment implements IFeedFragmentView, FeedRecyclerViewAdapter.IFeedInteractionListener{

    @BindView(R.id.rvFeed)
    RecyclerView rvFeed;

    private Unbinder unbinder;

    private FeedRecyclerViewAdapter adapter;

    private FeedFragmentPresenter presenter;


    private IFeedFragmentInteractionListener interactionListener;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance() {
        FeedFragment fragment = new FeedFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        unbinder = ButterKnife.bind(this, v);
        rvFeed.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FeedRecyclerViewAdapter(getContext(), this);
        rvFeed.setAdapter(adapter);
        presenter = new FeedFragmentPresenter();
        presenter.bind(this);
        presenter.loadBase();
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
        adapter.updateListWhole(list);
    }

    @Override
    public void onPartialUpdate(List<MemEntity> list) {
        adapter.updateListAtEnding(list);
    }

    @Override
    public void onErrorInLoading() {
        adapter.onFailedToLoad();
    }

    @Override
    public void onStartLoading() {
        adapter.onStartLoading();
    }

    @Override
    public void reloadFeedPartial(int offset) {
        presenter.loadWithOffset(offset);
    }

    @Override
    public void reloadFeedBase() {
        presenter.loadBase();
    }

    public void passLike(String id) {
    }

    public interface IFeedFragmentInteractionListener {
        void onFeedMemSelected(MemEntity memEntity);
    }
}
