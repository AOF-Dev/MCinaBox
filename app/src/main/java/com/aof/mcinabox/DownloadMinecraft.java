package com.aof.mcinabox;

import android.content.Context;
import android.widget.Toast;

import com.aof.mcinabox.downloadUtils.Downloader;

import java.io.File;

public class DownloadMinecraft {

    //无参数的的构造函数
    public DownloadMinecraft() {
        super();
        MINECRAFT_URL = "https://launchermeta.mojang.com";
        DOWNLOAD_DIR = "/MCinaBox/.minecraft/";
        DOWNLOAD_TEMP = DOWNLOAD_DIR + "Temp/";
        DOWNLOAD_VERSION_DIR = DOWNLOAD_DIR + "versions/";
        MINECRAFT_DIR = "/sdcard" + DOWNLOAD_DIR;
        MINECRAFT_TEMP = MINECRAFT_DIR + "Temp/";
        MINECRAFT_VERSION_DIR = MINECRAFT_DIR + "versions/";
        VERSION_MANIFEST_URL = MINECRAFT_URL + "/mc/game/version_manifest.json";
    }

    //下列路径定义为绝对路径
    private String MINECRAFT_URL; //Minecraft源地址
    private String MINECRAFT_DIR; //Minecraft本地路径
    private String MINECRAFT_TEMP; //Minecraft临时目录-用于保存其他文件
    private String MINECRAFT_VERSION_DIR; //Minecraft的version文件夹路径

    //下列路径定义为缺省/sdcard的路径
    private String DOWNLOAD_DIR; //Minecraft下载保存路径
    private String DOWNLOAD_TEMP; //Minecraft下载临时保存路径
    private String DOWNLOAD_VERSION_DIR; //Minecraft版本保存路径

    //部分文件
    private String VERSION_MANIFEST_URL; //version_manifest.json文件下载地址

    //Getter and Setter
    public String getMINECRAFT_TEMP(){ return MINECRAFT_TEMP; }
    public void setMINECRAFT_TEMP(String MINECRAFT_TEMP){this.MINECRAFT_TEMP = MINECRAFT_TEMP;}
    public String getMINECRAFT_URL() { return MINECRAFT_URL; }
    public void setMINECRAFT_URL(String MINECRAFT_URL) { this.MINECRAFT_URL = MINECRAFT_URL; }
    public String getVERSION_MANIFEST_URL() { return VERSION_MANIFEST_URL; }
    public void setVERSION_MANIFEST_URL(String VERSION_MANIFEST_URL) { this.VERSION_MANIFEST_URL = VERSION_MANIFEST_URL; }
    public String getDOWNLOAD_TEMP() { return DOWNLOAD_TEMP; }
    public void setDOWNLOAD_TEMP(String DOWNLOAD_TEMP) { this.DOWNLOAD_TEMP = DOWNLOAD_TEMP; }
    public String getDOWNLOAD_DIR() { return DOWNLOAD_DIR; }
    public void setDOWNLOAD_DIR(String DOWNLOAD_DIR) { this.DOWNLOAD_DIR = DOWNLOAD_DIR; }
    public String getMINECRAFT_DIR() { return MINECRAFT_DIR; }
    public void setMINECRAFT_DIR(String MINECRAFT_DIR) { this.MINECRAFT_DIR = MINECRAFT_DIR; }
    public String getDOWNLOAD_VERSION_DIR() { return DOWNLOAD_VERSION_DIR; }
    public void setDOWNLOAD_VERSION_DIR(String DOWNLOAD_VERSION_DIR) { this.DOWNLOAD_VERSION_DIR = DOWNLOAD_VERSION_DIR; }
    public String getMINECRAFT_VERSION_DIR() { return MINECRAFT_VERSION_DIR; }
    public void setMINECRAFT_VERSION_DIR(String MINECRAFT_VERSION_DIR) { this.MINECRAFT_VERSION_DIR = MINECRAFT_VERSION_DIR; }

    //!!!传入缺省/sdcard的相对路径!!!
    public void setInformation(String a, String b){
        //设置下载器参数
        this.setMINECRAFT_URL(a);
        this.setDOWNLOAD_DIR(b);
        this.setDOWNLOAD_VERSION_DIR(getDOWNLOAD_DIR()+"versions/");
        this.setDOWNLOAD_TEMP(getDOWNLOAD_DIR()+"Temp/");
        this.setMINECRAFT_DIR("/sdcard"+getDOWNLOAD_DIR());
        this.setMINECRAFT_TEMP(getMINECRAFT_DIR()+"Temp/");
        this.setMINECRAFT_VERSION_DIR(getMINECRAFT_DIR()+"versions/");
    }

    //下载或更新Minecraft的版本信息文件version_manifest.json
    public long UpdateVersionManifestJson(Context context){
        String fileUrl = getMINECRAFT_URL() + "/mc/game/version_manifest.json";
        String fileName = "version_manifest.json";
        String savePath = getDOWNLOAD_TEMP();
        String filePath = getMINECRAFT_TEMP()+fileName;
        long taskId;

        //先判断文件是否存在
        //若存在则删掉再下载
        File file=new File(filePath);
        if(file.exists()){
            file.delete();
        }
        //执行下载操作
        Downloader downloader = new Downloader();
        taskId = downloader.FileDownloader(context,savePath,fileName,fileUrl);

        /*
        //线程暂停等待下载任务完成
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

        Toast.makeText(context,"执行版本信息更新",Toast.LENGTH_SHORT).show();
        return taskId;
    }

    public long DownloadMinecraftVersionJson(String id,String url,Context context){
        String fileUrl = url;
        String fileName = id + ".json";
        String savePath = getDOWNLOAD_VERSION_DIR()+id+"/";
        String filePath = getMINECRAFT_VERSION_DIR()+fileName;
        long taskId;

        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
        Downloader downloader = new Downloader();
        taskId = downloader.FileDownloader(context,savePath,fileName,fileUrl);
        return taskId;
    }
}
