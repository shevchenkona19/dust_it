package dustit.clientapp.utils.managers;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.repositories.SharedPreferencesRepository;
import dustit.clientapp.utils.AlertBuilder;

public class ReviewManager {
    private static ReviewManager reviewManager;
    @Inject
    SharedPreferencesRepository sharedPreferencesRepository;

    private ReviewManager() {
        App.get().getAppComponent().inject(this);
    }

    public static ReviewManager get() {
        if (reviewManager == null) reviewManager = new ReviewManager();
        return reviewManager;
    }

    public void positiveCount(WeakReference<Context> context) {
        if (!sharedPreferencesRepository.isReviewed()) {
            int positiveCount = sharedPreferencesRepository.getPositiveCount();
            if (positiveCount + 1 >= sharedPreferencesRepository.getPositiveLimit()) {
                review(context);
            } else {
                sharedPreferencesRepository.savePositiveCount(positiveCount + 1);
            }
        }
    }

    private void review(WeakReference<Context> context) {
        AlertDialog.Builder builder = AlertBuilder.getReviewDialog(context);
        AlertDialog dialog = builder.setNeutralButton(context.get().getString(R.string.i_will_think), (dialog1, which) -> sharedPreferencesRepository.setPositiveLimit(sharedPreferencesRepository.getPositiveLimit() + 3))
                .setNegativeButton(context.get().getString(R.string.never), (dialog1, which) -> sharedPreferencesRepository.setReviewed(true))
                .setPositiveButton(context.get().getString(R.string.leave_review), (dialog1, which) -> {
                    sharedPreferencesRepository.setReviewed(true);
                    openAppRating(context.get());
                })
                .setOnCancelListener(dialog1 -> sharedPreferencesRepository.setPositiveLimit(sharedPreferencesRepository.getPositiveLimit() + 3))
                .create();
        dialog.setOnShowListener(dialog1 -> {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
            dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#000000"));
        });
        dialog.show();
    }

    private void openAppRating(Context context) {
        // you can also use BuildConfig.APPLICATION_ID
        String appId = context.getPackageName();
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="+appId));
            context.startActivity(webIntent);
        }
    }

}
