package com.aof.mcinabox.launcher.download.support;

import android.util.Log;

import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.launch.support.Utils;
import com.aof.mcinabox.minecraft.json.AssetsJson;
import com.aof.mcinabox.minecraft.json.VersionJson;
import com.aof.mcinabox.minecraft.json.VersionManifestJson;
import com.liulishuo.filedownloader.BaseDownloadTask;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class DownloadSupport {
    private final UrlSource urlSource;
    private String sourceName;

    private final static String TAG = "DownloadSupport";

    public DownloadSupport() {
        sourceName = getSourceName();
        urlSource = new UrlSource();
    }

    private String getDownloadUrlFromSource(String url, String type) {
        return urlSource.getFileUrl(url, sourceName, type);
    }

    private String getDownloadUrlFromLibraryName(String name, String type) {
        return urlSource.getSourceUrl(sourceName, type) + getLibraryJarRelatedPath(name) + "/" + getLibraryJarName(name);
    }

    private String getSourceName() {
        return OldMainActivity.Setting.getDownloadType();
    }

    /**
     * 【创建version_manifest.json下载任务】
     **/
    public BaseDownloadTask createVersionManifestDownloadTask() {
        RefreshSourceName();
        return DownloadHelper.createDownloadTask("version_manifest.json", AppManifest.MCINABOX_TEMP, urlSource.getFileUrl(urlSource.getSourceUrl("official", UrlSource.TYPE_VERSION_MANIFEST), sourceName, UrlSource.TYPE_VERSION_MANIFEST), 1);
    }

    /**
     * 【创建version.json下载任务】
     **/
    public BaseDownloadTask createVersionJsonDownloadTask(String id) {
        RefreshSourceName();
        VersionManifestJson.Version[] versions = com.aof.mcinabox.minecraft.JsonUtils.getVersionManifestFromFile(AppManifest.MCINABOX_TEMP + "/version_manifest.json").getVersions();
        if (versions == null) {
            Log.e(TAG, "Not found version_manifest from json.");
            return null;
        }
        for (VersionManifestJson.Version version : versions) {
            if (version.getId().equals(id)) {
                return DownloadHelper.createDownloadTask(id + ".json", AppManifest.MINECRAFT_VERSIONS + "/" + id, getDownloadUrlFromSource(version.getUrl(), UrlSource.TYPE_VERSION_JSON), null);
            }
        }
        Log.e(TAG, "Not found version " + id + " in manifest.");
        return null;
    }

    /**
     * 【创建version.jar下载任务】
     **/
    public BaseDownloadTask createVersionJarDownloadTask(String id) {
        RefreshSourceName();
        VersionJson version = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(id));
        if (version == null) {
            Log.e(TAG, "Not found Version " + id + ".json");
        }
        return DownloadHelper.createDownloadTask(id + ".jar", AppManifest.MINECRAFT_VERSIONS + "/" + id,
                getDownloadUrlFromSource(Objects.requireNonNull(version).getDownloads().getClient().getUrl(), UrlSource.TYPE_VERSION_JAR), null);
    }

    /**
     * 【创建libraries下载任务】
     **/
    public ArrayList<BaseDownloadTask> createLibrariesDownloadTask(String id) {
        RefreshSourceName();
        ArrayList<BaseDownloadTask> tasks = new ArrayList<>();
        VersionJson version = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(id));
        if (version == null) {
            Log.e(TAG, "Not found Version " + id + ".json");
            return null;
        }
        for (VersionJson.DependentLibrary library : version.getLibraries()) {
            if (library.getDownloads().getArtifact() != null) {
                tasks.add(DownloadHelper.createDownloadTask(getLibraryJarName(library.getName()), getLibraryJarPath(library.getName())
                        , getDownloadUrlFromSource(library.getDownloads().getArtifact().getUrl(), UrlSource.TYPE_LIBRARIES), null));
            }
        }
        return tasks;
    }

    /**
     * 【创建assetindex.json下载任务】
     **/
    public BaseDownloadTask createAssetIndexDownloadTask(String id) {
        RefreshSourceName();
        VersionJson version = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(id));
        if (version == null) {
            Log.e(TAG, "Not found Version " + id + ".json");
            return null;
        }
        return DownloadHelper.createDownloadTask(version.getAssets() + ".json", AppManifest.MINECRAFT_ASSETS + "/indexes", getDownloadUrlFromSource(version.getAssetIndex().getUrl(), UrlSource.TYPE_ASSETS_INDEX_JSON), null);
    }

    /**
     * 【创建assets下载任务】
     **/
    public ArrayList<BaseDownloadTask> createAssetObjectsDownloadTask(String id) {
        RefreshSourceName();
        ArrayList<BaseDownloadTask> tasks = new ArrayList<>();
        VersionJson version = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(id));
        if (version == null) {
            Log.e(TAG, "Not found Version " + id + ".json");
            return null;
        }
        AssetsJson assets = com.aof.mcinabox.minecraft.JsonUtils.getAssetsFromFile(Utils.getAssetsJsonAbsPath(version.getAssets()));
        if (assets == null) {
            Log.e(TAG, "Not found AssetIndex " + version.getAssets() + ".json");
            return null;
        }

        Set<String> keySets = assets.getObjects().keySet();
        for (String key : keySets) {
            String hashCode = Objects.requireNonNull(assets.getObjects().get(key)).hash;
            tasks.add(DownloadHelper.createDownloadTask(hashCode, getAssetsObjectPath(hashCode), getAssetsObjectUrl(hashCode), null));
        }
        return tasks;
    }

    /**
     * 【创建forge下载任务】
     **/
    public ArrayList<BaseDownloadTask> createForgeDownloadTask(String id) {
        RefreshSourceName();
        ArrayList<BaseDownloadTask> tasks = new ArrayList<>();
        VersionJson forge = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(id));
        if (forge == null) {
            Log.e(TAG, "Not found Forge " + id + ".json");
            return null;
        }
        for (VersionJson.DependentLibrary library : forge.getLibraries()) {
            if (library.getUrl() == null) {
                tasks.add(DownloadHelper.createDownloadTask(getLibraryJarName(library.getName()), getLibraryJarPath(library.getName()), getDownloadUrlFromLibraryName(library.getName(), "forge"), null));
            } else {
                tasks.add(DownloadHelper.createDownloadTask(getLibraryJarName(library.getName()), getLibraryJarPath(library.getName()), getDownloadUrlFromLibraryName(library.getName(), "libraries"), null));
            }
        }
        return tasks;
    }

    private String getLibraryJarRelatedPath(String name) {
        String packageName;
        String libraryName;
        String versionName;

        String[] Name = name.split(":");
        packageName = Name[0];
        libraryName = Name[1];
        versionName = Name[2];

        StringBuilder dirPath = new StringBuilder("/");
        for (int i = 0; i < packageName.length(); i++) {
            if (packageName.charAt(i) == '.') {
                dirPath.append("/");
            } else {
                dirPath.append(packageName.charAt(i));
            }
        }
        dirPath.append("/").append(libraryName).append("/").append(versionName);
        return dirPath.toString();
    }

    private String getLibraryJarPath(String name) {
        return AppManifest.MINECRAFT_LIBRARIES + getLibraryJarRelatedPath(name);
    }

    private String getLibraryJarName(String name) {
        String libraryName;
        String versionName;

        String[] Name = name.split(":");
        libraryName = Name[1];
        versionName = Name[2];

        return libraryName + "-" + versionName + ".jar";
    }

    private String getAssetsObjectUrl(String hashCode) {
        StringBuilder tip = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            tip.append(hashCode.charAt(i));
        }
        return urlSource.getSourceUrl(sourceName, "assets") + "/" + tip + "/" + hashCode;
    }

    private String getAssetsObjectPath(String hashCode) {
        StringBuilder tip = new StringBuilder();
        for (int i = 0; i < 2; i++) {
            tip.append(hashCode.charAt(i));
        }
        return AppManifest.MINECRAFT_ASSETS + "/objects/" + tip;
    }

    public void RefreshSourceName() {
        sourceName = getSourceName();
    }
}
