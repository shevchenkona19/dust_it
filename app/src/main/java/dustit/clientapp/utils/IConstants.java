package dustit.clientapp.utils;

public interface IConstants {
    String BASE_URL = "http://192.168.0.100";
    //    String BASE_URL = "http://193.111.63.173";
    String SHARED_PREFERENCES_NAME = "12Fgt2yyckt655";
    String NO_REGISTRATION_USERNAME = "user";
    String NO_REGISTRATION_PASSWORD = "lolkek123";
    String IMAGE_URL = BASE_URL + "/feed/imgs?id=";
    String USER_IMAGE_URL = BASE_URL + "/feed/userPhoto?targetUsername=";

    interface IRequest {
        int UPLOAD_PHOTO = 986;
        int RESULT_OK = 101;
        int RESULT_ERROR = 102;
    }

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
        String REFERRAL_NOT_PRESENT = "REFERRAL_NOT_PRESENT";
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
        String POSITIVE_KEY = "POSITIVE_KEY";
        String POSITIVE_LIMIT = "POSITIVE_LIMIT";
        String REVIEW = "REVIEW";
        String NOTIFICATIONS = "NOTIFICATIONS";
        String LAST_RUN = "LAST_RUN";
        String AUTOSTART = "AUTOSTART";
        String FIRST_TIME = "FIRST_TIME";
        String MY_ID = "MY_ID";
        String FCM_ID = "FCM_ID";
        String FCM_UPDATE = "FCM_UPDATE";
        String IS_NO_REGISTRATION = "IS_NO_REGISTRATION";
    }

    enum OPINION {
        LIKED,
        DISLIKED,
        NEUTRAL
    }

    enum ViewMode {
        LIST,
        GRID
    }

    interface IBundle {
        String IS_ME = "IS_ME";
        String ID = "ID";
        String ACHIEVEMENT = "ACHIEVEMENT";
        String IS_FIRST_TIME = "IS_FIRST_TIME";
        String SHOW_COMMENTS = "SHOW_COMMENTS";
        String MEM_ID = "MEM_ID";
        String PARENT_COMMENT_ID = "PARENT_COMMENT_ID";
        String NEW_COMMENT_ID = "NEW_COMMENT_ID";
        String BASE_COMMENT = "BASE_COMMENT";
        String SHOW_COMMENT = "SHOW_COMMENT";
        String MY_ID = "MY_ID";
        String RELOAD = "RELOAD";
        String USER_ID = "USER_ID";
        String UPLOAD = "UPLOAD";
        String VIEW_MODE = "VIEW_MODE";
    }

    interface INotifications {
        String NEW_MEMES = "NEW_MEMES";
        String COMMENT_RESPOND = "COMMENT_RESPOND";
        String CHANNEL_ID = "CHANNEL_ID";
    }

    interface IViewTypes {

        int LOADING = 0;
        int FAILED_TO_LOAD = 1;
        int ITEM = 2;
    }
}
