package dustit.clientapp.utils;

public interface IConstants {
    String BASE_URL = "http://hqnl0060547.online-vm.com";
//    String BASE_URL = "http://192.168.0.102                                                                                         ";
    String SHARED_PREFERENCES_NAME = "12Fgt2yyckt655";
    String NO_REGISTRATION_USERNAME = "test";
    String NO_REGISTRATION_PASSWORD = "test1";
    String SHARED_TRANSITION_NAME_KEY = "sh";

    interface ErrorCodes {
        String INTERNAL_ERROR = "INTERNAL_ERROR";
        String NO_CATEGORIES = "NO_CATEGORIES";
        String EMAIL_NOT_VALID = "EMAIL_NOT_VALID";
        String EMAIL_NOT_UNIQUE = "EMAIL_NOT_UNIQUE";
        String USERNAME_NOT_VALID = "USERNAME_NOT_VALID";
        String NOT_REGISTERED = "NOT_REGISTERED";
        String PASSWORDS_DONT_MATCH = "PASSWORDS_DONT_MATCH";
        String NO_SUCH_USER = "NO_SUCH_USER";
        String NO_SUCH_IMAGE = "NO_SUCH_IMAGE";
        String MEMES_ENDED = "MEMES_ENDED";
        String INCORRECT_DATA = "INCORRECT_DATA";
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
