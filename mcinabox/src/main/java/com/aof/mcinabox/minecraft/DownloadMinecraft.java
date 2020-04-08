package com.aof.mcinabox.minecraft;

import android.util.Log;


import com.aof.mcinabox.launcher.UrlSource;
import com.aof.mcinabox.minecraft.json.AssetsJson;
import com.aof.mcinabox.minecraft.json.VersionJson;
import com.aof.mcinabox.minecraft.json.VersionManifestJson;
import com.aof.mcinabox.utils.downloader.DownloadHelper;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import static com.aof.sharedmodule.Data.DataPathManifest.*;

public class DownloadMinecraft {
    private String MINECRAFT_HOME;
    private String MINECRAFT_HOME_VERSION;
    private String MINECRAFT_HOME_ASSETS;
    private String MINECRAFT_HOME_LIBRARIES;
    private String MINECRAFT_TEMP = MCINABOX_TEMP;
    private UrlSource urlSource;
    private String sourceName;

    public DownloadMinecraft(){
        sourceName = getSourceName();
        MINECRAFT_HOME = getMinecraftHomePath();
        MINECRAFT_HOME_VERSION = MINECRAFT_HOME + "/versions";
        MINECRAFT_HOME_ASSETS = MINECRAFT_HOME + "/assets";
        MINECRAFT_HOME_LIBRARIES = MINECRAFT_HOME + "/libraries";
        urlSource = new UrlSource();
    }

    private String getMinecraftHomePath(){
        switch(com.aof.mcinabox.launcher.JsonUtils.getSettingFromFile(MCINABOX_FILE_JSON).getLocalization()){
            case "private":
                return MINECRAFT_DATA_PRIVATE;
            case "public":
                return MINECRAFT_DATA_PUBLIC;
            default:
                Log.e("DownloadMinecraft","Can't get minecraft home path.");
                return null;
        }
    }

    private String getDownloadUrlFromSource(String url,String type){
        return urlSource.getFileUrl(url,sourceName,type);
    }

    private String getSourceName(){
        return com.aof.mcinabox.launcher.JsonUtils.getSettingFromFile(MCINABOX_FILE_JSON).getDownloadType();
    }

    /**【创建version_manifest.json下载任务】**/
    public BaseDownloadTask createVersionManifestDownloadTask(){
        RefreshSourceName();
        Log.e("Downloader","文件路径:"+this.MINECRAFT_TEMP+" 文件url:" +urlSource.getFileUrl(urlSource.getSourceUrl("official","version_manifest_json"),sourceName,"version_manifest_json"));
       return DownloadHelper.createDownloadTask("version_manifest.json",this.MINECRAFT_TEMP,urlSource.getFileUrl(urlSource.getSourceUrl("official","version_manifest_json"),sourceName,"version_manifest_json"),1);
    }

    /**【创建version.json下载任务】**/
    public BaseDownloadTask createVersionJsonDownloadTask(String id){
        RefreshSourceName();
        VersionManifestJson.Version[] versions = com.aof.mcinabox.minecraft.JsonUtils.getVersionManifestFromFile(this.MINECRAFT_TEMP + "/version_manifest.json").getVersions();
        if(versions == null){
            Log.e("DownloadMinecraft","Not found version_manifest from json.");
            return null;
        }
        for(VersionManifestJson.Version version: versions){
            if(version.getId().equals(id)){
                return DownloadHelper.createDownloadTask(id + ".json",MINECRAFT_HOME_VERSION + "/" + id,getDownloadUrlFromSource(version.getUrl(),"version_json"),null);
            }
        }
        Log.e("DownloadMinecraft","Not found version " + id + " in manifest.");
        return null;
    }

    /**【创建version.jar下载任务】**/
    public BaseDownloadTask createVersionJarDownloadTask(String id){
        RefreshSourceName();
        VersionJson version = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(MINECRAFT_HOME_VERSION + "/" + id + "/" + id + ".json");
        if(version == null){
            Log.e("DownloadMinecraft","Not found Version " + id + ".json");
        }
        return DownloadHelper.createDownloadTask(id + ".jar", MINECRAFT_HOME_VERSION + "/" + id, getDownloadUrlFromSource(version.getDownloads().getClient().getUrl(),"version_jar"),null);
    }

    /**【创建libraries下载任务】**/
    public ArrayList<BaseDownloadTask> createLibrariesDownloadTask(String id){
        RefreshSourceName();
        ArrayList<BaseDownloadTask> tasks = new ArrayList<BaseDownloadTask>();
        VersionJson version = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(MINECRAFT_HOME_VERSION + "/" + id + "/" + id + ".json");
        if(version == null){
            Log.e("DownloadMinecraft","Not found Version " + id + ".json");
            return null;
        }
        for(VersionJson.DependentLibrary library : version.getLibraries()){
            if(library.getDownloads().getArtifact() != null){
                tasks.add(DownloadHelper.createDownloadTask(getLibraryJarName(library.getName()),getLibraryJarPath(library.getName()),getDownloadUrlFromSource(library.getDownloads().getArtifact().getUrl(),"libraries"),null));
            }
        }
        return tasks;
    }

    /**【创建assetindex.json下载任务】**/
    public BaseDownloadTask createAssetIndexDownloadTask(String id){
        RefreshSourceName();
        VersionJson version = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(MINECRAFT_HOME_VERSION + "/" + id + "/" + id + ".json");
        if(version == null){
            Log.e("DownloadMinecraft","Not found Version " + id + ".json");
            return null;
        }
        return DownloadHelper.createDownloadTask(version.getAssets() + ".json",MINECRAFT_HOME_ASSETS + "/indexes",getDownloadUrlFromSource(version.getAssetIndex().getUrl(),"assetsIndex_json"),null);
    }

    /**【创建assets下载任务】**/
    public ArrayList<BaseDownloadTask> createAssetObjectsDownloadTask(String id){
        RefreshSourceName();
        ArrayList<BaseDownloadTask> tasks = new ArrayList<BaseDownloadTask>();
        VersionJson version = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(MINECRAFT_HOME_VERSION + "/" + id + "/" + id + ".json");
        if(version == null){
            Log.e("DownloadMinecraft","Not found Version " + id + ".json");
            return null;
        }
        AssetsJson assets = com.aof.mcinabox.minecraft.JsonUtils.getAssetsFromFile(MINECRAFT_HOME_ASSETS + "/indexes/" + version.getAssets() + ".json");
        if(assets == null){
            Log.e("DownloadMinecraft","Not found AssetIndex " + version.getAssets() + ".json");
            return null;
        }

        Set<String> keySets = assets.getObjects().keySet();
        //利用了Iterator迭代器
        Iterator<String> it = keySets.iterator();
        while (it.hasNext()) {
            //得到每一个key
            String key = it.next();
            //通过key获取对应的value
            String hashCode = assets.getObjects().get(key).hash;
            tasks.add(DownloadHelper.createDownloadTask(hashCode,getAssetsObjectPath(hashCode),getAssetsObjectUrl(hashCode),null));
        }
        return tasks;
    }

    private String getLibraryJarPath(String name){
        String packageName;
        String libraryName;
        String versionName;
        String filePath;

        String[] Name = name.split(":");
        packageName = Name[0];
        libraryName = Name[1];
        versionName = Name[2];

        String dirPath = MINECRAFT_HOME_LIBRARIES + "/";
        for(int i =0;i < packageName.length();i++){
            if(packageName.charAt(i) == '.'){
                dirPath = dirPath + "/";
            }else{
                dirPath = dirPath + packageName.charAt(i);
            }
        }
        dirPath = dirPath + "/" + libraryName + "/" + versionName;
        return dirPath;
    }

    private String getLibraryJarName(String name){
        String libraryName;
        String versionName;

        String[] Name = name.split(":");
        libraryName = Name[1];
        versionName = Name[2];

        return libraryName + "-" + versionName + ".jar";
    }

    private String getAssetsObjectUrl(String hashCode){
        String tip = "";
        for(int i=0;i<2;i++){
            tip = tip + hashCode.charAt(i);
        }
        String fileUrl = urlSource.getSourceUrl(sourceName,"assets")  + "/" + tip + "/" + hashCode;
        return fileUrl;
    }

    private String getAssetsObjectPath(String hashCode){
        String tip = "";
        for(int i=0;i<2;i++){
            tip = tip + hashCode.charAt(i);
        }
        String filePath = MINECRAFT_HOME_ASSETS + "/objects/" + tip;
        return filePath;
    }
    public void RefreshSourceName(){
        sourceName = getSourceName();
    }
}
