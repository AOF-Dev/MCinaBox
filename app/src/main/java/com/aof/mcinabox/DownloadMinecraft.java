package com.aof.mcinabox;

import android.content.Context;

import com.aof.mcinabox.downloadUtils.Downloader;

import java.io.File;

public class DownloadMinecraft {
    private String MINECRAFT_URL; //Minecraft源地址
    private String MINECRAFT_DIR; //Minecraft本地路径
    private String MINECRAFT_TEMP; //Minecraft临时目录-用于保存其他文件
    private String DOWNLOAD_DIR; //Minecraft下载器保存路径

    public void setInformation(String a,String b){
        //设置下载器参数
        MINECRAFT_URL = a;
        DOWNLOAD_DIR = b;
        MINECRAFT_DIR = "/sdcard" + DOWNLOAD_DIR;
        MINECRAFT_TEMP = DOWNLOAD_DIR + "Temp/";
    }

    //下载或更新Minecraft所有的版本信息
    public void UpdateVersionJson(Context context){
        String fileUrl = MINECRAFT_URL + "/mc/game/version_manifest.json";
        String fileName = "version_manifest.json";
        String filePath = MINECRAFT_DIR + "mc/game/version_manifest.json";

        //先判断文件是否存在
        //若存在则删掉再下载
        File file=new File(filePath);
        if(file.exists())
        {
            file.delete();
        }

        //执行下载操作
        Downloader downloader = new Downloader();
        downloader.FileDownloader(context,MINECRAFT_TEMP,fileName,fileUrl);
    }
}
