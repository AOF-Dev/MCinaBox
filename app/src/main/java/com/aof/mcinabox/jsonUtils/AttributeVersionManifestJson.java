package com.aof.mcinabox.jsonUtils;

public class AttributeVersionManifestJson {

    //解析version_manifest.json文件的模板对象

    public Version[] version;
    public class latest {
        String release;
        String snapshot;

        public void setRelease(String i){
            release = i;
        }
        public String getRelease(){
            return release;
        }
        public void setSnapshot(String i){
            snapshot = i;
        }
        public String getSnapshot(){
            return snapshot;
        }
    }
    public class Version {
        private String id;
        private String type;
        private String url;
        private String time;
        private String realeaseTime;

        public void setId(String i) {
            id = i;
        }

        public String getId() {
            return id;
        }

        public void setType(String i) {
            type = i;
        }

        public String getType() {
            return type;
        }

        public void setUrl(String i) {
            url = i;
        }

        public String getUrl() {
            return url;
        }

        public void setTime(String i) {
            time = i;
        }

        public String getTime() {
            return time;
        }

        public void setRealeaseTime(String i) {
            realeaseTime = i;
        }

        public String getReleaseTime() {
            return realeaseTime;
        }
    }
}
