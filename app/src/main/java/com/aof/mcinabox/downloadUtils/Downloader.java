package com.aof.mcinabox.downloadUtils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

public class Downloader {
    private Context mContext;
    private long taskId;
    private boolean isSucceed = true;

    private void setTaskId(long id){
        taskId = id;
    }

    public long getTaskId(){
        return taskId;
    }

    //context：当前活动的Activity fileDir：文件保存路径 fileName：文件名 fileUrl：文件下载路径
    public void FileDownloader(Context context,String fileDir,String fileName,String fileUrl){
        //这里使用安卓系统的DownloadManager下载器

        mContext = context;

        //创建下载管理器
        DownloadManager downloadManager= (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
        //设置下载路径和文件名
        request.setDestinationInExternalPublicDir(fileDir,fileName);
        //尝试将任务加入下载队列并返回任务Id
        //如果失败则将对象状态设置为失败
        setTaskId(downloadManager.enqueue(request));
    }
}
