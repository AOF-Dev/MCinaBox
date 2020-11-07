package com.aof.mcinabox.launcher.theme;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.theme.support.ThemeUtils;

public class ThemeManager {

    private final Context mContext;
    private final static String TAG = "ThemeManager";
    private final static String SUPPORTED_PIC_SUFFIX = "png";

    public ThemeManager(Context context) {
        this.mContext = context;
    }

    public boolean autoSetBackground(View v) {
        try {
            String[] result = ThemeUtils.getBackgroundsNames(SUPPORTED_PIC_SUFFIX);
            if (result.length != 0) {
                return ThemeUtils.replaceBackGround(mContext, v, AppManifest.MCINABOX_BACKGROUND + "/" + ThemeUtils.randomSelectedBackgroundFileName(result));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setFullScreen(Activity activity, boolean full) {
        if (full) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

}
