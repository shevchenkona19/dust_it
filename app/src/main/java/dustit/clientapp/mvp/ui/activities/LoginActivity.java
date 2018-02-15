package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.presenters.activities.LoginActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.ILoginActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.StringUtil;

public class LoginActivity extends AppCompatActivity implements ILoginActivityView {
    @BindView(R.id.etLoginPassword)
    EditText etPassword;
    @BindView(R.id.etLoginUsername)
    EditText etUsername;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tilLoginPassword)
    TextInputLayout tilLoginPassword;
    @BindView(R.id.tilLoginUsername)
    TextInputLayout tilLoginUsername;
    @BindView(R.id.pbLoginLoading)
    ProgressBar pbLoading;
    @BindView(R.id.tvLoginNotRegistered)
    TextView tvNotRegistered;

    private final LoginActivityPresenter loginActivityPresenter = new LoginActivityPresenter();
    private final StringUtil stringUtil = new StringUtil(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        etUsername.setText("admin");
        etPassword.setText("admin");
        loginActivityPresenter.bind(this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringUtil.hideError(tilLoginPassword, tilLoginUsername);
                if (stringUtil.isCorrectInput(etPassword, etUsername)) {
                    tilLoginPassword.setVisibility(View.GONE);
                    tilLoginUsername.setVisibility(View.GONE);
                    pbLoading.setVisibility(View.VISIBLE);
                    loginActivityPresenter.loginUser(etUsername.getText().toString(),
                            etPassword.getText().toString());
                } else {
                    stringUtil.showError(etPassword, etUsername, tilLoginPassword, tilLoginUsername);
                }
            }
        });
        tvNotRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        loginActivityPresenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onLoggedSuccessfully() {
        Intent intent = new Intent(this, FeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError(String message) {
        pbLoading.setVisibility(View.GONE);
        tilLoginUsername.setVisibility(View.VISIBLE);
        tilLoginPassword.setVisibility(View.VISIBLE);
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }
}
