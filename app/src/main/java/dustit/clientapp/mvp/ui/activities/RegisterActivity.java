package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.presenters.activities.RegisterActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IRegisterActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.StringUtil;

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
                btnRegister.setVisibility(View.GONE);
                pbLoading.setVisibility(View.VISIBLE);
                presenter.registerUser(etUsername.getText().toString(),
                        etPassword.getText().toString(), etEmail.getText().toString());
            } else {
                stringUtil.showError(etEmail, etPassword, etUsername, tilRegisterEmail,
                        tilRegisterPassword, tilRegisterUsername);
            }
        });
        tvRegistered.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onRegisteredSuccessfully() {
        Intent intent = new Intent(this, PreTestActivity.class);
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
        btnRegister.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        Toast.makeText(this, getText(R.string.error), Toast.LENGTH_SHORT).show();
    }
}
