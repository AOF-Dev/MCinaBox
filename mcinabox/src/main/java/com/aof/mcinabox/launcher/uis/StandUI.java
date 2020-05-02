package com.aof.mcinabox.launcher.uis;

import android.app.Activity;
import com.aof.mcinabox.launcher.json.SettingJson;

public abstract class StandUI {

    public Activity mContext;

    //Method instruction
    public StandUI(Activity context){
        super();
        setUIContext(context);
    }

    public StandUI(Activity context,SettingJson setting){
        this(context);
    }

    //Initate UI and functions.
    public abstract void initUI();

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

}
