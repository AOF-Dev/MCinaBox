package com.aof.mcinabox.launcher.setting.support;

import android.os.Environment;

public class SettingJson {

    public final static String USER_TYPE_OFFLINE = "offline";
    public final static String USER_TYPE_ONLINE = "online";
    public final static String USER_TYPE_EXTERNAL = "external";

    public final static String DOWNLOAD_SOURCE_OFFICIAL = "official";
    public final static String DOWNLOAD_SOURCE_BMCLAPI = "bmclapi";
    public final static String DOWNLOAD_SOURCE_MCBBS = "mcbbs";
    public final static String[] DOWNLOAD_SOURCES = {DOWNLOAD_SOURCE_OFFICIAL, DOWNLOAD_SOURCE_BMCLAPI, DOWNLOAD_SOURCE_MCBBS};

    public final static String DEFAULT_GAMEDIR = Environment.getExternalStorageDirectory().getPath() + "/MCinaBox/gamedir";

    private String downloadType; //下载源
    private Configurations configurations; //全局游戏设置
    private boolean backgroundAutoSwitch; //自动切换启动器背景
    private boolean fullscreen; //全屏显示
    private Account[] accounts; //用户信息
    private String lastVersion; //最后一次选择的版本
    private String gamedir; //我的世界目录

    public SettingJson() {
        //默认模板初始化
        super();

        downloadType = DOWNLOAD_SOURCE_OFFICIAL;
        lastVersion = "";
        gamedir = DEFAULT_GAMEDIR;

        configurations = new Configurations()
                .setJavaArgs("")
                .setMinecraftArgs("")
                .setMaxMemory(256)
                .setNotCheckGame(false)
                .setNotCheckPlatform(false);

        accounts = new Account[]{};
    }

    //全局游戏设置
    public class Configurations {
        private String javaArgs; //Java虚拟机附加启动参数
        private String minecraftArgs; //Minecraft附加启动参数
        private int maxMemory; //最大内存
        private boolean notCheckGame; //启动时不检查游戏完整性
        private boolean notCheckPlatform; //启动时不检查JVM架构的兼容性
        private boolean notCheckTipper; //启动时不检查消息管理器
        private boolean notCheckForge; //不检查forge
        private boolean notCheckOptions; //不检查options.txt
        private boolean alwaysChoiceRuntimeManifest; //总是选择运行库清单
        private boolean enableDebug; //启用调试
        private boolean enableAutoMemory; //启用自动内存设定

        public Configurations(){
            super();
        }

        //Getter and Setter
        public String getJavaArgs() {
            return javaArgs;
        }

        public Configurations setJavaArgs(String javaArgs) {
            this.javaArgs = javaArgs;
            return this;
        }

        public boolean isEnableDebug(){
            return enableDebug;
        }

        public Configurations setDebug(boolean b){
            this.enableDebug = b;
            return this;
        }

        public String getMinecraftArgs() {
            return minecraftArgs;
        }

        public Configurations setMinecraftArgs(String minecraftArgs) {
            this.minecraftArgs = minecraftArgs;
            return this;
        }

        public int getMaxMemory() {
            return maxMemory;
        }

        public Configurations setMaxMemory(int maxMemory) {
            this.maxMemory = maxMemory;
            return this;
        }

        public boolean isNotCheckGame() {
            return notCheckGame;
        }

        public Configurations setNotCheckGame(boolean notCheckGame) {
            this.notCheckGame = notCheckGame;
            return this;
        }

        public boolean isNotCheckPlatform() {
            return notCheckPlatform;
        }

        public Configurations setNotCheckPlatform(boolean b) {
            this.notCheckPlatform = b;
            return this;
        }

        public boolean isNotCheckTipper(){
            return notCheckTipper;
        }

        public Configurations setNotCheckTipper(boolean b){
            this.notCheckTipper = b;
            return this;
        }

        public Configurations setAlwaysChoiceRuntimeMainfest(boolean b){
            this.alwaysChoiceRuntimeManifest = b;
            return this;
        }

        public boolean isAlwaysChoiceRuntimeManifest(){
            return this.alwaysChoiceRuntimeManifest;
        }

        public Configurations setNotCheckForge(boolean b){
            this.notCheckForge = b;
            return this;
        }

        public boolean isNotCheckForge(){
            return this.notCheckForge;
        }

        public Configurations setAutoMemory(boolean b){
            this.enableAutoMemory = b;
            return this;
        }

        public boolean isEnableAutoMemory(){
            return this.enableAutoMemory;
        }

        public Configurations setNotCheckOptions(boolean b){
            this.notCheckOptions = b;
            return this;
        }

        public boolean isNotCheckOptions(){
            return this.notCheckOptions;
        }

    }


    //用户列表
    public class Account {
        String uuid; //唯一角色标识
        String userUUID; //唯一用户标识
        String username; //用户名
        String type; //用户类型 "offline"离线模式 "online"线上模式
        String accessToken; //通行令牌
        String apiMeta;
        String apiUrl;
        String serverName;
        boolean selected; //是否被选中

        public Account(){
            super();
        }

        //Getter and Setter
        public String getUserUUID() {
            return userUUID;
        }

        public Account setUserUuid(String uuid) {
            this.userUUID = uuid;
            return this;
        }

        public String getUuid() {
            return uuid;
        }

        public Account setUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public String getUsername() {
            return username;
        }

        public Account setUsername(String username) {
            this.username = username;
            return this;
        }

        public String getApiMeta() {
            return apiMeta;
        }

        public Account setApiMeta(String apiMeta) {
            this.apiMeta = apiMeta;
            return this;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public Account setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }

        public String getServerName() {
            return serverName;
        }

        public Account setServerName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        public String getType() {
            return type;
        }

        public Account setType(String type) {
            this.type = type;
            return this;
        }

        public boolean isSelected() {
            return selected;
        }

        public Account setSelected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public Account setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

    }

    public String getDownloadType() {
        return downloadType;
    }

    public SettingJson setDownloadType(String downloadType) {
        this.downloadType = downloadType;
        return this;
    }

    public Configurations getConfigurations() {
        return configurations;
    }

    public SettingJson setConfigurations(Configurations configurations) {
        this.configurations = configurations;
        return this;
    }

    public Account[] getAccounts() {
        return accounts;
    }

    public SettingJson setAccounts(Account[] accounts) {
        this.accounts = accounts;
        return this;
    }

    public String getLastVersion() {
        return lastVersion;
    }

    public SettingJson setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
        return this;
    }

    public String getGamedir() {
        return this.gamedir;
    }

    public SettingJson setGameDir(String dir) {
        this.gamedir = dir;
        return this;
    }

    public boolean isBackgroundAutoSwitch(){
        return this.backgroundAutoSwitch;
    }

    public SettingJson setBackgroundAutoSwitch(boolean b){
        this.backgroundAutoSwitch = b;
        return this;
    }

    public boolean isFullscreen(){
        return this.fullscreen;
    }

    public SettingJson setFullscreen(boolean b){
        this.fullscreen = b;
        return this;
    }
}
