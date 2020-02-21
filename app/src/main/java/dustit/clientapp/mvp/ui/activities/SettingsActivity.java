package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

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

    private final int LIGHT = 0;
    private final int NIGHT = 1;
    private final SettingsActivityPresenter presenter = new SettingsActivityPresenter();
    @BindView(R.id.btnSettingsLogout)
    Button btnLogout;
    @BindView(R.id.tbSettingsToolbar)
    Toolbar toolbar;
    @BindView(R.id.tvSettingsChooseThemeLabel)
    TextView tvSettingsChooseThemeLabel;
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
        spThemeChooser.setSelection(presenter.loadTheme(), false);
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        toolbar.setNavigationOnClickListener(v -> finish());
        rlViewPolicy.setOnClickListener((v) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(IConstants.BASE_URL + "/account/policy"));
            startActivity(browserIntent);
        });
        swNotifications.setChecked(userSettingsDataManager.isNotificationsEnabled());
        swNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> userSettingsDataManager.setNotificationsEnabled(isChecked));
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
