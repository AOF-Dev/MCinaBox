package com.aof.mcinabox.jsonUtils;

import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class AnaliesMinecraftAssetJson {
    public ModelMinecraftAssetsJson getModelMinecraftAssetsJson(String filePath) {
        try {
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            ModelMinecraftAssetsJson modelMinecraftAssetsJson = gson.fromJson(reader, ModelMinecraftAssetsJson.class);
            return modelMinecraftAssetsJson;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("getMinecraftAssetsJson ",e.toString());
        }
        return null;
    }

}
