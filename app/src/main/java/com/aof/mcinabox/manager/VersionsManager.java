package com.aof.mcinabox.manager;

import android.util.Log;

import com.aof.mcinabox.MCinaBox;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class VersionsManager {
    private static final String TAG = "VersionsManager";

    private static final String VERSIONS_FILENAME = "versions.json";

    private boolean isValid() {
        return true;
    }

    public static VersionsManager fromFile(MCinaBox mCinaBox) {
        final File accountsFile = mCinaBox.getFileHelper().getManager(VERSIONS_FILENAME);
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(VersionsManager.class, (JsonDeserializer<VersionsManager>)
                        (json, typeOfT, context) -> new VersionsManager()).create();

        VersionsManager versionsManager = null;
        try (Reader reader = new FileReader(accountsFile)) {
            versionsManager = gson.fromJson(reader, VersionsManager.class);
        } catch (Exception e) {
            Log.d(TAG, "fromFile: Failed to read file!");
        }

        if (versionsManager == null || !versionsManager.isValid()) {
            versionsManager = new VersionsManager();

            try (Writer writer = new FileWriter(accountsFile)) {
                gson.toJson(versionsManager, writer);
            } catch (IOException e) {
                Log.d(TAG, "fromFile: Failed to write file!");
            }
        }

        return versionsManager;
    }
}
