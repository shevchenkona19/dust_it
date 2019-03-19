package dustit.clientapp.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;

import java.lang.ref.WeakReference;

import dustit.clientapp.R;
import dustit.clientapp.mvp.ui.activities.ChooserActivity;

public class AlertBuilder {

    public static void showNotRegisteredPrompt(final Context context) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.you_arent_registered))
                .setMessage(context.getString(R.string.function_disabled_not_registered))
                .setPositiveButton(context.getText(R.string.yes), (dialog, which) -> {
                    L.print("wtf1");
                    final Intent intent = new Intent(context, ChooserActivity.class);
                    context.startActivity(intent);
                })
                .setNegativeButton(context.getText(R.string.no), (dialog, which) -> dialog.dismiss())
                .setNeutralButton(context.getText(R.string.i_will_think), (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .create();
        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorAccent));
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorAccent));
            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(context.getResources().getColor(R.color.colorAccent));
        });
        alertDialog.show();
    }

    public static void showMemSource(final Context context, final String src) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.about_mem_title))
                .setMessage(context.getString(R.string.about_mem_message) + " " + src)
                .setPositiveButton(context.getString(R.string.ok), null)
                .setCancelable(true)
                .create();
        dialog.setOnShowListener(dialog1 -> dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000")));
        dialog.show();
    }

    public static void showRegisterPrompt(Context context) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.create_account))
                .setMessage(context.getString(R.string.description_create_account))
                .setPositiveButton(context.getText(R.string.yes), (dialog, which) -> {
                    final Intent intent = new Intent(context, ChooserActivity.class);
                    context.startActivity(intent);
                })
                .setNegativeButton(context.getText(R.string.no), (dialog, which) -> dialog.dismiss())
                .setNeutralButton(context.getText(R.string.i_will_think), (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .create();
        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorAccent));
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.colorAccent));
            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(context.getResources().getColor(R.color.colorAccent));
        });
        alertDialog.show();
    }

    public static AlertDialog.Builder getReviewDialog(WeakReference<Context> reference) {
        Context context = reference.get();
        return new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.review_title))
                .setMessage(context.getString(R.string.review_message));
    }

    public static AlertDialog.Builder getReferralDialog(WeakReference<Context> contextReference) {
        Context context = contextReference.get();
        return new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.do_you_have_ref_code_title));
    }

    public static AlertDialog.Builder getReferralCodeDialog(WeakReference<Context> contextReference) {
        Context context = contextReference.get();
        return new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.input_ref_code));
    }

    public static AlertDialog getUploadPermissionsRequired(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.permission_upload_title))
                .setMessage(context.getString(R.string.permission_upload_message))
                .setPositiveButton(context.getText(R.string.ok), null)
                .setCancelable(true)
                .create();
        alertDialog.setOnShowListener(dialog -> alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorAccent)));
        return alertDialog;
    }

    public static AlertDialog getEmptyCategoriesError(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.empty_categories_selected_upload))
                .setMessage(context.getString(R.string.empty_categories_selected_upload_message))
                .setPositiveButton(context.getText(R.string.ok), null)
                .setCancelable(true)
                .create();
        return alertDialog;
    }
}
