package dustit.clientapp.mvp.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.mvp.ui.activities.TestActivity;
import dustit.clientapp.utils.GestureListener;


public class MemTestFragment extends Fragment implements GestureListener.IFragmentGestureListener{

    private static final String POSITION = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int position;
    private String mParam2;

    @BindView(R.id.ivTestMemImage)
    ImageView ivMemImage;
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
        void onInterested(int currPos);
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

    public static MemTestFragment newInstance(int pos, String param2) {
        MemTestFragment fragment = new MemTestFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, pos);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(POSITION);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        GestureListener gestureListener = new GestureListener(this);
        final GestureDetector gestureDetector = new GestureDetector(getContext(), gestureListener);
        View v = inflater.inflate(R.layout.fragment_mem_test, container, false);
        unbinder = ButterKnife.bind(this, v);
        ibInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInterested();
            }
        });
        ibNotInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNotInterested();
            }
        });
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return  true;
            }
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
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        memTestFragmentInteractionListener.onInterested(position);
                    }
                }, 100);

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
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        memTestFragmentInteractionListener.onNotInterested(position);
                    }
                }, 100);
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
            Log.d("MY", "animation cleared");
        }
        unbinder.unbind();
        super.onDestroyView();
    }
}
