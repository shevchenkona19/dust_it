package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.TestMemEntity;
import dustit.clientapp.mvp.presenters.activities.TestActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.TestViewPagerAdapter;
import dustit.clientapp.mvp.ui.fragments.MemTestFragment;
import dustit.clientapp.mvp.ui.interfaces.ITestActivityView;
import dustit.clientapp.utils.AlertBuilder;

public class TestActivity extends AppCompatActivity implements MemTestFragment.IMemTestFragmentInteractionListener, ITestActivityView {

    public static final String CATEGORY_LIST_KEY = "parametr";
    @BindView(R.id.ivTestIcon)
    ImageView ivIcon;
    @BindView(R.id.vpMemTest)
    ViewPager vpTest;
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

    private TestViewPagerAdapter adapter;
    private final List<String> interestedCategories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        presenter.bind(this);
        adapter = new TestViewPagerAdapter(getSupportFragmentManager(), presenter.getToken());
        vpTest.setAdapter(adapter);
        vpTest.setOffscreenPageLimit(1);
        presenter.loadTest();
        btnSkipAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTestFinished();
            }
        });
        btnCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correctPrevious();
            }
        });
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRetry.setVisibility(View.GONE);
                tvFailedToLoad.setVisibility(View.GONE);
                pbLoading.setVisibility(View.VISIBLE);
                presenter.loadTest();
            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onNotInterested(int currPos) {
        if (currPos == 0) {
            setupCorrectButton();
        }
        if (currPos + 1 == adapter.getCount()) {
            onTestFinished();
            return;
        }
        vpTest.setCurrentItem(currPos + 1, false);
        pbProgress.setProgress(currPos + 1);
    }

    @Override
    public void onInterested(int currPos, String categoryId) {
        if (interestedCategories.contains(categoryId)) {
            interestedCategories.add(interestedCategories.indexOf(categoryId), categoryId);
        } else {
            interestedCategories.add(categoryId);
        }
        if (currPos == 0) {
            setupCorrectButton();
        }
        if (currPos + 1 == adapter.getCount()) {
            onTestFinished();
            return;
        }
        vpTest.setCurrentItem(currPos + 1, false);
        pbProgress.setProgress(currPos + 1);
    }

    private void correctPrevious() {
        if (vpTest.getCurrentItem() - 1 == 0) {
            hideCorrectButton();
        }
        pbProgress.setProgress(vpTest.getCurrentItem() - 1);
        vpTest.setCurrentItem(vpTest.getCurrentItem() - 1);
    }

    private void onTestFinished() {
        vpTest.setVisibility(View.GONE);
        Intent intent = new Intent(this, ResultActivity.class);
        String[] array = new String[interestedCategories.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = interestedCategories.get(i);
        }
        intent.putExtra(CATEGORY_LIST_KEY, array);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setupCorrectButton() {
        btnCorrect.setVisibility(View.VISIBLE);
        ConstraintSet set = new ConstraintSet();
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
        ConstraintSet set = new ConstraintSet();
        set.clone(clTest);
        set.connect(R.id.btnTestSkipAll, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 16);
        set.connect(R.id.btnTestSkipAll, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 16);
        set.applyTo(clTest);
    }

    @Override
    public void onTestArrived(List<TestMemEntity> list) {
        adapter.updateList(list);
        pbProgress.setMax(list.size());
        pbLoading.setVisibility(View.GONE);
        pbProgress.setVisibility(View.VISIBLE);
        vpTest.setVisibility(View.VISIBLE);
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
}
