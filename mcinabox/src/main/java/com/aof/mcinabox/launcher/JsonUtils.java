package com.aof.mcinabox.launcher;

import android.content.Context;
import android.util.Log;

import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.KeyboardJson;
import com.aof.mcinabox.launcher.json.RuntimeJson;
import com.aof.mcinabox.launcher.json.SettingJson;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static com.aof.sharedmodule.Data.DataPathManifest.RUNTIME_HOME;

public class JsonUtils {
    /**【读入pack.json】**/
    public static RuntimeJson getPackFromFile(File file){
        if(file == null){
            Log.e("JsonUtils","Json File is null.");
            return null;
        }
        try {
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            //使用Gson将ListVersionManifestJson实例化
            return gson.fromJson(reader, RuntimeJson.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("JsonUtils","Json File not found.");
            return null;
        }
    }
    public static RuntimeJson getPackFromFile(String filepath){
        return getPackFromFile(new File(filepath));
    }
    public static String getPackInformation(Context context){
        RuntimeJson info = getPackFromFile(RUNTIME_HOME + "/pack.json");
        if(info == null){
            return "";
        }else{
            return ( context.getString(R.string.title_runtime_package_name) + " " + info.getPackName() + "\n" +
                    context.getString(R.string.title_runtime_package_releasetime) + " " + info.getReleaseTime() + "\n" +
                    context.getString(R.string.title_runtime_package_platform) + " " + info.getPlatform() + "\n" +
                    context.getString(R.string.title_runtime_java_version) + " " + info.getJavaVersion() + "\n" +
                    context.getString(R.string.title_runtime_opengl_version) + " " + info.getOpenGLVersion() + "\n" +
                    context.getString(R.string.title_runtime_openal_version) + " " + info.getOpenALVersion() + "\n" +
                    context.getString(R.string.title_runtime_lwjgl_version) + " " + info.getLwjgl2Version() + " " +  info.getLwjgl3Version() + "\n"
            );
        }
    }

    /**【读入mcinabox.json】**/
    public static SettingJson getSettingFromFile(File file){
        if(file == null){
            Log.e("JsonUtils","Json File is null.");
            return null;
        }
        try {
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            //使用Gson将ListVersionManifestJson实例化
            return gson.fromJson(reader, SettingJson.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("JsonUtils","Json File not found.");
            return null;
        }
    }
    public static SettingJson getSettingFromFile(String filepath){
        return getSettingFromFile(new File(filepath));
    }

    /**【读入keyboard.json】**/
    public static KeyboardJson getKeyboardFromFile(File file){
        if(file == null){
            Log.e("JsonUtils","Json File is null.");
            return null;
        }
        try {
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            //使用Gson将ListVersionManifestJson实例化
            return gson.fromJson(reader, KeyboardJson.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("JsonUtils","Json File not found.");
            return null;
        }
    }
    public static KeyboardJson getKeyboardFromFile(String filepath){
        return getKeyboardFromFile(new File(filepath));
    }


}
