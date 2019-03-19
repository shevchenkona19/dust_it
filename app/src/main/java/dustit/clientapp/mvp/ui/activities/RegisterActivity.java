package dustit.clientapp.mvp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
            if (checkErrors())
                presenter.onRegisterPressed();
        });
        ivGoBack.setOnClickListener(v -> finish());
        tvRegistered.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    private boolean checkErrors() {
        stringUtil.hideError(tilRegisterPassword, tilRegisterUsername, tilRegisterEmail);
        boolean isCorrect = stringUtil.isCorrectInput(etEmail, etPassword, etUsername);
        if (!isCorrect)
            stringUtil.showError(etEmail, etPassword, etUsername, tilRegisterEmail,
                    tilRegisterPassword, tilRegisterUsername);
        return isCorrect;
    }

    private void register(String referralCode) {
        stringUtil.hideError(tilRegisterPassword, tilRegisterUsername, tilRegisterEmail);
        if (stringUtil.isCorrectInput(etEmail, etPassword, etUsername)) {
            tilRegisterEmail.setVisibility(View.GONE);
            tilRegisterPassword.setVisibility(View.GONE);
            tilRegisterUsername.setVisibility(View.GONE);
            btnRegisterWrapper.setVisibility(View.GONE);
            pbLoading.setVisibility(View.VISIBLE);
            presenter.registerUser(etUsername.getText().toString(),
                    etPassword.getText().toString(), etEmail.getText().toString(), referralCode);
        } else {
            stringUtil.showError(etEmail, etPassword, etUsername, tilRegisterEmail,
                    tilRegisterPassword, tilRegisterUsername);
        }
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

    @Override
    public void showReferralPrompt() {
        final AlertDialog.Builder builder = AlertBuilder.getReferralDialog(new WeakReference<>(this));
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            presenter.showReferralDialog();
            dialog.cancel();
        });
        builder.setNegativeButton(R.string.no, ((dialog, which) -> register("")));
        builder.setNeutralButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#000000"));
        });
        dialog.show();
    }

    @Override
    public void showReferralCodeInputDialog() {
        final AlertDialog.Builder builder = AlertBuilder.getReferralCodeDialog(new WeakReference<>(this));
        final EditText editText = new EditText(this);
        editText.setHint(getString(R.string.ref_code_hint));
        builder.setView(editText);
        builder.setPositiveButton(R.string.ok, ((dialog, which) -> {
            final String refCode = editText.getText().toString();
            if (refCode.length() > 0) {
                register(refCode);
                dialog.cancel();
            } else {
                dialog.cancel();
                errorManager.showError(getString(R.string.ref_code_error), new WeakReference<>(this));
            }
        }));
        builder.setNegativeButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
        });
        dialog.show();
    }
}
