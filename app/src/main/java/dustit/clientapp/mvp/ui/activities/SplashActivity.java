package dustit.clientapp.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;

public class SplashActivity extends AppCompatActivity {

    @Inject
    UserSettingsDataManager userSettingsDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        Intent intent;
        if (userSettingsDataManager.isNoRegistration() || userSettingsDataManager.isRegistered()) {
            intent = new Intent(this, NewFeedActivity.class);
        } else {
            intent = new Intent(this, ChooserActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
