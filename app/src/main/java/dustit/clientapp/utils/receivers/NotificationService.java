package dustit.clientapp.utils.receivers;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import dustit.clientapp.mvp.ui.activities.AccountActivity;
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
                if (type == null) return;
                switch (type) {
                    case IConstants.INotifications.NEW_MEMES:
                        buildNewMemesNotification(data);
                        break;
                    case IConstants.INotifications.COMMENT_RESPOND:
                        buildNewRespondNotification(data);
                        break;
                    case IConstants.INotifications.NOTIFY_ABOUT_POSTED_MEME:
                        buildNewLikeNotification(data);
                        break;
                }
            }
        }
    }

    private void buildNewLikeNotification(Map<String, String> data) {
        String byUsername = data.get("byUsername");
        String byUserId = data.get("byUserId");
        String eventType = data.get("eventType");
        String myId = data.get("myId");
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra(IConstants.IBundle.IS_ME, true);
        intent.putExtra(IConstants.IBundle.USER_ID, myId);
        sendNewLikeNotification(byUsername, byUserId, eventType, intent);
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

    private void sendNewLikeNotification(String byUsername, String byUserId, String eventType, Intent intent) {
        if (byUserId == null || byUserId.equals("undefined") ||
                byUsername == null || byUsername.equals("undefined") ||
                eventType == null || eventType.equals("undefined")) return;


        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addNextIntent(new Intent(this, NewFeedActivity.class));
        stackBuilder.addNextIntent(intent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification noti = new NotificationCompat.Builder(this, IConstants.INotifications.CHANNEL_ID)
                .setContentTitle(getString(R.string.new_like_notification_title))
                .setContentText(String.format(getString(R.string.new_like_notification), byUsername))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_notific)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(String.format(getString(R.string.new_like_notification), byUsername)))
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setChannelId(IConstants.INotifications.CHANNEL_ID)
                .setSound(uri)
                .build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(567432, noti);
    }

    private void sendNewMemesNotification(String memesCount) {
        if (memesCount == null || memesCount.equals("undefined")) return;
        Intent toLaunch = new Intent(this, NewFeedActivity.class);
        toLaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

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
                .setSound(uri)
                .build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(456243, noti);
    }

    private void sendNewRespondOnCommentNotification(String username, String text, Intent toLaunch) {
        toLaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

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
                .setSound(uri)
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
