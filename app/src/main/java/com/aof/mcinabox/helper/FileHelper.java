package com.aof.mcinabox.helper;

import android.content.Context;

import java.io.File;

public class FileHelper {

    private final File filesDir;
    private File externalStorage;

    public FileHelper(Context context) {
        filesDir = context.getFilesDir();
        createDirectories();
    }

    private void createDirectories() {
        new File(filesDir, "heads/").mkdirs();
        getGameDirectory().mkdirs();
    }

    public File getManager(String filename) {
        return new File(filesDir, filename);
    }

    public File getHead(String username) {
        return new File(filesDir, "heads/" + username + ".png");
    }

    public File getGameDirectory() {
        return new File(filesDir, ".minecraft");
    }
}
