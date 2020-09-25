package com.aof.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class AppUtils {

    public static String getAppVersionName(Context context){
        String appVersionName = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return appVersionName;
    }

}
