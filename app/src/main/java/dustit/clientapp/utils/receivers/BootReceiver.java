package dustit.clientapp.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import dustit.clientapp.utils.L;
import dustit.clientapp.utils.managers.NotifyManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                context.startService(new Intent(context, NotifyManager.class));
            }
        }
    }
}
