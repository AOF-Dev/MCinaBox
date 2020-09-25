package com.aof.mcinabox.launcher.runtime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.runtime.support.ConditionResolve;
import com.aof.mcinabox.launcher.runtime.support.Definitions;
import com.aof.mcinabox.launcher.runtime.support.RuntimePackInfo;
import com.aof.mcinabox.minecraft.json.VersionJson;
import com.aof.utils.BoatUtils;
import com.aof.utils.FileTool;
import com.aof.utils.dialog.DialogUtils;
import com.aof.utils.dialog.support.TaskDialog;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Objects;

public class RuntimeManager {

    private final static String TAG = "RuntimeManager";

    /**
     * 【从路径安装运行库】
     **/
    public static void installRuntimeFromPath(final Context context, String globalPath) {

        final TaskDialog mDialog = DialogUtils.createTaskDialog(context, "正在安装运行库...","",false);
        mDialog.show();
        @SuppressLint("HandlerLeak") final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what) {
                    case 4:
                        Toast.makeText(MainActivity.CURRENT_ACTIVITY, MainActivity.CURRENT_ACTIVITY.getString(R.string.tips_runtime_notfound), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        break;
                    case 6:
                        Toast.makeText(MainActivity.CURRENT_ACTIVITY, MainActivity.CURRENT_ACTIVITY.getString(R.string.tips_runtime_install_success), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        break;
                    case 7:
                        Toast.makeText(MainActivity.CURRENT_ACTIVITY, MainActivity.CURRENT_ACTIVITY.getString(R.string.tips_runtime_install_fail) + " " + MainActivity.CURRENT_ACTIVITY.getString(R.string.tips_runtime_install_fail_exeable), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        final String mpackagePath = globalPath;
        new Thread() {
            @Override
            public void run() {
                File packageFile = new File(mpackagePath);
                if (!packageFile.exists()) {

                    Message msg_1 = new Message();
                    msg_1.what = 4;
                    mHandler.sendMessage(msg_1);
                    return;

                } else {
                    if (packageFile.isDirectory()) {
                        Toast.makeText(context, "Runtime packs should not be directories!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                Message msg_2 = new Message();
                Message msg_3 = new Message();
                msg_2.what = 5;
                mHandler.sendMessage(msg_2);
                File dir = new File(AppManifest.BOAT_RUNTIME_HOME);
                if(!dir.exists()){
                    FileTool.makeFloder(dir.getAbsolutePath());
                }
                BoatUtils.extractTarXZ(mpackagePath, AppManifest.BOAT_RUNTIME_HOME);
                if (BoatUtils.setExecutable(AppManifest.BOAT_RUNTIME_HOME)) {
                    msg_3.what = 6;
                } else {
                    msg_3.what = 7;
                }
                mHandler.sendMessage(msg_3);
            }
        }.start();
    }

    public static RuntimePackInfo getPackInfo(String path){
        File file = new File(path);
        try {
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            //使用Gson将ListVersionManifestJson实例化
            return gson.fromJson(reader, RuntimePackInfo.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static RuntimePackInfo getPackInfo(){
        return getPackInfo(AppManifest.BOAT_RUNTIME_INFO_JSON);
    }

    public static RuntimePackInfo.Manifest[] getRutinmeInfoManifest(String infoPath, VersionJson version){
        ArrayList<RuntimePackInfo.Manifest> mabifests = new ArrayList<>();
        RuntimePackInfo info = RuntimeManager.getPackInfo(infoPath);
        RuntimePackInfo.Manifest[] originalManifests = Objects.requireNonNull(info).manifest;
        //默认清单
        for(RuntimePackInfo.Manifest m : originalManifests){
            if(m.condition.equals(Definitions.RUNTIME_CONDITION_AS_DEFAULT)){
                mabifests.add(m);
                break;
            }
        }

        //根据启动器版本
        for(RuntimePackInfo.Manifest m : originalManifests){
            if(m.condition.equals(Definitions.RUNTIME_CONDITION_AS_LAUNCHER_VERSION) & ConditionResolve.handleConditionWithLauncherVersion(version.getMinimumLauncherVersion(), m.condition_info)){
                mabifests.add(m);
            }
        }

        //根据游戏版本
        for(RuntimePackInfo.Manifest m : originalManifests){
            if(m.condition.equals(Definitions.RUNTIME_CONDITION_AS_MINECRAFT_VERSION)  & ConditionResolve.handleConditionWithMinecraftVersion(version.getId(),m.condition_info)){
                mabifests.add(m);
            }
        }

        RuntimePackInfo.Manifest[] tmp = new RuntimePackInfo.Manifest[mabifests.size()];
        for(int a =0; a < tmp.length; a++){
            tmp[a] = mabifests.get(a);
        }
        return tmp;
    }

    public static RuntimePackInfo.Manifest[] getRutinmeInfoManifest(VersionJson version){
        return getRutinmeInfoManifest(AppManifest.BOAT_RUNTIME_INFO_JSON, version);
    }
}
