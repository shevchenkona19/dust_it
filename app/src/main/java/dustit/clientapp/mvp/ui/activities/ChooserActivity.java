package dustit.clientapp.mvp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

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
    @BindView(R.id.ibChooserChangeLanguage)
    ImageButton ibChangeLanguage;
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
        if (userSettingsDataManager.loadLanguage().equals("INIT")) {
            final Locale locale = getBaseContext().getResources().getConfiguration().locale;
            String langToLoad;
            switch (locale.getLanguage()) {
                case "ru":
                    langToLoad = "ru";
                    break;
                case "ua":
                    langToLoad = "ua";
                    break;
                case "us":
                case "uk":
                    langToLoad = "uk";
                    break;
                default:
                    langToLoad = "ru";
            }
            final Locale newLocale = new Locale(langToLoad);
            Locale.setDefault(newLocale);
            final Configuration config = new Configuration();
            config.locale = newLocale;
            final Resources resources = getBaseContext().getResources();
            resources.updateConfiguration(config,
                    resources.getDisplayMetrics());
            userSettingsDataManager.saveNewLanguagePref(langToLoad);
            restartActivity();
        }
        if (!userSettingsDataManager.loadLanguage().equals(getResources().getConfiguration().locale.getLanguage())) {
            final Locale locale = new Locale(userSettingsDataManager.loadLanguage());
            Locale.setDefault(locale);
            final Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
            restartActivity();
        }
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

        ibChangeLanguage.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ChooserActivity.this);
            builder.setTitle(getString(R.string.pick_language));
            builder.setItems(new String[]{"English", "Українська", "Русский"}, (dialog, which) -> {
                String langToLoad = "";
                switch (which) {
                    case 0:
                        langToLoad = "en";
                        break;
                    case 1:
                        langToLoad = "uk";
                        break;
                    case 2:
                        langToLoad = "ru";
                        break;
                }
                Locale locale = new Locale(langToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                userSettingsDataManager.saveNewLanguagePref(langToLoad);
                restartActivity();
            });
            builder.setNegativeButton(getString(R.string.cancel), null);
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialog0 -> dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000")));
            dialog.show();
        });
        tvNoRegistration.setOnClickListener(v -> {
            final AlertDialog alertDialog = new AlertDialog.Builder(ChooserActivity.this)
                    .setTitle(getString(R.string.continue_without_registration))
                    .setMessage(getString(R.string.continue_without_registration_message))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> mPresenter.continueNoRegistration())
                    .setNegativeButton(getString(R.string.no), null)
                    .create();
            alertDialog.setOnShowListener(dialog -> {
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
            });
            alertDialog.show();
        });
        tvViewPolicy.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(IConstants.BASE_URL + "/account/policy"));
            startActivity(browserIntent);
        });
    }

    public void restartActivity() {
        final Intent intent = new Intent(ChooserActivity.this, ChooserActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        mPresenter.unbind();
        super.onDestroy();
    }

    @Override
    public void userAlreadyRegistered() {
        startActivity(new Intent(this, FeedActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    @Override
    public void onNoRegistrationCompleted() {
        startActivity(new Intent(this, FeedActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
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
