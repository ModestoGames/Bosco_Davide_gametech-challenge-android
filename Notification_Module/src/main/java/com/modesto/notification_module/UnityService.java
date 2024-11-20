package com.modesto.notification_module;

import android.app.Activity;

public abstract class UnityService
{
    protected Activity _unityActivity;

    public  UnityService(Activity unityActivity){
        _unityActivity = unityActivity;
        OnInitialize();
    }

    public abstract void OnInitialize();
}
