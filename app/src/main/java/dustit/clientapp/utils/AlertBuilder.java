package dustit.clientapp.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;

import dustit.clientapp.R;
import dustit.clientapp.mvp.ui.activities.RegisterActivity;

public class AlertBuilder {

    public static void showNotRegisteredPrompt(final Context context) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.you_arent_registered))
                .setMessage(context.getString(R.string.function_disabled_not_registered))
                .setPositiveButton(context.getText(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Intent intent = new Intent(context, RegisterActivity.class);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(context.getText(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(context.getText(R.string.i_will_think), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#000000"));
            }
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
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog1) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
            }
        });
        dialog.show();
    }
}
