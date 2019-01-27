package dustit.clientapp.mvp.ui.activities;

import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.presenters.activities.SettingsActivityPresenter;
import dustit.clientapp.mvp.ui.interfaces.ISettingsActivityView;
import dustit.clientapp.utils.AlertBuilder;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.managers.ThemeManager;

public class SettingsActivity extends AppCompatActivity implements ISettingsActivityView {

    @BindView(R.id.btnSettingsLogout)
    Button btnLogout;
    @BindView(R.id.tbSettingsToolbar)
    Toolbar toolbar;
    @BindView(R.id.tvSettingsChooseThemeLabel)
    TextView tvSettingsChooseThemeLabel;
    @BindView(R.id.tvSettingChangeLanguage)
    TextView tvChangeLanguage;
    @BindView(R.id.tvSettingsCurrentLanguage)
    TextView tvCurrentLanguage;
    @BindView(R.id.rlSettingsLanguagePicker)
    ViewGroup rlLanguagePicker;
    @BindView(R.id.spSettingsThemeChooser)
    AppCompatSpinner spThemeChooser;
    @BindView(R.id.rlSettingsPrivacy)
    RelativeLayout rlViewPolicy;
    @BindView(R.id.rlSettingsNotifications)
    ViewGroup vgNotifications;
    @BindView(R.id.swSettingsNotifications)
    Switch swNotifications;

    @Inject
    ThemeManager themeManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;


    private final int LIGHT = 0;
    private final int NIGHT = 1;
    private boolean isFirstLaunch = true;

    private final SettingsActivityPresenter presenter = new SettingsActivityPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        presenter.bind(this);
        btnLogout.setOnClickListener(view -> presenter.logout());
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, themeManager.getThemeList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spThemeChooser.setAdapter(adapter);
        spThemeChooser.setSelection(presenter.loadTheme());
        spThemeChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case LIGHT:
                        themeManager.setCurrentTheme(ThemeManager.Theme.LIGHT);
                        presenter.saveTheme(ThemeManager.Theme.LIGHT);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case NIGHT:
                        themeManager.setCurrentTheme(ThemeManager.Theme.NIGHT);
                        presenter.saveTheme(ThemeManager.Theme.NIGHT);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    default:
                        return;
                }
                if (!isFirstLaunch) {
                    restartCurrentAndBackstack();
                } else {
                    isFirstLaunch = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        toolbar.setNavigationOnClickListener(v -> finish());
        String langToSet = "";
        switch (userSettingsDataManager.loadLanguage()) {
            case "ru":
                langToSet = "Русский";
                break;
            case "uk":
                langToSet = "Українська";
                break;
            case "en":
                langToSet = "English";
                break;
        }
        tvCurrentLanguage.setText(langToSet);
        rlLanguagePicker.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
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
                final Locale locale = new Locale(langToLoad);
                Locale.setDefault(locale);
                final Configuration config = new Configuration();
                config.locale = locale;
                final Resources resources = getBaseContext().getResources();
                resources.updateConfiguration(config,
                        resources.getDisplayMetrics());
                userSettingsDataManager.saveNewLanguagePref(langToLoad);
                restartActivity();
            });
            builder.create().show();
        });
        rlViewPolicy.setOnClickListener((v) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(IConstants.BASE_URL + "/account/policy"));
            startActivity(browserIntent);
        });
        swNotifications.setChecked(userSettingsDataManager.isNotificationsEnabled());
        swNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if ("Xiaomi".equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
                if (!userSettingsDataManager.enabledAutoStart()) {
                    AlertDialog dialog = AlertBuilder.showXiaomiNotifications(this)
                            .setPositiveButton(getText(R.string.yes), (dialog1, which) -> {
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                                startActivity(intent);
                                userSettingsDataManager.setEnabledAutostart(true);
                            })
                            .setNegativeButton(R.string.no, null).create();
                    swNotifications.setChecked(false);
                    dialog.setOnShowListener(dialog12 -> {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
                    });
                    dialog.show();
                    return;
                }
            }
            userSettingsDataManager.setNotificationsEnabled(isChecked);
        });
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

    private void restartCurrentAndBackstack() {
        NewAccountActivity.isReload = true;
        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, NewFeedActivity.class))
                .addNextIntent(new Intent(this, NewAccountActivity.class).putExtras(getIntent().getExtras() != null ? getIntent().getExtras() : new Bundle()))
                .addNextIntent(new Intent(this, SettingsActivity.class));
        stackBuilder.startActivities();
        finish();
    }

    public void restartActivity() {
        final Intent intent = new Intent(this, NewFeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();
    }

    @Override
    public void onErrorLogout(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(this);
    }

    @Override
    public void onSuccessfullyLogout() {
        startActivity(new Intent(this, ChooserActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
}
