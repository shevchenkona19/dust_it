package dustit.clientapp.mvp.model.repositories;

import android.content.SharedPreferences;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import dustit.clientapp.utils.managers.ThemeManager;

/**
 * Created by shevc on 11.10.2017.
 * Let's GO!
 */

public class SharedPreferencesRepository {
    private static final String NIGHT_THEME = "dark";
    private static final String LIGHT_THEME = "light";
    @Inject
    SharedPreferences preferences;

    public SharedPreferencesRepository() {
        App.get().getAppComponent().inject(this);
    }

    public String getSavedToken() {
        return preferences.getString(IConstants.IPreferences.TOKEN_KEY, null);
    }

    public void saveToken(String token) {
        if (!token.equals("")) {
            preferences.edit()
                    .putString(IConstants.IPreferences.TOKEN_KEY, "JWT " + token)
                    .apply();
        } else {
            preferences.edit()
                    .putString(IConstants.IPreferences.TOKEN_KEY, token)
                    .apply();
        }
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
                .apply();
    }

    public String getCachedUsername() {
        return preferences.getString(IConstants.IPreferences.USERNAME_KEY, "");
    }

    public void saveLanguagePref(String lang) {
        preferences.edit()
                .putString(IConstants.IPreferences.LANGUAGE_KEY, lang)
                .apply();
    }


    public String loadLanguage() {
        return preferences.getString(IConstants.IPreferences.LANGUAGE_KEY, "INIT");
    }

    public void saveTheme(ThemeManager.Theme t) {
        final SharedPreferences.Editor editor = preferences.edit();
        switch (t) {
            case NIGHT:
                editor.putString(IConstants.IPreferences.THEME_KEY, NIGHT_THEME);
                break;
            case LIGHT:
                editor.putString(IConstants.IPreferences.THEME_KEY, LIGHT_THEME);
                break;
        }
        editor.apply();
    }

    public ThemeManager.Theme loadTheme() {
        final String theme = preferences.getString(IConstants.IPreferences.THEME_KEY, LIGHT_THEME);
        switch (theme) {
            case LIGHT_THEME:
                return ThemeManager.Theme.LIGHT;
            case NIGHT_THEME:
                return ThemeManager.Theme.NIGHT;
            default:
                return null;
        }
    }

    public boolean isRegistered() {
        return preferences.getBoolean(IConstants.IPreferences.REGISTRATION_KEY, false);
    }

    public void setRegistered(boolean registered) {
        preferences.edit()
                .putBoolean(IConstants.IPreferences.REGISTRATION_KEY, registered)
                .apply();
    }

    public void setFeedVisited() {
        preferences.edit().putBoolean(IConstants.IPreferences.FIRST_TIME_FEED, false).apply();
    }

    public boolean isFeedFirstTime() {
        return preferences.getBoolean(IConstants.IPreferences.FIRST_TIME_FEED, true);
    }

    public void setAccountVisited() {
        preferences.edit().putBoolean(IConstants.IPreferences.FIRST_TIME_ACCOUNT, false).apply();
    }

    public boolean isAccountFirstTime() {
        return preferences.getBoolean(IConstants.IPreferences.FIRST_TIME_ACCOUNT, true);
    }

    public int getPositiveCount() {
        return preferences.getInt(IConstants.IPreferences.POSITIVE_KEY, 0);
    }

    public void savePositiveCount(int positive) {
        preferences.edit().putInt(IConstants.IPreferences.POSITIVE_KEY, positive).apply();
    }

    public int getPositiveLimit() {
        return preferences.getInt(IConstants.IPreferences.POSITIVE_LIMIT, 3);
    }

    public void setPositiveLimit(int limit) {
        preferences.edit().putInt(IConstants.IPreferences.POSITIVE_LIMIT, limit).apply();
    }

    public boolean isReviewed() {
        return preferences.getBoolean(IConstants.IPreferences.REVIEW, false);
    }

    public void setReviewed(boolean isReviewed) {
        preferences.edit().putBoolean(IConstants.IPreferences.REVIEW, isReviewed).apply();
    }

    public void setNotificationsEnabled(boolean enabled) {
        preferences.edit().putBoolean(IConstants.IPreferences.NOTIFICATIONS, enabled).apply();
    }

    public boolean isNotificationsEnabled() {
        return preferences.getBoolean(IConstants.IPreferences.NOTIFICATIONS, true);
    }

    public boolean enabledAutoStart() {
        return preferences.getBoolean(IConstants.IPreferences.AUTOSTART, true);
    }

    public void setEnabledAutostart(boolean enabledAutostart) {
        preferences.edit().putBoolean(IConstants.IPreferences.AUTOSTART, enabledAutostart).apply();
    }

    public void saveMyId(String id) {
        preferences.edit().putString(IConstants.IPreferences.MY_ID, id).apply();
    }

    public String loadId() {
        return preferences.getString(IConstants.IPreferences.MY_ID, "");
    }

    public void saveFcmId(String fcmId) {
        preferences.edit().putString(IConstants.IPreferences.FCM_ID, fcmId).apply();
    }

    public boolean isFcmUpdate() {
        return preferences.getBoolean(IConstants.IPreferences.FCM_UPDATE, false);
    }

    public void setFcmUpdate(boolean update) {
        preferences.edit().putBoolean(IConstants.IPreferences.FCM_UPDATE, update).apply();
    }

    public String getFcm() {
        return preferences.getString(IConstants.IPreferences.FCM_ID, "");
    }
}
