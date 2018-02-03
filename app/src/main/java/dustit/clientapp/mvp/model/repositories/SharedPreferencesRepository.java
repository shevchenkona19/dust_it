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
    private static final String DARK_THEME = "dark";
    private static final String LIGHT_THEME = "light";
    private static final String DEFAULT_THEME = "default";
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
                .putString(IConstants.IPreferences.TOKEN_KEY, "JWT " + token)
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
                .clear()
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
        return preferences.getString(IConstants.IPreferences.LANGUAGE_KEY, "ru");
    }

    public void saveTheme(ThemeManager.Theme t) {
        final SharedPreferences.Editor editor = preferences.edit();
        switch (t) {
            case DARK:
                editor.putString(IConstants.IPreferences.THEME_KEY, DARK_THEME);
                break;
            case LIGHT:
                editor.putString(IConstants.IPreferences.THEME_KEY, LIGHT_THEME);
                break;
            case DEFAULT:
                editor.putString(IConstants.IPreferences.THEME_KEY, DEFAULT_THEME);
                break;
        }
        editor.apply();
    }

    public ThemeManager.Theme loadTheme() {
        final String theme = preferences.getString(IConstants.IPreferences.THEME_KEY, DEFAULT_THEME);
        switch (theme) {
            case DEFAULT_THEME:
                return ThemeManager.Theme.DEFAULT;
            case LIGHT_THEME:
                return ThemeManager.Theme.LIGHT;
            case DARK_THEME:
                return ThemeManager.Theme.DARK;
            default:
                return null;
        }
    }
}
