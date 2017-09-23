package dustit.clientapp.di.modules;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dustit.clientapp.mvp.datamanager.DataManager;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */
@Module
@Singleton
public class AppModule {

    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    public DataManager provideDataManager() {
        return new DataManager();
    }
}

