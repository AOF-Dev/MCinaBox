package com.aof.mcinabox.launcher.uis;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.aof.mcinabox.activity.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.setting.support.SettingJson;

public class PluginUI extends BaseUI {

    public PluginUI(Context context) {
        super(context);
    }

    private LinearLayout lagout_plugin;
    private LinearLayout buttonAddPlugin;
    private LinearLayout buttonRefresh;
    private ListView listPlugins;
    private Animation showAnim;
    private SettingJson setting;

    @Override
    public void onCreate( ) {
        super.onCreate();
        setting = MainActivity.Setting;
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
        lagout_plugin = MainActivity.CURRENT_ACTIVITY.findViewById(R.id.layout_plugin);
        buttonAddPlugin = lagout_plugin.findViewById(R.id.plugin_button_addplugin);
        buttonRefresh = lagout_plugin.findViewById(R.id.plugin_button_refresh);
        listPlugins = lagout_plugin.findViewById(R.id.listview_plugins);

        for(View v : new View[]{buttonAddPlugin, buttonRefresh}){
            v.setOnClickListener(clickListener);
        }
        refreshUI();
    }

    @Override
    public void refreshUI() {
        refreshPluginList();
    }

    @Override
    public void saveUIConfig() {
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
                //TODO:刷新插件列表
            }
        }
    };

    private void refreshPluginList(){
        //TODO:刷新插件列表功能
    }
}
