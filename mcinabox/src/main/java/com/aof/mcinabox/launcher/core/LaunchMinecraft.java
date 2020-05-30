package com.aof.mcinabox.launcher.core;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.ArgsJson;
import com.aof.mcinabox.launcher.json.SettingJson;
import com.aof.mcinabox.utils.PathTool;
import com.aof.mcinabox.minecraft.json.VersionJson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.aof.mcinabox.DataPathManifest.*;

public class LaunchMinecraft {

    private String runtimePath = RUNTIME_HOME;
    private String minecraft_home_path;
    private String minecraft_assets_path;
    private String minecraft_version_path;
    private String minecraft_libraries_path;
    private Context mContext;
    private SettingJson setting;
    private VersionJson minecraftJson;
    private VersionJson apiJson;
    private String keyboardLayout;
    private boolean isCheckGame;
    private boolean isCheckFormat;
    private boolean hasAPI;
    private String apiID;
    private String minecraftID;
    private String[] commands;

    public LaunchMinecraft(Context context){
        mContext = context;
        setting = com.aof.mcinabox.launcher.JsonUtils.getSettingFromFile(MCINABOX_FILE_JSON);
        PathTool pathTool = new PathTool(setting.getLocalization(),true);
        minecraft_home_path = pathTool.getMINECRAFT_DIR();
        minecraft_assets_path = pathTool.getMINECRAFT_ASSETS_DIR();
        minecraft_version_path = pathTool.getMINECRAFT_VERSION_DIR();
        minecraft_libraries_path = pathTool.getMINECRAFT_LIBRARIES_DIR();
        preInitite();
    }

    public void initateWithAPI(){
        this.apiID = setting.getLastVersion();
        this.hasAPI = true;
        this.apiJson = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(minecraft_version_path,apiID));
        this.minecraftID = apiJson.getInheritsFrom();
        this.minecraftJson = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(minecraft_version_path,minecraftID));
        this.keyboardLayout = setting.getKeyboard();
        this.isCheckFormat = !setting.getConfigurations().isNotCheckJvm();
        this.isCheckGame = !setting.getConfigurations().isNotCheckGame();
    }

    public void initateNormal(){
        this.hasAPI = false;
        this.minecraftID = setting.getLastVersion();
        this.minecraftJson = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(minecraft_version_path,minecraftID));
        this.keyboardLayout = setting.getKeyboard();
        this.isCheckFormat = !setting.getConfigurations().isNotCheckJvm();
        this.isCheckGame = !setting.getConfigurations().isNotCheckGame();
    }

    private void preInitite(){
        String id = setting.getLastVersion();
        VersionJson json = com.aof.mcinabox.minecraft.JsonUtils.getVersionFromFile(minecraft_version_path + id + "/" + id + ".json");
        if(getApiState(json)){
            initateWithAPI();
        }else{
            initateNormal();
        }
    }

    //Get the state of API
    //If this version contains a API, the method will return true.
    private boolean getApiState(VersionJson json){
        return json.getInheritsFrom() != null;
    }

    private boolean checkGame(){
        boolean pass = true;
        //检查API
        if(hasAPI){
            if(!checkVersion(apiID)){
                pass = false;
                Toast.makeText(mContext, mContext.getString(R.string.tips_gamecheck_jar_notfound) + " " + apiID, Toast.LENGTH_SHORT).show();
            }
        }
        //检查Version
        if(!checkVersion(minecraftID)){
            pass = false;
            Toast.makeText(mContext, mContext.getString(R.string.tips_gamecheck_version_notfound) + " " + minecraftID, Toast.LENGTH_SHORT).show();
        }
        //检查完整性
        if(isCheckGame){
            if(hasAPI){
                if(! checkLibraries(apiJson) || ! checkLibraries(minecraftJson) || ! checkAssets(minecraftJson)){
                    pass = false;
                    Toast.makeText(mContext, mContext.getString(R.string.tips_gamecheck_file_notfull), Toast.LENGTH_SHORT).show();
                }
            }else{
                if(! checkLibraries(minecraftJson) || ! checkAssets(minecraftJson)){
                    pass = false;
                    Toast.makeText(mContext, mContext.getString(R.string.tips_gamecheck_file_notfull), Toast.LENGTH_SHORT).show();
                }
            }
        }

        //检查架构
        if(isCheckFormat){
            if(! checkFramework()){
                pass = false;
            }
        }
        return pass;
    }

    private boolean checkVersion(String id) {
        File json = new File(Utils.getJsonAbsPath(minecraft_version_path,id));
        File jar = new File(Utils.getJarAbsPath(minecraft_version_path,id));
        return json.exists() && jar.exists();
    }

    private boolean checkLibraries(VersionJson version){
        if(version == null){
            return false;
        }
        VersionJson.DependentLibrary[] libraries = version.getLibraries();
        for (VersionJson.DependentLibrary targetLibrary : libraries) {
            if (!filterLibraries(targetLibrary.getName())) {
                File file = new File(GetLibrariesPath(targetLibrary.getName()));
                if (!file.exists()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkAssets(VersionJson version){
        //TODO:检查资源文件
        return true;
    }

    private boolean checkFramework() {
        String abi = null;
        abi = Build.SUPPORTED_ABIS[0];
        //TODO:架构检查
        return true;
    }


    /**
     * 【执行启动游戏】
     **/
    public void StartGame() {
        commands = makeCommand(hasAPI);

        if(checkGame()){
            return;
        }

        //输出测试
        for (String arg : commands) {
            System.out.print(arg + " ");
            Log.e("StartGame", arg);
        }

        Intent intent = makeIntent();
        mContext.startActivity(intent);

    }

    private String[] makeCommand(boolean forgeMode) {
        //java虚拟机的路径
        String Java_Args = runtimePath + "/j2re-image/bin/java";

        //设定JVM参数
        ArrayList<String> JVM_Args = new ArrayList<String>();
        String JVM_client = "-client";
        String JVM_Xmx = "-Xmx" + setting.getConfigurations().getMaxMemory() + "m";
        String JVM_Xms = "-Xms128m";
        String JVM_java_library_path;
        String JVM_lwjgl_debug_true = "-Dorg.lwjgl.util.Debug=true";
        String JVM_lwjgl_debugloader_true = "-Dorg.lwjgl.util.DebugLoader=true";
        String JVM_ExtraArgs = setting.getConfigurations().getJavaArgs();
        String JVM_ClassPath = "-cp";
        String JVM_ClassPath_Info;
        String JVM_ClassPath_Runtime;

        if (minecraftJson.getMinimumLauncherVersion() >= 21) {
            //这是1.13.1以及之后的处理方法
            JVM_ClassPath_Runtime = runtimePath + "/lwjgl3/lwjgl-jemalloc.jar:" + runtimePath + "/lwjgl3/lwjgl-tinyfd.jar:" + runtimePath + "/lwjgl3/lwjgl-opengl.jar:" + runtimePath + "/lwjgl3/lwjgl-openal.jar:" + runtimePath + "/lwjgl3/lwjgl-glfw.jar:" + runtimePath + "/lwjgl3/lwjgl-stb.jar:" + runtimePath + "/lwjgl3/lwjgl.jar:";
            JVM_java_library_path = "-Djava.library.path=" + runtimePath + "/j2re-image/lib/aarch32/jli:" + runtimePath + "/j2re-image/lib/aarch32:" + runtimePath + "/lwjgl3:" + runtimePath;
        } else {
            //这是1.13.1之前的处理方法
            JVM_ClassPath_Runtime = runtimePath + "/lwjgl2/lwjgl_util.jar:" + runtimePath + "/lwjgl2/lwjgl.jar:";
            JVM_java_library_path = "-Djava.library.path=" + runtimePath + "/j2re-image/lib/aarch32/jli:" + runtimePath + "/j2re-image/lib/aarch32:" + runtimePath + "/lwjgl2:" + runtimePath;
        }

        //注意加入list时的顺序
        if(! JVM_ExtraArgs.equals("")){
            JVM_Args.addAll(Arrays.asList(SplitMinecraftArgument(JVM_ExtraArgs)));
        }
        JVM_Args.add(JVM_client);
        JVM_Args.add(JVM_Xmx);
        JVM_Args.add(JVM_Xms);
        JVM_Args.add(JVM_java_library_path);
        JVM_Args.add(JVM_lwjgl_debug_true);
        JVM_Args.add(JVM_lwjgl_debugloader_true);

        if (hasAPI) {
            String jarPath;
            File jar = new File(Utils.getJarAbsPath(minecraft_version_path,apiID));
            if(jar.exists()){
                jarPath = Utils.getJarAbsPath(minecraft_version_path,apiID);
            }else{
                jarPath = Utils.getJarAbsPath(minecraft_version_path,minecraftID);
            }
            JVM_ClassPath_Info = JVM_ClassPath_Runtime + GetClassPathArgs(apiJson.getLibraries()) + GetClassPathArgs(apiJson.getLibraries()) + jarPath;
        } else {
            String jarPath = Utils.getJarAbsPath(minecraft_version_path,minecraftID);
            JVM_ClassPath_Info = JVM_ClassPath_Runtime + GetClassPathArgs(minecraftJson.getLibraries()) + jarPath;
        }

        JVM_Args.add(JVM_ClassPath);
        JVM_Args.add(JVM_ClassPath_Info);

        //设定Minecraft参数
        ArrayList<String> Minecraft_Args = new ArrayList<String>();
        String Minecraft_MainClass = minecraftJson.getMainClass();
        String MinecraftExtraArgs = setting.getConfigurations().getMinecraftArgs();
        String MinecraftWindowArgs = "--width ${window_width} --height ${window_height}";
        String Minecraft_arguements = "";

        if (minecraftJson.getMinimumLauncherVersion() >= 21) {
            //这是1.13.1以及之后的处理方法
            Minecraft_arguements = ConvertJsStringModleToJavaStringModle(ConvertArgumentsToMinecraftArguments(minecraftJson));
        } else {
            //这是1.13.1之前的处理方法
            Minecraft_arguements = ConvertJsStringModleToJavaStringModle(minecraftJson.getMinecraftArguments());
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
        for (int i = 0; i < Command.length; i++) {
            Command[i] = CommandTemp.get(i);
        }

        return Command;

    }

    /**
     * 【将包含JS字符串占位符的字符串转化为转义后的Java字符串】
     **/
    private String ConvertJsStringModleToJavaStringModle(String mString) {
        String JsString = mString;
        String JavaString;
        String tempString = "";
        HashMap<String, String> ArgsMap = new HashMap<String, String>();
        SettingJson.Accounts account = GetUserFromLauncherSetting();

        //需要转义的键名-键值
        ArgsMap.put("{auth_player_name}", account.getUsername());
        ArgsMap.put("{auth_uuid}", account.getUuid());
        ArgsMap.put("{auth_access_token}", account.getAccessToken());
        ArgsMap.put("{auth_session}", "mojang");
        ArgsMap.put("{user_properties}", "{}");
        ArgsMap.put("{user_type}", "mojang");
        ArgsMap.put("{assets_index_name}", minecraftJson.getAssets());
        ArgsMap.put("{assets_root}", minecraft_assets_path);
        ArgsMap.put("{game_directory}", minecraft_home_path);
        ArgsMap.put("{game_assets}", minecraftJson.getAssets());
        ArgsMap.put("{version_name}", "\"" + "MCinaBox-" + MCINABOX_VERSION + "\"");
        ArgsMap.put("{version_type}", minecraftJson.getType());
        ArgsMap.put("{window_width}", Integer.toString(mContext.getResources().getDisplayMetrics().widthPixels));
        ArgsMap.put("{window_height}", Integer.toString(mContext.getResources().getDisplayMetrics().heightPixels));


        for (int i = 0; i < JsString.length(); i++) {
            if (JsString.charAt(i) == '$') {
                String tempString2 = "";
                do {
                    i++;
                    tempString2 = tempString2 + JsString.charAt(i);
                } while (JsString.charAt(i) != '}');
                tempString = tempString + ArgsMap.get(tempString2);
                Log.e("StartGameCheck", tempString2);
            } else {
                tempString = tempString + JsString.charAt(i);
            }
        }

        JavaString = tempString;
        return JavaString;
    }

    /**
     * 【获取用户信息】
     **/
    private SettingJson.Accounts GetUserFromLauncherSetting() {

        SettingJson.Accounts[] accounts = setting.getAccounts();
        for (SettingJson.Accounts targetAccount : accounts) {
            if (targetAccount.isSelected()) {
                return targetAccount;
            }
        }
        return null;
    }

    /**
     * 【将ArgsModel对象序列化得到Intent】
     **/
    private Intent makeIntent() {
        ArgsJson argsModel = new ArgsJson();
        argsModel.setArgs(commands);
        argsModel.setKeyboardName(keyboardLayout);
        argsModel.setHome(minecraft_home_path);

        Intent intent;
        if (minecraftJson.getMinimumLauncherVersion() >= 21) {
            //这是1.13.1以及之后的处理方法
            intent = new Intent(mContext, cosine.boat.version3.LauncherActivity.class);
        } else {
            //这是1.13.1之前的处理方法
            intent = new Intent(mContext, cosine.boat.LauncherActivity.class);
        }
        intent.putExtra("LauncherConfig", argsModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    /**
     * 【将MC1.13的Arguments对象转化为MinecraftArguments字符串】
     **/
    private String ConvertArgumentsToMinecraftArguments(VersionJson json) {
        String minecraftarguments = "";
        for (int i = 0; i < json.getArguments().getGame().length; i++) {
            if (json.getArguments().getGame()[i] instanceof String) {
                if (i == json.getArguments().getGame().length - 1) {
                    minecraftarguments = minecraftarguments + json.getArguments().getGame()[i];
                } else {
                    minecraftarguments = minecraftarguments + json.getArguments().getGame()[i] + " ";
                }
            }
        }
        return minecraftarguments;
    }

    /**
     * 【以空格来分割MinecraftArgument为字符串数组】
     **/
    private String[] SplitMinecraftArgument(String Str) {
        return Str.split(" ");
    }

    /**
     * 【不加载lwjgl和glfw】
     **/
    private boolean filterLibraries(String name) {
        String packname = "";
        String[] libraries = {"lwjgl", "lwjgl_util", "lwjgl-platform",
                "lwjgl-egl", "lwjgl-glfw", "lwjgl-jemalloc",
                "lwjgl-openal", "lwjgl-opengl", "lwjgl-opengles",
                "lwjgl-stb", "lwjgl-tinyfd", "jinput-platform", "twitch-platform", "twitch-external-platform"};
        boolean result = false;
        int a = 0;
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == ':') {
                a = i + 1;
                break;
            }
        }
        for (; a < name.length(); a++) {
            if (name.charAt(a) != ':') {
                packname = packname + name.charAt(a);
            } else {
                break;
            }
        }
        for (String str : libraries) {
            if (str.equals(packname)) {
                return true;
            }
        }
        return false;
    }

    private String GetLibrariesPath(String name) {
        String packageName;
        String libraryName;
        String versionName;
        String filePath;

        String[] Name = name.split(":");
        packageName = Name[0];
        libraryName = Name[1];
        versionName = Name[2];

        String dirPath = minecraft_libraries_path;
        for (int i = 0; i < packageName.length(); i++) {
            if (packageName.charAt(i) == '.') {
                dirPath = dirPath + "/";
            } else {
                dirPath = dirPath + packageName.charAt(i);
            }
        }
        dirPath = dirPath + "/" + libraryName + "/" + versionName + "/";

        String fileName = "";
        fileName = libraryName + "-" + versionName + ".jar";

        filePath = dirPath + fileName;
        return filePath;
    }

    private String GetClassPathArgs(VersionJson.DependentLibrary[] libraries) {
        String cp = "";
        for (VersionJson.DependentLibrary library : libraries) {
            if (!filterLibraries(library.getName())) {
                cp = cp + GetLibrariesPath(library.getName()) + ":";
            }
        }
        return cp;
    }

}
