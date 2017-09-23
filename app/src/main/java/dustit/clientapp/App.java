package dustit.clientapp;

import android.app.Application;

import dustit.clientapp.di.component.AppComponent;
import dustit.clientapp.di.component.DaggerAppComponent;
import dustit.clientapp.di.modules.AppModule;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public class App extends Application {

    private static App instance;
    private AppComponent appComponent;

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public static App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}
