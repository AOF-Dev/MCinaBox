package com.aof.mcinabox.launcher.download.support;

import androidx.annotation.Nullable;

import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;


public class DownloadHelper {

    public static BaseDownloadTask createDownloadTask(String filepath, String url, @Nullable Integer tag) {
        if (tag == null) {
            //filepath是下载文件的绝对路径而不是目录
            return FileDownloader.getImpl().create(url).setPath(filepath).addHeader("User-Agent", AppManifest.APP_NAME + "/" + AppManifest.MCINABOX_VERSION_NAME);
        } else {
            return FileDownloader.getImpl().create(url).setPath(filepath).setTag(tag).addHeader("User-Agent", AppManifest.APP_NAME + "/" + AppManifest.MCINABOX_VERSION_NAME);
        }
    }

    public static BaseDownloadTask createDownloadTask(String filename, String dirpath, String url, @Nullable Integer tag) {
        return createDownloadTask(dirpath + "/" + filename, url, tag);
    }

    public static void cancleAllDownloadTask() {
        FileDownloader.getImpl().pauseAll();
    }

}
