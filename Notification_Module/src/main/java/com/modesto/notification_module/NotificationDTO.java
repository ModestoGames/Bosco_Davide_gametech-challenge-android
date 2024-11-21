package com.modesto.notification_module;

import java.util.UUID;

public class NotificationDTO
{
    private  int status;
    private UUID workUUID;
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
        this.workUUID = workUUID;
        this.title = title;
        this.text = text;
        this.iconId = iconId;
        this.creationTime = creationTime;
        this.schedulationTime = schedulationTime;
    }

    public  UUID getWorkUUID(){
        return  workUUID;
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
