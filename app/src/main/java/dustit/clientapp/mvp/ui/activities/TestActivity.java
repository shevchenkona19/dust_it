package dustit.clientapp.mvp.ui.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.SwipeDirection;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.TestMemEntity;
import dustit.clientapp.mvp.presenters.activities.TestActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.TestDeckAdapter;
import dustit.clientapp.mvp.ui.interfaces.ITestActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.L;

public class TestActivity extends AppCompatActivity implements ITestActivityView, TestDeckAdapter.ITestDeckListener {

    public static final String CATEGORY_LIST_KEY = "parametr";
    @BindView(R.id.ivTestIcon)
    ImageView ivIcon;
    @BindView(R.id.csvTestDeck)
    CardStackView csvTestDeck;
    @BindView(R.id.pbTestProgress)
    ProgressBar pbProgress;
    @BindView(R.id.btnTestCorrect)
    Button btnCorrect;
    @BindView(R.id.btnTestSkipAll)
    Button btnSkipAll;
    @BindView(R.id.clTestLayout)
    ConstraintLayout clTest;
    @BindView(R.id.pbTestLoading)
    ProgressBar pbLoading;
    @BindView(R.id.btnTestRetry)
    Button btnRetry;
    @BindView(R.id.tvTestFailedToLoad)
    TextView tvFailedToLoad;

    private final TestActivityPresenter presenter = new TestActivityPresenter();
    private int currentIndex = 0;
    private boolean canShowCorrectButton = true;
    private boolean isLastMemLiked = false;
    private TestDeckAdapter adapter;
    private final List<String> interestedCategories = new ArrayList<>();
    private final List<TestMemEntity> arrivedCategories = new ArrayList<>();
    private boolean isFinished = false;
    private boolean isFinishedProceed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        presenter.bind(this);
        adapter = new TestDeckAdapter(this);
        csvTestDeck.setAdapter(adapter);
        csvTestDeck.setCardEventListener(new CardStackView.CardEventListener() {
            @Override
            public void onCardDragging(float percentX, float percentY) {

            }

            @Override
            public void onCardSwiped(SwipeDirection direction) {
                switch (direction) {
                    case Left:
                        dislikeCurrent();
                        //Dislike
                        break;
                    case Right:
                        likeCurrent();
                        //Like
                        break;
                }
                checkFinished();
                if (canShowCorrectButton) {
                    setupCorrectButton();
                    canShowCorrectButton = false;
                }
            }

            @Override
            public void onCardReversed() {

            }

            @Override
            public void onCardMovedToOrigin() {

            }

            @Override
            public void onCardClicked(int index) {

            }
        });
        presenter.loadTest();
        btnSkipAll.setOnClickListener(view -> onTestFinished());
        btnCorrect.setOnClickListener(view -> correctPrevious());
        btnRetry.setOnClickListener(view -> {
            btnRetry.setVisibility(View.GONE);
            tvFailedToLoad.setVisibility(View.GONE);
            pbLoading.setVisibility(View.VISIBLE);
            presenter.loadTest();
        });
        pbLoading.getIndeterminateDrawable().setColorFilter(Color.parseColor("#f98098"), PorterDuff.Mode.MULTIPLY);
    }

    private void checkFinished() {
        if (currentIndex == arrivedCategories.size()) {
            if (!isFinished) {
                onTestFinished();
                hideCorrectButton();
                isFinished = true;
            }
        }
    }

    private void likeCurrent() {
        isLastMemLiked = true;
        interestedCategories.add(arrivedCategories.get(currentIndex).getCategoryName());
        currentIndex++;
        updateProgress(currentIndex);
    }

    private void dislikeCurrent() {
        isLastMemLiked = false;
        currentIndex++;
        updateProgress(currentIndex);
    }



    private void correctPrevious() {
        csvTestDeck.reverse();
        currentIndex--;
        hideCorrectButton();
        canShowCorrectButton = true;
        if (isLastMemLiked) {
            interestedCategories.remove(interestedCategories.size()-1);
        }
        updateProgress(currentIndex);
    }

    private void updateProgress(int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pbProgress.setProgress(index, true);
        } else {
            pbProgress.setProgress(index);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    private void onTestFinished() {
        if (!isFinishedProceed) {
            isFinishedProceed = true;
            csvTestDeck.setVisibility(View.GONE);
            final Intent intent = new Intent(this, ResultActivity.class);
            String[] array = new String[interestedCategories.size()];
            array = interestedCategories.toArray(array);
            intent.putExtra(CATEGORY_LIST_KEY, array);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void setupCorrectButton() {
        btnCorrect.setVisibility(View.VISIBLE);
        final ConstraintSet set = new ConstraintSet();
        set.clone(clTest);
        set.clear(R.id.btnTestSkipAll, ConstraintSet.RIGHT);
        set.clear(R.id.btnTestSkipAll, ConstraintSet.LEFT);
        set.clear(R.id.btnTestSkipAll, ConstraintSet.START);
        set.clear(R.id.btnTestSkipAll, ConstraintSet.END);
        set.connect(R.id.btnTestSkipAll, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 16);
        set.connect(R.id.btnTestSkipAll, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16);
        set.applyTo(clTest);
    }

    private void hideCorrectButton() {
        btnCorrect.setVisibility(View.GONE);
        final ConstraintSet set = new ConstraintSet();
        set.clone(clTest);
        set.connect(R.id.btnTestSkipAll, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 16);
        set.connect(R.id.btnTestSkipAll, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
        set.applyTo(clTest);
    }

    @Override
    public void onTestArrived(List<TestMemEntity> list) {
        adapter.addAll(list);
        arrivedCategories.addAll(list);
        pbProgress.setMax(list.size());
        csvTestDeck.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        pbProgress.setVisibility(View.VISIBLE);
        csvTestDeck.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }

    @Override
    public void onErrorInLoadingTest() {
        pbLoading.setVisibility(View.GONE);
        tvFailedToLoad.setVisibility(View.VISIBLE);
        btnRetry.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLike() {
        View target = csvTestDeck.getTopView();
        View targetOverlay = csvTestDeck.getTopView().getOverlayContainer();

        ValueAnimator rotation = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("rotation", 10f));
        rotation.setDuration(200);
        ValueAnimator translateX = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0f, 2000f));
        ValueAnimator translateY = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationY", 0f, 500f));
        translateX.setStartDelay(100);
        translateY.setStartDelay(100);
        translateX.setDuration(500);
        translateY.setDuration(500);
        AnimatorSet cardAnimationSet = new AnimatorSet();
        cardAnimationSet.playTogether(rotation, translateX, translateY);

        ObjectAnimator overlayAnimator = ObjectAnimator.ofFloat(targetOverlay, "alpha", 0f, 1f);
        overlayAnimator.setDuration(200);
        AnimatorSet overlayAnimationSet = new AnimatorSet();
        overlayAnimationSet.playTogether(overlayAnimator);
        csvTestDeck.swipe(SwipeDirection.Right, cardAnimationSet, overlayAnimationSet);
    }

    @Override
    public void onDislike() {
        View target = csvTestDeck.getTopView();
        View targetOverlay = csvTestDeck.getTopView().getOverlayContainer();

        ValueAnimator rotation = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("rotation", -10f));
        rotation.setDuration(200);
        ValueAnimator translateX = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0f, -2000f));
        ValueAnimator translateY = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationY", 0f, 500f));
        translateX.setStartDelay(100);
        translateY.setStartDelay(100);
        translateX.setDuration(500);
        translateY.setDuration(500);
        AnimatorSet cardAnimationSet = new AnimatorSet();
        cardAnimationSet.playTogether(rotation, translateX, translateY);

        ObjectAnimator overlayAnimator = ObjectAnimator.ofFloat(targetOverlay, "alpha", 0f, 1f);
        overlayAnimator.setDuration(200);
        AnimatorSet overlayAnimationSet = new AnimatorSet();
        overlayAnimationSet.playTogether(overlayAnimator);
        csvTestDeck.swipe(SwipeDirection.Left, cardAnimationSet, overlayAnimationSet);
    }

    @Override
    public void finish() {
        onTestFinished();
    }
}
