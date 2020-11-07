package com.aof.mcinabox.launcher.launch.support;

import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.runtime.RuntimeManager;
import com.aof.mcinabox.launcher.runtime.support.RuntimePackInfo;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.tipper.TipperManager;
import com.aof.mcinabox.minecraft.JsonUtils;
import com.aof.mcinabox.minecraft.json.VersionJson;
import com.aof.mcinabox.utils.AppUtils;
import com.aof.mcinabox.utils.FileTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CheckManifest {

    private final static String TAG = "CheckManifest";

    public static boolean checkTipperLow() {
        return OldMainActivity.CURRENT_ACTIVITY.get().mTipperManager.getTipCounts(TipperManager.TIPPER_LEVEL_WARN) + OldMainActivity.CURRENT_ACTIVITY.get().mTipperManager.getTipCounts(TipperManager.TIPPER_LEVEL_ERROR) == 0;
    }

    public static boolean checkTipperHigh() {
        return OldMainActivity.CURRENT_ACTIVITY.get().mTipperManager.getTipCounts(TipperManager.TIPPER_LEVEL_ERROR) == 0;
    }

    public static boolean checkVersionThatSelected(SettingJson settingJson) {
        return !settingJson.getLastVersion().equals("");
    }

    public static boolean checkRuntimePack() {
        return RuntimeManager.getPackInfo() != null;
    }

    public static boolean checkPlatform() {
        RuntimePackInfo info = RuntimeManager.getPackInfo();
        String platform = info.platform;
        String abi = AppUtils.formatCpuAbi(AppUtils.getCpuAbi());
        return abi == null || platform.equals(AppUtils.formatCpuAbi(AppUtils.getCpuAbi()));
    }

    public static boolean checkMinecraftMainFiles(SettingJson settingJson) {
        //先检查是不是api，如果是，则只检查主版本文件是否存在
        String tmpId = Utils.judgeApi(settingJson.getLastVersion());
        if (tmpId != null) {
            for (File file : new File[]{new File(Utils.getJsonAbsPath(tmpId)), new File(Utils.getJarAbsPath(tmpId))}) {
                if (!file.exists()) {
                    return false;
                }
            }
            return true;
        } else {
            return new File(Utils.getJarAbsPath(settingJson.getLastVersion())).exists();
        }
    }

    public static String[] checkMinecraftLibraries(SettingJson settingJson) {
        String[] paths = Utils.getFilteredLibPaths(settingJson.getLastVersion());
        ArrayList<String> result = new ArrayList<>();

        //先检查是不是api，如果是，就合并原版本的检查结果。
        String tmpId = Utils.judgeApi(settingJson.getLastVersion());
        if (tmpId != null) {
            String[] paths2 = Utils.getFilteredLibPaths(tmpId);
            String[] all = new String[paths.length + paths2.length];
            System.arraycopy(paths, 0, all, 0, paths.length);
            System.arraycopy(paths2, 0, all, paths.length, paths2.length);
            paths = all;
        }

        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) {
                result.add(file.getName());
            }
        }
        if (result.size() == 0) {
            return null;
        } else {
            String[] tmp = new String[result.size()];
            for (int a = 0; a < tmp.length; a++) {
                tmp[a] = result.get(a);
            }
            return tmp;
        }
    }

    public static boolean checkMinecraftAssetsIndex(SettingJson settingJson) {
        VersionJson json = JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(settingJson.getLastVersion()));
        if (json.getInheritsFrom() == null)
            return new File(Utils.getAssetsJsonAbsPath(JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(settingJson.getLastVersion())))).exists();
        else
            return new File(Utils.getAssetsJsonAbsPath(JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(settingJson.getLastVersion())).getInheritsFrom())).exists();
    }

    public static String[] checkMinecraftAssetsObjects(SettingJson settingJson) {
        if (!checkMinecraftAssetsIndex(settingJson)) {
            return null;
        }
        String[] paths;
        VersionJson json = JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(settingJson.getLastVersion()));
        if (json.getInheritsFrom() == null)
            paths = Utils.getAssetsPaths(JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(settingJson.getLastVersion())));
        else
            paths = Utils.getAssetsPaths(JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(settingJson.getLastVersion())).getInheritsFrom());
        ArrayList<String> result = new ArrayList<>();
        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) {
                result.add(file.getName());
            }
        }
        if (result.size() == 0) {
            return null;
        } else {
            String[] tmp = new String[result.size()];
            for (int a = 0; a < tmp.length; a++) {
                tmp[a] = result.get(a);
            }
            return tmp;
        }
    }

    public static boolean checkForgeSplash() {
        String f;
        try {
            f = FileTool.readToString(AppManifest.MINECRAFT_HOME + "/config/splash.properties");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return f.contains("enabled=false");
    }

    public static boolean checkMinecraftOptionsMipmap(SettingJson settimg) {
        if (JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(settimg.getLastVersion())).getMinimumLauncherVersion() >= 21) {
            return true;
        } else {
            String f;
            try {
                f = FileTool.readToString(AppManifest.MINECRAFT_HOME + "/options.txt");
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }
            return f.contains("mipmapLevels:0");
        }
    }

    public static boolean checkMinecraftOptionsTouchMode() {
        String f;
        try {
            f = FileTool.readToString(AppManifest.MINECRAFT_HOME + "/options.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
        if (f.contains("touchscreen:true") && f.contains("touchscreen:false")) {
            return true;
        } else {
            return f.contains("touchscreen:false");
        }
    }
}
