package com.modesto.notification_module;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * A Worker class that schedules and displays notifications.
 * This class runs in the background, triggered by WorkManager.
 */
public class NotificationWorker extends Worker {
    private final NotificationManager _notificationManager;
    public NotificationWorker(@NonNull Context context,
                              @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        _notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

/**
 * This is the main method executed when the Worker runs.
 * Retrieves the notification ID from the input data and displays the notification.
 */
    @NonNull
    @Override
    public Result doWork() {
        int id = getInputData().getInt("id", 0);
        showNotification(getApplicationContext(), id);
        return Result.success();
    }

    private void showNotification(@NonNull Context context, int id){
        // Create an intent to launch the app when the notification is tapped
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add extra data for Unity integration
            intent.putExtra("notification_id", id);
            intent.putExtra("notification_title", Utils.getTitle(id));
            intent.putExtra("notification_text", Utils.getText(id));
            intent.putExtra("package_name", "com.modesto.notification_module");
            intent.putExtra("icon", String.valueOf(Utils.getIcon(context, id)));
            intent.putExtra("notification_timestamp", System.currentTimeMillis());
        }

        // Create a PendingIntent for the notification's tap action
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                id,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Build the notification with a title, text, icon, and tap action
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setContentTitle(Utils.getTitle(id))
                .setContentText(Utils.getText(id))
                .setSmallIcon(Utils.getIcon(context, id))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        _notificationManager.notify(id, builder.build());
    }
}