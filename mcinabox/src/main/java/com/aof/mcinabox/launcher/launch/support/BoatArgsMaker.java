package com.aof.mcinabox.launcher.launch.support;

import android.content.Context;
import android.util.Log;
import com.aof.mcinabox.definitions.manifest.AppManifest;
import com.aof.mcinabox.definitions.models.BoatArgs;
import com.aof.mcinabox.launcher.launch.LaunchManager;
import com.aof.mcinabox.launcher.runtime.RuntimeManager;
import com.aof.mcinabox.launcher.runtime.support.Definitions;
import com.aof.mcinabox.launcher.runtime.support.RuntimePackInfo;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.minecraft.JsonUtils;
import com.aof.mcinabox.minecraft.json.VersionJson;
import com.aof.utils.dialog.support.DialogSupports;
import com.aof.utils.dialog.DialogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static com.aof.mcinabox.definitions.manifest.AppManifest.MCINABOX_VERSION_NAME;

public class BoatArgsMaker {

    private VersionJson version;
    private VersionJson forge;
    private Context mContext;
    private SettingJson mSetting;
    private RuntimePackInfo mRuntime;
    private RuntimePackInfo.Manifest runtimeManifest;
    private RuntimePackInfo.Manifest[] manifests;
    private LaunchManager mLaunchManager;
    private BoatArgs mArgs;

    private final static String TAG = "BoatArgsMaker";

    public BoatArgsMaker(Context context, SettingJson setting, LaunchManager launchmanager){
        this.mContext = context;
        this.mSetting = setting;
        this.mLaunchManager = launchmanager;
        this.mRuntime = RuntimeManager.getPackInfo();
    }

    public BoatArgs getBoatArgs(){
        return this.mArgs;
    }

    public void setup(String id){
        mLaunchManager.brige_setProgressText("正在初始化启动参数...");
        //读入version的json信息
        version = JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(id));
        //判断读入的version是否使用了API,如果使用了就先交换变量，再读入原版的version信息
        if(version.getInheritsFrom() != null){
            forge = version;
            version = JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(forge.getInheritsFrom()));
        }
        //初始化运行库的清单对象
        manifestSelecter();
    }

    private void onSetupFinished(){
        //等待setup完成之后回调LaunhcerManager的方法来继续执行参数拼接
        mLaunchManager.launchMinecraft(mSetting, LaunchManager.LAUNCH_PARM_MAKE);
    }

    private void manifestSelecter(){
        manifests = RuntimeManager.getRutinmeInfoManifest(version);

        if(manifests.length == 1 && !mSetting.getConfigurations().isAlwaysChoiceRuntimeManifest()){
            //如果只有一种可选策略，并且设置中仅用了AlwaysChoiceRuntimeMainfest，则不显示选择对话框
            onManifestSelected(0);
            return;
        }

        String[] items = new String[manifests.length];
        for(int a = 0; a < items.length ; a++){
            items[a] = manifests[a].name;
        }
        DialogUtils.createItemsChoiceDialog(mContext,"请选择运行库加载策略:",null,"取消",false,items,new DialogSupports(){
            @Override
            public void runWhenItemsSelected(int pos){
                onManifestSelected(pos);
            }
            @Override
            public void runWhenNegative(){
                mLaunchManager.brige_exitWithError("用户取消了操作。");
            }
        });
    }

    private void onManifestSelected(int pos){
        //等待manifestSelecter的回调，然后初始化清单
        this.runtimeManifest = manifests[pos];
        //回调setup完成
        onSetupFinished();
    }

    public void make(){
        mLaunchManager.brige_setProgressText("正在生成启动参数...");
        //执行参数拼接
        try {
            this.mArgs =  new BoatArgs()
                    .setArgs(getArgs())
                    .setJava_home(AppManifest.BOAT_RUNTIME_HOME + "/" + runtimeManifest.jre_home)
                    .setGamedir(AppManifest.MINECRAFT_HOME)
                    .setDebug(mSetting.getConfigurations().isEnableDebug())
                    .setShared_libraries(this.getSharedLibrariesPaths());
            mLaunchManager.launchMinecraft(mSetting,LaunchManager.LAUNCH_GAME);
        }catch (Exception e){
            e.printStackTrace();
            mLaunchManager.brige_exitWithError("出现未知错误，启动参数生成失败！");
        }
    }

    private String[] getArgs(){

        //初始化各参数
        String JVM_java = AppManifest.BOAT_RUNTIME_HOME + "/" + runtimeManifest.jre_home + "/bin/java";
        String JVM_mode = "-" + runtimeManifest.jvmMode;
        String JVM_Xmx = "-Xmx" + mSetting.getConfigurations().getMaxMemory() + "m";
        String JVM_Xms = "-Xms128m";
        String JVM_java_library_path = this.getJava_library_path();
        //String JVM_lwjgl_debug_true = "-Dorg.lwjgl.util.Debug=true";
        //String JVM_lwjgl_debugloader_true = "-Dorg.lwjgl.util.DebugLoader=true";
        String JVM_ExtraArgs = mSetting.getConfigurations().getJavaArgs();
        String JVM_ClassPath = "-cp";
        String JVM_ClassPath_info = this.getClasspath();
        String Minecraft_MainClass = this.getMainClass();
        String MinecraftExtraArgs = mSetting.getConfigurations().getMinecraftArgs();
        String MinecraftWindowArgs = this.ConvertJsStringModleToJavaStringModle("--width ${window_width} --height ${window_height}");
        String Minecraft_Args = this.getMinecraftArgs();

        //对参数进行拼接
        ArrayList<String> tmp = new ArrayList<>();
        tmp.add(JVM_java);
        tmp.add(JVM_mode);
        tmp.add(JVM_Xmx);
        tmp.add(JVM_Xms);
        tmp.add(JVM_java_library_path);
        //tmp.addAll(Arrays.asList(JVM_ExtraArgs.split(" ")));
        tmp.add(JVM_ClassPath);
        tmp.add(JVM_ClassPath_info);
        tmp.addAll(Arrays.asList(Minecraft_MainClass.split(" ")));
        //tmp.addAll(Arrays.asList(MinecraftExtraArgs.split(" ")));
        tmp.addAll(Arrays.asList(MinecraftWindowArgs.split(" ")));
        tmp.addAll(Arrays.asList(Minecraft_Args.split(" ")));

        //过滤空的元素并返回参数数组
        for(int a = 0 ; a < tmp.size() ; a++){
            if(tmp.get(a).equals("")){
                tmp.remove(a);
                a--;
            }
        }
        String[] result = new String[tmp.size()];
        for(int a = 0; a < tmp.size(); a++){
            result[a] = tmp.get(a);
        }

        return result;
    }

    private String getJava_library_path(){
        String[] tmp = runtimeManifest.java_library_path.split(Definitions.RUNTIME_CONDITION_SPILT);
        StringBuilder result = new StringBuilder("-Djava.library.path=");
        for(String str : tmp){
            result.append(AppManifest.BOAT_RUNTIME_HOME).append("/").append(str).append(":");
        }
        result.append(AppManifest.BOAT_RUNTIME_HOME);
        return result.toString();
    }

    private String getClasspath(){
        String[] in_runtime = runtimeManifest.classpath.split(Definitions.RUNTIME_CONDITION_SPILT);
        StringBuilder result = new StringBuilder();
        for(VersionJson.DependentLibrary library : version.getLibraries()){
            if(!Utils.filterLib(library.getName())){
                result.append(Utils.getLibPathByPkgName(library.getName())).append(":");
            }
        }
        if(forge != null){
            for(VersionJson.DependentLibrary library : forge.getLibraries()){
                if(!Utils.filterLib(library.getName())){
                    result.append(Utils.getLibPathByPkgName(library.getName())).append(":");
                }
            }
        }
        for(String str : in_runtime){
            result.append(AppManifest.BOAT_RUNTIME_HOME).append("/").append(str).append(":");
        }
        result.append(Utils.getJarAbsPath(version));
        return result.toString();
    }

    private String getMainClass(){
        if(forge != null){
            return forge.getMainClass();
        }else{
            return version.getMainClass();
        }
    }

    private String getMinecraftArgs(){
        if(forge != null){
            if (version.getMinimumLauncherVersion() >= 21) {
                //这是1.13.1以及之后的处理方法
                return this.ConvertJsStringModleToJavaStringModle(this.ConvertArgumentsToMinecraftArguments(forge));
            } else if (version.getMinimumLauncherVersion() < 21){
                //这是1.13.1之前的处理方法
                return this.ConvertJsStringModleToJavaStringModle(forge.getMinecraftArguments());
            }
        }else{
            if (version.getMinimumLauncherVersion() >= 21) {
                //这是1.13.1以及之后的处理方法
                return this.ConvertJsStringModleToJavaStringModle(this.ConvertArgumentsToMinecraftArguments(version));
            } else if (version.getMinimumLauncherVersion() < 21){
                //这是1.13.1之前的处理方法
                return this.ConvertJsStringModleToJavaStringModle(version.getMinecraftArguments());
            }
        }
        return null;
    }

    private String ConvertJsStringModleToJavaStringModle(String str) {
        String JavaString;
        StringBuilder tempString = new StringBuilder();
        SettingJson.Account account = null;
        HashMap<String, String> ArgsMap = new HashMap<>();
        for(SettingJson.Account a : mSetting.getAccounts()){
            if(a.isSelected()){
                account = a;
            }
        }

        //需要转义的键名-键值
        ArgsMap.put("{auth_player_name}", Objects.requireNonNull(account).getUsername());
        ArgsMap.put("{auth_uuid}", account.getUuid());
        ArgsMap.put("{auth_access_token}", account.getAccessToken());
        ArgsMap.put("{auth_session}", "mojang");
        ArgsMap.put("{user_properties}", "{}");
        ArgsMap.put("{user_type}", "mojang");
        ArgsMap.put("{assets_index_name}", version.getAssets());
        ArgsMap.put("{assets_root}", AppManifest.MINECRAFT_ASSETS);
        ArgsMap.put("{game_directory}", AppManifest.MINECRAFT_HOME);
        ArgsMap.put("{game_assets}", version.getAssets());
        ArgsMap.put("{version_name}", "MCinaBox_" + MCINABOX_VERSION_NAME);
        ArgsMap.put("{version_type}", version.getType());
        ArgsMap.put("{window_width}", String.valueOf(mContext.getResources().getDisplayMetrics().widthPixels));
        ArgsMap.put("{window_height}", String.valueOf(mContext.getResources().getDisplayMetrics().heightPixels));


        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '$') {
                StringBuilder tempString2 = new StringBuilder();
                do {
                    i++;
                    tempString2.append(str.charAt(i));
                } while (str.charAt(i) != '}');
                tempString.append(ArgsMap.get(tempString2.toString()));
            } else {
                tempString.append(str.charAt(i));
            }
        }

        JavaString = tempString.toString();
        return JavaString;
    }

    /**
     * 【将MC1.13的Arguments对象转化为MinecraftArguments字符串】
     **/
    private String ConvertArgumentsToMinecraftArguments(VersionJson json) {
        StringBuilder ma = new StringBuilder();
        for (int i = 0; i < json.getArguments().getGame().length; i++) {
            if (json.getArguments().getGame()[i] instanceof String) {
                if (i == json.getArguments().getGame().length - 1) {
                    ma.append(json.getArguments().getGame()[i]);
                } else {
                    ma.append(json.getArguments().getGame()[i]).append(" ");
                }
            }
        }
        return ma.toString();
    }

    private String[] getSharedLibrariesPaths(){
        String[] paths = runtimeManifest.so.split(Definitions.RUNTIME_CONDITION_SPILT);
        String[] result = new String[paths.length];
        for(int a = 0; a < paths.length; a++){
            result[a] = AppManifest.BOAT_RUNTIME_HOME + "/" +paths[a];
        }
        return result;
    }

}

