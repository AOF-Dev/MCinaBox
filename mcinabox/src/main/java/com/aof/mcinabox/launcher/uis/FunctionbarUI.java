package com.aof.mcinabox.launcher.uis;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
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
    private TextView textUserName;
    private TextView textUserType;
    private SettingJson setting;

    @Override
    public void onCreate() {
        super.onCreate();
        setting = MainActivity.Setting;
        layout_functionbar = MainActivity.CURRENT_ACTIVITY.findViewById(R.id.layout_functions);
        buttonUser = layout_functionbar.findViewById(R.id.main_button_user);
        buttonPlugin = layout_functionbar.findViewById(R.id.main_button_plugin);
        buttonGamelist = layout_functionbar.findViewById(R.id.main_button_gamelist);
        buttonGamedir = layout_functionbar.findViewById(R.id.main_button_gamedir);
        buttonSetting = layout_functionbar.findViewById(R.id.main_button_setting);
        buttonKeyboard = layout_functionbar.findViewById(R.id.main_button_keyboard);
        buttonHome = layout_functionbar.findViewById(R.id.main_button_home);
        textUserName = layout_functionbar.findViewById(R.id.functionbar_username);
        textUserType = layout_functionbar.findViewById(R.id.functionbar_usertype);

        for (View v : new View[]{buttonUser, buttonPlugin, buttonGamelist, buttonGamedir, buttonSetting, buttonKeyboard, buttonHome}) {
            v.setOnClickListener(clickListener);
        }
        refreshUI();

    }

    @Override
    public void refreshUI() {
        refreshUserInfo();
    }

    @Override
    public void saveUIConfig() {

    }

    @Override
    public void setUIVisiability(int visiability) {
        //FunctionbarUI should keep visiable.
    }

    @Override
    public int getUIVisiability() {
        return layout_functionbar.getVisibility();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == buttonUser) {
                MainActivity.CURRENT_ACTIVITY.switchUIs(MainActivity.CURRENT_ACTIVITY.mUiManager.uiUser, mContext.getString(R.string.title_user));
            }
            if (v == buttonPlugin) {
                MainActivity.CURRENT_ACTIVITY.switchUIs(MainActivity.CURRENT_ACTIVITY.mUiManager.uiPlugin, mContext.getString(R.string.title_plugin));
            }
            if (v == buttonGamelist) {
                MainActivity.CURRENT_ACTIVITY.switchUIs(MainActivity.CURRENT_ACTIVITY.mUiManager.uiGamelist, mContext.getString(R.string.title_game_list));
            }
            if (v == buttonGamedir) {
                MainActivity.CURRENT_ACTIVITY.switchUIs(MainActivity.CURRENT_ACTIVITY.mUiManager.uiGamedir, mContext.getString(R.string.title_game_dir));
            }
            if (v == buttonSetting) {
                MainActivity.CURRENT_ACTIVITY.switchUIs(MainActivity.CURRENT_ACTIVITY.mUiManager.uiLauncherSetting, mContext.getString(R.string.title_launcher_setting));
            }
            if (v == buttonKeyboard) {
                Intent intent = new Intent(mContext, CustomizeKeyboardEditorActivity.class);
                mContext.startActivity(intent);
            }
            if (v == buttonHome) {
                MainActivity.CURRENT_ACTIVITY.switchUIs(MainActivity.CURRENT_ACTIVITY.mUiManager.uiStartGame, mContext.getString(R.string.title_home));
            }
        }
    };

    private void refreshUserInfo() {
        boolean selected = false;
        SettingJson.Account[] accounts = setting.getAccounts();
        if (accounts != null && accounts.length != 0) {
            for (SettingJson.Account a : accounts) {
                if (a.isSelected()) {
                    selected = true;
                    textUserName.setText(a.getUsername());
                    String type;
                    switch (a.getType()) {
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
        if (!selected) {
            textUserName.setText(mContext.getString(R.string.title_user));
            textUserType.setText("");
        }
    }

}
