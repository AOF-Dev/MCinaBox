package com.aof.mcinabox.utils;

import android.util.Log;

import java.io.File;

public class ErrorUtils {

    private static String FileNotFound = "FileNotFound";

    public static void errorOutput(String tag,String info){
        Log.e(tag,info);
    }

    public static void errorOutput(String tag,String info,int level){
        switch (level){
            case 1:
                Log.i(tag,info);
                break;
            case 2:
                Log.d(tag,info);
                break;
            case 3:
                Log.w(tag,info);
                break;
            case 4:
                Log.e(tag,info);
                break;
            default:
                Log.d(tag,info);
        }
    }

    public static void FileNotFound(File file){
        FileNotFound(file.getAbsolutePath());

    }

    public static void FileNotFound(String path){
        errorOutput(FileNotFound,path,4);
    }

}
