package com.aof.mcinabox.initUtils;

import android.accounts.Account;
import android.os.Build;

import java.util.UUID;

public class LauncherSettingModel {


    public Accounts newAccounts = new Accounts();

    public LauncherSettingModel(){
        //默认模板初始化
        super();

        downloadType = "official";
        keyboard = "IceSty";
        localization = "public";

        configurations = new Configurations();
        configurations.javaArgs = "";
        configurations.minecraftArgs = "";
        configurations.maxMemory = 256;
        configurations.java = "1.8.0_211_AArch32";
        configurations.runtime = "AArch32";
        configurations.opengl = "OpenGL_2.0";
        configurations.openal = "OpenAL_soft";
        configurations.lwjgl = "Lwjgl_2.9.1";
        configurations.notCheckGame = false;
        configurations.notCheckJvm = false;
        configurations.notEnableVirtualKeyboard = false;
        configurations.enableOtg = false;

        /*
        Accounts account_temp = new Accounts();
        Accounts[] accounts_temp = {account_temp};
        accounts_temp[0].setUsername("Steve");
        accounts_temp[0].uuid = UUID.nameUUIDFromBytes((accounts_temp[0].getUsername()).getBytes()).toString();
        accounts_temp[0].setType("offline");
        accounts_temp[0].setSelected(true);
        */
        accounts = new Accounts[0];
    }



    private String localization; //存储路径："public"共有目录 "private"私有目录
    private  String downloadType; //下载源："office"官方 "bmclapi"国内BMCLAPI "mcbbs"国内MCBBS
    private String keyboard; //虚拟键盘： ""选择的键盘模板
    private boolean isUsing; //该配置是否正在被使用
    private Configurations configurations; //全局游戏设置
    private Accounts[] accounts; //用户信息

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
        boolean enableOtg; //启用物理键鼠支持

        //Getter and Setter
        public boolean isEnableOtg() { return enableOtg; }
        public void setEnableOtg(boolean enableOtg) { this.enableOtg = enableOtg; }
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
    public boolean isUsing() { return isUsing; }
    public void setUsing(boolean using) { isUsing = using; }
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
