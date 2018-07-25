package dustit.clientapp.utils;

public interface IConstants {
    String BASE_URL = "http://hqnl0060547.online-vm.com";
    String SHARED_PREFERENCES_NAME = "12Fgt2yyckt655";
    String NO_REGISTRATION_USERNAME = "nikita";
    String NO_REGISTRATION_PASSWORD = "someshit";

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
        String FIRST_TIME_FEED = "FIRST_TIME_FEED";
        String FIRST_TIME_ACCOUNT = "FIRST_TIME_ACCOUNT";
    }

    interface ISpotlight {
        String FEED_ICON = "FEED_ICON";
        String HOT_ICON = "HOT_ICON";
        String CATEGORIES_ICON = "CATEGORIES_ICON";
        String ACCOUNT_ICON = "ACCOUNT_ICON";
        String FAB_FEED = "FAB_FEED";
        String ADD_MEM_FAVS = "ADD_MEM_FAVS";
        String ACCOUNT_ALL_FAVS = "ACCOUNT_ALL_FAVS";
    }

    enum OPINION {
        LIKED,
        DISLIKED,
        NEUTRAL
    }
}
