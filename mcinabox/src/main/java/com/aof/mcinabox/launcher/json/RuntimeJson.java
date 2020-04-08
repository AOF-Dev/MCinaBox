package com.aof.mcinabox.launcher.json;

public class RuntimeJson {
    private String PackName; //运行库包名
    private String ReleaseTime; //发行日期
    private String Platform; //架构
    private String JavaVersion; //Java版本
    private String OpenGLVersion; //OpenGL版本
    private String OpenALVersion; //OpenAL版本
    private String Lwjgl2Version; //Lwjgl2版本
    private String Lwjgl3Version; //Lwjgl3版本

    public String getPackName() {
        return PackName;
    }
    public void setPackName(String packName) {
        PackName = packName;
    }
    public String getReleaseTime() {
        return ReleaseTime;
    }
    public void setReleaseTime(String releaseTime) {
        ReleaseTime = releaseTime;
    }
    public String getPlatform() {
        return Platform;
    }
    public void setPlatform(String platform) {
        Platform = platform;
    }
    public String getJavaVersion() {
        return JavaVersion;
    }
    public void setJavaVersion(String javaVersion) {
        JavaVersion = javaVersion;
    }
    public String getOpenGLVersion() {
        return OpenGLVersion;
    }
    public void setOpenGLVersion(String openGLVersion) {
        OpenGLVersion = openGLVersion;
    }

    public String getOpenALVersion() {
        return OpenALVersion;
    }

    public void setOpenALVersion(String openALVersion) {
        OpenALVersion = openALVersion;
    }

    public String getLwjgl2Version() {
        return Lwjgl2Version;
    }

    public void setLwjgl2Version(String lwjgl2Version) {
        Lwjgl2Version = lwjgl2Version;
    }

    public String getLwjgl3Version() {
        return Lwjgl3Version;
    }

    public void setLwjgl3Version(String lwjgl3Version) {
        Lwjgl3Version = lwjgl3Version;
    }
}
