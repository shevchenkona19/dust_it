package dustit.clientapp;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import dustit.clientapp.di.component.AppComponent;
import dustit.clientapp.di.component.DaggerAppComponent;
import dustit.clientapp.di.modules.AppModule;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.utils.managers.ThemeManager;

public class App extends Application {

    private static App instance;
    private AppComponent appComponent;

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public static App get() {
        return instance;
    }

    @Inject
    ThemeManager themeManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;


    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        Fresco.initialize(this);
        Picasso.get().setLoggingEnabled(true);
        instance = this;
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);
        themeManager.setCurrentTheme(userSettingsDataManager.loadTheme());
        switch (themeManager.getCurrentTheme()) {
            case LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case NIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

}
