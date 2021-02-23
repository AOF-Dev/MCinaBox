package com.aof.mcinabox.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;

import com.aof.mcinabox.activity.MainActivity;
import com.aof.mcinabox.activity.OldMainActivity;

import java.lang.reflect.Method;

public class DisplayUtils {

    public static int getPxFromDp(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    public static float getDpFromPx(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue / scale);
    }

    public static int getPxFromSp(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public static boolean checkDeviceHasNavigationBar(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ViewGroup vp = (ViewGroup) OldMainActivity.CURRENT_ACTIVITY.get().getWindow().getDecorView();
            if (vp != null) {
                for (int i = 0; i < vp.getChildCount(); i++) {
                    vp.getChildAt(i).getContext().getPackageName();

                    if (vp.getChildAt(i).getId() != -1 && "navigationBarBackground".equals(OldMainActivity.CURRENT_ACTIVITY.get().getResources().getResourceEntryName(vp.getChildAt(i).getId()))) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            boolean hasNavigationBar = false;
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }
            try {
                Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
                Method m = systemPropertiesClass.getMethod("get", String.class);
                String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
                if ("1".equals(navBarOverride)) {
                    hasNavigationBar = false;
                } else if ("0".equals(navBarOverride)) {
                    hasNavigationBar = true;
                }
            } catch (Exception e) {
                return false;
            }
            return hasNavigationBar;
        }
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static int[] getApplicationWindowSize(Context context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        return new int[]{screenWidth, screenHeight};
    }

    public static int[] getDisplayWindowSize(Context context){
        return new int[]{DisplayUtils.checkDeviceHasNavigationBar(context) ? DisplayUtils.getApplicationWindowSize(context)[0] + DisplayUtils.getNavigationBarHeight(context) : context.getResources().getDisplayMetrics().widthPixels, DisplayUtils.getApplicationWindowSize(context)[1]};
    }

}
