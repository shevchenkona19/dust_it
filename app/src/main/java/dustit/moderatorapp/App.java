package dustit.moderatorapp;

import android.app.Application;

import dustit.moderatorapp.di.components.AppComponent;
import dustit.moderatorapp.di.components.DaggerAppComponent;
import dustit.moderatorapp.di.modules.AppModule;

/**
 * Created by shevc on 15.09.2017.
 * Let's GO!
 */

public class App extends Application {

    protected static App instance;
    private AppComponent appComponent;

    public static App get() {
        return instance;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(instance))
                .build();
    }
}
