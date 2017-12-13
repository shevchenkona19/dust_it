package dustit.clientapp.mvp.model.repositories;

import android.content.SharedPreferences;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.utils.IConstants;

/**
 * Created by shevc on 11.10.2017.
 * Let's GO!
 */

public class SharedPreferencesRepository {
    @Inject
    SharedPreferences preferences;

    public SharedPreferencesRepository() {
        App.get().getAppComponent().inject(this);
    }

    public String getSavedToken() {
        return preferences.getString(IConstants.IPreferences.TOKEN_KEY, null);
    }

    public void saveToken(String token) {
        preferences.edit()
                .putString(IConstants.IPreferences.TOKEN_KEY, token)
                .apply();
    }

    public boolean isUsernameCached() {
        return preferences.getBoolean(IConstants.IPreferences.USERNAME_CACHED_KEY, false);
    }

    public void cacheUsername(String username) {
        preferences.edit()
                .putString(IConstants.IPreferences.USERNAME_KEY, username)
                .putBoolean(IConstants.IPreferences.USERNAME_CACHED_KEY, true)
                .apply();
    }

    public void clearUsername() {
        preferences.edit()
                .putString(IConstants.IPreferences.USERNAME_KEY, "")
                .putBoolean(IConstants.IPreferences.USERNAME_CACHED_KEY, false)
                .apply();
    }

    public String getCachedUsername() {
        return preferences.getString(IConstants.IPreferences.USERNAME_KEY, "");
    }
}
