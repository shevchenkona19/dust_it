package dustit.clientapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.di.component.AppComponent;
import dustit.clientapp.di.component.DaggerAppComponent;
import dustit.clientapp.di.modules.AppModule;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.TimeTracking;
import dustit.clientapp.utils.managers.NotifyManager;
import dustit.clientapp.utils.managers.ThemeManager;
import rx.Subscriber;

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
    @Inject
    DataManager dataManager;


    @Override
    public void onCreate() {
        super.onCreate();
        TimeTracking.getInstance().setStartDate(System.currentTimeMillis());
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
        ClearCacheTask clearCacheTask = new ClearCacheTask();
        clearCacheTask.execute();
        createNotificationChannel();
        SharedPreferences preferences = getSharedPreferences(IConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (preferences.getBoolean(IConstants.IPreferences.FIRST_TIME, true)) {
            if ("Xiaomi".equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
                editor.putBoolean(IConstants.IPreferences.NOTIFICATIONS, false)
                        .putBoolean(IConstants.IPreferences.AUTOSTART, false);
            }
        }
        if (!userSettingsDataManager.isFcmUpdated()) {
            if (!userSettingsDataManager.getFcm().equals("")) {
                AtomicReference<ResponseEntity> reference = new AtomicReference<>();
                dataManager.setFcmId(userSettingsDataManager.getFcm()).subscribe(new Subscriber<ResponseEntity>() {
                    @Override
                    public void onCompleted() {
                        L.print("New FCM Token set!");
                        if (reference.get().getResponse() == 200) {
                            userSettingsDataManager.setFcmUpdate(true);
                        } else {
                            userSettingsDataManager.setFcmUpdate(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        L.print("Error setting FCM Token: " + e.getMessage());
                        userSettingsDataManager.setFcmUpdate(false);
                    }

                    @Override
                    public void onNext(ResponseEntity responseEntity) {
                        reference.set(responseEntity);
                    }
                });
            }
        }
        editor.putLong(IConstants.IPreferences.LAST_RUN, System.currentTimeMillis()).apply();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MemSpace";
            String description = "MemSpace";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("MemSpace", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    private static class ClearCacheTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Glide.get(App.get()).clearDiskCache();
            return null;
        }
    }
}


