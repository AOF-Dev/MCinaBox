package com.aof.mcinabox.manager;

import android.util.Log;

import com.aof.mcinabox.MCinaBox;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.FileReader;
import java.io.Reader;

public class SettingsManager {
    private static final String TAG = "SettingsManager";

    private static final String SETTINGS_FILENAME = "settings.json";

    private SettingsManager() {

    }

    private boolean isValid() {
        return true;
    }

    public static SettingsManager fromFile(MCinaBox mCinaBox) {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(SettingsManager.class, (JsonDeserializer<SettingsManager>)
                        (json, typeOfT, context) -> new SettingsManager()).create();

        SettingsManager settingsManager = null;
        try (Reader reader = new FileReader(mCinaBox.getFileHelper().getManager(SETTINGS_FILENAME))) {
            settingsManager = gson.fromJson(reader, SettingsManager.class);
        } catch (Exception e) {
            Log.d(TAG, "fromFile: Failed to read file!");
        }

        if (settingsManager == null || !settingsManager.isValid()) {
            settingsManager = new SettingsManager();
        }

        return settingsManager;
    }
}
