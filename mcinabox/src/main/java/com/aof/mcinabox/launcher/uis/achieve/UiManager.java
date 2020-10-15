package com.aof.mcinabox.launcher.uis.achieve;

import android.content.Context;
import android.view.View;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.uis.BaseUI;
import com.aof.mcinabox.launcher.uis.FunctionbarUI;
import com.aof.mcinabox.launcher.uis.GameSettingUI;
import com.aof.mcinabox.launcher.uis.GamedirUI;
import com.aof.mcinabox.launcher.uis.GamelistUI;
import com.aof.mcinabox.launcher.uis.InstallVersionUI;
import com.aof.mcinabox.launcher.uis.LauncherSettingUI;
import com.aof.mcinabox.launcher.uis.MainToolbarUI;
import com.aof.mcinabox.launcher.uis.PluginUI;
import com.aof.mcinabox.launcher.uis.StartGameUI;
import com.aof.mcinabox.launcher.uis.UserUI;

public class UiManager {

    public PluginUI uiPlugin;
    public InstallVersionUI uiInstallVersion;
    public GamedirUI uiGamedir;
    public GamelistUI uiGamelist;
    public GameSettingUI uiGameSetting;
    public LauncherSettingUI uiLauncherSetting;
    public StartGameUI uiStartGame;
    public UserUI uiUser;
    public MainToolbarUI uiMainToolbar;
    public FunctionbarUI uiFunctionbar;

    public BaseUI[] Uis;

    private Context mContext;

    public UiManager(Context context, SettingJson setting) {
        this.mContext = context;
        uiInstallVersion = new InstallVersionUI(context);
        uiPlugin = new PluginUI(context);
        uiGamedir = new GamedirUI(context);
        uiGamelist = new GamelistUI(context);
        uiGameSetting = new GameSettingUI(context);
        uiLauncherSetting = new LauncherSettingUI(context);
        uiStartGame = new StartGameUI(context);
        uiUser = new UserUI(context);
        uiMainToolbar = new MainToolbarUI(context);
        uiFunctionbar = new FunctionbarUI(context);

        Uis = new BaseUI[]{uiMainToolbar, uiFunctionbar, uiInstallVersion, uiPlugin, uiGamedir, uiGamelist, uiGameSetting, uiLauncherSetting, uiStartGame, uiUser};
    }

    public void switchUIs(BaseUI ui, String position) {
        if (ui.getUIVisiability() != View.VISIBLE) {
            hideAllUIs();
            ui.setUIVisiability(View.VISIBLE);
        }

        currentUI = ui;
        uiMainToolbar.setCurrentPosition(position);
    }

    private BaseUI currentUI;

    public void backFromHere() {
        if (currentUI == uiStartGame || currentUI == null) {
            MainActivity.CURRENT_ACTIVITY.finish();
        }

        if (currentUI == uiGamedir ||
                currentUI == uiGamelist ||
                currentUI == uiLauncherSetting ||
                currentUI == uiUser ||
                currentUI == uiPlugin) {
            switchUIs(uiStartGame, mContext.getString(R.string.title_home));
        }

        if (currentUI == uiGameSetting ||
                currentUI == uiInstallVersion) {
            switchUIs(uiGamelist, mContext.getString(R.string.title_game_list));
        }
    }

    /**
     * 【隐藏全部界面】
     * Hide all UIs.
     **/
    public void hideAllUIs() {
        for (BaseUI ui : Uis) {
            if (ui.getUIVisiability() != View.INVISIBLE) {
                ui.setUIVisiability(View.INVISIBLE);
            }
        }
    }

    public void refreshUis() {
        for (BaseUI ui : Uis) {
            if (ui != null) {
                ui.refreshUI();
            }
        }
    }

    public void onStop() {
        for (BaseUI ui : Uis) {
            if (ui != null) {
                ui.onStop();
            }
        }
    }

    public void onCreate() {
        for (BaseUI ui : Uis) {
            if (ui != null) {
                ui.onCreate();
            }
        }
    }

    public void onRestart() {
        for (BaseUI ui : Uis) {
            if (ui != null) {
                ui.onRestart();
            }
        }
    }

    public void saveConfigToSetting() {
        for (BaseUI ui : Uis) {
            if (ui != null) {
                ui.saveUIConfig();
            }
        }
    }

}
