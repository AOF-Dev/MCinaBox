package com.aof.mcinabox.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.aof.mcinabox.R;
import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.launcher.lang.LangManager;
import com.aof.mcinabox.launcher.setting.SettingManager;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.theme.ThemeManager;
import com.aof.mcinabox.launcher.tipper.TipperManager;
import com.aof.mcinabox.launcher.uis.BaseUI;
import com.aof.mcinabox.launcher.uis.achieve.UiManager;
import com.aof.mcinabox.utils.FileTool;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class OldMainActivity extends BaseActivity {

    public static final int LAUNCHER_IMPT_RTPACK = 127;
    public static WeakReference<OldMainActivity> CURRENT_ACTIVITY;
    public Timer mTimer;
    public UiManager mUiManager;
    public TipperManager mTipperManager;
    public SettingManager mSettingManager;
    public ThemeManager mThemeManager;
    private static final int REFRESH_DELAY = 0; //ms
    private static final int REFRESH_PERIOD = 500; //ms
    private static final String TAG = "MainActivity";
    public static SettingJson Setting;
    private boolean enableSettingChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_main);
        //静态对象
        CURRENT_ACTIVITY = new WeakReference<>(this);
        //使用语言管理器切换语言
        if (!new LangManager(this).fitSystemLang()) {
            return;
        }
        //初始化配置管理器
        mSettingManager = new SettingManager(this);
        //检查配置文件
        if (Setting == null) {
            Setting = checkLauncherSettingFile();
        }
        //初始化清单
        AppManifest.initManifest(this, Setting.getGamedir());
        //检查目录
        CheckMcinaBoxDir();
        //初始化主题管理器
        mThemeManager = new ThemeManager(this);
        //初始化消息管理器
        mTipperManager = new TipperManager(this);
        //初始化界面管理器
        mUiManager = new UiManager(this, Setting);
        //Life Circle
        mUiManager.onCreate();

        findViewById(R.id.new_ui).setOnClickListener(v -> {
            Intent i = new Intent(OldMainActivity.this, MainActivity.class);
            startActivity(i);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //执行自动刷新
        this.mTimer = new Timer();
        this.mTimer.schedule(createTimerTask(), REFRESH_DELAY, REFRESH_PERIOD);
        //启用检查
        switchSettingChecker(true);
        //添加无媒体文件标签
        setMCinaBoxNoMedia();
    }

    /**
     * 【重写返回键】
     **/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            backFromHere();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 【界面切换】
     **/
    public void switchUIs(BaseUI ui, String position) {
        mUiManager.switchUIs(ui, position);
    }

    /**
     * 【设定返回键的执行逻辑和顶部指示器】
     **/
    public void backFromHere() {
        mUiManager.backFromHere();
    }

    /**
     * 【保存启动器配置到配置文件】
     **/
    private void saveLauncherSettingToFile(SettingJson settingJson) {
        mSettingManager.saveSettingToFile();
    }

    /**
     * 【检查MCinaBox的目录结构是否正常】
     **/
    private void updateSettingFromUis() {
        mUiManager.saveConfigToSetting();
    }

    /**
     * 【检查MCinaBox的目录结构是否正常】
     **/
    private void CheckMcinaBoxDir() {
        for (String path : AppManifest.getAllPath()) {
            FileTool.checkFilePath(new File(path), true);
        }
    }

    /**
     * 【检查启动器模板】
     **/
    private SettingJson checkLauncherSettingFile() {
        return mSettingManager.getSettingFromFile();
    }

    /**
     * 【刷新启动器设置】
     **/
    public void refreshLauncher() {
        mUiManager.refreshUis();
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.e("mcinabox", "Updata Setting.");
                refreshLauncher();
                updateSettingFromUis();
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 【给Minecraft目录设置无媒体标签】
     **/
    private void setMCinaBoxNoMedia() {
        File file = new File(AppManifest.MINECRAFT_HOME + "/.nomedia");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 【移除缓存文件夹】
     **/
    private void removeTmpFloder() {
        FileTool.deleteDir(AppManifest.MCINABOX_TEMP);
        FileTool.makeFloder(AppManifest.MCINABOX_TEMP);
    }

    @Override
    public void onStop() {
        super.onStop();
        mUiManager.onStop();
        saveLauncherSettingToFile(Setting);
        // recover Timer Task.
        mTimer.cancel();
        //首先要关闭SettingManager的自动检查
        switchSettingChecker(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 重新创建缓存文件夹
        removeTmpFloder();
        switchSettingChecker(false);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        mUiManager.onRestart();
        // stat Timer Task
        this.mTimer = new Timer();
        this.mTimer.schedule(createTimerTask(), REFRESH_DELAY, REFRESH_PERIOD);
        //重新启动SettingManager的自动检查
        switchSettingChecker(true);
    }

    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
    }

    public void restarter() {
        //首先要关闭SettingManager的自动检查
        switchSettingChecker(false);
        //重启Activity
        Intent i = new Intent(this, OldMainActivity.class);
        this.startActivity(i);

        finish();
    }

    private void switchSettingChecker(boolean enable) {
        if (mSettingManager != null) {
            if (enable && !enableSettingChecker) {
                mSettingManager.startChecking();
                enableSettingChecker = true;
            } else if (!enable && enableSettingChecker) {
                mSettingManager.stopChecking();
                enableSettingChecker = false;
            }
        }
    }
}