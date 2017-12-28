package dustit.clientapp.di.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.repositories.SharedPreferencesRepository;
import dustit.clientapp.utils.IConstants;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */
@Module
@Singleton
public class AppModule {

    private final Application application;

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
    public SharedPreferences provideSharedPreferences() {
        return application.getSharedPreferences(IConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public SharedPreferencesRepository sharedPreferencesRepository() {
        return new SharedPreferencesRepository();
    }
}
