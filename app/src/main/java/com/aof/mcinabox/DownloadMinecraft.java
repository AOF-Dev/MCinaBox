package com.aof.mcinabox;

import android.content.Context;
import android.widget.Toast;

import com.aof.mcinabox.downloadUtils.Downloader;

import java.io.File;

public class DownloadMinecraft {
    //下列路径定义为绝对路径
    private String MINECRAFT_URL; //Minecraft源地址
    private String MINECRAFT_DIR; //Minecraft本地路径
    private String MINECRAFT_TEMP; //Minecraft临时目录-用于保存其他文件

    //下列路径定义为缺省/sdcard的路径
    private String DOWNLOAD_DIR; //Minecraft下载保存路径
    private String DOWNLOAD_TEMP; //Minecraft下载临时保存路径

    public void setInformation(String a,String b){
        //设置下载器参数
        MINECRAFT_URL = a;
        DOWNLOAD_DIR = b;
        DOWNLOAD_TEMP = DOWNLOAD_DIR + "Temp/";
        MINECRAFT_DIR = "/sdcard" + DOWNLOAD_DIR;
        MINECRAFT_TEMP = MINECRAFT_DIR + "Temp/";
    }

    //下载或更新Minecraft所有的版本信息
    public void UpdateVersionJson(Context context){
        String fileUrl = MINECRAFT_URL + "/mc/game/version_manifest.json";
        String fileName = "version_manifest.json";
        String filePath = MINECRAFT_TEMP + fileName;

        //先判断文件是否存在
        //若存在则删掉再下载
        File file=new File(filePath);
        if(file.exists())
        {
            file.delete();
        }
        //执行下载操作
        Downloader downloader = new Downloader();
        downloader.FileDownloader(context,DOWNLOAD_TEMP,fileName,fileUrl);
        Toast.makeText(context,"版本信息更新完成",Toast.LENGTH_SHORT).show();
    }
}
