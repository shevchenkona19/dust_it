package dustit.clientapp.mvp.ui.activities;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.ui.adapters.TestViewPagerAdapter;
import dustit.clientapp.mvp.ui.fragments.MemTestFragment;

public class TestActivity extends AppCompatActivity implements MemTestFragment.IMemTestFragmentInteractionListener {

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
    @BindView(R.id.clTestTutorialLayout)
    ConstraintLayout clTutorial;
    @BindView(R.id.clTestLayout)
    ConstraintLayout clTest;
    @BindView(R.id.btnTestTutorialContinue)
    Button btnTutorialContinue;
    @BindView(R.id.tvTestTutorialText)
    TextView tvTutorialText;
    @BindView(R.id.ivTestTutorialSwipeIcon)
    ImageView ivTutorialSwipeIcon;

    private int step = 0;

    private int totalTestMemCount = 10;

    private TestViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        adapter = new TestViewPagerAdapter(getSupportFragmentManager(), totalTestMemCount);
        vpTest.setAdapter(adapter);
        vpTest.setOffscreenPageLimit(1);
        pbProgress.setMax(totalTestMemCount);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            clTutorial.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }
        btnTutorialContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (step) {
                    case 0:
                        ivTutorialSwipeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_swipe_right));
                        ivTutorialSwipeIcon.setVisibility(View.VISIBLE);
                        tvTutorialText.setText("Если тебе понравился мем - свайпни вправо.");
                        break;
                    case 1:
                        ivTutorialSwipeIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_swipe_left));
                        tvTutorialText.setText("Если мем тебе не понравился - смахни его влево.");
                        break;
                    case 2:
                        ivTutorialSwipeIcon.setVisibility(View.GONE);
                        tvTutorialText.setText("Запомни. Свайп влево - не нравится\nСвайп вправо - нравится\nВсё просто! Стрелочки внизу тебе помогут.");
                        break;
                    case 3:
                        clTest.setVisibility(View.VISIBLE);
                        clTutorial.setVisibility(View.GONE);
                        break;
                }
                step++;
            }
        });
        btnSkipAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTestFinished();
            }
        });
        btnCorrect.setEnabled(false);
        btnCorrect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correctPrevious();
            }
        });
    }



    @Override
    public void onNotInterested(int currPos) {
        if (currPos == 0) {
            btnCorrect.setEnabled(true);
        }
        Log.d("MY", "Test activity: " + "OnNotInterested");
        if (currPos + 1 == adapter.getCount()) {
            onTestFinished();
            //Last fragment
            return;
        }
        vpTest.setCurrentItem(currPos + 1, false);
        pbProgress.setProgress(currPos + 1);
        //Not interested logic
    }

    @Override
    public void onInterested(int currPos) {
        if (currPos == 0) {
            btnCorrect.setEnabled(true);
        }
        Log.d("MY", "Test activity: " + "OnInterested");
        if (currPos + 1 == adapter.getCount()) {
            //Last fragment
            onTestFinished();
            return;
        }
        vpTest.setCurrentItem(currPos + 1, false);
        pbProgress.setProgress(currPos + 1);
        //Interested logic
    }

    private void correctPrevious() {
        pbProgress.setProgress(vpTest.getCurrentItem() - 1);
        vpTest.setCurrentItem(vpTest.getCurrentItem() - 1);
    }

    private void onTestFinished() {
        vpTest.setVisibility(View.GONE);
        Intent intent = new Intent(this, ResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
