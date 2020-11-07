package com.aof.mcinabox.launcher.gamedir;

import android.content.Context;

import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.utils.FileTool;

import java.io.File;

public class GamedirManager {

    private final static String TAG = "GamedirManager";

    public final static String PUBLIC_GAMEDIR = SettingJson.DEFAULT_GAMEDIR;
    public final static String PRIVATE_GAMEDIR = AppManifest.MCINABOX_HOME + "/gamedir";

    public static boolean setGamedir(Context context, SettingJson setting, String gamedir) {
        if (gamedir != null) {
            File file = new File(gamedir);
            if (file.exists() && !file.isDirectory()) {
                return false;
            } else if (!file.exists()) {
                if (!FileTool.makeFloder(file)) {
                    return false;
                }
            }
            setting.setGameDir(gamedir);
            AppManifest.initManifest(context, gamedir);
            return true;
        } else {
            return false;
        }
    }

    public static String getGamedir(SettingJson setting) {
        if (setting.getGamedir() != null) {
            return setting.getGamedir();
        } else {
            return SettingJson.DEFAULT_GAMEDIR;
        }
    }

}
