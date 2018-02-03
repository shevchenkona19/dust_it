package dustit.clientapp;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
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
        Fresco.initialize(this);
        Picasso.with(this).setLoggingEnabled(true);
        instance = this;
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        appComponent.inject(this);
        themeManager.setCurrentTheme(userSettingsDataManager.loadTheme());
    }
}
