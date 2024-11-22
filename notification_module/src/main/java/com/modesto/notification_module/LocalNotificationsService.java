package com.modesto.notification_module;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.SystemClock;

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

    private int[] oldSortingOrder;
    private int[] newSortingOrder;

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

    public  void setOldSorting(int[] oldSorting){
        this.oldSortingOrder = oldSorting;
    }

    public void setNewSorting(int[] newSorting) {
        this.newSortingOrder = newSorting;

        if(oldSortingOrder.length <= 0) {
            this.oldSortingOrder = null;
            this.newSortingOrder = null;
            return;
        }

        //when new sorting and old sorting are set if there are difference
        //reschedule notification
        if(!Utils.arraysHaveSameItems(this.oldSortingOrder, this.newSortingOrder))
            switchNotificationSchedules();
    }

    public Map<Integer, NotificationDTO> getCurrentNotifications(){
        return NotificationStore.scheduledNotifications;
    }

    public NotificationDTO scheduleNotification(int id, int delaySeconds) {
        // Create a one-time work request with the delay specified.
        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
            .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
            .setInputData(
                new androidx.work.Data.Builder()
                        .putInt("id", id)
                        .build()
            )
            .build();

        //create a DTO to keep track of the scheduled notification
        NotificationDTO dto = new NotificationDTO(
                notificationWork.getId(),
                Utils.getTitle(id),
                Utils.getText(id),
                Utils.getIcon(id),
                //get the current time in milliseconds
                SystemClock.elapsedRealtime(),
                //get the schedulation time by adding n minutes in milliseconds
                SystemClock.elapsedRealtime() + ((long) delaySeconds * 1000)
        );

        //cache reference to the scheduledNotification list
        NotificationStore.addNotification(id, dto);
        // Enqueue the work request to WorkManager.
        WorkManager.getInstance(_unityActivity).enqueue(notificationWork);
        return dto;
    }

    //return system time to update Unity UI
    public long getCurrentSystemTime()
    {
        return SystemClock.elapsedRealtime();
    }

    //debug method
    public  String getNotificationAsString(){
        return  NotificationStore.getAllNotificationsAsString();
    }

    //retrieve a scheduled work UUID and use it to delete the work
    public void deleteScheduledNotification(int id)
    {
        UUID workId = NotificationStore.getWorkerUUID(id);
        WorkManager.getInstance(_unityActivity).cancelWorkById(workId);
        NotificationStore.removeNotification(id);
    }

    //Switch
    private void switchNotificationSchedules() {

        //create a list to store the old remaining times
        List<Long> remainingTime = new ArrayList<>();

        //cycle through map and store them
        for (Map.Entry<Integer, NotificationDTO> entry : NotificationStore.scheduledNotifications.entrySet()) {
            long schedulationTime = entry.getValue().getSchedulationTime();
            long remainingMillisec = schedulationTime - SystemClock.elapsedRealtime();
            remainingTime.add(remainingMillisec);
        }

        //delete all the current schedules notification
        for(int i = 0; i < oldSortingOrder.length; i++){
            deleteScheduledNotification(oldSortingOrder[i]);
        }

        //reschedule all notification with the same as previous
        for(int i = 0; i < newSortingOrder.length; i++){
            reScheduleNotification(newSortingOrder[i], remainingTime.get(i));
        }
    }

    public void reScheduleNotification(int id, long delayMilliseconds) {
        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delayMilliseconds, TimeUnit.MILLISECONDS)
                .setInputData(
                        new androidx.work.Data.Builder()
                                .putInt("id", id)
                                .build()
                )
                .build();

        //create a DTO to keep track of the scheduled notification
        NotificationDTO dto = new NotificationDTO(
                notificationWork.getId(),
                Utils.getTitle(id),
                Utils.getText(id),
                Utils.getIcon(id),
                //get the current time in milliseconds
                SystemClock.elapsedRealtime(),
                //get the schedulation time by adding remaining milliseconds
                SystemClock.elapsedRealtime() + delayMilliseconds
        );

        //cache reference to the scheduledNotification list
        NotificationStore.addNotification(id, dto);
        // Enqueue the work request to WorkManager.
        WorkManager.getInstance(_unityActivity).enqueue(notificationWork);
    }
}