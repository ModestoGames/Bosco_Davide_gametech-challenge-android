package com.modesto.notification_module;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A service to handle local notifications for the Unity application.
 * Extends the UnityService to integrate with Unity's activity lifecycle.
 */
public class LocalNotificationsService extends UnityService{
    public LocalNotificationsService(Activity unityActivity) {
        super(unityActivity);
    }

    //keep track of the scheduled notifications to handle them during app runtime
    private Map<Integer, UUID> scheduledNotifications = new HashMap<>();
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

            channel.enableVibration(true);
            // vibration schema (initial delay, vibration, pause, vibration)
            long[] vibrationPattern = {0, 500, 250, 500};
            channel.setVibrationPattern(vibrationPattern);

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

        //cache reference to the scheduledNotification list
        scheduledNotifications.put(id, notificationWork.getId());
        // Enqueue the work request to WorkManager.
        WorkManager.getInstance(_unityActivity).enqueue(notificationWork);
    }

    //retrieve a scheduled work UUID and use it to delete the work
    public void deleteScheduledNotification(int id)
    {
        UUID workId = scheduledNotifications.get(id);
        WorkManager.getInstance(_unityActivity).cancelWorkById(workId);
        scheduledNotifications.remove(id);
    }

    //iterate over scheduled notification work id and delete them all
    public void deleteAllScheduledNotifications() {
        //create a list for iteration to avoid ConcurrentModificationException
        List<Integer> ids = new ArrayList<>(scheduledNotifications.keySet());
        for (Integer id : ids) {
            deleteScheduledNotification(id);
        }
    }
}