package com.aof.mcinabox.launcher.launch.support;

import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.minecraft.JsonUtils;
import com.aof.mcinabox.minecraft.json.AssetsJson;
import com.aof.mcinabox.minecraft.json.VersionJson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class Utils {

    /**
     * [获取某version的主json文件路径]
     **/

    public static String getJsonAbsPath(String versionHome, String id) {
        return (versionHome + "/" + id + "/" + id + ".json");
    }

    public static String getJsonAbsPath(String id) {
        return getJsonAbsPath(AppManifest.MINECRAFT_VERSIONS, id);
    }

    public static String getJsonAbsPath(VersionJson version) {
        return getJsonAbsPath(AppManifest.MINECRAFT_VERSIONS, version.getId());
    }

    public static String getJsonAbsPath(String versionHome, VersionJson version) {
        return getJsonAbsPath(versionHome, version.getId());
    }

    /**
     * [获取某version的主jar文件路径]
     **/

    public static String getJarAbsPath(String versionHome, String id) {
        return (versionHome + "/" + id + "/" + id + ".jar");
    }

    public static String getJarAbsPath(String id) {
        return getJarAbsPath(AppManifest.MINECRAFT_VERSIONS, id);
    }

    public static String getJarAbsPath(VersionJson version) {
        return getJarAbsPath(AppManifest.MINECRAFT_VERSIONS, version.getId());
    }

    public static String getJarAbsPath(String versionHome, VersionJson version) {
        return getJarAbsPath(versionHome, version.getId());
    }

    /**
     * [获取某version的assets资源json文件路径]
     **/

    public static String getAssetsJsonAbsPath(String assetsHome, String assetsId) {
        return (assetsHome + "/indexes/" + assetsId + ".json");
    }

    public static String getAssetsJsonAbsPath(String assetsId) {
        return getAssetsJsonAbsPath(AppManifest.MINECRAFT_ASSETS, assetsId);
    }

    public static String getAssetsJsonAbsPath(VersionJson version) {
        return getAssetsJsonAbsPath(AppManifest.MINECRAFT_ASSETS, version.getAssets());
    }

    public static String getAssetsJsonAbsPath(String assetsHome, VersionJson version) {
        return getAssetsJsonAbsPath(assetsHome, version.getAssets());
    }

    /**
     * [根据某libraries的name值来获取对应的文件路径]
     **/

    public static String getLibPathByPkgName(String libraryHome, String pkgName) {
        String packageName;
        String libraryName;
        String versionName;
        String filePath;

        String[] Name = pkgName.split(":");
        packageName = Name[0];
        libraryName = Name[1];
        versionName = Name[2];

        StringBuilder dirPath = new StringBuilder(libraryHome + "/");
        for (int i = 0; i < packageName.length(); i++) {
            if (packageName.charAt(i) == '.') {
                dirPath.append("/");
            } else {
                dirPath.append(packageName.charAt(i));
            }
        }
        dirPath.append("/").append(libraryName).append("/").append(versionName).append("/");

        String fileName = libraryName + "-" + versionName + ".jar";
        filePath = dirPath + fileName;

        return filePath;
    }

    public static String getLibPathByPkgName(String pkgName) {
        return getLibPathByPkgName(AppManifest.MINECRAFT_LIBRARIES, pkgName);
    }

    /**
     * [检查依赖库是否需要被过滤]
     **/

    public static boolean filterLib(String pkgName) {
        String packname = "";
        boolean result = false;
        int a = 0;
        for (int i = 0; i < pkgName.length(); i++) {
            if (pkgName.charAt(i) == ':') {
                a = i + 1;
                break;
            }
        }
        for (; a < pkgName.length(); a++) {
            if (pkgName.charAt(a) != ':') {
                packname = packname + pkgName.charAt(a);
            } else {
                break;
            }
        }
        for (String str : AppManifest.BOAT_RUNTIME_FILTERED_LIBRARIES) {
            if (str.equals(packname)) {
                return true;
            }
        }
        return false;
    }

    /**
     * [获取某版本的全部libraries路径数组]
     **/

    public static String[] getLibPaths(String libraryHome, VersionJson version) {
        String[] tmp = new String[version.getLibraries().length];
        for (int a = 0; a < tmp.length; a++) {
            tmp[a] = getLibPathByPkgName(libraryHome, version.getLibraries()[a].getName());
        }
        return tmp;
    }

    public static String[] getLibPaths(String libraryHome, String versionId) {
        return getLibPaths(libraryHome, JsonUtils.getVersionFromFile(getJsonAbsPath(AppManifest.MINECRAFT_VERSIONS, versionId)));
    }

    public static String[] getLibPaths(String versionId) {
        return getLibPaths(AppManifest.MINECRAFT_LIBRARIES, versionId);
    }

    public static String[] getLibPaths(VersionJson version) {
        return getLibPaths(AppManifest.MINECRAFT_LIBRARIES, version);
    }

    public static String[] getLibPaths(String libraryHome, String[] pkgNames) {
        String[] tmp = new String[pkgNames.length];
        for (int a = 0; a < tmp.length; a++) {
            tmp[a] = getLibPathByPkgName(libraryHome, pkgNames[a]);
        }
        return tmp;
    }

    public static String[] getLibPaths(String[] pkgNames) {
        return getLibPaths(AppManifest.MINECRAFT_LIBRARIES, pkgNames);
    }

    /**
     * [获取过滤后的全部Libraries路径数组]
     **/

    public static String[] getFilteredLibPaths(String libraryHome, VersionJson version) {
        ArrayList<String> tmps = new ArrayList<>();

        for (VersionJson.DependentLibrary lib : version.getLibraries()) {
            if (!filterLib(lib.getName())) {
                tmps.add(getLibPathByPkgName(libraryHome, lib.getName()));
            }
        }

        String[] tmp = new String[tmps.size()];
        for (int a = 0; a < tmps.size(); a++) {
            tmp[a] = tmps.get(a);
        }

        return tmp;
    }

    public static String[] getFilteredLibPaths(String libraryHome, String versionId) {
        return getFilteredLibPaths(libraryHome, JsonUtils.getVersionFromFile(getJsonAbsPath(AppManifest.MINECRAFT_VERSIONS, versionId)));
    }

    public static String[] getFilteredLibPaths(String versionId) {
        return getFilteredLibPaths(AppManifest.MINECRAFT_LIBRARIES, versionId);
    }

    public static String[] getFilteredLibPaths(VersionJson version) {
        return getFilteredLibPaths(AppManifest.MINECRAFT_LIBRARIES, version);
    }

    /**
     * [根据某assets的object的hashcode来获取该文件路径]
     **/

    public static String getAssetsPathByObjHash(String assetsPath, String hashCode) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            str.append(hashCode.charAt(i));
        }
        return assetsPath + "/objects/" + str + "/" + hashCode;
    }

    public static String getAssetsPathByObjHash(String hashCode) {
        return getAssetsPathByObjHash(AppManifest.MINECRAFT_ASSETS, hashCode);
    }


    /**
     * [获取某版本的全部assets路径数组]
     **/

    public static String[] getAssetsPaths(String assetsHome, String assetsId) {
        AssetsJson assets = JsonUtils.getAssetsFromFile(getAssetsJsonAbsPath(assetsHome, assetsId));
        String[] tmp = new String[assets.getObjects().size()];

        Set<String> keySets = assets.getObjects().keySet();
        Iterator<String> it = keySets.iterator();
        int i = 0;
        while (it.hasNext()) {
            String key = it.next();
            String hashCode = Objects.requireNonNull(assets.getObjects().get(key)).hash;
            tmp[i] = getAssetsPathByObjHash(assetsHome, hashCode);
            i++;
        }
        return tmp;
    }

    public static String[] getAssetsPaths(String assetsId) {
        return getAssetsPaths(AppManifest.MINECRAFT_ASSETS, assetsId);
    }

    public static String[] getAssetsPaths(String assetsHome, VersionJson version) {
        return getAssetsPaths(assetsHome, version.getAssets());
    }

    public static String[] getAssetsPaths(VersionJson version) {
        return getAssetsPaths(AppManifest.MINECRAFT_ASSETS, version.getAssets());
    }

    /**
     * [根据libraries的包名过滤相关条目]
     **/

    public static String[] filterByPkgName(final String[] originalPkgNames, final String[] filterPkgNames) {

        ArrayList<String> result = new ArrayList<>();
        for (String name : originalPkgNames) {
            int a = 0;
            String packname = "";
            boolean res = false;
            for (int i = 0; i < name.length(); i++) {
                if (name.charAt(i) == ':') {
                    a = i + 1;
                    break;
                }
            }
            for (; a < name.length(); a++) {
                if (name.charAt(a) != ':') {
                    packname = packname + name.charAt(a);
                } else {
                    break;
                }
            }
            for (String str : filterPkgNames) {
                if (str.equals(packname)) {
                    res = true;
                    break;
                }
            }
            if (!res) {
                result.add(name);
            }
        }

        String[] tmp = new String[result.size()];
        for (int a = 0; a < result.size(); a++) {
            tmp[a] = result.get(a);
        }
        return tmp;
    }

    /**
     * [检查某版本是否是api，如果是就返回原版的id]
     **/

    public static String judgeApi(String versionId) {
        VersionJson version = JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(versionId));
        if (version.getInheritsFrom() == null) {
            return null;
        } else {
            return version.getInheritsFrom();
        }
    }


}
