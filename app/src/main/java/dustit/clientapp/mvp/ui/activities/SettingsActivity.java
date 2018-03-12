package dustit.clientapp.mvp.ui.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
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
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.managers.ThemeManager;

public class SettingsActivity extends AppCompatActivity implements ISettingsActivityView {

    @BindView(R.id.btnSettingsLogout)
    Button btnLogout;
    @BindView(R.id.tbSettingsToolbar)
    Toolbar toolbar;
    @BindView(R.id.flActivitySettingsContainer)
    ViewGroup flContainer;
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
    @BindView(R.id.rlSettingsUseImmersive)
    ViewGroup vgUseImmersive;
    @BindView(R.id.cbSettingsUseImmersive)
    CheckBox cbUseImmersive;

    @Inject
    ThemeManager themeManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    private final int LIGHT = 0;
    private final int DEFAULT = 1;
    private final int DARK = 2;

    private final SettingsActivityPresenter presenter = new SettingsActivityPresenter();
    private String themeSubscriberId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        App.get().getAppComponent().inject(this);
        ButterKnife.bind(this);
        presenter.bind(this);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.logout();
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, themeManager.getThemeList());
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
                        break;
                    case DEFAULT:
                        themeManager.setCurrentTheme(ThemeManager.Theme.DEFAULT);
                        presenter.saveTheme(ThemeManager.Theme.DEFAULT);
                        break;
                    case DARK:
                        themeManager.setCurrentTheme(ThemeManager.Theme.DARK);
                        presenter.saveTheme(ThemeManager.Theme.DARK);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        rlLanguagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(getString(R.string.pick_language));
                builder.setItems(new String[]{"English", "Українська", "Русский"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                    }
                });
                builder.create().show();
            }
        });
        cbUseImmersive.setChecked(userSettingsDataManager.useImmersiveMode());
        cbUseImmersive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userSettingsDataManager.setUseImmersiveMode(isChecked);
            }
        });
        vgUseImmersive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbUseImmersive.setChecked(!cbUseImmersive.isChecked());
                userSettingsDataManager.setUseImmersiveMode(cbUseImmersive.isChecked());
            }
        });
        setColors();
        themeSubscriberId = themeManager.subscribeToThemeChanges(new ThemeManager.IThemable() {
            @Override
            public void notifyThemeChanged(ThemeManager.Theme t) {
                setColors();
            }
        });
    }

    private void setColors() {
        cbUseImmersive.setBackgroundColor(getColorFromResources(themeManager.getAccentColor()));
        toolbar.setBackgroundColor(getColorFromResources(themeManager.getPrimaryColor()));
        toolbar.setTitleTextColor(getColorFromResources(themeManager.getMainTextToolbarColor()));
        toolbar.getNavigationIcon().setColorFilter(getColorFromResources(themeManager.getAccentColor()), PorterDuff.Mode.SRC_ATOP);
        tvSettingsChooseThemeLabel.setTextColor(getColorFromResources(themeManager.getMainTextMainAppColor()));
        tvCurrentLanguage.setTextColor(getColorFromResources(themeManager.getSecondaryTextMainAppColor()));
        tvChangeLanguage.setTextColor(getColorFromResources(themeManager.getMainTextMainAppColor()));
        animate(themeManager.getPrevBackgroundMainColor(), themeManager.getBackgroundMainColor(), flContainer);
        animate(themeManager.getPrevPrimaryColor(), themeManager.getPrimaryColor(), toolbar);

    }

    public void restartActivity() {
        Intent intent = new Intent(this, FeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private int getColorFromResources(int c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(c);
        } else {
            return getResources().getColor(c);
        }
    }

    private void animate(int fromColor, int toColor, final View v) {
        int colorFrom = getColorFromResources(fromColor);
        int colorTo = getColorFromResources(toColor);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                v.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        themeManager.unsubscribe(themeSubscriberId);
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
