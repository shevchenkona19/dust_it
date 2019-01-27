package dustit.clientapp.mvp.ui.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.customviews.WrapperLinearLayoutManager;
import dustit.clientapp.mvp.model.entities.CommentEntity;
import dustit.clientapp.mvp.model.entities.NewAchievementEntity;
import dustit.clientapp.mvp.presenters.activities.AnswersActivityPresenter;
import dustit.clientapp.mvp.ui.adapters.AnswersCommentAdapter;
import dustit.clientapp.mvp.ui.dialog.AchievementUnlockedDialog;
import dustit.clientapp.mvp.ui.interfaces.IAnswersActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;

public class AnswersActivity extends Activity implements IAnswersActivityView, AnswersCommentAdapter.IAnswersInteraction {
    @BindView(R.id.ibCloseAnswers)
    View close;
    @BindView(R.id.srlAnswers)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rvAnswers)
    RecyclerView rvAnswers;
    @BindView(R.id.etMemViewComment)
    EditText commentField;
    @BindView(R.id.ivMemViewCommentSend)
    View sendComment;
    @BindView(R.id.clLayout)
    RelativeLayout clLayout;
    @BindView(R.id.tvAnswerUsername)
    TextView tvAnswerUsername;

    private CommentEntity baseComment;
    private AnswersCommentAdapter adapter;
    private AnswersActivityPresenter presenter;

    private String answeringUsername = "";
    private String answeringUserId = "";
    private boolean isAnsweringToSomebody = false;
    private String imageId = "";

    private boolean startComment = false;
    private String newCommentId = "";
    private String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_answers);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            myId = bundle.getString(IConstants.IBundle.MY_ID);
            baseComment = bundle.getParcelable(IConstants.IBundle.BASE_COMMENT);
            imageId = bundle.getString(IConstants.IBundle.MEM_ID);
            startComment = bundle.getBoolean(IConstants.IBundle.SHOW_COMMENT);
            newCommentId = bundle.getString(IConstants.IBundle.NEW_COMMENT_ID);
        } else {
            finish();
            return;
        }
        setAnsweringUsername(baseComment.getUsername());
        adapter = new AnswersCommentAdapter(this, this, baseComment, myId);
        presenter = new AnswersActivityPresenter(baseComment.getId());
        rvAnswers.setAdapter(adapter);
        rvAnswers.setLayoutManager(new WrapperLinearLayoutManager(this));
        presenter.loadBase();
        refreshLayout.setOnRefreshListener(() -> presenter.loadBase());
        presenter.bind(this);
        close.setOnClickListener((v) -> finish());
        commentField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isAnsweringToSomebody) {
                    if (!commentField.getText().toString().contains("@" + answeringUsername) && !commentField.getText().toString().equals("")) {
                        isAnsweringToSomebody = false;
                        answeringUsername = "";
                        answeringUserId = "";
                        setAnsweringUsername(baseComment.getUsername());
                    }
                }
            }
        });
        sendComment.setOnClickListener(v -> {
            if (isAnsweringToSomebody) {
                presenter.postRespond(answeringUserId, commentField.getText().toString(), imageId);
            } else {
                presenter.postRespond(baseComment.getUserId(), commentField.getText().toString(), imageId);
            }
        });
        if (startComment) {
            presenter.loadCommentsToId(newCommentId, baseComment.getId(), imageId);
        }
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

    private void setAnsweringUsername(String username) {
        tvAnswerUsername.setText(String.format(getString(R.string.answering_to), username));
    }

    @Override
    public void onAnswered(String newCommentId) {
        isAnsweringToSomebody = false;
        setAnsweringUsername(baseComment.getUsername());
        commentField.setText("");
        answeringUserId = "";
        answeringUsername = "";
        presenter.loadCommentsToId(newCommentId, baseComment.getId(), imageId);
    }

    @Override
    public void onAnswerFailed() {
        showError(getString(R.string.error));
    }

    @Override
    public void onBaseUpdate(List<CommentEntity> list) {
        refreshLayout.setRefreshing(false);
        adapter.onBaseUpdate(list);
    }

    @Override
    public void onPartialUpdate(List<CommentEntity> list) {
        refreshLayout.setRefreshing(false);
        adapter.onPartialUpdate(list);
    }

    @Override
    public void onFailedToLoadComments() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onAchievementUpdate(NewAchievementEntity achievementEntity) {
        if (presenter.isRegistered())
            new AchievementUnlockedDialog(this, achievementEntity.isFinalLevel()).bind(achievementEntity).show();
    }

    @Override
    public void onError() {
        showError(getString(R.string.error));
    }

    @Override
    public void onAnswersToIdLoaded(List<CommentEntity> list) {
        adapter.onBaseUpdate(list);
        rvAnswers.scrollToPosition(list.size());
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }

    @Override
    public void onAnswerClicked(CommentEntity comment) {
        answeringUsername = comment.getUsername();
        answeringUserId = comment.getUserId();
        commentField.setText("@" + answeringUsername + ", ");
        setAnsweringUsername(answeringUsername);
        isAnsweringToSomebody = true;
    }

    @Override
    public void onLoadMore(int offset) {
        presenter.loadPartial(offset);
    }

    private void showError(String message) {
        Snackbar.make(clLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
