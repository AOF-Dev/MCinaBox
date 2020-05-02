package com.aof.mcinabox.launcher.uis;

import android.app.Activity;
import android.widget.LinearLayout;

import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.SettingJson;

public class PluginUI extends StandUI {

    public PluginUI(Activity context){
        super(context);
        initUI();
    }

    public PluginUI(Activity context,SettingJson setting){
        this(context);
        refreshUI(setting);
    }

    LinearLayout lagout_plugin;

    @Override
    public void initUI() {
        lagout_plugin = mContext.findViewById(R.id.layout_plugin);
    }

    @Override
    public void refreshUI(SettingJson setting) {

    }

    @Override
    public SettingJson saveUIConfig(SettingJson setting) {
        return setting;
    }

    @Override
    public void setUIVisiability(int visiability) {
        lagout_plugin.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return lagout_plugin.getVisibility();
    }
}
