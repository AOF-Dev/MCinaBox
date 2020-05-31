package com.aof.mcinabox.minecraft;

import android.content.Context;
import android.util.Log;

import com.aof.mcinabox.minecraft.json.VersionJson;
import com.aof.mcinabox.utils.FileTool;
import com.aof.mcinabox.utils.ZipUtils;

import java.io.File;


import static com.aof.sharedmodule.Data.DataPathManifest.*;

public class ForgeInstaller {

    public ForgeInstaller(Context context){
        this.context = context;
    }
    private Context context;
    private String MINECRAFT_HOME = getMinecraftHomePath();
    private String MINECRAFT_HOME_VERSION = MINECRAFT_HOME + "/versions";

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

    public void unzipForgeInstaller(String filename) throws Exception {
        ZipUtils.UnZipFolder(FORGEINSTALLER_HOME + "/" + filename,MCINABOX_TEMP + "/forge");
    }
    public String makeForgeData(){
        File forgeFile = new File(MCINABOX_TEMP + "/forge/version.json");
        VersionJson forgeJson;
        if(forgeFile.exists()){
            forgeJson = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(forgeFile);
            if(forgeJson != null) {
                FileTool.checkFilePath(new File(MINECRAFT_HOME_VERSION + "/" + forgeJson.getId()),true);
                FileTool.copyFile(MCINABOX_TEMP + "/forge/version.json",MINECRAFT_HOME_VERSION + "/" + forgeJson.getId() + "/" + forgeJson.getId() + ".json", true);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return forgeJson.getId();
            }else{
                return null;
            }
        }
        return null;
    }

}
