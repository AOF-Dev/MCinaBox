package com.aof.mcinabox.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.format.Formatter;

public class MemoryUtils {

    public static String getAvailMemory(Context mContext) {// 获取android当前可用内存大小
        ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(mContext, mi.availMem);// 将获取的内存大小规格化
    }

    public static String getTotalMemory(Context context) {// 获取android当前总内存大小
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(context, mi.totalMem);// 将获取的内存大小规格化
    }

    public static int getTotalMemoryMB(Context context){
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return (int)(mi.totalMem / 1024 / 1024);
    }

    public static int getDynamicHeapSize(Context mContext){
        ActivityManager manager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        return manager.getMemoryClass();
    }

}
