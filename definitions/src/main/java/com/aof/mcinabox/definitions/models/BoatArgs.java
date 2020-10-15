package com.aof.mcinabox.definitions.models;

import java.io.Serializable;

public class BoatArgs implements Serializable {

    private String[] args; //启动参数
    private String java_home; //jre环境
    private String[] shared_libraries; //动态链接库
    private String gamedir; //游戏目录
    private boolean debug; //调试

    public boolean getDebug() {
        return debug;
    }

    public BoatArgs setDebug(boolean b) {
        this.debug = b;
        return this;
    }

    public String getJava_home() {
        return java_home;
    }

    public BoatArgs setJava_home(String path) {
        this.java_home = path;
        return this;
    }

    public String getGamedir() {
        return gamedir;
    }

    public BoatArgs setGamedir(String dir) {
        this.gamedir = dir;
        return this;
    }

    public String[] getArgs() {
        return args;
    }

    public BoatArgs setArgs(String[] args) {
        this.args = args;
        return this;
    }

    public BoatArgs setShared_libraries(String[] libraries) {
        this.shared_libraries = libraries;
        return this;
    }

    public String[] getShared_libraries() {
        return shared_libraries;
    }
}
