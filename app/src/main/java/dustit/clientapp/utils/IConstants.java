package dustit.clientapp.utils;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public interface IConstants {
    String BASE_URL = "http://testmemspace.herokuapp.com";
    String SHARED_PREFERENCES_NAME = "12Fgt2yyckt655";

    interface IPreferences {
        String TOKEN_KEY = "token";
        String USERNAME_KEY = "cacheK";
        String USERNAME_CACHED_KEY = "a";
        String LANGUAGE_KEY = "lang ";
        String THEME_KEY = "Theme";
    }

    enum OPINION {
        LIKED,
        DISLIKED,
        NEUTRAL
    }
}
