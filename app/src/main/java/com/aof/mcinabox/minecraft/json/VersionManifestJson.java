package com.aof.mcinabox.minecraft.json;

import java.util.Map;

//解析version_manifest.json文件的模板对象
public class VersionManifestJson {

    public static final String TYPE_SNAPSHOT = "snapshot"; //快照
    public static final String TYPE_RELEASE = "release"; //正式版
    public static final String TYPE_OLD_BETA = "old_beta"; //Beta版
    public static final String TYPE_OLD_ALPHA = "old_alpha"; //Alpha版
    private Version[] versions;

    //其中“release”为最新的稳定版 “snapshot”为最新的快照版
    private Map<String, String> latest;

    public Map<String, String> getLatest() { return latest; }
    public void setLatest(Map<String, String> latest) { this.latest = latest; }
    public Version[] getVersions() { return versions; }
    public void setVersions(Version[] versions) { this.versions = versions; }

    public class Version {
        private String id;
        private String type;
        private String url;
        private String time;
        private String realeaseTime;

        //Getter and Setter
        public void setId(String i) { id = i; }
        public String getId() { return id; }
        public void setType(String i) { type = i; }
        public String getType() { return type; }
        public void setUrl(String i) { url = i; }
        public String getUrl() { return url; }
        public void setTime(String i) { time = i; }
        public String getTime() { return time; }
        public void setRealeaseTime(String i) { realeaseTime = i; }
        public String getReleaseTime() { return realeaseTime; }
    }
}
