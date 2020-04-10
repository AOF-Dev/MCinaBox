package com.aof.mcinabox;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.aof.mcinabox.launcher.json.ArgsJson;
import com.aof.mcinabox.launcher.json.SettingJson;
import com.aof.mcinabox.utils.FileTool;
import com.aof.mcinabox.utils.PathTool;
import com.aof.mcinabox.minecraft.json.VersionJson;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static com.aof.mcinabox.DataPathManifest.*;

public class ReadyToStart {

    private boolean forceRootRuntime =false;
    private String[] CMD;
    private String MCHome;
    private Context context;
    private boolean forgeMode = false;

    /**【执行启动游戏】**/
    public void StartGame(){
        forgeMode = IsForgeMode();

        if(forgeMode){
            File json = new File(minecraft_version_path + versionSetting.getInheritsFrom() + "/" + versionSetting.getInheritsFrom() + ".json");
            File jar = new File(minecraft_version_path + versionSetting.getInheritsFrom() + "/" + versionSetting.getInheritsFrom() + ".jar");
            if(!json.exists() || !jar.exists()){
                Toast.makeText(context, context.getString(R.string.tips_gamecheck_version_notfound) + " " + versionSetting.getInheritsFrom(), Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            File jar = new File(minecraft_version_path + versionSetting.getId() + "/" + versionSetting.getId() + ".jar");
            if(!jar.exists()){
                Toast.makeText(context, context.getString(R.string.tips_gamecheck_jar_notfound) + " " + versionSetting.getInheritsFrom(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //若是forge模式，检查时会初始化versionSettingS
        boolean gameCheckResult = CheckGame(forgeMode);

        if(isCheckGame && !gameCheckResult){
            Toast.makeText(context, context.getString(R.string.tips_gamecheck_file_notfull), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isCheckFormat && !CheckFramework()){
            Toast.makeText(context, context.getString(R.string.tips_gamecheck_platform_uncorrect), Toast.LENGTH_SHORT).show();
            return;
        }
        CMD = MakeStartCmd(forgeMode);
        //输出测试
        for (String arg : CMD){
            System.out.print(arg+" ");
            Log.e("StartGame",arg);
        }
        Intent intent = MakePushIntent();
        context.startActivity(intent);

    }

    public ReadyToStart(Context context,String MCinaBox_Version,String DATA_PATH,String versionId,String KeyboardName){
        this.context = context;
        this.MCinaBox_Version = MCinaBox_Version;
        PathTool pathTool = new PathTool(DATA_PATH);
        minecraft_home_path = pathTool.getMINECRAFT_DIR();
        minecraft_assets_path = pathTool.getMINECRAFT_ASSETS_DIR();
        minecraft_version_path = pathTool.getMINECRAFT_VERSION_DIR();
        minecraft_libraries_path = pathTool.getMINECRAFT_LIBRARIES_DIR();
        launcherSetting = GetLauncherSettingFromFile();
        isCheckGame = !launcherSetting.getConfigurations().isNotCheckGame();
        isCheckFormat = !launcherSetting.getConfigurations().isNotCheckJvm();
        KeyboardFileName = KeyboardName;
        versionSetting = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(minecraft_version_path + versionId + "/" + versionId + ".json");
        if(IsForgeMode()){
            versionSettingS = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(minecraft_version_path + versionSetting.getInheritsFrom() + "/" + versionSetting.getInheritsFrom() + ".json");
        }
    }

    private String runtimePath = RUNTIME_HOME;
    private String MCinaBox_Version;
    private String KeyboardFileName;
    private String minecraft_home_path;
    private String minecraft_assets_path;
    private String minecraft_version_path;
    private String minecraft_libraries_path;
    //初始化该类必须要传入MCinaBox的前端设置
    private SettingJson launcherSetting;
    private boolean isCheckGame;
    private boolean isCheckFormat;
    //根据传入的versionSetting来配置Minecraft信息
    private VersionJson versionSetting;
    private VersionJson versionSettingS;


    /**【检查Minecraft游戏文件的完整性】**/
    //如果不完整就返回false
    //如果完整就返回true
    private boolean CheckGame(boolean isforge){
        boolean isOK = true;
        //检查forge
        if(isforge){
            File json = new File(minecraft_version_path + versionSetting.getInheritsFrom() + "/" + versionSetting.getInheritsFrom() + ".json");
            File jar = new File(minecraft_version_path + versionSetting.getInheritsFrom() + "/" + versionSetting.getInheritsFrom() + ".jar");
            if(!json.exists() || !jar.exists()){
                isOK = false;
            }else{
                //如果有forge，就先检查原版的依赖库,再检查forge的依赖库
                versionSettingS = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(minecraft_version_path + versionSetting.getInheritsFrom() + "/" + versionSetting.getInheritsFrom() + ".json");
                VersionJson.DependentLibrary[] libraries = versionSettingS.getLibraries();
                for(VersionJson.DependentLibrary targetLibrary : libraries){
                    if(!IsSpecialFile(targetLibrary.getName())){
                        File file = new File(GetLibrariesPath(targetLibrary.getName()));
                        if(!file.exists()){
                            isOK = false;
                        }
                    }
                }

            }
        }

        //检查依赖库
        VersionJson.DependentLibrary[] libraries = versionSetting.getLibraries();
        for(VersionJson.DependentLibrary targetLibrary : libraries){
            if(!IsSpecialFile(targetLibrary.getName())){
                File file = new File(GetLibrariesPath(targetLibrary.getName()));
                if(!file.exists()){
                    isOK = false;
                }
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
    private boolean CheckFramework(){
        String abi = null;
        abi = Build.SUPPORTED_ABIS[0];
        //TODO:暂时不知道会输出什么
        return true;
    }

    /**【获得启动参数】**/
    private String[] MakeStartCmd(boolean forgeMode){
        //java虚拟机的路径
        String Java_Args = runtimePath + "/j2re-image/bin/java";

        //设定JVM参数
        ArrayList<String> JVM_Args = new ArrayList<String>();
        String JVM_client = "-client";
        String JVM_Xmx = "-Xmx" + launcherSetting.getConfigurations().getMaxMemory() + "m";
        String JVM_Xms = "-Xms128m";
        String JVM_java_library_path;
        String JVM_lwjgl_debug_true = "-Dorg.lwjgl.util.Debug=true";
        String JVM_lwjgl_debugloader_true = "-Dorg.lwjgl.util.DebugLoader=true";
        String JVM_ExtraArgs = launcherSetting.getConfigurations().getJavaArgs();
        String JVM_ClassPath = "-cp";
        String JVM_ClassPath_Info;
        String JVM_ClassPath_Runtime;
        if(versionSetting.getMinimumLauncherVersion() >= 21){
            //这是1.13.1以及之后的处理方法
            JVM_ClassPath_Runtime = runtimePath + "/lwjgl3/lwjgl-jemalloc.jar:" + runtimePath + "/lwjgl3/lwjgl-tinyfd.jar:" + runtimePath + "/lwjgl3/lwjgl-opengl.jar:" + runtimePath + "/lwjgl3/lwjgl-openal.jar:" + runtimePath + "/lwjgl3/lwjgl-glfw.jar:" + runtimePath + "/lwjgl3/lwjgl-stb.jar:" + runtimePath + "/lwjgl3/lwjgl.jar:";
            JVM_java_library_path = "-Djava.library.path=" + runtimePath + "/j2re-image/lib/aarch32/jli:" + runtimePath + "/j2re-image/lib/aarch32:" + runtimePath + "/lwjgl3:" + runtimePath;
        }else{
            //这是1.13.1之前的处理方法
            JVM_ClassPath_Runtime = runtimePath + "/lwjgl2/lwjgl_util.jar:" + runtimePath + "/lwjgl2/lwjgl.jar:";
            JVM_java_library_path = "-Djava.library.path=" + runtimePath + "/j2re-image/lib/aarch32/jli:" + runtimePath + "/j2re-image/lib/aarch32:" + runtimePath + "/lwjgl2:" + runtimePath;
        }

        //注意加入list时的顺序
        //JVM_Args.addAll(Arrays.asList(SplitMinecraftArgument(JVM_ExtraArgs)));
        JVM_Args.add(JVM_client);
        JVM_Args.add(JVM_Xmx);
        JVM_Args.add(JVM_Xms);
        JVM_Args.add(JVM_java_library_path);
        JVM_Args.add(JVM_lwjgl_debug_true);
        JVM_Args.add(JVM_lwjgl_debugloader_true);

        ArrayList<String> DependentLibrariesPaths = new ArrayList<String>();

        if(forgeMode){
            File jar = new File(minecraft_version_path + versionSetting.getId() + "/" + versionSetting.getId() + ".jar");
            String jarPath;
            if(jar.exists()){
                jarPath = minecraft_version_path + versionSetting.getId() + "/" + versionSetting.getId() + ".jar";
            }else{
                jarPath = minecraft_version_path + versionSetting.getInheritsFrom() + "/" + versionSettingS.getInheritsFrom() + ".jar";
            }
            JVM_ClassPath_Info = JVM_ClassPath_Runtime + GetClassPathArgs(versionSetting.getLibraries()) + GetClassPathArgs(versionSettingS.getLibraries()) + jarPath;
        }else{
            JVM_ClassPath_Info = JVM_ClassPath_Runtime + GetClassPathArgs(versionSetting.getLibraries()) + minecraft_version_path + versionSetting.getId() + "/" + versionSetting.getId() + ".jar";
        }
        JVM_Args.add(JVM_ClassPath);
        JVM_Args.add(JVM_ClassPath_Info);

        //设定Minecraft参数
        ArrayList<String> Minecraft_Args = new ArrayList<String>();
        String Minecraft_MainClass = versionSetting.getMainClass();
        String MinecraftExtraArgs = launcherSetting.getConfigurations().getMinecraftArgs();
        String MinecraftWindowArgs = "--width ${window_width} --height ${window_height}";
        String Minecraft_arguements = "";
            //首先要判断version是1.13.1之前的结构还是1.13.1之后的结构,用于处理两种不同的arguement结构
        if(versionSetting.getMinimumLauncherVersion() >= 21){
            //这是1.13.1以及之后的处理方法
            Minecraft_arguements = ConvertJsStringModleToJavaStringModle(ConvertArgumentsToMinecraftArguments());
        }else{
            //这是1.13.1之前的处理方法
            Minecraft_arguements = ConvertJsStringModleToJavaStringModle(versionSetting.getMinecraftArguments());
        }

        Minecraft_Args.add(Minecraft_MainClass);
        Minecraft_Args.addAll(Arrays.asList(SplitMinecraftArgument(Minecraft_arguements)));
        Minecraft_Args.addAll(Arrays.asList(SplitMinecraftArgument(ConvertJsStringModleToJavaStringModle(MinecraftWindowArgs))));
        Minecraft_Args.addAll(Arrays.asList(SplitMinecraftArgument(MinecraftExtraArgs)));

        //获得总命令
        ArrayList<String> CommandTemp = new ArrayList<String>();
        CommandTemp.add(Java_Args);
        CommandTemp.addAll(JVM_Args);
        CommandTemp.addAll(Minecraft_Args);

        String[] Command = new String[CommandTemp.size()];
        for(int i = 0; i < Command.length;i++){
            Command[i] = CommandTemp.get(i);
        }

        return Command;

    }

    /**【将包含JS字符串占位符的字符串转化为转义后的Java字符串】**/
    private String ConvertJsStringModleToJavaStringModle(String mString){
        String JsString = mString;
        String JavaString;
        String tempString = "";
        HashMap<String , String> ArgsMap = new HashMap<String ,String>();
        SettingJson.Accounts account = GetUserFromLauncherSetting();

        //需要转义的键名-键值
        ArgsMap.put("{auth_player_name}",account.getUsername());
        ArgsMap.put("{auth_uuid}",account.getUuid());
        ArgsMap.put("{auth_access_token}",account.getAccessToken());
        ArgsMap.put("{auth_session}","mojang");
        ArgsMap.put("{user_properties}","{}");
        ArgsMap.put("{user_type}","mojang");
        ArgsMap.put("{assets_index_name}",versionSetting.getAssets());
        ArgsMap.put("{assets_root}",minecraft_assets_path);
        ArgsMap.put("{game_directory}",minecraft_home_path);
        ArgsMap.put("{game_assets}",versionSetting.getAssets());
        ArgsMap.put("{version_name}","\"" + "MCinaBox-" + MCinaBox_Version + "\"");
        ArgsMap.put("{version_type}",versionSetting.getType());
        ArgsMap.put("{window_width}",Integer.toString(context.getResources().getDisplayMetrics().widthPixels));
        ArgsMap.put("{window_height}",Integer.toString(context.getResources().getDisplayMetrics().heightPixels));


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
    private SettingJson.Accounts GetUserFromLauncherSetting(){

        SettingJson.Accounts[] accounts = launcherSetting.getAccounts();
        for(SettingJson.Accounts targetAccount : accounts) {
            if (targetAccount.isSelected()) {
                return targetAccount;
            }
        }
        return null;
    }

    /**【获取启动器设置信息】**/
    private SettingJson GetLauncherSettingFromFile(){
        Gson gson = new Gson();
        InputStream inputStream;
        Reader reader = null;
        SettingJson launcherSetting;
        File configFile = new File(MCINABOX_FILE_JSON);

        try {
            inputStream = new FileInputStream(configFile);
            reader = new InputStreamReader(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("LoadSetting",e.toString());
        }

        launcherSetting = new Gson().fromJson(reader, SettingJson.class);
        return launcherSetting;
    }

    /**【根据启动器设置初始化必要的变量】**/
    private void InitSomeSettngs(){
        SettingJson Setting = GetLauncherSettingFromFile();
        forceRootRuntime = true;

        if (Setting.getLocalization().equals("public")){
            MCHome = MCINABOX_DATA_PUBLIC;
        }else if(Setting.getLocalization().equals("private")){
            MCHome = MCINABOX_DATA_PRIVATE;
        }
    }

    /**【将ArgsModel对象序列化得到Intent】**/
    private Intent MakePushIntent(){
        InitSomeSettngs();
        ArgsJson argsModel = new ArgsJson();
        argsModel.setArgs(CMD);
        argsModel.setForceRootRuntime(forceRootRuntime);
        argsModel.setKeyboardName(KeyboardFileName);
        argsModel.setHome(MCHome);

        Intent intent ;
        if(versionSetting.getMinimumLauncherVersion() >= 21){
            //这是1.13.1以及之后的处理方法
            intent= new Intent(context, cosine.boat.version3.LauncherActivity.class);
        }else{
            //这是1.13.1之前的处理方法
            intent= new Intent(context, cosine.boat.LauncherActivity.class);
        }
        intent.putExtra("LauncherConfig",argsModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    /**【将MC1.13的Arguments对象转化为MinecraftArguments字符串】**/
    private String ConvertArgumentsToMinecraftArguments(){
        String minecraftarguments = "";
        for(int i = 0;i < versionSetting.getArguments().getGame().length ; i++ ){
            if(versionSetting.getArguments().getGame()[i] instanceof String) {
                if (i == versionSetting.getArguments().getGame().length - 1) {
                    minecraftarguments = minecraftarguments + versionSetting.getArguments().getGame()[i];
                } else {
                    minecraftarguments = minecraftarguments + versionSetting.getArguments().getGame()[i] + " ";
                }
            }
        }
        return minecraftarguments;
    }

    /**【以空格来分割MinecraftArgument为字符串数组】**/
    private String[] SplitMinecraftArgument(String Str){
        return Str.split(" ");
    }

    /**【不加载lwjgl和glfw】**/
    private boolean IsSpecialFile(String name){
        String packname = "";
        String[] libraries = {"lwjgl","lwjgl_util","lwjgl-platform",
                "lwjgl-egl","lwjgl-glfw","lwjgl-jemalloc",
                "lwjgl-openal","lwjgl-opengl","lwjgl-opengles",
                "lwjgl-stb","lwjgl-tinyfd", "jinput-platform", "twitch-platform","twitch-external-platform"};
        boolean result = false;
        int a =0;
        for(int i = 0; i < name.length() ;i++){
            if(name.charAt(i) == ':'){
                a = i + 1;
                break;
            }
        }
        for(;a < name.length() ;a++){
            if (name.charAt(a) != ':'){
                packname = packname + name.charAt(a);
            }else{
                break;
            }
        }
        for(String str : libraries){
            if(str.equals(packname)){
                return true;
            }
        }
        return false;
    }

    private String GetLibrariesPath(String name){
        String packageName;
        String libraryName;
        String versionName;
        String filePath;

        String[] Name = name.split(":");
        packageName = Name[0];
        libraryName = Name[1];
        versionName = Name[2];

        String dirPath = minecraft_libraries_path ;
        for(int i =0;i < packageName.length();i++){
            if(packageName.charAt(i) == '.'){
                dirPath = dirPath + "/";
            }else{
                dirPath = dirPath + packageName.charAt(i);
            }
        }
        dirPath = dirPath + "/" + libraryName + "/" + versionName + "/";

        String fileName = "";
        fileName = libraryName + "-" + versionName + ".jar";

        filePath = dirPath + fileName;
        return filePath;
    }

    private String GetClassPathArgs(VersionJson.DependentLibrary[] libraries){
        String cp = "";
        for(VersionJson.DependentLibrary library : libraries){
            if(!IsSpecialFile(library.getName())){
                cp = cp + GetLibrariesPath(library.getName()) + ":";
            }
        }
        return cp;
    }

    private boolean IsForgeMode(){
        if(versionSetting.getInheritsFrom() != null){
            return true;
        }else{
            return false;
        }
    }

}
