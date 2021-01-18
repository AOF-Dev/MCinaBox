package com.aof.mcinabox.launcher.uis;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.gamecontroller.ckb.CustomizeKeyboardEditorActivity;
import com.aof.mcinabox.launcher.setting.support.SettingJson;

public class FunctionbarUI extends BaseUI {

    public FunctionbarUI(Context context) {
        super(context);
    }

    private LinearLayout layout_functionbar;
    private LinearLayout buttonUser;
    private LinearLayout buttonPlugin;
    private LinearLayout buttonGamelist;
    private LinearLayout buttonGamedir;
    private LinearLayout buttonSetting;
    private LinearLayout buttonKeyboard;
    private LinearLayout buttonHome;
    private LinearLayout buttonLog;
    private TextView textUserName;
    private TextView textUserType;
    private SettingJson setting;

    private final View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == buttonUser) {
                OldMainActivity.CURRENT_ACTIVITY.get().switchUIs(OldMainActivity.CURRENT_ACTIVITY.get().mUiManager.uiUser, mContext.getString(R.string.title_user));
            }
            if (v == buttonPlugin) {
                OldMainActivity.CURRENT_ACTIVITY.get().switchUIs(OldMainActivity.CURRENT_ACTIVITY.get().mUiManager.uiPlugin, mContext.getString(R.string.title_plugin));
            }
            if (v == buttonGamelist) {
                OldMainActivity.CURRENT_ACTIVITY.get().switchUIs(OldMainActivity.CURRENT_ACTIVITY.get().mUiManager.uiGamelist, mContext.getString(R.string.title_game_list));
            }
            if(v == buttonGamedir){
                OldMainActivity.CURRENT_ACTIVITY.get().switchUIs(OldMainActivity.CURRENT_ACTIVITY.get().mUiManager.uiGamedir, mContext.getString(R.string.title_game_dir));
            }
            if(v == buttonSetting){
                OldMainActivity.CURRENT_ACTIVITY.get().switchUIs(OldMainActivity.CURRENT_ACTIVITY.get().mUiManager.uiLauncherSetting, mContext.getString(R.string.title_launcher_setting));
            }
            if (v == buttonKeyboard) {
                Intent intent = new Intent(mContext, CustomizeKeyboardEditorActivity.class);
                mContext.startActivity(intent);
            }
            if (v == buttonHome) {
                OldMainActivity.CURRENT_ACTIVITY.get().switchUIs(OldMainActivity.CURRENT_ACTIVITY.get().mUiManager.uiStartGame, mContext.getString(R.string.title_home));
            }
            if (v == buttonLog) {
                OldMainActivity.CURRENT_ACTIVITY.get().switchUIs(OldMainActivity.CURRENT_ACTIVITY.get().mUiManager.uiLog, mContext.getString(R.string.title_log));
            }
        }
    };

    @Override
    public void refreshUI() {
        refreshUserInfo();
    }

    @Override
    public void saveUIConfig() {

    }

    @Override
    public void setUIVisibility(int visibility) {
        //FunctionbarUI should keep visiable.
    }

    @Override
    public int getUIVisibility() {
        return layout_functionbar.getVisibility();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setting = OldMainActivity.Setting;
        layout_functionbar = OldMainActivity.CURRENT_ACTIVITY.get().findViewById(R.id.layout_functions);
        buttonUser = layout_functionbar.findViewById(R.id.main_button_user);
        buttonPlugin = layout_functionbar.findViewById(R.id.main_button_plugin);
        buttonGamelist = layout_functionbar.findViewById(R.id.main_button_gamelist);
        buttonGamedir = layout_functionbar.findViewById(R.id.main_button_gamedir);
        buttonSetting = layout_functionbar.findViewById(R.id.main_button_setting);
        buttonKeyboard = layout_functionbar.findViewById(R.id.main_button_keyboard);
        buttonHome = layout_functionbar.findViewById(R.id.main_button_home);
        buttonLog = layout_functionbar.findViewById(R.id.main_button_log);
        textUserName = layout_functionbar.findViewById(R.id.functionbar_username);
        textUserType = layout_functionbar.findViewById(R.id.functionbar_usertype);

        for (View v : new View[]{buttonUser, buttonPlugin, buttonGamelist, buttonGamedir, buttonSetting, buttonKeyboard, buttonHome, buttonLog}) {
            v.setOnClickListener(clickListener);
        }
        refreshUI();

    }

    private void refreshUserInfo() {
        boolean selected = false;
        SettingJson.Account[] accounts = setting.getAccounts();
        if (accounts != null && accounts.length != 0) {
            for (SettingJson.Account a : accounts) {
                if (a.isSelected()) {
                    selected = true;
                    textUserName.setText(a.getUsername());
                    String type;
                    switch(a.getType()){
                        case SettingJson.USER_TYPE_OFFLINE:
                            type = mContext.getString(R.string.title_offline);
                            break;
                        case SettingJson.USER_TYPE_ONLINE:
                            type = mContext.getString(R.string.title_online);
                            break;
                        case SettingJson.USER_TYPE_EXTERNAL:
                            type = mContext.getString(R.string.title_external);
                            break;
                        default:
                            type = mContext.getString(R.string.title_unknown);
                            break;
                    }
                    textUserType.setText(type);
                    break;
                }
            }
        }
        if(!selected){
            textUserName.setText(mContext.getString(R.string.title_user));
            textUserType.setText("");
        }
    }

}
