package com.aof.mcinabox.launcher.theme.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;

import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.utils.FileTool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class ThemeUtils {

    private final static String TAG = "ThemeUtils";

    public static Bitmap getBitmapFromFile(File file) {
        FileInputStream stream;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to read File: " + file.getAbsolutePath());
            return null;
        }

        //根据配置读入图片
        return BitmapFactory.decodeStream(stream);
    }

    public static Bitmap getBitmapFromFile(String path) {
        return getBitmapFromFile(new File(path));
    }

    public static boolean replaceBackGround(Context context, View v, File pic) {
        Bitmap bm = getBitmapFromFile(pic);
        if (bm == null) {
            return false;
        } else {
            v.setBackground(new BitmapDrawable(context.getResources(), bm));
            return true;
        }
    }

    public static boolean replaceBackGround(Context context, View v, String path) {
        return replaceBackGround(context, v, new File(path));
    }

    public static String[] getBackgroundsNames(String suffix) {
        return FileTool.listChildFileFromTargetDirFilterSuffix(suffix, AppManifest.MCINABOX_BACKGROUND);
    }

    public static File getBackFileFromName(String name) {
        return new File(AppManifest.MCINABOX_BACKGROUND + "/" + name);
    }

    public static String randomSelectedBackgroundFileName(String[] list) {
        int max = list.length;
        Random rd = new Random();
        return list[rd.nextInt(max)];
    }


}
