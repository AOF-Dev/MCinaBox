package com.aof.mcinabox;

import com.aof.mcinabox.downloadUtils.Downloader;

public class DownloadMinecraft {
    private String MINECRAFT_URL; //Minecraft源地址
    private String MINECRAFT_DIR; //Minecraft本地路径
    private String MINECRAFT_TEMP; //Minecraft临时目录

    public void setInformation(String a,String b){
        //设置下载器参数
        MINECRAFT_URL = a;
        MINECRAFT_DIR = b;
        MINECRAFT_TEMP = MINECRAFT_DIR + "/Temp";
    }
    public void UpdateVersionJson(){
        String fileUrl = MINECRAFT_URL + "/mc/game/version_manifest.json";
        String fileName = "version_manifest.json";
        Downloader downloader = new Downloader();
        downloader.FileDownloader(MINECRAFT_TEMP,fileName,fileUrl);
    }
}
