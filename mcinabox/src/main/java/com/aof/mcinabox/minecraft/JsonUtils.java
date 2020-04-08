package com.aof.mcinabox.minecraft;

import android.util.Log;

import com.aof.mcinabox.minecraft.json.AssetsJson;
import com.aof.mcinabox.minecraft.json.VersionJson;
import com.aof.mcinabox.minecraft.json.VersionManifestJson;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class JsonUtils {
    /**【读入version_manifest.json】**/
    public static VersionManifestJson getVersionManifestFromFile(File file){
        if(file == null){
            Log.e("JsonUtils","Json File is null.");
            return null;
        }
        try {
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            //使用Gson将ListVersionManifestJson实例化
            return gson.fromJson(reader, VersionManifestJson.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("JsonUtils","Json File not found.");
            return null;
        }
    }
    public static VersionManifestJson getVersionManifestFromFile(String filepath){
        return getVersionManifestFromFile(new File(filepath));
    }

    /**【读入version.json】**/
    public static VersionJson getVersionFromFile(File file){
        if(file == null){
            Log.e("JsonUtils","Json File is null.");
            return null;
        }
        try {
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            //使用Gson将ListVersionManifestJson实例化
            return gson.fromJson(reader, VersionJson.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("JsonUtils","Json File not found.");
            return null;
        }
    }
    public static VersionJson getVersionFromFile(String filepath){
        return getVersionFromFile(new File(filepath));
    }

    /**【读入assets.json】**/
    public static AssetsJson getAssetsFromFile(File file){
        if(file == null){
            Log.e("JsonUtils","Json File is null.");
            return null;
        }
        try {
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            //使用Gson将ListVersionManifestJson实例化
            return gson.fromJson(reader, AssetsJson.class);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("JsonUtils","Json File not found.");
            return null;
        }
    }
    public static AssetsJson getAssetsFromFile(String filepath){
        return getAssetsFromFile(new File(filepath));
    }
}
