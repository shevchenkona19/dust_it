package dustit.clientapp.utils.receivers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.R;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.datamanager.UserSettingsDataManager;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.ui.activities.NewFeedActivity;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;
import rx.Subscriber;

public class NotificationService extends FirebaseMessagingService {

    @Inject
    DataManager dataManager;
    @Inject
    UserSettingsDataManager userSettingsDataManager;

    int counter = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            if (userSettingsDataManager.isNotificationsEnabled()) {
                Map<String, String> data = remoteMessage.getData();
                String type = data.get("type");
                if (type.equals(IConstants.INotifications.NEW_MEMES)) {
                    buildNewMemesNotification(data);
                } else if (type.equals(IConstants.INotifications.COMMENT_RESPOND)) {
                    buildNewRespondNotification(data);
                }

            }
        }
    }

    private void buildNewMemesNotification(Map<String, String> data) {
        String memesCount = data.get("memesCount");
        sendNewMemesNotification(memesCount);
    }

    private void buildNewRespondNotification(Map<String, String> data) {
        String username = data.get("username");
        String text = data.get("text");
        Intent intent = new Intent(this, NewFeedActivity.class);
        intent.putExtra(IConstants.IBundle.SHOW_COMMENTS, true);
        intent.putExtra(IConstants.IBundle.MEM_ID, data.get("memId"));
        intent.putExtra(IConstants.IBundle.PARENT_COMMENT_ID, data.get("parentCommentId"));
        intent.putExtra(IConstants.IBundle.NEW_COMMENT_ID, data.get("newCommentId"));
        sendNewRespondOnCommentNotification(username, text, intent);
    }

    private void sendNewMemesNotification(String memesCount) {
        if (memesCount == null || memesCount.equals("undefined")) return;
        Intent toLaunch = new Intent(this, NewFeedActivity.class);
        toLaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Notification noti = new NotificationCompat.Builder(this, IConstants.INotifications.CHANNEL_ID)
                .setContentTitle(getString(R.string.return_notification_title))
                .setContentText(String.format(getString(R.string.return_notitfication_message), Integer.valueOf(memesCount)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_notific)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(String.format(getString(R.string.return_notitfication_message), Integer.valueOf(memesCount))))
                .setContentIntent(PendingIntent.getActivity(this, 131314, toLaunch,
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
                .setChannelId(IConstants.INotifications.CHANNEL_ID)
                .build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(456243, noti);
    }

    private void sendNewRespondOnCommentNotification(String username, String text, Intent toLaunch) {
        toLaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Notification noti = new NotificationCompat.Builder(this, IConstants.INotifications.CHANNEL_ID)
                .setContentTitle(String.format(getString(R.string.new_comment), username))
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_notific)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setContentIntent(PendingIntent.getActivity(this, 131314, toLaunch,
                        PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(true)
                .setChannelId(IConstants.INotifications.CHANNEL_ID)
                .build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(123653, noti);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        setToken(s);
        userSettingsDataManager.saveFcmId(s);
    }

    private void setToken(String s) {
        counter += 1;
        AtomicReference<ResponseEntity> reference = new AtomicReference<>();
        dataManager.setFcmId(s).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                L.print("New FCM Token set!");
                if (reference.get().getResponse() == 200) {
                    counter = 0;
                    userSettingsDataManager.setFcmUpdate(true);
                } else {
                    userSettingsDataManager.setFcmUpdate(false);
                }
            }

            @Override
            public void onError(Throwable e) {
                L.print("Error setting FCM Token: " + e.getMessage());
                if (counter <= 5) {
                    setToken(s);
                } else {
                    userSettingsDataManager.setFcmUpdate(false);
                }
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                reference.set(responseEntity);
            }
        });
    }
}
