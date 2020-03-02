package com.aof.mcinabox.ioUtils;

import android.util.Log;

public class PathTool {
    public PathTool(String homePath){
        super();
        MCinaBox_Home = homePath;
        MINECRAFT_URL = "";
        MINECRAFT_ASSETS_URL ="";
        MINECRAFT_DIR = MCinaBox_Home + "/.minecraft/";
        MINECRAFT_TEMP = MINECRAFT_DIR + "Temp/";
        MINECRAFT_VERSION_DIR = MINECRAFT_DIR + "versions/";
        MINECRAFT_LIBRARIES_DIR = MINECRAFT_DIR + "libraries/";
        MINECRAFT_ASSETS_DIR = MINECRAFT_DIR+"assets/";
        VERSION_MANIFEST_URL = MINECRAFT_URL + "/mc/game/version_manifest.json";
    }
    //下列路径定义为绝对路径
    String MCinaBox_Home; //
    String MINECRAFT_URL; //Minecraft 的 版本清单 version.json assetsIndex.json 下载地址
    String MINECRAFT_ASSETS_URL; //Minecraft 的 assets资源 下载地址
    String MINECRAFT_DIR; //Minecraft本地路径
    String MINECRAFT_TEMP; //Minecraft临时目录-用于保存其他文件
    String MINECRAFT_VERSION_DIR; //Minecraft的version文件夹路径
    String MINECRAFT_LIBRARIES_DIR; //Minecraft的libraries文件夹路径
    String MINECRAFT_ASSETS_DIR; //Minecraft的assets文件夹路径
    //部分文件
    String VERSION_MANIFEST_URL; //version_manifest.json文件下载地址

    public String getMINECRAFT_ASSETS_URL() { return MINECRAFT_ASSETS_URL; }
    public void setMINECRAFT_ASSETS_URL(String MINECRAFT_ASSETS_URL) { this.MINECRAFT_ASSETS_URL = MINECRAFT_ASSETS_URL; }
    public void setMINECRAFT_LIBRARIES_DIR(String MINECRAFT_LIBRARIES_DIR) { this.MINECRAFT_LIBRARIES_DIR = MINECRAFT_LIBRARIES_DIR; }
    public String getMINECRAFT_ASSETS_DIR() { return MINECRAFT_ASSETS_DIR;}
    public void setMINECRAFT_ASSETS_DIR(String MINECRAFT_ASSETS_DIR) { this.MINECRAFT_ASSETS_DIR = MINECRAFT_ASSETS_DIR; }
    public String getMINECRAFT_TEMP(){ return MINECRAFT_TEMP; }
    public void setMINECRAFT_TEMP(String MINECRAFT_TEMP){this.MINECRAFT_TEMP = MINECRAFT_TEMP;}
    public String getMINECRAFT_URL() { return MINECRAFT_URL; }
    public void setMINECRAFT_URL(String MINECRAFT_URL) { this.MINECRAFT_URL = MINECRAFT_URL; }
    public String getVERSION_MANIFEST_URL() { return VERSION_MANIFEST_URL; }
    public void setVERSION_MANIFEST_URL(String VERSION_MANIFEST_URL) { this.VERSION_MANIFEST_URL = VERSION_MANIFEST_URL; }
    public String getMINECRAFT_DIR() { return MINECRAFT_DIR; }
    public void setMINECRAFT_DIR(String MINECRAFT_DIR) { this.MINECRAFT_DIR = MINECRAFT_DIR; }
    public String getMINECRAFT_VERSION_DIR() { return MINECRAFT_VERSION_DIR; }
    public void setMINECRAFT_VERSION_DIR(String MINECRAFT_VERSION_DIR) { this.MINECRAFT_VERSION_DIR = MINECRAFT_VERSION_DIR; }
    public String getMINECRAFT_LIBRARIES_DIR() {return MINECRAFT_LIBRARIES_DIR;}
}
