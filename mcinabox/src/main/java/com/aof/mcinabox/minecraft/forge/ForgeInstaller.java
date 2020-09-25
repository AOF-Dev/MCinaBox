package com.aof.mcinabox.minecraft.forge;

import android.content.Context;

import com.aof.mcinabox.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.download.DownloadManager;
import com.aof.mcinabox.minecraft.json.VersionJson;
import com.aof.utils.FileTool;
import com.aof.mcinabox.utils.ZipUtils;
import java.io.File;
import static com.aof.mcinabox.definitions.manifest.AppManifest.*;

public class ForgeInstaller {

    private Context context;
    private String MINECRAFT_HOME;
    private String MINECRAFT_HOME_VERSION;

    public ForgeInstaller(Context context){
        this.context = context;
        this.MINECRAFT_HOME = AppManifest.MINECRAFT_HOME;
        this.MINECRAFT_HOME_VERSION = MINECRAFT_HOME + "/versions";
    }

    public void unzipForgeInstaller(String filename) throws Exception {
        ZipUtils.UnZipFolder(FORGE_HOME + "/" + filename,MCINABOX_TEMP + "/forge");
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

    public void startDownloadForge(String id){
        new DownloadManager(context).startPresetDownload(DownloadManager.DOWNLOAD_FORGE_LIBS,id);
    }


}
