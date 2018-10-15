package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.presenters.activities.RegisterActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IRegisterActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.ErrorCodeResolver;
import dustit.clientapp.utils.StringUtil;
import dustit.clientapp.utils.managers.ErrorManager;

public class RegisterActivity extends AppCompatActivity implements IRegisterActivityView {
    @BindView(R.id.etRegisterUsername)
    EditText etUsername;
    @BindView(R.id.etRegisterEmail)
    EditText etEmail;
    @BindView(R.id.etRegisterPassword)
    EditText etPassword;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.tilRegisterEmail)
    TextInputLayout tilRegisterEmail;
    @BindView(R.id.tilLoginPassword)
    TextInputLayout tilRegisterPassword;
    @BindView(R.id.tilLoginUsername)
    TextInputLayout tilRegisterUsername;
    @BindView(R.id.pbRegisterLoading)
    ProgressBar pbLoading;
    @BindView(R.id.tvAlreadyRegistered)
    TextView tvRegistered;
    @BindView(R.id.btnRegisterWrapper)
    View btnRegisterWrapper;
    @BindView(R.id.ivRegisterGoBack)
    ImageView ivGoBack;
    private ErrorManager errorManager = ErrorManager.get();

    private final RegisterActivityPresenter presenter = new RegisterActivityPresenter();
    private final StringUtil stringUtil = new StringUtil(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        presenter.bind(this);
        btnRegister.setOnClickListener(view -> {
            stringUtil.hideError(tilRegisterPassword, tilRegisterUsername, tilRegisterEmail);
            if (stringUtil.isCorrectInput(etEmail, etPassword, etUsername)) {
                tilRegisterEmail.setVisibility(View.GONE);
                tilRegisterPassword.setVisibility(View.GONE);
                tilRegisterUsername.setVisibility(View.GONE);
                btnRegisterWrapper.setVisibility(View.GONE);
                pbLoading.setVisibility(View.VISIBLE);
                presenter.registerUser(etUsername.getText().toString(),
                        etPassword.getText().toString(), etEmail.getText().toString());
            } else {
                stringUtil.showError(etEmail, etPassword, etUsername, tilRegisterEmail,
                        tilRegisterPassword, tilRegisterUsername);
            }
        });
        ivGoBack.setOnClickListener(v -> finish());
        tvRegistered.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onRegisteredSuccessfully() {
        final Intent intent = new Intent(this, PreTestActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }

    @Override
    public void onError(String message) {
        tilRegisterEmail.setVisibility(View.VISIBLE);
        tilRegisterPassword.setVisibility(View.VISIBLE);
        tilRegisterUsername.setVisibility(View.VISIBLE);
        btnRegisterWrapper.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        String error = ErrorCodeResolver.resolveError(message, new WeakReference<>(this));
        if (error.equals(getString(R.string.error))) {
            error = getString(R.string.connection_issue);
        }
        errorManager.showError(error,
                new WeakReference<>(this));
    }
}
