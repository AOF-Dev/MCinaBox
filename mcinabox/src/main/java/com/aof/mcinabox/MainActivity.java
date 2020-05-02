package com.aof.mcinabox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.aof.mcinabox.launcher.dialogs.CreateUserDialog;
import com.aof.mcinabox.launcher.dialogs.DownloaderDialog;
import com.aof.mcinabox.launcher.dialogs.LanguageDialog;
import com.aof.mcinabox.launcher.json.SettingJson;
import com.aof.mcinabox.launcher.uis.FunctionbarUI;
import com.aof.mcinabox.launcher.uis.GameSettingUI;
import com.aof.mcinabox.launcher.uis.GamedirUI;
import com.aof.mcinabox.launcher.uis.GamelistUI;
import com.aof.mcinabox.launcher.uis.InstallVersionUI;
import com.aof.mcinabox.launcher.uis.LauncherSettingUI;
import com.aof.mcinabox.launcher.uis.MainToolbarUI;
import com.aof.mcinabox.launcher.uis.PluginUI;
import com.aof.mcinabox.launcher.uis.StandUI;
import com.aof.mcinabox.launcher.uis.StartGameUI;
import com.aof.mcinabox.launcher.uis.UserUI;
import com.aof.mcinabox.utils.FileTool;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Timer;
import java.util.TimerTask;

import cosine.boat.Utils;

import static com.aof.mcinabox.DataPathManifest.*;

public class MainActivity extends AppCompatActivity {

    public static final int LAUNCHER_IMPT_RTPACK = 127;

    public Animation ShowAnim, HideAnim;
    public Timer timer_tipper = new Timer();

    //--------

    public PluginUI uiPlugin;
    public InstallVersionUI uiInstallVersion;
    public GamedirUI uiGamedir;
    public GamelistUI uiGamelist;
    public GameSettingUI uiGameSetting;
    public LauncherSettingUI uiLauncherSetting;
    public StartGameUI uiStartGame;
    public UserUI uiUser;

    //Do not add toolbar into UIs!
    public MainToolbarUI uiMainToolbar;

    //Do not add functionbar into UIs!
    public FunctionbarUI uiFunctionbar;

    public DownloaderDialog dialogDownloader;
    public LanguageDialog dialogLanguage;
    public CreateUserDialog dialogCreateUser;

    //UIs includes all switchable UIs in MainActivity
    public StandUI[] UIs;

    //--------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //请求权限
        requestPermission();
        //检查目录
        CheckMcinaBoxDir();
        //检查配置文件
        checkLauncherSettingFile();
        //初始化控件
        initUIs();
        //添加无媒体文件标签
        setMCinaBoxNoMedia();
        //删除tmp文件夹
        removeTmpFloder();
        //执行自动刷新
        timer_tipper.schedule(TipperTask, 1000, 3000);
    }

    private void initUIs() {
        ShowAnim = AnimationUtils.loadAnimation(this, R.anim.layout_show);
        HideAnim = AnimationUtils.loadAnimation(this, R.anim.layout_hide);

        SettingJson setting = com.aof.mcinabox.launcher.JsonUtils.getSettingFromFile(MCINABOX_FILE_JSON);
        dialogDownloader = new DownloaderDialog(this, R.layout.dialog_download);
        dialogLanguage = new LanguageDialog(this, R.layout.dialog_languages);
        dialogCreateUser = new CreateUserDialog(this, R.layout.dialog_createuser);

        uiInstallVersion = new InstallVersionUI(this, setting);
        uiPlugin = new PluginUI(this, setting);
        uiGamedir = new GamedirUI(this, setting);
        uiGamelist = new GamelistUI(this, setting);
        uiGameSetting = new GameSettingUI(this, setting);
        uiLauncherSetting = new LauncherSettingUI(this, setting);
        uiStartGame = new StartGameUI(this, setting);
        uiUser = new UserUI(this, setting);
        uiMainToolbar = new MainToolbarUI(this, setting);

        UIs = new StandUI[]{uiInstallVersion, uiPlugin, uiGamedir, uiGamelist, uiGameSetting, uiLauncherSetting, uiStartGame, uiUser};

        uiFunctionbar = new FunctionbarUI(this);
    }

    /**
     * 【Toolbar 菜单创建】
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * 【Toolbar 菜单按键监听】
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.toolbar_action1:
                //ToolBar菜单的按键监听
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 【请求权限】
     * App permission.
     **/
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    /**
     * 【重写返回键】
     **/
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            backFromHere();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 【界面切换】
     * Switch to the UI.
     **/
    public void switchUIs(StandUI ui, String position) {
        if (ui.getUIVisiability() != View.VISIBLE) {
            hideAllUIs();
            ui.setUIVisiability(View.VISIBLE);
        }

        currentUI = ui;
        uiMainToolbar.setCurrentPosition(position);
    }

    /**
     * 【隐藏全部界面】
     * Hide all UIs.
     **/
    private void hideAllUIs() {
        for (StandUI ui : UIs) {
            if (ui.getUIVisiability() != View.INVISIBLE) {
                ui.setUIVisiability(View.INVISIBLE);
            }
        }
    }

    /**
     * 【设定返回键的执行逻辑和顶部指示器】
     * Back
     **/
    private StandUI currentUI;

    public void backFromHere() {
        if (currentUI == uiStartGame || currentUI == null) {
            finish();
        }

        if (currentUI == uiGamedir ||
                currentUI == uiGamelist ||
                currentUI == uiLauncherSetting ||
                currentUI == uiUser ||
                currentUI == uiPlugin) {
            switchUIs(uiStartGame, getString(R.string.title_home));
        }

        if (currentUI == uiGameSetting ||
                currentUI == uiInstallVersion) {
            switchUIs(uiGamelist, getString(R.string.title_gamelist));
        }
    }

    /**
     * 【保存启动器配置到配置文件】
     * Save Launcher Setting to file.
     **/
    private SettingJson saveLauncherSettingToFile() {
        SettingJson setting = new SettingJson();
        for (StandUI ui : UIs) {
            setting = ui.saveUIConfig(setting);
        }

        if (!com.aof.mcinabox.launcher.JsonUtils.saveSettingToFile(setting, MCINABOX_FILE_JSON)) {
            //TODO:Save Failed.
        }
        return setting;
    }

    /**
     * 【检查MCinaBox的目录结构是否正常】
     * Check floaders.
     **/
    private void CheckMcinaBoxDir() {
        for (String path : MCINABOX_ALLPATH) {
            FileTool.checkFilePath(new File(path), true);
        }
    }

    /**
     * 【检查启动器模板】
     * Check the state of Launcher Setting file.
     **/
    private SettingJson checkLauncherSettingFile() {
        File configFile = new File(MCINABOX_FILE_JSON);
        Gson gson = new Gson();
        InputStream inputStream;
        Reader reader;
        SettingJson settingModel = null;

        //检测启动器配置文件是否存在
        if (!configFile.exists()) {
            //如果不存在，就创建一个空文件
            try {
                configFile.createNewFile();
                Log.e("初始化", "模板不存在，开始创建");
            } catch (IOException e) {
                //如果创建失败，就退出程序
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.tips_launcher_new_fail), Toast.LENGTH_SHORT).show();
                Log.e("initLauncher ", e.toString());
                finish();
            }
            //初始化模板并写出配置文件
            settingModel = new SettingJson();
            String jsonString = gson.toJson(settingModel);
            try {
                Log.e("初始化", "开始写入新的模板");
                FileWriter jsonWriter = new FileWriter(configFile);
                BufferedWriter out = new BufferedWriter(jsonWriter);
                out.write(jsonString);
                out.close();
                Log.e("初始化", "写入成功");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.tips_launcher_new_fail), Toast.LENGTH_SHORT).show();
                Log.e("initLauncher ", e.toString());
                finish();
            }
            Toast.makeText(this, getString(R.string.tips_launcher_new_success), Toast.LENGTH_SHORT).show();
        } else {
            //如果文件存在，就读入配置文件
            try {
                inputStream = new FileInputStream(configFile);
                reader = new InputStreamReader(inputStream);
                settingModel = new Gson().fromJson(reader, SettingJson.class);
                Log.e("初始化", "文件存在");
                if (settingModel == null) {
                    Toast.makeText(this, getString(R.string.tips_launcher_init_fail), Toast.LENGTH_SHORT).show();
                    Log.e("initLauncher ", "SettingModel is null");
                    finish();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.tips_launcher_load_fail), Toast.LENGTH_SHORT).show();
                Log.e("initLauncher ", e.toString());
                finish();
            }
        }
        Log.e("初始化", "设置载入成功");
        return settingModel;
    }

    /**
     * 【刷新启动器设置】
     * Refresh launcher.
     **/
    public void refreshLauncher(SettingJson setting, boolean auto) {
        SettingJson mSetting;
        if (auto || setting == null) {
            mSetting = checkLauncherSettingFile();
        } else {
            mSetting = setting;
        }
        for (StandUI ui : UIs) {
            ui.refreshUI(mSetting);
        }
    }

    /**
     * 【定时器 定时保存并刷新界面】
     * Auto timer task.
     **/
    private TimerTask TipperTask = new TimerTask() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    saveLauncherSettingToFile();
                    refreshLauncher(checkLauncherSettingFile(), false);
            }
            super.handleMessage(msg);

            switch (msg.what) {
                case 4:
                    Toast.makeText(getApplication(), getString(R.string.tips_runtime_notfound), Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(getApplication(), getString(R.string.tips_runtime_installing), Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(getApplication(), getString(R.string.tips_runtime_install_success), Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    Toast.makeText(getApplication(), getString(R.string.tips_runtime_install_fail) + " " + getString(R.string.tips_runtime_install_fail_exeable), Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 【给Minecraft目录设置无媒体标签】
     * Set no media tag for Minecraft floader.
     **/
    private void setMCinaBoxNoMedia() {
        File file = new File(MCINABOX_DATA_PUBLIC + "/.nomedia");
        File file2 = new File(MCINABOX_DATA_PRIVATE + "/.nomedia");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!file2.exists()) {
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 【移除缓存文件夹】
     * Remove temp floder.
     **/
    private void removeTmpFloder() {
        FileTool.deleteDir(MCINABOX_TEMP);
        FileTool.makeFloder(MCINABOX_TEMP);
    }

    /**
     * 【重新启动主页】
     * Restart MainActivity.
     **/
    public void restartLauncher() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LAUNCHER_IMPT_RTPACK:
                if (resultCode == RESULT_OK) {

                    Uri uri = data.getData();
                    System.out.println("URI=" + uri.toString());
                    String path = uri.getPath();
                    System.out.println("PTH=" + path);
                    //Move the method to LauncherSettingUI.
                    uiLauncherSetting.installRuntimeFromPath(path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 【当Activity停止时】
     **/
    @Override
    public void onStop() {
        super.onStop();
        saveLauncherSettingToFile();
    }
}

