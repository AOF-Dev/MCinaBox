package com.aof.mcinabox.Utils;

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

    public static String getTotalMemory(Context mContext) {// 获取android当前总内存大小
        ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(mContext, mi.totalMem);// 将获取的内存大小规格化
    }

}
