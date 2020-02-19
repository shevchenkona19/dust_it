package dustit.clientapp.mvp.ui.fragments;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapContentDraweeView;
import dustit.clientapp.mvp.model.entities.TestMemEntity;
import dustit.clientapp.utils.GestureListener;
import dustit.clientapp.utils.IConstants;


public class MemTestFragment extends Fragment implements GestureListener.IFragmentGestureListener{

    private static final String POSITION = "param1";
    private static final String MEM_KEY = "param2";
    private static final String TOKEN_KEY = "param3";

    private int position;
    private TestMemEntity memEntity;
    private String token;

    @BindView(R.id.sdvMemTestView)
    WrapContentDraweeView ivMemImage;
    @BindView(R.id.ibTestMemInterestedButton)
    ImageButton ibInterested;
    @BindView(R.id.ibTestMemNotInterestedButton)
    ImageButton ibNotInterested;
    @BindView(R.id.clTestMemLayout)
    ConstraintLayout clLayout;

    private Unbinder unbinder;


    @Override
    public void onSwipeRight() {
        onInterested();
    }

    @Override
    public void onSwipeLeft() {
        onNotInterested();
    }


    public interface IMemTestFragmentInteractionListener {
        void onNotInterested(int currPos);
        void onInterested(int currPos, String categoryId);
    }

    private IMemTestFragmentInteractionListener memTestFragmentInteractionListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IMemTestFragmentInteractionListener) {
            memTestFragmentInteractionListener = (IMemTestFragmentInteractionListener) context;
        }
    }

    public MemTestFragment() {
    }

    public static MemTestFragment newInstance(int pos, TestMemEntity entity, String token) {
        MemTestFragment fragment = new MemTestFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, pos);
        args.putParcelable(MEM_KEY, entity);
        args.putString(TOKEN_KEY, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
            memEntity = getArguments().getParcelable(MEM_KEY);
            token = getArguments().getString(TOKEN_KEY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GestureListener gestureListener = new GestureListener(this);
        final GestureDetector gestureDetector = new GestureDetector(getContext(), gestureListener);
        View v = inflater.inflate(R.layout.fragment_mem_test, container, false);
        unbinder = ButterKnife.bind(this, v);
        ivMemImage.setImageURI(Uri.parse(IConstants.BASE_URL + "/feed/imgs?id=" + memEntity.getMemId()));
        ibInterested.setOnClickListener(view -> onInterested());
        ibNotInterested.setOnClickListener(view -> onNotInterested());
        v.setOnTouchListener((view, motionEvent) -> {
            gestureDetector.onTouchEvent(motionEvent);
            view.performClick();
            return  true;
        });
        return v;
    }

    public void onInterested() {
        Animation dismissRightAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.vp_dismiss_right);
        dismissRightAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clLayout.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(() -> memTestFragmentInteractionListener.onInterested(position, memEntity.getCategoryName()), 100);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (getView() != null) {
            getView().startAnimation(dismissRightAnimation);
        }
    }

    public void onNotInterested() {
        Animation dismissLeftAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.vp_dismiss_left);
        dismissLeftAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clLayout.setVisibility(View.GONE);
                Handler handler = new Handler();
                handler.postDelayed(() -> memTestFragmentInteractionListener.onNotInterested(position), 100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (getView() != null) {
            getView().startAnimation(dismissLeftAnimation);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {
                getView().startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.vp_test_mem_animation));
            }
        } else {
            if (clLayout !=null) {
                clLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (getView() != null) {
            getView().clearAnimation();
        }
        unbinder.unbind();
        super.onDestroyView();
    }
}
