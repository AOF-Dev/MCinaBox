package com.aof.mcinabox.launcher.launch.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.aof.mcinabox.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.launch.LaunchManager;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.minecraft.JsonUtils;
import com.aof.utils.FileTool;
import com.aof.utils.dialog.support.DialogSupports;
import com.aof.utils.dialog.DialogUtils;

import java.io.File;

public class AsyncManager {

    private final static String TAG = "AsyncManager";

    private Context mContext;
    private LaunchManager mLaunchManager;
    private SettingJson mSetting;

    private final static int DEFAULT_DELAY = 200;

    private enum PROGRESS {
        CHECK_TIPPER,
        CHECK_SETTING,
        CHECK_RUNTIME_INFO,
        CHECK_RUNTIME_PLATFORM,
        CHECK_MINECRAFT_MAIN,
        CHECK_MINECRAFT_LIBRARIES,
        CHECK_MINECRAFT_ASSETS_INDEX,
        CHECK_MINECRAFT_ASSETS_OBJS,
        CHECK_FORGE_SPLASH,
        CHECK_MINECRAFT_OPTIONS_MIPMAP,
        CHECK_MINECRAFT_OPTIONS_TOUCHMODE
    }

    private int currentPorgress = PROGRESS.CHECK_TIPPER.ordinal();

    public AsyncManager(Context context, LaunchManager launchManager, SettingJson setting) {
        this.mContext = context;
        this.mLaunchManager = launchManager;
        this.mSetting = setting;
    }

    public void start() {
        next();
    }

    private void execute(PROGRESS p) {
        createThread(p).start();
    }

    private void execute(int p) {
        execute(PROGRESS.values()[p]);
    }

    private void closeWithError() {

    }

    public void next() {
        if (currentPorgress < PROGRESS.values().length) {
            execute(currentPorgress);
            currentPorgress++;
        } else {
            mLaunchManager.launchMinecraft(mSetting, LaunchManager.LAUNCH_PARM_SETUP);
        }
    }

    private Thread createThread(PROGRESS p) {
        Thread progress = null;
        switch (p) {
            case CHECK_TIPPER:
                progress = new Thread() {
                    @Override
                    public void run() {
                        if (mSetting.getConfigurations().isNotCheckTipper()) {
                            ui_thread_next();
                            return;
                        }
                        ui_thread_set_progress("正在检查消息管理器...");
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkTipper()) {
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setType(DialogRecorder.TYPE_NORMAL)
                                    .setTitle("警告")
                                    .setMsg("检测到消息管理器存在未处理的重要消息，忽略这些消息可能会导致启动出现异常。是否继续？")
                                    .setPName("继续")
                                    .setNName("取消")
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            ui_thread_next();
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_send_error("消息管理器存在未处理的消息，用户结束了操作。");
                                        }
                                    })
                            );
                        }
                    }
                };

                break;
            case CHECK_SETTING:
                progress = new Thread() {
                    @Override
                    public void run() {
                        ui_thread_set_progress("正在检查启动器配置...");
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkVersionThatSelected(mSetting)) {
                            ui_thread_next();
                        } else {
                            ui_thread_send_error("未检测到游戏，请先安装一个Minecraft。");
                        }
                    }
                };
                break;
            case CHECK_RUNTIME_INFO:
                progress = new Thread() {
                    @Override
                    public void run() {
                        ui_thread_set_progress("正在检查运行库...");
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkRuntimePack()) {
                            ui_thread_next();
                        } else {
                            ui_thread_send_error("未检测到运行库，请先安装运行库。");
                        }
                    }
                };
                break;
            case CHECK_RUNTIME_PLATFORM:
                progress = new Thread() {
                    @Override
                    public void run() {
                        if (mSetting.getConfigurations().isNotCheckPlatform()) {
                            ui_thread_next();
                            return;
                        }
                        ui_thread_set_progress("正在检查系统架构...");
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkPlatform()) {
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setTitle("警告")
                                    .setMsg("检测到运行库架构和您的系统架构不符，可能导致启动失败或性能损失，是否继续？")
                                    .setPName("继续")
                                    .setNName("取消")
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            ui_thread_next();
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_send_error("运行库架构与系统不符合，用户结束了操作。");
                                        }
                                    })
                                    .setType(DialogRecorder.TYPE_NORMAL)
                            );
                        }
                    }
                };
                break;
            case CHECK_MINECRAFT_MAIN:
                progress = new Thread() {
                    @Override
                    public void run() {
                        ui_thread_set_progress("正在检查游戏主文件...");
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkMinecraftMainFiles(mSetting)) {
                            ui_thread_next();
                        } else {
                            ui_thread_send_error("未找到Minecraft JAR主文件，请重新安装该Minecraft版本。");
                        }
                    }
                };
                break;
            case CHECK_MINECRAFT_LIBRARIES:
                progress = new Thread() {
                    @Override
                    public void run() {
                        if (mSetting.getConfigurations().isNotCheckGame()) {
                            ui_thread_next();
                            return;
                        }
                        ui_thread_set_progress("正在检查游戏依赖库...");
                        paused(DEFAULT_DELAY);
                        String[] result = CheckManifest.checkMinecraftLibraries(mSetting);
                        if (result == null) {
                            ui_thread_next();
                        } else {
                            StringBuilder tmp = new StringBuilder();
                            for (String str : result) {
                                tmp.append(str).append("\n");
                            }
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setTitle("警告")
                                    .setMsg("检测到运行库不完整，缺少文件: " + "\n" + tmp + "是否继续？")
                                    .setPName("继续")
                                    .setNName("取消")
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            ui_thread_next();
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_send_error("Minecraft依赖库不完整，请重新安装该Mineccraft版本，用户结束了操作。");
                                        }
                                    })
                                    .setType(DialogRecorder.TYPE_NORMAL)
                            );
                        }
                    }
                };
                break;
            case CHECK_MINECRAFT_ASSETS_INDEX:
                progress = new Thread() {
                    @Override
                    public void run() {
                        if (mSetting.getConfigurations().isNotCheckGame()) {
                            ui_thread_next();
                            return;
                        }
                        ui_thread_set_progress("正在检查游戏资源索引文件...");
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkMinecraftAssetsIndex(mSetting)) {
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setTitle("警告")
                                    .setMsg("未检测到Minecraft资源索引文件，将会导致Minecraft没有声音和多语言支持，是否继续？")
                                    .setPName("继续")
                                    .setNName("取消")
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            ui_thread_next();
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_send_error("缺少Minecraft资源索引文件，用户结束了操作。");
                                        }
                                    })
                                    .setType(DialogRecorder.TYPE_NORMAL)
                            );
                        }
                    }
                };
                break;
            case CHECK_MINECRAFT_ASSETS_OBJS:
                progress = new Thread() {
                    @Override
                    public void run() {
                        if (mSetting.getConfigurations().isNotCheckGame()) {
                            ui_thread_next();
                            return;
                        }
                        ui_thread_set_progress("正在检查游戏资源文件...");
                        paused(DEFAULT_DELAY);
                        String[] result = CheckManifest.checkMinecraftAssetsObjects(mSetting);
                        if (result == null) {
                            ui_thread_next();
                        } else {
                            StringBuilder tmp = new StringBuilder();
                            for (String str : result) {
                                tmp.append(str).append("\n");
                            }
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setTitle("警告")
                                    .setMsg("检测到资源文件不完整，缺少文件: " + "\n" + tmp + "是否继续？")
                                    .setPName("继续")
                                    .setNName("取消")
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            ui_thread_next();
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_send_error("Minecraft资源不完整，用户结束了操作。");
                                        }
                                    })
                                    .setType(DialogRecorder.TYPE_NORMAL)
                            );
                        }
                    }
                };
                break;
            case CHECK_FORGE_SPLASH:
                progress = new Thread() {
                    @Override
                    public void run() {
                        if ( mSetting.getConfigurations().isNotCheckForge() || JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(mSetting.getLastVersion())).getInheritsFrom() == null) {
                            ui_thread_next();
                            return;
                        }
                        ui_thread_set_progress("正在检查Forge配置...");
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkForgeSplash()) {
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setTitle("警告")
                                    .setMsg("检测到Forge动画没有禁用，是否禁用？")
                                    .setPName("继续")
                                    .setNName("取消")
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            try {
                                                FileTool.checkFilePath(new File(AppManifest.MINECRAFT_HOME + "/config"), true);
                                                String config_file = AppManifest.MINECRAFT_HOME + "/config/splash.properties";
                                                if (!FileTool.isFileExists(config_file)) {
                                                    FileTool.addFile(config_file);
                                                }
                                                FileTool.writeData(config_file, "enabled=false");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                ui_thread_send_error("发生未知错误，禁用forge动画失败");
                                            }
                                            ui_thread_next();
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_next();
                                        }
                                    })
                                    .setType(DialogRecorder.TYPE_NORMAL)
                            );
                        }
                    }
                };
                break;
            case CHECK_MINECRAFT_OPTIONS_MIPMAP:
                progress = new Thread() {
                    @Override
                    public void run() {
                        if (mSetting.getConfigurations().isNotCheckOptions()) {
                            ui_thread_next();
                            return;
                        }
                        ui_thread_set_progress("正在检查options.txt...");
                        paused(DEFAULT_DELAY);
                        if(CheckManifest.checkMinecraftOptionsMipmap(mSetting)){
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setTitle("警告")
                                    .setMsg("检测到Minecraft的Mipmap等级不为0, 这将造成游戏流畅度下降并产生一些渲染错误。是否修改Mipmap等级为0？")
                                    .setPName("修改")
                                    .setNName("取消")
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            if(FileTool.addStringLineToFile("\nmipmapLevels:0",AppManifest.MINECRAFT_HOME + "/options.txt")){
                                                ui_thread_next();
                                            }else{
                                                ui_thread_send_error(String.format("发生未知错误， %s 修改失败！",AppManifest.MINECRAFT_HOME + "/options.txt"));
                                            }
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_next();
                                        }
                                    })
                                    .setType(DialogRecorder.TYPE_NORMAL)
                            );
                        }
                    }
                };
                break;
            case CHECK_MINECRAFT_OPTIONS_TOUCHMODE:
                progress = new Thread() {
                    @Override
                    public void run() {
                        if (mSetting.getConfigurations().isNotCheckOptions()) {
                            ui_thread_next();
                            return;
                        }
                        ui_thread_set_progress("正在检查options.txt...");
                        paused(DEFAULT_DELAY);
                        if(CheckManifest.checkMinecraftOptionsTouchMode()){
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setTitle("警告")
                                    .setMsg("检测到Minecraft的触屏模式开启, 这将使游戏难以操作。是否关闭触屏模式？")
                                    .setPName("修改")
                                    .setNName("取消")
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            if(FileTool.addStringLineToFile("\ntouchscreen:false",AppManifest.MINECRAFT_HOME + "/options.txt")){
                                                ui_thread_next();
                                            }else{
                                                ui_thread_send_error(String.format("发生未知错误， %s 修改失败！",AppManifest.MINECRAFT_HOME + "/options.txt"));
                                            }
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_next();
                                        }
                                    })
                                    .setType(DialogRecorder.TYPE_NORMAL)
                            );
                        }
                    }
                };
                break;
            default:
        }
        return progress;
    }

    private void paused(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(int what) {
        Message msg = new Message();
        msg.what = what;
        mHandler.sendMessage(msg);
    }

    private void ui_thread_next() {
        sendMsg(0);
    }

    private String tmpDes;

    private void ui_thread_set_progress(String des) {
        tmpDes = des;
        sendMsg(1);
    }

    private void ui_thread_send_error(String des) {
        tmpDes = des;
        sendMsg(3);
    }

    private DialogRecorder tmpRec;

    private void ui_thread_create_dialog(DialogRecorder recorder) {
        this.tmpRec = recorder;
        sendMsg(2);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    AsyncManager.this.next();
                    break;
                case 1:
                    mLaunchManager.brige_setProgressText(tmpDes);
                    break;
                case 2:
                    switch (tmpRec.type) {
                        case DialogRecorder.TYPE_NORMAL:
                            DialogUtils.createBothChoicesDialog(mContext, tmpRec.title, tmpRec.message, tmpRec.pName, tmpRec.nName, tmpRec.support);
                            break;
                        case DialogRecorder.TYPE_ONLY_NOTE:
                            DialogUtils.createSingleChoiceDialog(mContext, tmpRec.title, tmpRec.message, tmpRec.pName, tmpRec.support);
                            break;
                    }
                    break;
                case 3:
                    mLaunchManager.brige_exitWithError(tmpDes);
                    break;
            }
        }
    };

    class DialogRecorder {

        public final static int TYPE_NORMAL = 0;
        public final static int TYPE_ONLY_NOTE = 1;

        public int type;
        public String title;
        public String message;
        public String pName;
        public String nName;
        public DialogSupports support;

        public DialogRecorder setTitle(String t) {
            this.title = t;
            return this;
        }

        public DialogRecorder setMsg(String m) {
            this.message = m;
            return this;
        }

        public DialogRecorder setPName(String p) {
            this.pName = p;
            return this;
        }

        public DialogRecorder setNName(String n) {
            this.nName = n;
            return this;
        }

        public DialogRecorder setSupport(DialogSupports d) {
            this.support = d;
            return this;
        }

        public DialogRecorder setType(int t) {
            this.type = t;
            return this;
        }

    }

}
