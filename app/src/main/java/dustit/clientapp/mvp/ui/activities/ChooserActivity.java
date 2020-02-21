package dustit.clientapp.mvp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.presenters.activities.ChooserActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.IChooserActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;

public class ChooserActivity extends AppCompatActivity implements IChooserActivityView {
    @BindView(R.id.btnChooserLogin)
    Button btnLogin;
    @BindView(R.id.btnChooserRegister)
    Button btnRegister;
    @BindView(R.id.ivChooserIcon)
    ImageView ivIcon;
    @BindView(R.id.tvChooserContinueWithoutRegistration)
    TextView tvNoRegistration;
    @BindView(R.id.clChooserLayout)
    ConstraintLayout clMainLayout;
    @BindView(R.id.pbChooserLoading)
    View rlLoadingLayout;
    @BindView(R.id.tvChooserViewPolicy)
    TextView tvViewPolicy;

    @Inject
    UserSettingsDataManager userSettingsDataManager;

    private final ChooserActivityPresenter mPresenter = new ChooserActivityPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        setContentView(R.layout.activity_chooser);
        ButterKnife.bind(this);
        mPresenter.bind(this);
        mPresenter.checkIfRegistered();
        btnLogin.setOnClickListener(view -> {
            final Intent intent = new Intent(ChooserActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(view -> {
            final Intent intent = new Intent(ChooserActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        tvNoRegistration.setOnClickListener(v -> {
            final AlertDialog alertDialog = new AlertDialog.Builder(ChooserActivity.this)
                    .setTitle(getString(R.string.continue_without_registration))
                    .setMessage(getString(R.string.continue_without_registration_message))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> mPresenter.continueNoRegistration())
                    .setNegativeButton(getString(R.string.no), null)
                    .create();
            alertDialog.setOnShowListener(dialog -> {
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.fabSecond));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.fabSecond));
            });
            alertDialog.show();
        });
        tvViewPolicy.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(IConstants.BASE_URL + "/account/policy"));
            startActivity(browserIntent);
        });
    }

    @Override
    protected void onDestroy() {
        mPresenter.unbind();
        super.onDestroy();
    }

    @Override
    public void userAlreadyRegistered() {
        startActivity(new Intent(this, NewFeedActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    @Override
    public void onNoRegistrationCompleted() {
        startActivity(new Intent(this, NewFeedActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    @Override
    public void showLoading() {
        clMainLayout.setVisibility(View.GONE);
        rlLoadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        clMainLayout.setVisibility(View.VISIBLE);
        rlLoadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onErrorNoRegistration() {
        Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }
}
