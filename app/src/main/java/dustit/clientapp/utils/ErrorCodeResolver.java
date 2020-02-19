package dustit.clientapp.utils;

import android.content.Context;

import java.lang.ref.WeakReference;

import dustit.clientapp.R;

/*
INTERNAL_ERROR: "INTERNAL_ERROR",
    INCORRECT_DATA: "INCORRECT_DATA",
    NOT_REGISTERED: "NOT_REGISTERED",
    PASSWORDS_DONT_MATCH: "PASSWORDS_DONT_MATCH",
    EMAIL_NOT_UNIQUE: "EMAIL_NOT_UNIQUE",
    NO_SUCH_USER: "NO_SUCH_USER",
    NO_SUCH_IMAGE: "NO_SUCH_IMAGE",
    MEMES_ENDED: "MEMES_ENDED",
    NO_CATEGORIES: "NO_CATEGORIES",
    EMAIL_NOT_VALID: "EMAIL_NOT_VALID",
    USERNAME_NOT_VALID: "USERNAME_NOT_VALID"
*/


public class ErrorCodeResolver {
    public static String resolveError(String message, WeakReference<Context> contextWeakReference) {
        if (message == null) message = "";
        String error;
        switch (message) {
            case IConstants.ErrorCodes.USER_BANNED:
                error = contextWeakReference.get().getString(R.string.you_are_banned);
                break;
            case IConstants.ErrorCodes.EMAIL_NOT_UNIQUE:
                error = contextWeakReference.get().getString(R.string.email_not_unique);
                break;
            case IConstants.ErrorCodes.EMAIL_NOT_VALID:
                error = contextWeakReference.get().getString(R.string.email_not_valid);
                break;
            case IConstants.ErrorCodes.INTERNAL_ERROR:
                error = contextWeakReference.get().getString(R.string.INTERNAL_ERROR);
                break;
            case IConstants.ErrorCodes.NO_CATEGORIES:
                error = contextWeakReference.get().getString(R.string.no_categories);
                break;
            case IConstants.ErrorCodes.USERNAME_NOT_VALID:
                error = contextWeakReference.get().getString(R.string.username_not_valid);
                break;
            case IConstants.ErrorCodes.INCORRECT_DATA:
                error = contextWeakReference.get().getString(R.string.internal_error);
                break;
            case IConstants.ErrorCodes.MEMES_ENDED:
                error = contextWeakReference.get().getString(R.string.memes_ended);
                break;
            case IConstants.ErrorCodes.NO_SUCH_IMAGE:
                error = contextWeakReference.get().getString(R.string.internal_error);
                break;
            case IConstants.ErrorCodes.NO_SUCH_USER:
                error = contextWeakReference.get().getString(R.string.internal_error);
                break;
            case IConstants.ErrorCodes.NOT_REGISTERED:
                error = contextWeakReference.get().getString(R.string.not_registred_error);
                break;
            case IConstants.ErrorCodes.PASSWORDS_DONT_MATCH:
                error = contextWeakReference.get().getString(R.string.password_dont_match);
                break;
            case IConstants.ErrorCodes.REFERRAL_NOT_PRESENT:
                error = contextWeakReference.get().getString(R.string.referral_not_present);
                break;
            default:
                error = contextWeakReference.get().getString(R.string.internal_error);
                break;
        }
        contextWeakReference.clear();
        return error;
    }
}
