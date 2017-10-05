package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.mvp.ui.interfaces.IFeedFragmentView;


public class FeedFragment extends Fragment implements IFeedFragmentView{

    @BindView(R.id.rvFeed)
    RecyclerView rvFeed;

    private Unbinder unbinder;


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
        return v;
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


    public interface IFeedFragmentInteractionListener {
        //
    }
}
