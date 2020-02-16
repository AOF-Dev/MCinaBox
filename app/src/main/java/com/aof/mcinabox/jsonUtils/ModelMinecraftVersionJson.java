package com.aof.mcinabox.jsonUtils;

import java.util.Map;

public class ModelMinecraftVersionJson {

    //通用参数
    private String mainClass;
    private String releaseTime;
    private String time;
    private String type;
    private String assets;
    private String id;
    private DependentLibrary[] libraries;
    //private Map<String,String> assetIndex;
    //private Map<String,String> downloads;
    //private Map<String,String> logging;
    public class DependentLibrary{
        private String name;
        //private String path;
        private String url;

        //Setter and Getter

        //public String getPath() { return path; }
        //public void setPath(String path) { this.path = path; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

    }
    //1.13.0之前的参数
    private String minecraftArguments;
    private String minimumLauncherVersion;
    //1.13.0之后的参数
    //private Map<String,String> arguments;

    //Getter and Setter

    //public Map<String, String> getArguments() { return arguments; }
    //public void setArguments(Map<String, String> arguments) { this.arguments = arguments; }
    public String getMinimumLauncherVersion() { return minimumLauncherVersion; }
    public void setMinimumLauncherVersion(String minimumLauncherVersion) { this.minimumLauncherVersion = minimumLauncherVersion; }
    public String getMinecraftArguments() { return minecraftArguments; }
    public void setMinecraftArguments(String minecraftArguments) { this.minecraftArguments = minecraftArguments; }
    //public Map<String, String> getLogging() {return logging; }
    //public void setLogging(Map<String, String> logging) { this.logging = logging; }
    //public Map<String, String> getDownloads() { return downloads; }
    //public void setDownloads(Map<String, String> downloads) { this.downloads = downloads; }
    //public Map<String, String> getAssetIndex() { return assetIndex; }
    //public void setAssetIndex(Map<String, String> assetIndex) { this.assetIndex = assetIndex; }
    //public Libraries[] getLibraries() { return libraries; }
    //public void setLibraries(Libraries[] libraries) { this.libraries = libraries; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAssets() { return assets; }
    public void setAssets(String assets) { this.assets = assets; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getReleaseTime() { return releaseTime; }
    public void setReleaseTime(String releaseTime) { this.releaseTime = releaseTime; }
    public String getMainClass() { return mainClass; }
    public void setMainClass(String mainClass) { this.mainClass = mainClass; }


}
