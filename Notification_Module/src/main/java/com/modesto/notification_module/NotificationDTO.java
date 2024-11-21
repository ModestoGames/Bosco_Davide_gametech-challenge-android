package com.modesto.notification_module;

import java.util.UUID;

public class NotificationDTO
{
    private  int status;
    private String workUUID;
    private String title;
    private  String text;
    private int iconId;
    //the time at which work has been create
    private  long creationTime;
    //the time at which work has been scheduled
    private  long schedulationTime;

    public NotificationDTO(UUID workUUID, String title, String text, int iconId, long creationTime, long schedulationTime)
    {
        this.status = 0;
        this.workUUID = workUUID.toString();
        this.title = title;
        this.text = text;
        this.iconId = iconId;
        this.creationTime = creationTime;
        this.schedulationTime = schedulationTime;
    }

    public int getStatus() {
        return status;
    }

    public String getUUIDToString() {
        return workUUID;
    }

    public UUID getWorkUUID(){
        return  UUID.fromString(workUUID);
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public int getIconId() {
        return iconId;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getSchedulationTime() {
        return schedulationTime;
    }

    @Override
    public String toString() {
        return "NotificationDTO {" +
                "status=" + status +
                ", workUUID=" + workUUID +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", iconId=" + iconId +
                ", creationTime=" + creationTime +
                ", schedulationTime=" + schedulationTime +
                '}';
    }
}
