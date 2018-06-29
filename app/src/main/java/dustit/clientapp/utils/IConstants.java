package dustit.clientapp.utils;

/**
 * Created by shevc on 22.09.2017.
 * Let's GO!
 */

public interface IConstants {
    String BASE_URL = "http://hqnl0060547.online-vm.com";
    String SHARED_PREFERENCES_NAME = "12Fgt2yyckt655";
    String NO_REGISTRATION_USERNAME = "test";
    String NO_REGISTRATION_PASSWORD = "test1";
    String SHARED_TRANSITION_NAME_KEY = "sh";

    interface ErrorCodes {
        String INTERNAL_ERROR = "INTERNAL_ERROR";
        String NO_CATEGORIES = "NO_CATEGORIES";
    }

    interface IPreferences {
        String TOKEN_KEY = "token";
        String USERNAME_KEY = "cacheK";
        String USERNAME_CACHED_KEY = "a";
        String LANGUAGE_KEY = "lang ";
        String THEME_KEY = "Theme";
        String REGISTRATION_KEY = "REGISTRATION";
        String IMMERSIVE_KEY = "immersive";
    }

    enum OPINION {
        LIKED,
        DISLIKED,
        NEUTRAL
    }
}
