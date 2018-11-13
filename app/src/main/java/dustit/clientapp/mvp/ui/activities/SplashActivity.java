package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Locale;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;

public class SplashActivity extends AppCompatActivity {

    @Inject
    UserSettingsDataManager userSettingsDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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
        }
        if (!userSettingsDataManager.loadLanguage().equals(getResources().getConfiguration().locale.getLanguage())) {
            final Locale locale = new Locale(userSettingsDataManager.loadLanguage());
            Locale.setDefault(locale);
            final Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        final Intent intent = new Intent(this, ChooserActivity.class);
        startActivity(intent);
        finish();
    }
}
