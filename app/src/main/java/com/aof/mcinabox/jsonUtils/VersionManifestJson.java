package com.aof.mcinabox.jsonUtils;

public class VersionManifestJson {
    private String id;
    private String type;
    private String url;
    private String time;
    private String realeaseTime;

    public void setId(String i){
        id = i;
    }
    public String getId(){
        return id;
    }
    public void setType(String i){
        type = i;
    }
    public String getType(){
        return type;
    }
    public void setUrl(String i){
        url = i;
    }
    public String getUrl(){
        return url;
    }
    public void setTime(String i){
        time = i;
    }
    public String getTime(){
        return time;
    }
    public void setRealeaseTime(String i){
        realeaseTime = i;
    }
    public String getReleaseTime(){
        return realeaseTime;
    }
}
