package com.aof.mcinabox.initUtils;

public class LauncherSettingModel {
    private String localization; //存储路径："public"共有目录 "private"私有目录
    private  String downloadType; //下载源："office"官方 "bmclapi"国内BMCLAPI "mcbbs"国内MCBBS
    private String keyboard; //虚拟键盘： ""选择的键盘模板

    Configurations configurations;
    Accounts[] accounts;

    //全局游戏设置
    public class Configurations{
        String javaArgs; //Java虚拟机附加启动参数
        String minecraftArgs; //Minecraft附加启动参数
        int maxMemory; //最大内存
        String java; //Java虚拟机的类型(版本+架构 如:1.8.0_211_aarch64)
        String runtime; //运行库的类型(版本+架构 如:1.1.0_101_aarch64)
        String opengl; //OpenGL的类型(版本 如:OpenGL_2.0 OpenGL_1.5 OpenGL_1.4)
        String openal; //OpenAL的类型(版本 如:OpenAL_soft)
        String lwjgl; //LWJGL的类型(版本 如:Lwjgl_2.x Lwjgl_3.x)
        boolean notCheckGame; //启动时不检查游戏完整性
        boolean notCheckJvm; //启动时不检查JVM架构的兼容性
        boolean notEnableVirtualKeyboard; //不启用虚拟键盘


        //Getter and Setter
        public boolean isNotEnableVirtualKeyboard() { return notEnableVirtualKeyboard; }
        public void setNotEnableVirtualKeyboard(boolean notEnableVirtualKeyboard) { this.notEnableVirtualKeyboard = notEnableVirtualKeyboard; }
        public String getJavaArgs() { return javaArgs; }
        public void setJavaArgs(String javaArgs) { this.javaArgs = javaArgs; }
        public String getMinecraftArgs() { return minecraftArgs; }
        public void setMinecraftArgs(String minecraftArgs) { this.minecraftArgs = minecraftArgs; }
        public int getMaxMemory() { return maxMemory; }
        public void setMaxMemory(int maxMemory) { this.maxMemory = maxMemory; }
        public String getJava() { return java; }
        public void setJava(String java) { this.java = java; }
        public String getRuntime() { return runtime; }
        public void setRuntime(String runtime) { this.runtime = runtime; }
        public String getOpengl() { return opengl; }
        public void setOpengl(String opengl) { this.opengl = opengl; }
        public String getOpenal() { return openal; }
        public void setOpenal(String openal) {this.openal = openal; }
        public String getLwjgl() { return lwjgl; }
        public void setLwjgl(String lwjgl) { this.lwjgl = lwjgl; }
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

    }

    //Getter and Setter
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
}
