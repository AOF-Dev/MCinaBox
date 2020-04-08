package com.aof.mcinabox.utils.downloader;

import android.util.Log;

import androidx.annotation.Nullable;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;


public class DownloadHelper {

    public static BaseDownloadTask createDownloadTask(String filepath, String url, @Nullable Integer tag){
        Log.e("DownloadHelper","Url: "+url+" Filepath: "+ filepath);
        if(tag == null){
            //filepath是下载文件的绝对路径而不是目录
            return FileDownloader.getImpl().create(url).setPath(filepath);
        }else{
            return FileDownloader.getImpl().create(url).setPath(filepath).setTag(tag);
        }
    }

    public static BaseDownloadTask createDownloadTask(String filename,String dirpath,String url,@Nullable Integer tag){
        return createDownloadTask(dirpath + "/" + filename ,url ,tag);
    }

    public static void cancleAllDownloadTask(){
        FileDownloader.getImpl().pauseAll();
    }

}
