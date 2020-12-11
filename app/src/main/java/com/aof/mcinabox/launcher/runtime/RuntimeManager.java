package com.aof.mcinabox.launcher.runtime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.runtime.support.ConditionResolve;
import com.aof.mcinabox.launcher.runtime.support.Definitions;
import com.aof.mcinabox.launcher.runtime.support.RuntimePackInfo;
import com.aof.mcinabox.minecraft.json.VersionJson;
import com.aof.mcinabox.utils.BoatUtils;
import com.aof.mcinabox.utils.FileTool;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.TaskDialog;
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
    private static String filename;

    /**
     * 【从路径安装运行库】
     **/
    public static void installRuntimeFromPath(final Context context, String globalPath) {

        final TaskDialog mDialog = DialogUtils.createTaskDialog(context, "", "", false);
        mDialog.show();
        @SuppressLint("HandlerLeak") final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 3:
                        mDialog.setTotalTaskName(context.getString(R.string.tips_installing_runtime));
                        break;
                    case 4:
                        Toast.makeText(OldMainActivity.CURRENT_ACTIVITY.get(), OldMainActivity.CURRENT_ACTIVITY.get().getString(R.string.tips_runtime_notfound), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        break;
                    case 6:
                        Toast.makeText(OldMainActivity.CURRENT_ACTIVITY.get(), OldMainActivity.CURRENT_ACTIVITY.get().getString(R.string.tips_runtime_install_successed), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        break;
                    case 7:
                        Toast.makeText(OldMainActivity.CURRENT_ACTIVITY.get(), OldMainActivity.CURRENT_ACTIVITY.get().getString(R.string.tips_runtime_install_failed) + " " + OldMainActivity.CURRENT_ACTIVITY.get().getString(R.string.tips_runtime_install_fail_exeable), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        break;
                    case 8:
                        mDialog.setTotalTaskName("Unzipping Runtime pack...");
                        mDialog.setCurrentTaskName(filename);
                        break;
                }
                super.handleMessage(msg);
            }
        };

        final String mpackagePath = globalPath;
        new Thread() {
            @Override
            public void run() {
                sendMsg(3);
                File packageFile = new File(mpackagePath);
                if (!packageFile.exists()) {
                    sendMsg(4);
                    return;
                } else {
                    if (packageFile.isDirectory()) {
                        Toast.makeText(context, "Runtime packs should not be directories!", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                File dir = new File(AppManifest.BOAT_RUNTIME_HOME);
                if (!dir.exists()) {
                    FileTool.makeFloder(dir.getAbsolutePath());
                }
                BoatUtils.extractTarXZ(mpackagePath, AppManifest.BOAT_RUNTIME_HOME, new BoatUtils.CompressCallback() {
                    @Override
                    public void onFileCompressing(File file) {
                        if(file != null){
                            Message msg = new Message();
                            msg.what = 8;
                            filename = file.getName();
                            mHandler.sendMessage(msg);
                        }
                    }
                });
                if (BoatUtils.setExecutable(AppManifest.BOAT_RUNTIME_HOME)) {
                    sendMsg(6);
                } else {
                    sendMsg(7);
                }
            }

            public void sendMsg(int what) {
                Message msg = new Message();
                msg.what = what;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    public static RuntimePackInfo getPackInfo(String path) {
        File file = new File(path);
        try {
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            //使用Gson将ListVersionManifestJson实例化
            return gson.fromJson(reader, RuntimePackInfo.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "packinfo.json not found.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static RuntimePackInfo getPackInfo() {
        return getPackInfo(AppManifest.BOAT_RUNTIME_INFO_JSON);
    }

    public static RuntimePackInfo.Manifest[] getRuntinmeInfoManifest(String infoPath, VersionJson version) {
        ArrayList<RuntimePackInfo.Manifest> mabifests = new ArrayList<>();
        RuntimePackInfo info = RuntimeManager.getPackInfo(infoPath);
        RuntimePackInfo.Manifest[] originalManifests = Objects.requireNonNull(info).manifest;
        //默认清单
        for (RuntimePackInfo.Manifest m : originalManifests) {
            if (m.condition.equals(Definitions.RUNTIME_CONDITION_AS_DEFAULT)) {
                mabifests.add(m);
                break;
            }
        }

        //根据启动器版本
        for (RuntimePackInfo.Manifest m : originalManifests) {
            if (m.condition.equals(Definitions.RUNTIME_CONDITION_AS_LAUNCHER_VERSION) & ConditionResolve.handleConditionWithLauncherVersion(version.getMinimumLauncherVersion(), m.condition_info)) {
                mabifests.add(m);
            }
        }

        //根据游戏版本
        for (RuntimePackInfo.Manifest m : originalManifests) {
            if (m.condition.equals(Definitions.RUNTIME_CONDITION_AS_MINECRAFT_VERSION) & ConditionResolve.handleConditionWithMinecraftVersion(version.getId(), m.condition_info)) {
                mabifests.add(m);
            }
        }

        RuntimePackInfo.Manifest[] tmp = new RuntimePackInfo.Manifest[mabifests.size()];
        for (int a = 0; a < tmp.length; a++) {
            tmp[a] = mabifests.get(a);
        }
        return tmp;
    }

    public static RuntimePackInfo.Manifest[] getRuntinmeInfoManifest(VersionJson version) {
        return getRuntinmeInfoManifest(AppManifest.BOAT_RUNTIME_INFO_JSON, version);
    }

    public static void clearRuntime(final Context context) {

        final TaskDialog mDialog = DialogUtils.createTaskDialog(context, "", "", false);
        mDialog.show();
        @SuppressLint("HandlerLeak") final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        mDialog.setTotalTaskName(context.getString(R.string.tips_installing_runtime));
                        break;
                    case 2:
                        mDialog.dismiss();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        new Thread() {
            @Override
            public void run() {
                sendMsg(1);
                File file = new File(AppManifest.BOAT_RUNTIME_HOME);
                if (file.exists()) {
                    FileTool.deleteDir(file.getAbsolutePath());
                }
                sendMsg(2);
            }

            public void sendMsg(int what) {
                Message msg = new Message();
                msg.what = what;
                mHandler.sendMessage(msg);
            }
        }.start();
    }
}
