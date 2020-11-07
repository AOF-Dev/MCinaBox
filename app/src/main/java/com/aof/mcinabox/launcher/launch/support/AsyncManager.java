package com.aof.mcinabox.launcher.launch.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.aof.mcinabox.R;
import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.launch.LaunchManager;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.minecraft.JsonUtils;
import com.aof.mcinabox.utils.FileTool;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;

import java.io.File;

public class AsyncManager {

    private final static String TAG = "AsyncManager";

    private final Context mContext;
    private final LaunchManager mLaunchManager;
    private final SettingJson mSetting;

    private final static int DEFAULT_DELAY = 200;

    private enum PROGRESS {
        CHECK_TIPPER_HIGH,
        CHECK_TIPPER_LOW,
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

    private int currentPorgress = PROGRESS.CHECK_TIPPER_HIGH.ordinal();

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
            case CHECK_TIPPER_LOW:
                progress = new Thread() {
                    @Override
                    public void run() {
                        if (mSetting.getConfigurations().isNotCheckTipper()) {
                            ui_thread_next();
                            return;
                        }
                        ui_thread_set_progress(mContext.getString(R.string.tips_checking_tipper_manager));
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkTipperLow()) {
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setType(DialogRecorder.TYPE_NORMAL)
                                    .setTitle(mContext.getString(R.string.title_warn))
                                    .setMsg(mContext.getString(R.string.tips_tipper_is_not_void_may_cause_crash))
                                    .setPName(mContext.getString(R.string.title_continue))
                                    .setNName(mContext.getString(R.string.title_cancel))
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            ui_thread_next();
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_send_error(mContext.getString(R.string.tips_tipper_is_not_void_and_user_canceled));
                                        }
                                    })
                            );
                        }
                    }
                };

                break;
            case CHECK_TIPPER_HIGH:
                progress = new Thread() {
                    @Override
                    public void run() {
                        if (mSetting.getConfigurations().isNotCheckTipper()) {
                            ui_thread_next();
                            return;
                        }
                        ui_thread_set_progress(mContext.getString(R.string.tips_checking_tipper_manager));
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkTipperHigh()) {
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setType(DialogRecorder.TYPE_ONLY_NOTE)
                                    .setTitle(mContext.getString(R.string.title_error))
                                    .setMsg(mContext.getString(R.string.tips_tipper_not_allow_launch))
                                    .setPName(mContext.getString(R.string.title_ok))
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            ui_thread_send_error(mContext.getString(R.string.tips_tipper_is_not_void_and_user_canceled));
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
                        ui_thread_set_progress(mContext.getString(R.string.tips_checking_launcher_setting));
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkVersionThatSelected(mSetting)) {
                            ui_thread_next();
                        } else {
                            ui_thread_send_error(mContext.getString(R.string.tips_have_no_minecraft_please_install));
                        }
                    }
                };
                break;
            case CHECK_RUNTIME_INFO:
                progress = new Thread() {
                    @Override
                    public void run() {
                        ui_thread_set_progress(mContext.getString(R.string.tips_checking_runtime));
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkRuntimePack()) {
                            ui_thread_next();
                        } else {
                            ui_thread_send_error(mContext.getString(R.string.tips_have_no_runtime_please_install));
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
                        ui_thread_set_progress(mContext.getString(R.string.tips_checking_system_platform));
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkPlatform()) {
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setTitle(mContext.getString(R.string.title_warn))
                                    .setMsg(mContext.getString(R.string.tips_platform_not_correct_may_cause_crash))
                                    .setPName(mContext.getString(R.string.title_continue))
                                    .setNName(mContext.getString(R.string.title_cancel))
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            ui_thread_next();
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_send_error(mContext.getString(R.string.tips_platforn_not_correct_and_user_canceled));
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
                        ui_thread_set_progress(mContext.getString(R.string.tips_checking_main_file));
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkMinecraftMainFiles(mSetting)) {
                            ui_thread_next();
                        } else {
                            ui_thread_send_error(mContext.getString(R.string.tips_not_found_main_jar_please_reinstall));
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
                        ui_thread_set_progress(mContext.getString(R.string.tips_checking_libraries));
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
                                    .setTitle(mContext.getString(R.string.title_warn))
                                    .setMsg(String.format(mContext.getString(R.string.tips_lose_libraries), tmp))
                                    .setPName(mContext.getString(R.string.title_continue))
                                    .setNName(mContext.getString(R.string.title_cancel))
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            ui_thread_next();
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_send_error(mContext.getString(R.string.tips_lose_libraries_and_user_canceled_please_reinstall_minecraft));
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
                        ui_thread_set_progress(mContext.getString(R.string.tips_checking_assets_index));
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkMinecraftAssetsIndex(mSetting)) {
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setTitle(mContext.getString(R.string.title_warn))
                                    .setMsg(mContext.getString(R.string.tips_not_found_assets_index))
                                    .setPName(mContext.getString(R.string.title_continue))
                                    .setNName(mContext.getString(R.string.title_cancel))
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            ui_thread_next();
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_send_error(mContext.getString(R.string.tips_not_found_assets_index_and_user_canceled));
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
                        ui_thread_set_progress(mContext.getString(R.string.tips_checking_assets_objs));
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
                                    .setTitle(mContext.getString(R.string.title_warn))
                                    .setMsg(String.format(mContext.getString(R.string.tips_lose_assets_objs), tmp))
                                    .setPName(mContext.getString(R.string.title_continue))
                                    .setNName(mContext.getString(R.string.title_cancel))
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            ui_thread_next();
                                        }

                                        @Override
                                        public void runWhenNegative() {
                                            ui_thread_send_error(mContext.getString(R.string.tips_lose_assets_objs_and_user_canceled));
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
                        if (mSetting.getConfigurations().isNotCheckForge() || JsonUtils.getVersionFromFile(Utils.getJsonAbsPath(mSetting.getLastVersion())).getInheritsFrom() == null) {
                            ui_thread_next();
                            return;
                        }
                        ui_thread_set_progress(mContext.getString(R.string.tips_checking_forge_config));
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkForgeSplash()) {
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setTitle(mContext.getString(R.string.title_warn))
                                    .setMsg(mContext.getString(R.string.tips_not_disable_forge_splash))
                                    .setPName(mContext.getString(R.string.title_continue))
                                    .setNName(mContext.getString(R.string.title_cancel))
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
                                                ui_thread_send_error(mContext.getString(R.string.tips_failed_to_disable_forge_splash));
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
                        ui_thread_set_progress(mContext.getString(R.string.tips_checking_options_txt));
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkMinecraftOptionsMipmap(mSetting)) {
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setTitle(mContext.getString(R.string.title_warn))
                                    .setMsg(mContext.getString(R.string.tips_mipmap_level_is_not_0))
                                    .setPName(mContext.getString(R.string.title_revision))
                                    .setNName(mContext.getString(R.string.title_cancel))
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            if (FileTool.addStringLineToFile("\nmipmapLevels:0", AppManifest.MINECRAFT_HOME + "/options.txt")) {
                                                ui_thread_next();
                                            } else {
                                                ui_thread_send_error(String.format(mContext.getString(R.string.tips_failed_to_revise), AppManifest.MINECRAFT_HOME + "/options.txt"));
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
                        ui_thread_set_progress(mContext.getString(R.string.tips_checking_options_txt));
                        paused(DEFAULT_DELAY);
                        if (CheckManifest.checkMinecraftOptionsTouchMode()) {
                            ui_thread_next();
                        } else {
                            ui_thread_create_dialog(new DialogRecorder()
                                    .setTitle(mContext.getString(R.string.title_warn))
                                    .setMsg(mContext.getString(R.string.tips_not_disable_touch_screen))
                                    .setPName(mContext.getString(R.string.title_revision))
                                    .setNName(mContext.getString(R.string.title_cancel))
                                    .setSupport(new DialogSupports() {
                                        @Override
                                        public void runWhenPositive() {
                                            if (FileTool.addStringLineToFile("\ntouchscreen:false", AppManifest.MINECRAFT_HOME + "/options.txt")) {
                                                ui_thread_next();
                                            } else {
                                                ui_thread_send_error(String.format(mContext.getString(R.string.tips_failed_to_revise), AppManifest.MINECRAFT_HOME + "/options.txt"));
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
    private final Handler mHandler = new Handler() {
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
