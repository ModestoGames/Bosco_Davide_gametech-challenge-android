package com.modesto.notification_module;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.util.concurrent.TimeUnit;

/**
 * A service to handle local notifications for the Unity application.
 * Extends the UnityService to integrate with Unity's activity lifecycle.
 */
public class LocalNotificationsService extends UnityService{
    public LocalNotificationsService(Activity unityActivity) {
        super(unityActivity);
    }

    /**
     * Initializes the service. Creates a notification channel if the device is running
     * Android Oreo (API 26) or above. Channels are mandatory for notifications on these versions.
     */
    @Override
    public void OnInitialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.CHANNEL_ID,
                    Constants.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = (NotificationManager) _unityActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    public void scheduleNotification(int id, int delayMinutes) {
        // Create a one-time work request with the delay specified.
        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .setInputData(
                        new androidx.work.Data.Builder()
                                .putInt("id", id)
                                .build()
                )
                .build();
        // Enqueue the work request to WorkManager.
        WorkManager.getInstance(_unityActivity).enqueue(notificationWork);
    }
}