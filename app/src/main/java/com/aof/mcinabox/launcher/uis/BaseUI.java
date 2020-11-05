package com.aof.mcinabox.launcher.uis;

import android.content.Context;

public abstract class BaseUI implements UILifecycleCallbacks {

    public Context mContext;

    //Method instruction
    public BaseUI(Context context){
        super();
        setUIContext(context);
    }

    //Apply states from Setting to UIs
    public abstract void refreshUI();

    //Save States of UIs to Setting
    public abstract void saveUIConfig();

    //Set the visiability of the UI
    public abstract void setUIVisiability(int visiability);

    //Get the visiability of the UI
    public abstract int getUIVisiability();

    //Set Android Context
    public void setUIContext(Context context){
        this.mContext = context;
    }

    @Override
    public void onCreate(){ }

    @Override
    public void onStart(){ }

    @Override
    public void onResume(){ }

    @Override
    public void onPause(){ }

    @Override
    public void onStop(){ }

    @Override
    public void onDestory(){}

    @Override
    public void onRestart(){}
}
