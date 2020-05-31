package com.aof.mcinabox.launcher.uis;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.SettingJson;

public class PluginUI extends BaseUI {

    public PluginUI(Activity context,SettingJson setting){
        super(context);
        initUI(setting);
        refreshUI(setting);
    }

    private LinearLayout lagout_plugin;
    private LinearLayout buttonAddPlugin;
    private LinearLayout buttonRefresh;
    private ListView listPlugins;
    private Animation showAnim;

    private View[] views;

    @Override
    public void initUI(SettingJson setting) {
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
        lagout_plugin = mContext.findViewById(R.id.layout_plugin);
        buttonAddPlugin = lagout_plugin.findViewById(R.id.plugin_button_addplugin);
        buttonRefresh = lagout_plugin.findViewById(R.id.plugin_button_refresh);
        listPlugins = lagout_plugin.findViewById(R.id.listview_plugins);

        views = new View[]{buttonRefresh,buttonAddPlugin};
        for(View v : views){
            v.setOnClickListener(clickListener);
        }
    }

    @Override
    public void refreshUI(SettingJson setting) {
        refreshPluginList();
    }

    @Override
    public SettingJson saveUIConfig(SettingJson setting) {
        return setting;
    }

    @Override
    public void setUIVisiability(int visiability) {
        if(visiability == View.VISIBLE){
            lagout_plugin.startAnimation(showAnim);
        }
        lagout_plugin.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return lagout_plugin.getVisibility();
    }

    private View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(v == buttonAddPlugin){
                //TODO:添加插件功能
            }
            if(v == buttonRefresh){
                ((MainActivity)mContext).refreshLauncher(null,true);
            }
        }
    };

    private void refreshPluginList(){
        //TODO:刷新插件列表功能
    }
}
