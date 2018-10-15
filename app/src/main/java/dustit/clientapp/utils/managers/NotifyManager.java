package dustit.clientapp.utils.managers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import dustit.clientapp.R;
import dustit.clientapp.mvp.ui.activities.FeedActivity;
import dustit.clientapp.utils.IConstants;

public class NotifyManager extends Service {

    private static Long MILLISECS_PER_DAY = 86400000L;
    private static Long MILLISECS_PER_MIN = 5000L;

//    private static long delay = MILLISECS_PER_MIN * 1;   // 5 seconds (for testing)
    private static long delay = MILLISECS_PER_DAY;   // 1 day

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences settings = getSharedPreferences(IConstants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        if (settings.getBoolean(IConstants.IPreferences.NOTIFICATIONS, true)) {
            if (settings.getLong(IConstants.IPreferences.LAST_RUN, Long.MAX_VALUE) < System.currentTimeMillis() - delay) {
                sendNotification();
            }
        }

        setAlarm();

        stopSelf();
    }

    public void setAlarm() {

        Intent serviceIntent = new Intent(this, NotifyManager.class);
        PendingIntent pi = PendingIntent.getService(this, 131313, serviceIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pi);
        }
    }

    public void sendNotification() {

        Intent mainIntent = new Intent(this, FeedActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Notification noti = new NotificationCompat.Builder(this, "MemSpace")
                .setContentTitle(getString(R.string.return_notification_title))
                .setContentText(getString(R.string.return_notitfication_message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_notific)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.return_notitfication_message)))
                .setContentIntent(PendingIntent.getActivity(this, 131314, mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
                .build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(123653, noti);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}