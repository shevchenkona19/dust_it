package dustit.clientapp.mvp.ui.activities;

import android.animation.LayoutTransition;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.presenters.activities.UserFeedbackPresenter;
import dustit.clientapp.mvp.ui.interfaces.IUserFeedbackActivityView;
import dustit.clientapp.utils.AlertBuilder;

public class UserFeedbackActivity extends AppCompatActivity implements IUserFeedbackActivityView {
    @BindView(R.id.tbUserFeedback)
    Toolbar toolbar;
    @BindView(R.id.etUserFeedbackTitle)
    EditText etTitle;
    @BindView(R.id.etUserFeedbackMessage)
    EditText etMessage;
    @BindView(R.id.btnUserFeedbackSend)
    Button btnSend;
    @BindView(R.id.pbUserFeedbackLoading)
    ProgressBar pbLoading;
    @BindView(R.id.shadowEtTitle)
    View shadow;
    @BindView(R.id.highlightLine)
    View highlight;
    @BindView(R.id.cvUserFeedbackDone)
    ViewGroup cvDone;
    @BindView(R.id.cvUserFeedbackBtnContainer)
    ViewGroup btnContainer;
    @BindView(R.id.flUserFeedbackCardContainer)
    ViewGroup feedbackContainer;
    @BindView(R.id.rlUserFeedbackContainer)
    RelativeLayout rlContainer;

    private String title;
    private String message;

    private UserFeedbackPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);
        ButterKnife.bind(this);
        presenter = new UserFeedbackPresenter();
        presenter.bind(this);
        toolbar.setNavigationOnClickListener((view -> finish()));
        btnSend.setOnClickListener((view -> {
            title = etTitle.getText().toString();
            message = etMessage.getText().toString();
            if (title.length() < 3) {
                showError(getString(R.string.empty_title));
                return;
            }
            if (message.length() < 3) {
                showError(getString(R.string.empty_message));
                return;
            }
            presenter.sendFeedback(title, message);
        }));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            etMessage.setOnScrollChangeListener((view, i, i1, i2, i3) -> {
                if (i1 == 0) {
                    if (shadow.getVisibility() == View.VISIBLE) {
                        shadow.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (shadow.getVisibility() == View.INVISIBLE) {
                        shadow.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        layoutTransition.enableTransitionType(LayoutTransition.APPEARING);
        layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING);
        rlContainer.setLayoutTransition(layoutTransition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                int flags = 0;
                window.getDecorView().setSystemUiVisibility(flags);
            } else {
                int flags = window.getDecorView().getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(flags);
                window.setStatusBarColor(Color.WHITE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    @Override
    public void showLoading() {
        btnSend.setVisibility(View.GONE);
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        btnSend.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccess() {
        btnContainer.setVisibility(View.GONE);
        feedbackContainer.setVisibility(View.GONE);
        highlight.setVisibility(View.VISIBLE);
        cvDone.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }
}
