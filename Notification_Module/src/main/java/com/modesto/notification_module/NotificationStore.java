package com.modesto.notification_module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//This class keep track of all the currently scheduled notifications
public class NotificationStore {
    public static Map<Integer, NotificationDTO> scheduledNotifications = new HashMap<>();


    public  static NotificationDTO getNotification(int id){
        return scheduledNotifications.get(id);
    }

    public static UUID getWorkerUUID(int id){
        return  scheduledNotifications.get(id).getWorkUUID();
    }

    public static void addNotification(int id, NotificationDTO dto){
        scheduledNotifications.put(id, dto);
    }

    public static void removeNotification(int id){
        scheduledNotifications.remove(id);
    }

    public static String getAllNotificationsAsString() {
        StringBuilder sb = new StringBuilder();

        // Itera attraverso tutte le notifiche nella mappa
        for (Map.Entry<Integer, NotificationDTO> entry : scheduledNotifications.entrySet()) {
            // Aggiungi il toString della notifica
            sb.append("Notification ID: ").append(entry.getKey()).append("\n");
            sb.append(entry.getValue().toString()).append("\n\n");
        }

        return sb.toString();
    }
}