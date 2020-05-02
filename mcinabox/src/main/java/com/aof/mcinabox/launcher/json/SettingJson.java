package com.aof.mcinabox.launcher.json;

import android.annotation.SuppressLint;
import android.text.Html;

public class SettingJson {


    public Accounts newAccounts = new Accounts();


    public SettingJson(){
        //默认模板初始化
        super();

        downloadType = "official";
        keyboard = "";
        localization = "public";
        language = "default(system)"; //default: Definited By System.
        lastVersion = "";

        configurations = new Configurations();
        configurations.javaArgs = "";
        configurations.minecraftArgs = "";
        configurations.maxMemory = 256;
        configurations.notCheckGame = false;
        configurations.notCheckJvm = false;

        accounts = new Accounts[0];
    }



    private String localization; //存储路径："public"共有目录 "private"私有目录
    private String downloadType; //下载源："office"官方 "bmclapi"国内BMCLAPI "mcbbs"国内MCBBS
    private String keyboard; //虚拟键盘： ""选择的键盘模板
    private Configurations configurations; //全局游戏设置
    private Accounts[] accounts; //用户信息
    private String language; //语言
    private String lastVersion; //最后一次选择的版本

    //全局游戏设置
    public class Configurations{
        String javaArgs; //Java虚拟机附加启动参数
        String minecraftArgs; //Minecraft附加启动参数
        int maxMemory; //最大内存
        boolean notCheckGame; //启动时不检查游戏完整性
        boolean notCheckJvm; //启动时不检查JVM架构的兼容性

        //Getter and Setter
        public String getJavaArgs() { return javaArgs; }
        public void setJavaArgs(String javaArgs) { this.javaArgs = javaArgs; }
        public String getMinecraftArgs() { return minecraftArgs; }
        public void setMinecraftArgs(String minecraftArgs) { this.minecraftArgs = minecraftArgs; }
        public int getMaxMemory() { return maxMemory; }
        public void setMaxMemory(int maxMemory) { this.maxMemory = maxMemory; }
        public boolean isNotCheckGame() { return notCheckGame; }
        public void setNotCheckGame(boolean notCheckGame) { this.notCheckGame = notCheckGame; }
        public boolean isNotCheckJvm() { return notCheckJvm; }
        public void setNotCheckJvm(boolean notCheckJvm){ this.notCheckJvm = notCheckJvm; }
    }


    //用户列表
    public class Accounts{
        String uuid; //唯一用户标识
        String username; //用户名
        String type; //用户类型 "offline"离线模式 "online"线上模式
        String accessToken; //通行令牌
        boolean selected; //是否被选中

        //Getter and Setter
        public String getUuid() { return uuid; }
        public void setUuid(String uuid) { this.uuid = uuid; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public boolean isSelected() { return selected; }
        public void setSelected(boolean selected) { this.selected = selected; }
        public String getAccessToken() { return accessToken;}
        public void setAccessToken(String accessToken) {this.accessToken = accessToken;}

    }

    //Getter and Setter
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getLocalization() { return localization; }
    public void setLocalization(String localization) { this.localization = localization; }
    public String getDownloadType() { return downloadType; }
    public void setDownloadType(String downloadType) { this.downloadType = downloadType; }
    public String getKeyboard() { return keyboard; }
    public void setKeyboard(String keyboard) { this.keyboard = keyboard; }
    public Configurations getConfigurations() { return configurations; }
    public void setConfigurations(Configurations configurations) { this.configurations = configurations; }
    public Accounts[] getAccounts() { return accounts; }
    public void setAccounts(Accounts[] accounts) { this.accounts = accounts; }
    public String getLastVersion() { return lastVersion; }
    public void setLastVersion(String lastVersion) { this.lastVersion = lastVersion; }
}
