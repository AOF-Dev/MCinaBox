package com.aof.mcinabox.gamecontroller.definitions.manifest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.util.Objects;

public class AppManifest {

    private final static String TAG = "AppManifest";

    /**
     * 【全局目录清单】
     **/
    public static String APP_NAME;
    public static String DATA_HOME;
    public static String SDCARD_HOME;
    public static String MCINABOX_HOME;
    public static String MCINABOX_TEMP;
    public static String MCINABOX_KEYBOARD;
    public static String MCINABOX_SETTING_JSON;
    public static String MCINABOX_VERSION_NAME;
    public static String MCINABOX_RUNTIME;
    public static String MCINABOX_BACKGROUND;
    public static String BOAT_CACHE_HOME;
    public static String RUNTIME_HOME;
    public static String BOAT_RUNTIME_HOME;
    public static String BOAT_RUNTIME_INFO_JSON;
    public static String FORGE_HOME;
    public static String AUTHLIB_HOME;
    public static String AUTHLIB_INJETOR_JAR;

    public static String MINECRAFT_HOME;
    public static String MINECRAFT_VERSIONS;
    public static String MINECRAFT_ASSETS;
    public static String MINECRAFT_MODS;
    public static String MINECRAFT_SAVES;
    public static String MINECRAFT_LIBRARIES;
    public static String MINECRAFT_LOGS;
    public static String MINECRAFT_RESOURCEPACKS;

    //文件过滤清单
    public final static String[] BOAT_RUNTIME_FILTERED_LIBRARIES = {
            "lwjgl", "lwjgl_util", "lwjgl-platform", "lwjgl-egl", "lwjgl-glfw",
            "lwjgl-jemalloc", "lwjgl-openal", "lwjgl-opengl",
            "lwjgl-opengles", "lwjgl-stb", "lwjgl-tinyfd", "jinput-platform",
            "twitch-platform", "twitch-external-platform"};

    public static void initManifest(Context context, String mchome) {
        APP_NAME = "MCinaBox";
        DATA_HOME = context.getFilesDir().getAbsolutePath();
        SDCARD_HOME = Environment.getExternalStorageDirectory().getAbsolutePath();
        MCINABOX_HOME = Objects.requireNonNull(context.getExternalFilesDir("mcinabox")).getAbsolutePath();
        MCINABOX_TEMP = MCINABOX_HOME + "/temp";
        MCINABOX_KEYBOARD = MCINABOX_HOME + "/keyboards";
        MCINABOX_SETTING_JSON = MCINABOX_HOME + "/mcinabox.json";
        MCINABOX_RUNTIME = MCINABOX_HOME + "/runtime";
        MCINABOX_BACKGROUND = MCINABOX_HOME + "/backgrounds";
        try {
            MCINABOX_VERSION_NAME = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            MCINABOX_VERSION_NAME = "Unknown";
        }
        BOAT_CACHE_HOME = MCINABOX_HOME + "/boatapp";
        RUNTIME_HOME = DATA_HOME + "/runtime";
        BOAT_RUNTIME_HOME = RUNTIME_HOME + "/boat";
        BOAT_RUNTIME_INFO_JSON = BOAT_RUNTIME_HOME + "/packinfo.json";
        FORGE_HOME = MCINABOX_HOME + "/forge";
        AUTHLIB_HOME = MCINABOX_HOME + "/authlib-injector";
        AUTHLIB_INJETOR_JAR = AUTHLIB_HOME + "/authlib-injector.jar";

        MINECRAFT_HOME = mchome;
        MINECRAFT_ASSETS = MINECRAFT_HOME + "/assets";
        MINECRAFT_LIBRARIES = MINECRAFT_HOME + "/libraries";
        MINECRAFT_LOGS = MINECRAFT_HOME + "/crach-reports";
        MINECRAFT_MODS = MINECRAFT_HOME + "/mods";
        MINECRAFT_RESOURCEPACKS = MINECRAFT_HOME + "/resourcepacks";
        MINECRAFT_SAVES = MINECRAFT_HOME + "/saves";
        MINECRAFT_VERSIONS = MINECRAFT_HOME + "/versions";
    }


    /**
     * 【一个全局目录的数组】
     **/
    public static String[] getAllPath() {
        return new String[]{MCINABOX_RUNTIME, MCINABOX_HOME, MCINABOX_KEYBOARD, MCINABOX_TEMP, BOAT_CACHE_HOME, RUNTIME_HOME, MCINABOX_BACKGROUND, AUTHLIB_HOME};
    }
}
