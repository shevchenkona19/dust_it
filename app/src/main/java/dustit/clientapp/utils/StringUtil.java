package dustit.clientapp.utils;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import java.util.regex.Pattern;

import dustit.clientapp.R;

/**
 * Created by малолетка on 12.10.2017.
 */

public class StringUtil {
    private final Context context;
    private String emailPattern = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(emailPattern);

    public StringUtil(Context context) {
        this.context = context;
    }

    //Email check
    private boolean isEmailCorrect(EditText editText) {
        return pattern.matcher(editText.getText().toString()).matches();
    }

    private void setEmailError(TextInputLayout textInputLayout) {
        textInputLayout.setError(context.getString(R.string.email_not_valid));
    }

    //Username check
    private boolean isUsernameNormLength(EditText editText) {
        return editText.getText().length() > 0 && editText.getText().length() < 17;
    }

    private void setUsernameIncorrectLengthError(TextInputLayout textInputLayout) {
        textInputLayout.setError(context.getString(R.string.login_length));
    }

    private boolean isUsernameContainLetter(EditText editText) {
        char[] c = editText.getText().toString().toCharArray();
        for (char ch : c
                ) {
            if (Pattern.matches("[a-zA-Z]+", String.valueOf(ch))) {
                return true;
            }
        }
        return false;
    }

    private void setUsernameNoLetterError(TextInputLayout textInputLayout) {
        textInputLayout.setError(context.getString(R.string.must_containt_at_least_one_latin));
    }

    private boolean isUsernameValid(EditText editText) {
        char[] c = editText.getText().toString().toCharArray();
        for (char ch : c
                ) {
            if (!Pattern.matches("[a-zA-Z0-9]+", String.valueOf(ch))) {
                return false;
            }
        }
        return true;
    }

    private void setUsernameValidError(TextInputLayout textInputLayout) {
        textInputLayout.setError(context.getString(R.string.only_numbers_and_latin));
    }

    //Password check
    private boolean isPasswordNormLength(EditText editText) {
        return editText.getText().length() > 5 && editText.getText().length() < 23;
    }

    private void setPasswordIncorrectLengthError(TextInputLayout textInputLayout) {
        textInputLayout.setError(context.getString(R.string.password_length_error));
    }

    private boolean isPasswordContainLetterNumber(EditText editText) {
        boolean isLetter = false;
        char[] c = editText.getText().toString().toCharArray();
        for (char ch : c
                ) {
            if (Pattern.matches("[a-zA-Z]+", String.valueOf(ch))) {
                isLetter = true;
            }
        }
        return (isLetter);
    }

    private void setPasswordNoLetterNumberError(TextInputLayout textInputLayout) {
        textInputLayout.setError(context.getString(R.string.password_not_valid));
    }

    private boolean isPasswordValid(EditText editText) {
        char[] c = editText.getText().toString().toCharArray();
        for (char ch : c
                ) {
            if (!Pattern.matches("[a-zA-Z0-9]+", String.valueOf(ch))) {
                return false;
            }
        }
        return true;
    }

    private void setPasswordValidError(TextInputLayout textInputLayout) {
        textInputLayout.setError(context.getString(R.string.only_numbers_and_latin));
    }

    /**
     * for registration
     **/
    public boolean isCorrectInput(EditText etRegisterEmail, EditText etRegisterPassword,
                                  EditText etRegisterUsername) {
        return (isUsernameContainLetter(etRegisterUsername) &&
                isUsernameNormLength(etRegisterUsername) && isUsernameValid(etRegisterUsername) &&
                isPasswordContainLetterNumber(etRegisterPassword) &&
                isPasswordNormLength(etRegisterPassword) && isPasswordValid(etRegisterPassword) &&
                isEmailCorrect(etRegisterEmail));
    }

    private void usernameInputChecker(EditText etRegisterUsername, TextInputLayout tilRegisterUsername) {
        if (!isUsernameContainLetter(etRegisterUsername)) {
            setUsernameNoLetterError(tilRegisterUsername);
        }
        if (!isUsernameValid(etRegisterUsername)) {
            setUsernameValidError(tilRegisterUsername);
        }
        if (!isUsernameNormLength(etRegisterUsername)) {
            setUsernameIncorrectLengthError(tilRegisterUsername);
        }
    }

    private void passwordInputChecker(EditText etRegisterPassword, TextInputLayout tilRegisterPassword) {
        if (!isPasswordValid(etRegisterPassword)) {
            setPasswordValidError(tilRegisterPassword);
        }
        if (!isPasswordContainLetterNumber(etRegisterPassword)) {
            setPasswordNoLetterNumberError(tilRegisterPassword);
        }
        if (!isPasswordNormLength(etRegisterPassword)) {
            setPasswordIncorrectLengthError(tilRegisterPassword);
        }
    }

    private void emailInputChecker(EditText etRegisterEmail, TextInputLayout tilRegisterEmail) {
        if (!isEmailCorrect(etRegisterEmail)) {
            setEmailError(tilRegisterEmail);
        }
    }

    public void showError(EditText etRegisterEmail, EditText etRegisterPassword,
                          EditText etRegisterUsername, TextInputLayout tilRegisterEmail,
                          TextInputLayout tilRegisterPassword, TextInputLayout tilRegisterUsername) {
        usernameInputChecker(etRegisterUsername, tilRegisterUsername);
        passwordInputChecker(etRegisterPassword, tilRegisterPassword);
        emailInputChecker(etRegisterEmail, tilRegisterEmail);
    }

    public void hideError(TextInputLayout tilRegisterPassword, TextInputLayout tilRegisterUsername,
                          TextInputLayout tilRegisterEmail) {
        tilRegisterPassword.setError("");
        tilRegisterUsername.setError("");
        tilRegisterEmail.setError("");
    }


    /**
     * for login
     **/
    public boolean isCorrectInput(EditText etLoginPassword, EditText etLoginUsername) {
        return (isUsernameNormLength(etLoginUsername) && isPasswordNormLength(etLoginPassword) &&
                isPasswordValid(etLoginPassword) && isUsernameValid(etLoginUsername));
    }

    public void showError(EditText etLoginPassword, EditText etLoginUsername,
                          TextInputLayout tilLoginPassword, TextInputLayout tilLoginUsername) {
        if (!isUsernameNormLength(etLoginUsername)) {
            setUsernameIncorrectLengthError(tilLoginUsername);
        } else if (!isUsernameValid(etLoginUsername)) {
            setUsernameValidError(tilLoginUsername);
        }
        if (!isPasswordNormLength(etLoginPassword)) {
            setPasswordIncorrectLengthError(tilLoginPassword);
        } else if (!isPasswordValid(etLoginPassword)) {
            setPasswordValidError(tilLoginPassword);
        }
    }

    public void hideError(TextInputLayout tilLoginPassword, TextInputLayout tilLoginUsername) {
        tilLoginPassword.setError("");
        tilLoginUsername.setError("");
    }
}
