package com.aof.mcinabox.launcher.uis;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.VirtualKeyBoardActivity;
import com.aof.mcinabox.launcher.json.SettingJson;

public class FunctionbarUI extends StandUI {

    public FunctionbarUI(Activity context){
        super(context);
        initUI();
    }

    private LinearLayout layout_functionbar;
    private Button buttonUser;
    private Button buttonPlugin;
    private Button buttonGamelist;
    private Button buttonGamedir;
    private Button buttonSetting;
    private Button buttonKeyboard;

    private View[] views;


    @Override
    public void initUI() {
        layout_functionbar = mContext.findViewById(R.id.layout_functions);
        buttonUser = layout_functionbar.findViewById(R.id.main_button_user);
        buttonPlugin = layout_functionbar.findViewById(R.id.main_button_plugin);
        buttonGamelist = layout_functionbar.findViewById(R.id.main_button_gamelist);
        buttonGamedir = layout_functionbar.findViewById(R.id.main_button_gamedir);
        buttonSetting = layout_functionbar.findViewById(R.id.main_button_setting);
        buttonKeyboard = layout_functionbar.findViewById(R.id.main_button_keyboard);

        views = new View[]{buttonUser,buttonPlugin,buttonGamelist,buttonGamedir,buttonSetting,buttonKeyboard};
        for(View v : views){
            v.setOnClickListener(clickListener);
        }
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
        layout_functionbar.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return layout_functionbar.getVisibility();
    }

    private View.OnClickListener clickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(v == buttonUser){
                ((MainActivity)mContext).switchUIs(((MainActivity) mContext).uiUser,mContext.getString(R.string.title_user));
            }
            if(v == buttonPlugin){
                ((MainActivity)mContext).switchUIs(((MainActivity) mContext).uiPlugin,mContext.getString(R.string.title_plugin));
            }
            if(v == buttonGamelist){
                ((MainActivity)mContext).switchUIs(((MainActivity) mContext).uiGamelist,mContext.getString(R.string.title_gamelist));
            }
            if(v == buttonGamedir){
                ((MainActivity)mContext).switchUIs(((MainActivity) mContext).uiGamedir,mContext.getString(R.string.title_gamedir));
            }
            if(v == buttonSetting){
                ((MainActivity)mContext).switchUIs(((MainActivity) mContext).uiLauncherSetting,mContext.getString(R.string.title_launchersetting));
            }
            if(v == buttonKeyboard){
                //Start VirtualKeyboardActivity.
                Intent intent = new Intent(mContext, VirtualKeyBoardActivity.class);
                mContext.startActivity(intent);
            }
        }
    };


}
