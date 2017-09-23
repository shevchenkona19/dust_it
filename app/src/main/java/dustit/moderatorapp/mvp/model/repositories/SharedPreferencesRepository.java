package dustit.moderatorapp.mvp.model.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import dustit.moderatorapp.mvp.model.entities.TokenEntity;
import dustit.moderatorapp.utils.IConstants;

/**
 * Created by shevc on 17.09.2017.
 * Let's GO!
 */

public class SharedPreferencesRepository {
    private SharedPreferences sharedPreferences;

    public SharedPreferencesRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(IConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFirstTime() {
        boolean res = sharedPreferences.getBoolean(IConstants.IS_FIRST_TIME_KEY, true);
        if (res) {
            sharedPreferences
                    .edit()
                    .putBoolean(IConstants.IS_FIRST_TIME_KEY, false)
                    .apply();
        }
        return res;
    }

    public void saveToken(String token) {
        sharedPreferences
                .edit()
                .putString(IConstants.TOKEN_KEY, token)
                .apply();
    }

    public TokenEntity getCurrentToken() {
        return new TokenEntity(sharedPreferences
                .getString(IConstants.TOKEN_KEY, ""));
    }

    public void deleteToken() {
        sharedPreferences
                .edit()
                .putString(IConstants.TOKEN_KEY, "")
                .apply();
    }

}
