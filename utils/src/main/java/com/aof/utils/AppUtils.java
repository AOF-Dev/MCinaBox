package com.aof.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AppUtils {

    public static String getAppVersionName(Context context) {
        String appVersionName = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersionName;
    }

    public static String getCpuAbi() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Runtime.getRuntime().exec("getprop ro.product.cpu.abi").getInputStream()))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatCpuAbi(String abi) {
        if (abi == null) {
            return null;
        }
        switch (abi) {
            case "armeabi-v7a":
            case "armeabi":
                return "aarch32";
            case "arm64-v8a":
                return "aarch64";
            case "x86":
                return "x86";
            case "x86_64":
                return "x86_64";
            default:
                return abi;
        }
    }
}
