package com.aof.mcinabox.helper;

import android.content.Context;

import java.io.File;

public class FileHelper {

    private File filesDir;
    private File externalStorage;

    public FileHelper(Context context) {
        filesDir = context.getFilesDir();
    }

    public File getManager(String filename) {
        return new File(filesDir, filename);
    }
}
