package dustit.clientapp.utils.managers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import java.lang.ref.WeakReference;

import dustit.clientapp.R;

public class ErrorManager {
    private static ErrorManager errorManager;

    private ErrorManager() {
    }

    public static ErrorManager get() {
        if (errorManager == null) errorManager = new ErrorManager();
        return errorManager;
    }

    public void showError(String error, WeakReference<Context> context) {
        buildErrorDialog(context, context.get().getString(R.string.error), error).show();
    }

    private AlertDialog buildErrorDialog(WeakReference<Context> context, String title, String error) {
        AlertDialog dialog = new AlertDialog.Builder(context.get())
                .setTitle(title)
                .setMessage(error)
                .setPositiveButton(context.get().getString(R.string.ok), null)
                .create();
        dialog.setOnShowListener(dialog1 -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000")));
        return dialog;
    }
}
