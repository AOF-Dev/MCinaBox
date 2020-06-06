package com.aof.mcinabox.launcher.uis;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.aof.mcinabox.launcher.json.SettingJson;

public abstract class BaseUI implements UILifecycleCallbacks {

    public Activity mContext;

    //Method instruction
    public BaseUI(Activity context){
        super();
        setUIContext(context);
    }

    //Apply states from Setting to UIs
    public abstract void refreshUI(SettingJson setting);

    //Save States of UIs to Setting
    public abstract SettingJson saveUIConfig(SettingJson setting);

    //Set the visiability of the UI
    public abstract void setUIVisiability(int visiability);

    //Get the visiability of the UI
    public abstract int getUIVisiability();

    //Set Android Context
    public void setUIContext(Activity context){
        this.mContext = context;
    }

    @Override
    public void onCreate(SettingJson setting){ }

    @Override
    public void onStart(SettingJson setting){ }

    @Override
    public void onResumed(){ }

    @Override
    public void onPaused(){ }

    @Override
    public void onStop(){ }

    @Override
    public void onDestory(){ }
}

interface UILifecycleCallbacks{
    void onCreate(SettingJson setting);
    void onStart(SettingJson setting);
    void onResumed();
    void onPaused();
    void onStop();
    void onDestory();
}
