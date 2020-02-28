package com.aof.mcinabox;

import android.os.Build;
import android.util.Log;

import com.aof.mcinabox.initUtils.LauncherSettingModel;
import com.aof.mcinabox.ioUtils.FileTool;
import com.aof.mcinabox.jsonUtils.AnaliesMinecraftVersionJson;
import com.aof.mcinabox.jsonUtils.ModelMinecraftVersionJson;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import cosine.boat.MinecraftVersion;

public class ReadyToStart {
    //首先初始化所需要的全部实例变量
    //然后执行Minecraft完整性和正确性检查(包括游戏主文件，游戏依赖库，游戏资源文件) *根据用户选择
    //然后执行运行库完整性和架构正确性检查 *根据用户选择
    //然后执行java虚拟机位置获取，jvm参数的获取，Minecraft参数的获取
    //最后执行java虚拟机位置+jvm参数+Minecraft参数的拼接，并传递到Boat后端，实现启动。
    /*

     JVM参数以及含义:
        -server 效果:"启动耗时延长" "内存占用提高" "性能提高"
        -d64 含义:"强制启用64位JVM" 效果:"在64位设备上配合64位JVM获得性能提高"

        -Xmx 含义:"JVM最大堆内存" 注:"用户设置参数" 例:"-Xmx1024m"
        -Xms 含义:"JVM初始堆内存" 注:"建议设为256m" 例:"-Xms256m"
        -Xss 含义:"每个线程的栈大小" 注:"默认值为1m，更小的值意味着更多的线程数，反之也如此，建议512k" 例:"-Xss512k"
        -Xmn 含义:"设置年轻代大小" 注:"此值关系到GC，对性能影响大，建议设置为最大堆大小的3/8" 例:"-Xmn512m"

        -XX:+UseParNewGC 含义:"为年轻代使用并行回收" 效果:"提高GC回收效率"
        -XX:+UseConcMarkSweepGC 含义:"为年老代使用并行回收" 效果:"提高GC回收效率"
        -XX:+UseAdaptiveSizePolicy 含义:"启用自适应GC策略，在此条件下，GC各项参数都将自动调整"
        -XX:+UseG1GC 含义:"开启G1收集器"
        -XX:-UseAdaptiveSizePolicy 含义:"自动选择年轻代区大小和相应的Survivor区比例"
        -XX:-OmitStackTraceInFastThrow 含义:"省略异常栈信息从而快速抛出"

        -Dminecraft.launcher.brand=MCinaBox 含义:"当前启动器的名称"
        -Dminecraft.launcher.version= 含义:"当前启动器的版本"
        -Dlog4j.configurationFile=<文件路径>\client-1.12.xml 含义:"游戏日志配置文件"

        -cp 含义:"后接当前版本的游戏主文件及普通库文件的路径，用;隔开"
     Minecraft参数以及含义:注意顺序
        <主类名> 含义:"一般为net.minecraft.client.main.Main 或 net.minecraft.launchwrapper.Launch"
        --username 含义:"后接用户名" 例:"--username Steve"
        --version 含义:"后接游戏版本"
        --gameDir 含义:"后接游戏路径"
        --assetsDir 含义:"后接资源文件路径"
        --assetIndex 含义:"后接资源索引版本"
        --uuid 含义:"后接用户uuid"
        --accessToken 含义:"后接登陆令牌"
        --userType 含义:"后接用户类型"
        --versionType 含义:"后接版本类型"
    */

    /**【执行启动游戏】**/
    public void StartGame(){
        /*if(isCheckGame){
            if(!CheckGame()){
                return;
            }
        }
        if (isCheckFormat){
            if(!CheckFramework()){
                return;
            }
        }*/
        String[] CMD = MakeStartCmd();
        for (String arg : CMD){
            System.out.print(arg+" ");
            Log.e("StartGame",arg);
        }

    }

    public ReadyToStart(String MCinaBox_Version,String MCinaBox_HomePath,String MCinaBox_privatePath,String versionId){
        this.MCinaBox_Version = MCinaBox_Version;
        this.MCinaBox_HomePath = MCinaBox_HomePath;
        this.MCinaBox_privatePath = MCinaBox_privatePath;
        DownloadMinecraft PathTool = new DownloadMinecraft(MCinaBox_HomePath,MCinaBox_privatePath);
        minecraft_home_path = PathTool.getMINECRAFT_DIR();
        minecraft_assets_path = PathTool.getMINECRAFT_ASSETS_DIR();
        minecraft_version_path = PathTool.getMINECRAFT_VERSION_DIR();
        minecraft_libraries_path = PathTool.getMINECRAFT_LIBRARIES_DIR();
        launcherSetting = GetLauncherSettingFromFile();
        maxMemory = launcherSetting.getConfigurations().getMaxMemory();
        isCheckGame = !launcherSetting.getConfigurations().isNotCheckGame();
        isCheckFormat = !launcherSetting.getConfigurations().isNotCheckJvm();
        versionSetting = (new AnaliesMinecraftVersionJson()).getModelMinecraftVersionJson(minecraft_version_path + versionId + "/" + versionId + ".json");
    }

    //
    private String runtimePath = "/data/user/0/cosine.boat/app_runtime"; //**
    private String MCinaBox_Version; //*
    private String MCinaBox_HomePath; //*
    private String MCinaBox_privatePath; //*
    private String minecraft_home_path;
    private String minecraft_version_path;
    private String minecraft_assets_path;
    private String minecraft_libraries_path;
    //初始化该类必须要传入MCinaBox的前端设置
    LauncherSettingModel launcherSetting;
    int maxMemory;
    boolean isCheckGame;
    boolean isCheckFormat;
    //根据传入的versionSetting来配置Minecraft信息
    ModelMinecraftVersionJson versionSetting;


    /**【检查Minecraft游戏文件的完整性】**/
    //如果不完整就返回false
    //如果完整就返回true
    public boolean CheckGame(){
        boolean isOK = true;
        //检查依赖库
        ModelMinecraftVersionJson.DependentLibrary[] libraries = versionSetting.getLibraries();
        for(ModelMinecraftVersionJson.DependentLibrary targetLibrary : libraries){
            if(targetLibrary.getDownloads().getArtifact() != null && FileTool.isFileExists(minecraft_libraries_path + targetLibrary.getDownloads().getArtifact().getPath())){
                // file check pass.
            }else{
                isOK = false;
            }
        }

        //检查Jar主文件
        if(FileTool.isFileExists(minecraft_version_path + versionSetting.getId() + "/" +versionSetting.getId() + ".json")){
            // file check pass.
        }else{
            isOK =false;
        }

        //检查资源主文件
        //TODO:暂时不添加资源文件的检查

        return isOK;
    }

    /**【检查架构兼容性】**/
    public boolean CheckFramework(){
        String abi = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            abi = Build.CPU_ABI;
        } else {
            abi = Build.SUPPORTED_ABIS[0];
        }
        //TODO:暂时不知道会输出什么
        return true;
    }

    /**【获得启动参数】**/
    public String[] MakeStartCmd(){
        //java虚拟机的路径
        String Java_Args = runtimePath + "/j2re-image/bin/java";

        //设定JVM参数
        ArrayList<String> JVM_Args = new ArrayList<String>();
        String JVM__minecraft_client_jar = "-Dminecraft.client.jar=" + minecraft_version_path + versionSetting.getId() + "/" + versionSetting.getId() + ".jar";
        String JVM_server = "-server";
        String JVM_Xmx = "-Xmx" + launcherSetting.getConfigurations().getMaxMemory() + "m";
        String JVM_Xms = "-Xms128m";
        String JVM_UseG1GC = "-XX:+UseG1GC";
        String JVM_UseAdaptiveSizePolicy = "-XX:-UseAdaptiveSizePolicy";
        String JVM_OmitStackTraceInFastThrow = "-XX:-OmitStackTraceInFastThrow";
        String JVM_minecraft_launcher_band = "-Dminecraft.launcher.brand=MCinaBox";
        String JVM_minecraft_launcher_version = "-Dminecraft.launcher.version=" + MCinaBox_Version;
        String JVM_java_library_path = "-Djava.library.path=" + runtimePath + "/j2re-image/lib/aarch32/jli:" + runtimePath + "/j2re-image/lib/aarch32:" + runtimePath;
        String JVM_ExtraArgs = launcherSetting.getConfigurations().getJavaArgs();
        String JVM_ClassPath = "-cp";

        //注意加入list时的顺序
        JVM_Args.add(JVM__minecraft_client_jar);
        JVM_Args.add(JVM_server);
        JVM_Args.add(JVM_Xmx);
        JVM_Args.add(JVM_Xms);
        JVM_Args.add(JVM_UseG1GC);
        JVM_Args.add(JVM_UseAdaptiveSizePolicy);
        JVM_Args.add(JVM_OmitStackTraceInFastThrow);
        JVM_Args.add(JVM_minecraft_launcher_band);
        JVM_Args.add(JVM_minecraft_launcher_version);
        JVM_Args.add(JVM_java_library_path);
        JVM_Args.add(JVM_ExtraArgs);

        ArrayList<String> DependentLibrariesPaths = new ArrayList<String>();
        String temp ="";
        ModelMinecraftVersionJson.DependentLibrary[] libraries = versionSetting.getLibraries();
        ArrayList<ModelMinecraftVersionJson.DependentLibrary> libraries_copy = new ArrayList<ModelMinecraftVersionJson.DependentLibrary>();
        for(ModelMinecraftVersionJson.DependentLibrary targetLibrary : libraries){
            if(targetLibrary.getDownloads().getArtifact() != null){
                libraries_copy.add(targetLibrary);
            }
        }
        for (int i = 0;i < libraries_copy.size();i++){
            ModelMinecraftVersionJson.DependentLibrary targetLibrary = libraries_copy.get(i);
            if(i < libraries_copy.size() -1){
                temp = temp +  minecraft_libraries_path + targetLibrary.getDownloads().getArtifact().getPath() + ":";
            }else{
                temp = temp + minecraft_libraries_path + targetLibrary.getDownloads().getArtifact().getPath();
            }
        }
        JVM_ClassPath = JVM_ClassPath + temp;
        JVM_Args.add(JVM_ClassPath);


        //设定Minecraft参数
        ArrayList<String> Minecraft_Args = new ArrayList<String>();
        String Minecraft_MainClass = versionSetting.getMainClass();
        String MinecraftExtraArgs = launcherSetting.getConfigurations().getMinecraftArgs();
        String Minecraft_arguements = "";
            //首先要判断version是1.13.1之前的结构还是1.13.1之后的结构,用于处理两种不同的arguement结构
        if(versionSetting.getMinecraftArguments() == null){
            //这是1.13.1以及之后的处理方法
            //TODO:有时间就把这里写完
        }else{
            //这是1.13.1之前的处理方法
            Minecraft_arguements = ConvertJsStringModleToJavaStringModle(versionSetting.getMinecraftArguments());
        }

        Minecraft_Args.add(Minecraft_MainClass);
        Minecraft_Args.add(Minecraft_arguements);
        Minecraft_Args.add(MinecraftExtraArgs);

        //获得总命令
        ArrayList<String> CommandTemp = new ArrayList<String>();
        CommandTemp.add(Java_Args);
        for(String arg : JVM_Args) {
            CommandTemp.add(arg);
        }
        for (String arg : Minecraft_Args){
            CommandTemp.add(arg);
        }
        String[] Command = new String[CommandTemp.size()];
        for(int i = 0; i < Command.length;i++){
            Command[i] = CommandTemp.get(i);
        }

        return Command;

    }

    /**【将包含JS字符串占位符的字符串转化为转义后的Java字符串】**/
    public String ConvertJsStringModleToJavaStringModle(String mString){
        String JsString = mString;
        String JavaString;
        String tempString = "";
        HashMap<String , String> ArgsMap = new HashMap<String ,String>();
        LauncherSettingModel.Accounts account = GetUserFromLauncherSetting();

        //需要转义的键名-键值
        ArgsMap.put("{auth_player_name}",account.getUsername());
        ArgsMap.put("{auth_uuid}",account.getUuid());
        ArgsMap.put("{auth_access_token}","0");
        ArgsMap.put("{user_properties}","{}");
        ArgsMap.put("{user_type}","mojang");
        ArgsMap.put("{assets_index_name}",versionSetting.getAssets());
        ArgsMap.put("{assets_root}",minecraft_assets_path);
        ArgsMap.put("{game_directory}",minecraft_home_path);
        ArgsMap.put("{version_name}","/'" + "MCinaBox " + MCinaBox_Version + "/'");
        ArgsMap.put("{version_type}",versionSetting.getType());

        for(int i = 0;i < JsString.length();i++){
            if(JsString.charAt(i) == '$'){
                String tempString2 = "";
                do{
                    i++;
                    tempString2 = tempString2 + JsString.charAt(i);
                }while(JsString.charAt(i) != '}');
                tempString = tempString + ArgsMap.get(tempString2);
                Log.e("StartGameCheck",tempString2);
            }else{
                tempString = tempString + JsString.charAt(i);
            }
        }

        JavaString = tempString;
        return JavaString;
    }

    /**【获取用户信息】**/
    public LauncherSettingModel.Accounts GetUserFromLauncherSetting(){

        LauncherSettingModel.Accounts[] accounts = launcherSetting.getAccounts();
        for(LauncherSettingModel.Accounts targetAccount : accounts) {
            if (targetAccount.isSelected()) {
                return targetAccount;
            }
        }
        return null;
    }

    /**【获取启动器设置信息】**/
    public LauncherSettingModel GetLauncherSettingFromFile(){
        Gson gson = new Gson();
        InputStream inputStream;
        Reader reader = null;
        LauncherSettingModel launcherSetting;
        File configFile = new File(MCinaBox_HomePath + "/mcinabox.json");

        try {
            inputStream = new FileInputStream(configFile);
            reader = new InputStreamReader(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        launcherSetting = new Gson().fromJson(reader, LauncherSettingModel.class);
        return launcherSetting;
    }

}
