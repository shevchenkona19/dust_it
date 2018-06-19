package dustit.clientapp.mvp.ui.activities;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
                userSettingsDataManager.saveNewLanguagePref(langToLoad);
                restartActivity();
            });
            builder.create().show();
        });
    }

    private void restartCurrentAndBackstack() {
        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(this)
                .addNextIntent(new Intent(this, FeedActivity.class))
                .addNextIntent(new Intent(this, AccountActivity.class))
                .addNextIntent(new Intent(this, SettingsActivity.class));
        stackBuilder.startActivities();
        finish();
    }

    public void restartActivity() {
        final Intent intent = new Intent(this, FeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private int getColorFromResources(int c) {
        return ContextCompat.getColor(this, c);
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
        startActivity(new Intent(this, ChooserActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }
}
