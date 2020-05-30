package com.aof.mcinabox.launcher.core;

public class Utils {
    public static String getJsonAbsPath(String versionHome,String id){
        return (versionHome + id + "/" + id + ".json");
    }
    public static String getJarAbsPath(String versionHome,String id){
        return (versionHome + id + "/" + id + ".jar");
    }
    public static String getAssetsJsonAbsPath(String assetsHome,String id){
        //TODO: 返回资源清单文件路径
        return null;
    }
}
