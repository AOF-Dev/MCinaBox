package com.aof.mcinabox.launcher.version;

import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.launch.support.Utils;
import com.aof.mcinabox.launcher.version.support.LocalVersionListBean;
import com.aof.mcinabox.minecraft.JsonUtils;
import com.aof.mcinabox.minecraft.json.VersionJson;
import com.aof.mcinabox.utils.FileTool;

import java.io.File;
import java.util.ArrayList;

public class VersionManager {

    /*
     * 这是一个本地版本管理类
     * 通过该类来获得本地版本的列表信息
     * 通过该类来管理本地版本(导出、删除等)
     */

    public final static int REMOVE_VERSION_ONLY = 0;
    public final static int REMOVE_VERSION_WITH_LIBRARIES = 1;

    private final static String TAG = "VersionManager";

    public static String[] getVersionsList() {
        ArrayList<String> dirList = FileTool.listChildDirFromTargetDir(AppManifest.MINECRAFT_VERSIONS);
        ArrayList<String> fileList = new ArrayList<>();
        for (String dirName : dirList) {
            if (new File(AppManifest.MINECRAFT_VERSIONS + "/" + dirName + "/" + dirName + ".json").exists()) {
                fileList.add(dirName);
            }
        }
        String[] tmp = new String[fileList.size()];
        for (int a = 0; a < fileList.size(); a++) {
            tmp[a] = fileList.get(a);
        }
        return tmp;
    }

    public static ArrayList<LocalVersionListBean> getVersionBeansList() {
        String[] versions = getVersionsList();
        ArrayList<LocalVersionListBean> beans = new ArrayList<>();
        for (String id : versions) {
            beans.add(new LocalVersionListBean().setVersion_Id(id));
        }
        return beans;
    }

    public static boolean removeVersion(String id, int mode) {
        boolean hasVersion = false;
        for (String str : getVersionsList()) {
            if (str.equals(id)) {
                hasVersion = true;
                break;
            }
        }
        if (!hasVersion) {
            return false;
        }
        switch (mode) {
            case REMOVE_VERSION_ONLY:
                FileTool.deleteDir(AppManifest.MINECRAFT_VERSIONS + "/" + id);
                break;
            case REMOVE_VERSION_WITH_LIBRARIES:
                for (VersionJson.DependentLibrary lib : JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(id)).getLibraries()) {
                    FileTool.deleteFile(new File(Utils.getLibPathByPkgName(AppManifest.MINECRAFT_LIBRARIES, lib.getName())));
                }
        }
        return true;

    }


}
