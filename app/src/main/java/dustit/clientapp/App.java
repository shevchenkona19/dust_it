package dustit.clientapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.di.component.AppComponent;
import dustit.clientapp.di.component.DaggerAppComponent;
import dustit.clientapp.di.modules.AppModule;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.FeedbackManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
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
    @Inject
    FeedbackManager feedbackManager;


    @Override
    public void onCreate() {
        super.onCreate();


        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                // other setters
                .setRequestListeners(requestListeners)
                .build();
        Fresco.initialize(this, config);
        FLog.setMinimumLoggingLevel(FLog.ERROR);


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
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        ClearCacheTask clearCacheTask = new ClearCacheTask();
        clearCacheTask.execute();
        createNotificationChannel();
        SharedPreferences preferences = getSharedPreferences(IConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
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
            NotificationChannel channel = new NotificationChannel(IConstants.INotifications.CHANNEL_ID, name, importance);
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


