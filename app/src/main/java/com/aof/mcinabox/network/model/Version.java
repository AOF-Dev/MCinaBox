package com.aof.mcinabox.network.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Version {
    private String[] gameArguments;
    private String[] jvmArguments;
    private AssetIndexInfo assetIndex;
    private String assets;
    private Map<DownloadType, DownloadInfo> downloads;
    private String id;
    private Library[] libraries;
    private String mainClass;
    private Date releaseTime;
    private Date time;
    private ReleaseType type;

    public String[] getGameArguments() {
        return gameArguments;
    }

    public void setGameArguments(String[] gameArguments) {
        this.gameArguments = gameArguments;
    }

    public String[] getJvmArguments() {
        return jvmArguments;
    }

    public void setJvmArguments(String[] jvmArguments) {
        this.jvmArguments = jvmArguments;
    }

    public AssetIndexInfo getAssetIndexInfo() {
        return assetIndex;
    }

    public void setAssetIndexInfo(AssetIndexInfo assetIndex) {
        this.assetIndex = assetIndex;
    }

    public String getAssets() {
        return assets;
    }

    public void setAssets(String assets) {
        this.assets = assets;
    }

    public Map<DownloadType, DownloadInfo> getDownloads() {
        return downloads;
    }

    public void setDownloads(Map<DownloadType, DownloadInfo> downloads) {
        this.downloads = downloads;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Library[] getLibraries() {
        return libraries;
    }

    public void setLibraries(Library[] libraries) {
        this.libraries = libraries;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public ReleaseType getType() {
        return type;
    }

    public void setType(ReleaseType type) {
        this.type = type;
    }

    public Collection<Library> getRelevantLibraries(CompatibilityRule.FeatureMatcher featureMatcher) {
        List<Library> result = new ArrayList<>();
        for (Library library : this.libraries) {
            if (library.appliesToCurrentEnvironment(featureMatcher))
                result.add(library);
        }
        return result;
    }

    public Collection<File> getClassPath(OperatingSystem os, File base, CompatibilityRule.FeatureMatcher featureMatcher) {
        Collection<Library> libraries = getRelevantLibraries(featureMatcher);
        Collection<File> result = new ArrayList<>();
        for (Library library : libraries) {
            if (library.getNatives() == null)
                result.add(new File(base, "libraries/" + library.getArtifactPath()));
        }
        result.add(new File(base, "versions/" + getId() + "/" + getId() + ".jar"));
        return result;
    }

    public static class AssetIndexInfo {
        private String id;
        private String sha1;
        private int size;
        private int totalSize;
        private String url;

        public String getId() {
            return id;
        }

        public String getSha1() {
            return sha1;
        }

        public int getSize() {
            return size;
        }

        public int getTotalSize() {
            return totalSize;
        }

        public String getUrl() {
            return url;
        }
    }
}
