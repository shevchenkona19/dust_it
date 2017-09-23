package dustit.moderatorapp.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dustit.moderatorapp.mvp.datamanager.DataManager;
import dustit.moderatorapp.mvp.model.repositories.SharedPreferencesRepository;

/**
 * Created by shevc on 15.09.2017.
 * Let's GO!
 */
@Module
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

    @Provides
    @Singleton
    public SharedPreferencesRepository provideSharedPreferencesRepository(Context context) {
        return new SharedPreferencesRepository(context);
    }
}
