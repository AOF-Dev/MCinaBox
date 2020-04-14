package com.aof.mcinabox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aof.mcinabox.launcher.json.SettingJson;
import com.aof.mcinabox.launcher.tipper.TipperListAdapter;
import com.aof.mcinabox.launcher.tipper.TipperListBean;
import com.aof.mcinabox.minecraft.ForgeInstaller;
import com.aof.mcinabox.minecraft.Login;
import com.aof.mcinabox.utils.FileTool;
import com.aof.mcinabox.utils.LanguageUtils;
import com.aof.mcinabox.utils.MemoryUtils;
import com.aof.mcinabox.utils.PathTool;
import com.aof.mcinabox.minecraft.json.VersionManifestJson;
import com.aof.mcinabox.launcher.keyboard.ConfigDialog;
import com.aof.mcinabox.launcher.version.LocalVersionListAdapter;
import com.aof.mcinabox.launcher.version.LocalVersionListBean;
import com.aof.mcinabox.launcher.user.UserListAdapter;
import com.aof.mcinabox.launcher.user.UserListBean;

import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;

import com.google.gson.Gson;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.zip.ZipException;

import cosine.boat.Utils;

import static com.aof.mcinabox.DataPathManifest.*;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    public Button[] launcherBts;
    public Button button_user, button_gameselected, button_gamelist, button_gamedir, button_launchersetting, button_launchercontrol, toolbar_button_backhome, toolbar_button_backfromhere,ImportRuntime,download_ok,download_cancle,toolbar_button_language,installForgeInstaller;
    public RadioGroup radioGroup_version_type;
    public RadioButton radioButton_type_release, radioButton_type_snapshot, radioButton_type_old;
    public RadioButton radioButton_gamedir_public, radioButton_gamedir_private;
    public Spinner setting_downloadtype, setting_keyboard, spinner_choice_version,spinner_runtimepacks,spinner_forgeinstaller;
    public Switch setting_notcheckJvm, setting_notcheckMinecraft;
    public LinearLayout[] launcherBts2;
    public LinearLayout gamelist_button_reflash, gamelist_button_installnewgame, gamelist_button_backfrom_installnewversion, gamelist_button_setting, main_button_startgame, gamelist_button_download, user_button_adduser, gamelist_button_reflash_locallist, user_button_reflash_userlist;
    public View[] launcherLins;
    public View layout_user, layout_gamelist, layout_gameselected, layout_gamedir, layout_launchersetting, layout_gamelist_installversion, layout_gamelist_setting, layout_startgame;
    public ListView listview_minecraft_manifest, listview_user, listView_localversion, listView_tipper, dialog_listview_languages;
    public VersionManifestJson.Version[] versionList;
    public EditText editText_maxMemory, editText_javaArgs, editText_minecraftArgs;
    //用于存储当前显示的layout的id值
    public int layout_here_Id = R.id.layout_fictionlist;
    public TextView logText, main_text_showstate, gamelist_text_show_slectedversion;
    public int selectedVersionPos = -1;
    public File LauncherConfigFile;
    public ReadyToStart toStart;
    public Button dialog_button_confrom_createuser, dialog_button_cancle_createuser;
    public EditText dialog_editText_username, dialog_editText_userpasswd;
    public LinearLayout dialog_linearlayout_userpasswd;
    public CheckBox dialog_checkBox_usermodel;
    public ConfigDialog userCreateDialog;
    public ConfigDialog downloaderDialog;
    public ConfigDialog languageDialog;
    public String DATA_PATH;
    public Animation ShowAnim,HideAnim;
    public Button launcher_refresh,launcher_info;
    public BubbleLayout bubbleLayout_tipper;
    public PopupWindow popupWindow;
    public Timer timer_tipper = new Timer();
    public ArrayList<TipperListBean> tipslist;
    public TextView runtime_info;
    public com.aof.mcinabox.minecraft.DownloadMinecraft mDownloadMinecraft;
    private FileDownloadQueueSet queueSet;
    private ArrayList<BaseDownloadTask> downloadTasks = new ArrayList<BaseDownloadTask>();
    private ProgressBar downloader_total_process,downloader_current_process;
    private TextView downloader_total_count,downloader_current_count,downloader_current_task,downloader_target_version;
    private String Language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Activity生命周期开始，执行初始化
        super.onCreate(savedInstanceState);

        //显示activity_main为当前Activity布局
        setContentView(R.layout.activity_main);

        //初始化控件
        InitUI();

        //配置MCinaBox的全局目录
        LauncherConfigFile = new File(MCINABOX_HOME + "/mcinabox.json");
        DATA_PATH = "";

        //请求软件所需的权限
        requestPermission();

        //载入启动器配置文件
        initLauncher();

        //添加无媒体文件标签
        SetMCinaBoxNoMedia();

        //删除tmp文件夹
        RemoveTmpFloder();

        //初始化下载器
        FileDownloader.setup(this);
        queueSet = new FileDownloadQueueSet(downloadListener);
        mDownloadMinecraft = new com.aof.mcinabox.minecraft.DownloadMinecraft();

        //执行自动刷新
        timer_tipper.schedule(TipperTask,1000,3000);

    }

    private void InitUI(){
        //使用Toolbar作为Actionbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //初始化用户创建界面
        userCreateDialog = new ConfigDialog(MainActivity.this, R.layout.dialog_createuser, true);
        dialog_button_confrom_createuser = userCreateDialog.findViewById(R.id.dialog_button_confirm_createuser);
        dialog_button_cancle_createuser = userCreateDialog.findViewById(R.id.dialog_button_cancle_createuser);
        dialog_editText_username = userCreateDialog.findViewById(R.id.dialog_edittext_input_username);
        dialog_editText_userpasswd = userCreateDialog.findViewById(R.id.dialog_edittext_input_userpasswd);
        dialog_linearlayout_userpasswd = userCreateDialog.findViewById(R.id.dialog_linearlayout_input_userpasswd);
        dialog_checkBox_usermodel = userCreateDialog.findViewById(R.id.dialog_checkbox_online_model);
        dialog_checkBox_usermodel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)dialog_linearlayout_userpasswd.setVisibility(View.VISIBLE);
                else dialog_linearlayout_userpasswd.setVisibility(View.GONE);
            }
        });

        //初始化下载器交互界面
        downloaderDialog = new ConfigDialog(MainActivity.this,R.layout.dialog_download,false);
        downloader_total_process = downloaderDialog.findViewById(R.id.dialog_total_process);
        downloader_current_process = downloaderDialog.findViewById(R.id.dialog_current_process);
        downloader_total_count = downloaderDialog.findViewById(R.id.dialog_total_count);
        downloader_current_count = downloaderDialog.findViewById(R.id.dialog_current_count);
        downloader_current_task = downloaderDialog.findViewById(R.id.dialog_process_name);
        downloader_target_version = downloaderDialog.findViewById(R.id.dialog_version_id);
        download_ok = downloaderDialog.findViewById(R.id.dialog_download_ok);
        download_cancle = downloaderDialog.findViewById(R.id.dialog_download_cancle);
        download_ok.setOnClickListener(listener);
        download_cancle.setOnClickListener(listener);

        //初始化语言选择器
        languageDialog = new ConfigDialog(MainActivity.this,R.layout.dialog_languages,false);
        dialog_listview_languages = languageDialog.findViewById(R.id.dialog_listview_languages);
        dialog_listview_languages.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                ChangeLauncherLanguage(dialog_listview_languages.getAdapter().getItem(pos).toString());
                languageDialog.dismiss();
            }
        });

        bubbleLayout_tipper = (BubbleLayout) LayoutInflater.from(this).inflate(R.layout.layout_popup_tipper, null);
        popupWindow = BubblePopupHelper.create(this, bubbleLayout_tipper);

        //给界面的按键设置按键监听
        button_user = findViewById(R.id.main_button_user);
        button_gameselected = findViewById(R.id.main_button_gameselected);
        button_gamelist = findViewById(R.id.main_button_gamelist);
        button_gamedir = findViewById(R.id.main_button_gamedir);
        button_launchersetting = findViewById(R.id.main_button_launchersetting);
        button_launchercontrol = findViewById(R.id.main_button_launchercontrol);
        toolbar_button_backhome = findViewById(R.id.toolbar_button_backhome);
        toolbar_button_backfromhere = findViewById(R.id.toolbar_button_backfromhere);
        toolbar_button_language = findViewById(R.id.toolbar_button_language);
        radioButton_gamedir_public = findViewById(R.id.radiobutton_gamedir_public);
        radioButton_gamedir_private = findViewById(R.id.radiobutton_gamedir_private);
        ImportRuntime = findViewById(R.id.launchersetting_button_import);
        installForgeInstaller = findViewById(R.id.launchersetting_button_forgeinstaller);
        launcher_info = findViewById(R.id.toolbar_button_taskinfo);
        launcher_refresh = findViewById(R.id.toolbar_button_reflash);
        launcherBts = new Button[]{launcher_refresh,launcher_info,radioButton_gamedir_public, radioButton_gamedir_private, button_user, button_gameselected, button_gamelist, button_gamedir, button_launchersetting, button_launchercontrol, toolbar_button_backhome, toolbar_button_backfromhere,toolbar_button_language ,dialog_button_confrom_createuser, dialog_button_cancle_createuser,ImportRuntime,installForgeInstaller};
        for (Button button : launcherBts) {
            button.setOnClickListener(listener);
        }

        gamelist_button_reflash = findViewById(R.id.gamelist_button_reflash);
        gamelist_button_installnewgame = findViewById(R.id.gamelist_button_installnewgame);
        gamelist_button_backfrom_installnewversion = findViewById(R.id.gamelist_button_backfrom_installnewversion);
        gamelist_button_setting = findViewById(R.id.gamelist_button_setting);
        gamelist_button_download = findViewById(R.id.gamelist_button_download);
        main_button_startgame = findViewById(R.id.main_button_startgame);
        user_button_adduser = findViewById(R.id.layout_user_adduser);
        user_button_reflash_userlist = findViewById(R.id.layout_user_reflash_userlist);
        gamelist_button_reflash_locallist = findViewById(R.id.gamelist_button_reflash_locallist);
        launcherBts2 = new LinearLayout[]{main_button_startgame, gamelist_button_download, gamelist_button_reflash, gamelist_button_installnewgame, gamelist_button_backfrom_installnewversion, gamelist_button_setting, user_button_adduser, user_button_reflash_userlist, gamelist_button_reflash_locallist};
        for (LinearLayout button : launcherBts2) {
            button.setOnClickListener(listener);
        }

        radioGroup_version_type = findViewById(R.id.radiogroup_version_type);
        radioButton_type_release = findViewById(R.id.radiobutton_type_release);
        radioButton_type_snapshot = findViewById(R.id.radiobutton_type_snapshot);
        radioButton_type_old = findViewById(R.id.radiobutton_type_old);
        radioGroup_version_type.setOnCheckedChangeListener(this);

        setting_keyboard = findViewById(R.id.setting_spinner_keyboard);
        setting_downloadtype = findViewById(R.id.setting_spinner_downloadtype);
        spinner_choice_version = findViewById(R.id.spinner_choice_version);

        spinner_runtimepacks = findViewById(R.id.launchersetting_spinner_runtimepack);
        spinner_forgeinstaller = findViewById(R.id.launchersetting_spinner_forgeinstaller);

        editText_javaArgs = findViewById(R.id.setting_edit_javaargs);
        editText_minecraftArgs = findViewById(R.id.setting_edit_minecraftargs);
        editText_maxMemory = findViewById(R.id.setting_edit_maxmemory);

        setting_notcheckJvm = findViewById(R.id.setting_switch_notcheckjvm);
        setting_notcheckMinecraft = findViewById(R.id.setting_switch_notcheckminecraft);

        //将所有的linearlayout和scrollview布局都作为view处理
        layout_user = findViewById(R.id.layout_user);
        layout_gameselected = findViewById(R.id.layout_gameselected);
        layout_gamelist = findViewById(R.id.layout_gamelist);
        layout_gamedir = findViewById(R.id.layout_gamedir);
        layout_launchersetting = findViewById(R.id.layout_launchersetting);
        layout_gamelist_installversion = findViewById(R.id.layout_gamelist_installversion);
        layout_gamelist_setting = findViewById(R.id.layout_gamelist_setting);
        layout_startgame = findViewById(R.id.layout_startgame);
        launcherLins = new View[]{layout_startgame, layout_user, layout_gameselected, layout_gamelist, layout_gamedir, layout_launchersetting, layout_gamelist_installversion, layout_gamelist_setting};

        //初始化ListView控件
        listview_minecraft_manifest = findViewById(R.id.list_minecraft_manifest);
        listview_minecraft_manifest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                selectedVersionPos = pos;
                gamelist_text_show_slectedversion.setText(listview_minecraft_manifest.getAdapter().getItem(pos).toString());
            }
        });

        //runtime_info
        runtime_info = findViewById(R.id.runtime_info);

        //tipper
        listView_tipper = bubbleLayout_tipper.findViewById(R.id.tipper_list);

        //user_list
        listview_user = findViewById(R.id.list_user);

        //localversion_list
        listView_localversion = findViewById(R.id.list_local_version);

        //动画
        ShowAnim = AnimationUtils.loadAnimation(this,R.anim.layout_show);
        HideAnim = AnimationUtils.loadAnimation(this,R.anim.layout_hide);

        //初始化LogTextView控件
        logText = findViewById(R.id.logTextView);
        gamelist_text_show_slectedversion = findViewById(R.id.gamelist_text_show_selectedversion);
        main_text_showstate = findViewById(R.id.main_text_showstate);

    }

    //重写boolean onCreatOptionsMenu(Menu menu)方法实现Toolbar的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //重写boolean onOptionsItemSelected(MenuItem item)方法实现Toolbar的菜单的按键监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.toolbar_action1:
                //ToolBar菜单的按键监听
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

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

    //Button数组launchbts中的按键监听
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch (arg0.getId()) {
                case R.id.main_button_user:
                    //具体点击操作的逻辑
                    SetOnlyVisibleTargetView(layout_user);
                    main_text_showstate.setText(getString(R.string.title_user));
                    break;
                case R.id.main_button_gameselected:
                    SetOnlyVisibleTargetView(layout_gameselected);
                    main_text_showstate.setText(getString(R.string.title_manager));
                    break;
                case R.id.main_button_gamelist:
                    SetOnlyVisibleTargetView(layout_gamelist);
                    main_text_showstate.setText(getString(R.string.main_text_gamelist));
                    break;
                case R.id.main_button_gamedir:
                    SetOnlyVisibleTargetView(layout_gamedir);
                    main_text_showstate.setText(getString(R.string.main_text_gamedir));
                    break;
                case R.id.main_button_launchersetting:
                    SetOnlyVisibleTargetView(layout_launchersetting);
                    main_text_showstate.setText(getString(R.string.main_text_launchersetting));
                    break;
                case R.id.main_button_launchercontrol:
                    //页面跳转
                    Intent intent = new Intent(getApplicationContext(), VirtualKeyBoardActivity.class);
                    startActivity(intent);
                    break;
                case R.id.gamelist_button_reflash:
                    downloadTasks.add(mDownloadMinecraft.createVersionManifestDownloadTask());
                    StartDownloadQueueSet(queueSet,downloadTasks);
                    break;
                case R.id.gamelist_button_installnewgame:
                    SetOnlyVisibleTargetView(layout_gamelist_installversion);
                    ReflashOnlineGameListWhenClick();
                    main_text_showstate.setText(getString(R.string.title_install_newversion) + " - " + getString(R.string.main_text_gamelist));
                    break;
                case R.id.gamelist_button_backfrom_installnewversion:
                    SetOnlyVisibleTargetView(layout_gamelist);
                    main_text_showstate.setText(getString(R.string.main_text_gamelist));
                    break;
                case R.id.gamelist_button_setting:
                    SetOnlyVisibleTargetView(layout_gamelist_setting);
                    main_text_showstate.setText(getString(R.string.title_setting_minecraft) + " - " + getString(R.string.main_text_gamelist));
                    break;
                case R.id.gamelist_button_download:
                    DownloadSelectedVersion();
                    break;
                case R.id.toolbar_button_backhome:
                    SetOnlyVisibleTargetView(layout_startgame);
                    main_text_showstate.setText(getString(R.string.main_text_defaultlayout));
                    break;
                case R.id.toolbar_button_backfromhere:
                    setBackFromHere(layout_here_Id);
                    break;
                case R.id.radiobutton_gamedir_public:
                    radioButton_gamedir_public.setChecked(true);
                    radioButton_gamedir_private.setChecked(false);
                    DATA_PATH = MCINABOX_DATA_PUBLIC;
                    SaveLauncherSettingToFile(LauncherConfigFile);
                    initLauncher();
                    break;
                case R.id.radiobutton_gamedir_private:
                    radioButton_gamedir_private.setChecked(true);
                    radioButton_gamedir_public.setChecked(false);
                    DATA_PATH = MCINABOX_DATA_PRIVATE;
                    SaveLauncherSettingToFile(LauncherConfigFile);
                    initLauncher();
                    break;

                case R.id.main_button_startgame:
                    if(CheckUsersData(SaveLauncherSettingToFile(LauncherConfigFile))) {
                        toStart = new ReadyToStart(getApplicationContext(), MCINABOX_VERSION, DATA_PATH, spinner_choice_version.getSelectedItem().toString(), setting_keyboard.getSelectedItem().toString());
                        toStart.StartGame();
                    }else{
                        Toast.makeText(getApplicationContext(), getString(R.string.tips_check_setting), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.spinner_choice_version:
                    ReflashLocalVersionList();
                    break;
                case R.id.dialog_button_confirm_createuser:
                    CreateNewUser();
                    userCreateDialog.dismiss();
                    break;
                case R.id.dialog_button_cancle_createuser:
                    userCreateDialog.cancel();
                    break;
                case R.id.layout_user_adduser:
                    userCreateDialog.show();
                    break;
                case R.id.layout_user_reflash_userlist:
                    ReflashLocalUserList(true);
                    break;
                case R.id.gamelist_button_reflash_locallist:
                    ReflashLocalVersionList();
                    break;
                case R.id.launchersetting_button_import:
                    InstallRuntime();
                    break;
                case R.id.toolbar_button_reflash:
                    SaveLauncherSettingToFile(LauncherConfigFile);
                    initLauncher();
                    Toast.makeText(getApplication(), getString(R.string.tips_reflash_finish), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.toolbar_button_taskinfo:
                    ShowPopupTipperUnderView(arg0);
                    break;
                case R.id.dialog_download_ok:
                    downloaderDialog.dismiss();
                    break;
                case R.id.dialog_download_cancle:
                    downloaderDialog.dismiss();
                    FileDownloader.getImpl().pauseAll();
                    break;
                case R.id.toolbar_button_language:
                    languageDialog.show();
                    break;
                case R.id.launchersetting_button_forgeinstaller:
                    installForgeFromInstaller();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 【下载从网络版本列表中选择的版本】
     **/
    private void DownloadSelectedVersion() {
        if(versionList == null){
            Toast.makeText(this, getString(R.string.tips_online_version_reflash), Toast.LENGTH_SHORT).show();
            return;
        }
        if(selectedVersionPos == -1){
            Toast.makeText(this, getString(R.string.tips_online_version_select), Toast.LENGTH_SHORT).show();
            return;
        }

        StartDownloadDialog(listview_minecraft_manifest.getAdapter().getItem(selectedVersionPos).toString());

    }


    /**
     * 【更新网络版本列表】
     **/
    //可以通过版本类型将版本分类并更新列表
    //使用前必须保证更新一次版本清单文件
    private void ReflashOnlineVersionList() {
        //获取实例化后的versionList
        versionList = com.aof.mcinabox.minecraft.JsonUtils.getVersionManifestFromFile(MCINABOX_TEMP + "/version_manifest.json").getVersions();
        String[] nameList;

        ArrayList<VersionManifestJson.Version> version_type_release = new ArrayList<VersionManifestJson.Version>() {
        };
        ArrayList<VersionManifestJson.Version> version_type_snapsht = new ArrayList<VersionManifestJson.Version>() {
        };
        ArrayList<VersionManifestJson.Version> version_type_old = new ArrayList<VersionManifestJson.Version>() {
        };

        for (VersionManifestJson.Version version : versionList) {
            switch (version.getType()) {
                default:
                    break;
                case "release":
                    version_type_release.add(version);
                    break;
                case "snapshot":
                    version_type_snapsht.add(version);
                    break;
                case "old_beta":
                    version_type_old.add(version);
                    break;
                case "old_alpha":
                    version_type_old.add(version);
                    break;
            }
        }

        switch (radioGroup_version_type.getCheckedRadioButtonId()) {
            default:
                nameList = new String[0];
                break;
            case R.id.radiobutton_type_release:
                nameList = new String[version_type_release.size()];
                for (int i = 0; i < version_type_release.size(); i++) {
                    nameList[i] = version_type_release.get(i).getId();
                }
                break;
            case R.id.radiobutton_type_snapshot:
                nameList = new String[version_type_snapsht.size()];
                for (int i = 0; i < version_type_snapsht.size(); i++) {
                    nameList[i] = version_type_snapsht.get(i).getId();
                }
                break;
            case R.id.radiobutton_type_old:
                nameList = new String[version_type_old.size()];
                for (int i = 0; i < version_type_old.size(); i++) {
                    nameList[i] = version_type_old.get(i).getId();
                }
                break;
        }

        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nameList);
        listview_minecraft_manifest.setAdapter(adapter);
    }

    /**
     * 【主界面切换】
     **/
    //可以隐藏其他全部的主界面(数组launcherLins内的)，只显示传入的界面
    //实现界面切换的目的
    private void SetOnlyVisibleTargetView(View view) {
        for (View tempview : launcherLins) {
            if(view.getVisibility() == View.VISIBLE){
                view.setAnimation(HideAnim);
            }
            tempview.setVisibility(View.INVISIBLE);
        }
        if (view != null) {
            view.startAnimation(ShowAnim);
            view.setVisibility(View.VISIBLE);
            layout_here_Id = view.getId();
        } else {
            return;
        }
    }

    /**
     * 【当Activity销毁时】
     **/
    @Override
    public void onDestroy() {
        super.onDestroy();
        SaveLauncherSettingToFile(LauncherConfigFile);
    }

    /**【当Activity停止时】**/
    @Override
    public void onStop(){
        super.onStop();
        SaveLauncherSettingToFile(LauncherConfigFile);
    }

    /**
     * 【当版本类型发生变化时】
     **/
    @Override
    public void onCheckedChanged(RadioGroup radioGroup_version_type, int checkedId) {
        if (versionList != null) {
            ReflashOnlineVersionList();
            switch (checkedId) {
                case R.id.radiobutton_type_release:
                    //Toast.makeText(this, "稳定版", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.radiobutton_type_snapshot:
                    //Toast.makeText(this, "测试版", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.radiobutton_type_old:
                    //Toast.makeText(this, "远古版", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(this, getString(R.string.tips_online_version_nodata), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 【设定全局返回键的执行逻辑和顶部指示器】
     **/
    //可以通过传入的位置来设定ToolBar右侧返回键和系统返回键的执行逻辑，并设定ToolBar右侧指示器。
    //必须在每一次界面切换时执行一次，以设定正确的执行逻辑
    private void setBackFromHere(int location) {
        switch (location) {
            default:
                SetOnlyVisibleTargetView(layout_startgame);
                main_text_showstate.setText(getString(R.string.main_text_defaultlayout));
                break;
            case R.id.layout_user:
            case R.id.layout_gameselected:
            case R.id.layout_gamelist:
            case R.id.layout_gamedir:
            case R.id.layout_launchersetting:
                SetOnlyVisibleTargetView(layout_startgame);
                main_text_showstate.setText(getString(R.string.main_text_defaultlayout));
                layout_here_Id = R.id.layout_fictionlist;
                break;
            case R.id.layout_gamelist_installversion:
            case R.id.layout_gamelist_setting:
                SetOnlyVisibleTargetView(layout_gamelist);
                main_text_showstate.setText(getString(R.string.main_text_gamelist));
                layout_here_Id = R.id.layout_gamelist;
                break;
            case R.id.layout_fictionlist:
                finish();
                break;
        }
    }

    //将字符串与Spinner中的字符串进行匹配，然后返回匹配的位置上的id
    private int getSpinnerFitString(Spinner spinner, String tag) {
        int pos = -1;
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (tag.equals(spinner.getItemAtPosition(i))) {
                pos = i;
            }
        }
        if (pos == -1) {
            //TODO:需要自动删除错误的模板，再关闭程序，以免下次启动仍遇到问题。
            Toast.makeText(this, getString(R.string.tips_launcher_init_fail) + " " + tag, Toast.LENGTH_SHORT).show();
        }
        return pos;
    }


    /**
     * 【保存启动器配置到配置文件】
     **/
    //可以得到现在的启动器配置并将其写入到本地配置文件 mcinabox.json 中
    //必须在关闭启动器时执行一次，以保存启动器配置
    //可以根据情况传入isContinueUsing参数，来设定配置文件的isUsing参数
    //但是你必须保证，如果你已经同时启用了共有目录和私有目录，不要将两个模板的isUsing设定为相同的值
    //你还可以传入configFile参数，来将配置存储到特定的配置文件中
    private SettingJson SaveLauncherSettingToFile(File configFile) {
        if (configFile == null) {
            configFile = new File(MCINABOX_FILE_JSON);
        }
        Gson gson = new Gson();

        //存储全部启动器设置到模板对象中
        SettingJson settingModel = new SettingJson();
        SettingJson.Configurations configurations = settingModel.getConfigurations();

        //将当前所有用户的信息存入模板对象
        SettingJson.Accounts[] accounts;
        if (listview_user.getAdapter() == null) {
            accounts = new SettingJson.Accounts[0];
        } else {
            accounts = new SettingJson.Accounts[listview_user.getAdapter().getCount()];
            for (int i = 0; i < listview_user.getAdapter().getCount(); i++) {
                SettingJson.Accounts account = new SettingJson().newAccounts;
                UserListBean user = (UserListBean) listview_user.getAdapter().getItem(i);
                account.setSelected(user.isIsSelected());
                account.setUsername(user.getUser_name());
                account.setType(user.getUser_model());
                account.setUuid(user.getAuth_UUID());
                account.setAccessToken(user.getAuth_Access_Token());

                accounts[i] = account;
            }
        }
        settingModel.setAccounts(accounts);

        //给模板对象设定参数
        settingModel.setLanguage(Language);
        settingModel.setDownloadType((String) setting_downloadtype.getSelectedItem());
        settingModel.setKeyboard((String) setting_keyboard.getSelectedItem());

        if(editText_maxMemory.getText().toString().equals("")){
            configurations.setMaxMemory(0);
        }else{
            configurations.setMaxMemory(Integer.parseInt((String) editText_maxMemory.getText().toString()));
        }
        configurations.setJavaArgs(editText_javaArgs.getText().toString());
        configurations.setMinecraftArgs(editText_minecraftArgs.getText().toString());
        configurations.setNotCheckGame(setting_notcheckMinecraft.isChecked());
        configurations.setNotCheckJvm(setting_notcheckJvm.isChecked());

        if (radioButton_gamedir_public.isChecked()) {
            settingModel.setLocalization("public");
        } else if (radioButton_gamedir_private.isChecked()) {
            settingModel.setLocalization("private");
        }

        //写出模板对象到启动器配置文件
        String jsonString = gson.toJson(settingModel);
        try {
            FileWriter jsonWriter = new FileWriter(configFile);
            BufferedWriter out = new BufferedWriter(jsonWriter);
            out.write(jsonString);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.tips_launcher_save_fail), Toast.LENGTH_SHORT).show();
            Log.e("saveLauncher ", e.toString());
        }
        //返回一个模板对象
        return settingModel;
    }

    /**
     * 【检查MCinaBox的目录结构是否正常】
     **/
    //可以检查MCinaBox必要的目录结构，如果目录结构不完整将自动创建目录
    //必须在启动时执行一次，如果目录结构不完整将会导致启动器崩溃
    private void CheckMcinaBoxDir() {
        for(String path : MCINABOX_ALLPATH){
            FileTool.checkFilePath(new File(path), true);
        }
    }

    /**
     * 【刷新本地游戏列表】
     **/
    //可以根据游戏目录下的version文件夹和文件夹下是否存在同名jar,json文件来判断是否有这一版本
    //然后将这些版本存入列表中并执行刷新
    //必须在启动器配置时使用一次，以显示本地游戏列表
    //也可再根据情况使用。
    private ArrayList<LocalVersionListBean> localversionList;
    private ArrayList<String> versionIdList;

    public void ReflashLocalVersionList() {
        PathTool pathTool = new PathTool(DATA_PATH);
        ArrayList<String> versionIdListTmp;
        try {
            versionIdListTmp = FileTool.listChildDirFromTargetDir(pathTool.getMINECRAFT_VERSION_DIR());
        }catch(NullPointerException e){
            e.printStackTrace();
            versionIdListTmp = new ArrayList<String>(){};
        }
        ArrayList<String> versionIdList = new ArrayList<String>();
        ArrayList<LocalVersionListBean> mlocalversionList = new ArrayList<LocalVersionListBean>();
        for (String fileName : versionIdListTmp) {
            if ((new File(pathTool.getMINECRAFT_VERSION_DIR() + fileName + "/" + fileName + ".json")).exists()) {
                versionIdList.add(fileName);
            }
        }
        for (String fileName : versionIdList) {
            LocalVersionListBean localVersionListBean = new LocalVersionListBean();
            localVersionListBean.setVersion_Id(fileName);
            mlocalversionList.add(localVersionListBean);
        }

        if(listView_localversion.getAdapter() == null){
            this.localversionList = mlocalversionList;
            LocalVersionListAdapter localversionlistadapter = new LocalVersionListAdapter(this, this.localversionList);
            listView_localversion.setAdapter(localversionlistadapter);
        }else{
            this.localversionList.clear();
            this.localversionList.addAll(mlocalversionList);
            ((BaseAdapter)listView_localversion.getAdapter()).notifyDataSetChanged();
        }

        if(spinner_choice_version.getAdapter() == null){
            this.versionIdList = versionIdList;
            ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.versionIdList);
            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_choice_version.setAdapter(mAdapter);
        }else{
            this.versionIdList.clear();
            this.versionIdList.addAll(versionIdList);
            ((BaseAdapter)spinner_choice_version.getAdapter()).notifyDataSetChanged();
        }
    }


    /**
     * 【刷新本地用户列表】
     **/
    //可以根据启动器设置，配置用户列表并刷新
    //必须在启动器配置时使用一次，以显示用户列表
    //也可再根据情况使用，使用前请保证启动器设置模板为最新状态
    private ArrayList<UserListBean> userlist = new ArrayList<UserListBean>(){};
    private ArrayList<UserListBean> ReflashLocalUserList(boolean isSaveBeforeReflash) {
        if (isSaveBeforeReflash) {
            SaveLauncherSettingToFile(LauncherConfigFile);
        }
        SettingJson.Accounts[] accounts = CheckLauncherSettingFile().getAccounts();
        ArrayList<UserListBean> tmp = new ArrayList<UserListBean>(){};
        if (accounts == null) {
            userlist = new ArrayList<UserListBean>(){};
        } else {
            for (SettingJson.Accounts account : accounts) {
                UserListBean user = new UserListBean();
                user.setUser_name(account.getUsername());
                user.setUser_model(account.getType());
                user.setIsSelected(account.isSelected());
                user.setAuth_UUID(account.getUuid());
                user.setAuth_Access_Token(account.getAccessToken());
                user.setContext(this);
                tmp.add(user);
            }
        }
        userlist = tmp;
        if(listview_user.getAdapter() == null){
            UserListAdapter userlistadapter = new UserListAdapter(this, userlist);
            listview_user.setAdapter(userlistadapter);
        }else{
            listview_user.deferNotifyDataSetChanged();
        }

        return userlist;
    }


    /**
     * 【刷新键盘模板列表】
     **/
    private ArrayList<String> KeyboardList = new ArrayList<String>();
    private void ReflashLocalKeyboardList() {
        ArrayList<String> KeyboardList = new ArrayList<String>();
        File file = new File(MCINABOX_KEYBOARD+"/");
        File[] files = file.listFiles();
        if (files == null) {
            this.KeyboardList.clear();

        } else {
            for (File targetFile : files) {
                KeyboardList.add(targetFile.getName());
            }
            if(setting_keyboard.getAdapter() == null){
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.KeyboardList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                setting_keyboard.setAdapter(adapter);
            }else{
                this.KeyboardList.clear();
                this.KeyboardList.addAll(KeyboardList);
                ((BaseAdapter)setting_keyboard.getAdapter()).notifyDataSetChanged();
            }

        }
    }

    /**
     * 【添加一个新用户】
     **/
    private void CreateNewUser() {
        ReflashLocalUserList(true);
		
        ArrayList<UserListBean> userlist = ReflashLocalUserList(false);
		UserListBean newUser = new UserListBean();
		String username = dialog_editText_username.getText().toString();
		String userpasswd = dialog_editText_userpasswd.getText().toString();
		
		if (username.equals("")) {
			Toast.makeText(this, getString(R.string.tips_user_nousername), Toast.LENGTH_SHORT).show();
			return;
		}
		if (userlist != null) {
			for (UserListBean user : userlist) {
				if (user.getUser_name().equals(username)) {
					Toast.makeText(this, getString(R.string.tips_user_sameusername), Toast.LENGTH_SHORT).show();
					return;
				}
			}
		}
		
        if (dialog_checkBox_usermodel.isChecked()) {
			Toast.makeText(this, getString(R.string.tips_login_wait), Toast.LENGTH_SHORT).show();
            new Login(this).execute(username, userpasswd);
        } else {
            newUser.setUser_name(username);
            newUser.setUser_model("offline");
            newUser.setIsSelected(false);
			newUser.setAuth_UUID(UUID.nameUUIDFromBytes((username).getBytes()).toString());
			newUser.setAuth_Access_Token("0");
            userlist.add(newUser);
            UserListAdapter userlistadapter = new UserListAdapter(this, userlist);
            listview_user.setAdapter(userlistadapter);
            Toast.makeText(this, getString(R.string.tips_add_success), Toast.LENGTH_SHORT).show();
            ReflashLocalUserList(true);
        }
    }

    public void OnlineLogin(String e) {
        if(e == null){
            UserListBean newUser = new UserListBean();
			
			SharedPreferences prefs = this.getSharedPreferences("launcher_prefs", 0);
			String accessToken = prefs.getString("auth_accessToken", "0");
			String userUUID = prefs.getString("auth_profile_id", "00000000-0000-0000-0000-000000000000");
			String username = prefs.getString("auth_profile_name", "Player");

            newUser.setUser_name(username);
            newUser.setUser_model("online");
            newUser.setIsSelected(false);
			newUser.setAuth_UUID(userUUID);
			newUser.setAuth_Access_Token(accessToken);
            userlist.add(newUser);
            UserListAdapter userlistadapter = new UserListAdapter(this, userlist);
            listview_user.setAdapter(userlistadapter);
            Toast.makeText(this, getString(R.string.tips_add_success), Toast.LENGTH_SHORT).show();
            ReflashLocalUserList(true);
        } else Toast.makeText(this, e, Toast.LENGTH_SHORT).show();
    }

    /**
     * 【检查启动器模板】
     **/
    //可以检查启动器设置文件是否存在
    //若不存在则先创建新的文件
    //若存在则直接读入文件并返回一个启动器设置对象
    private SettingJson CheckLauncherSettingFile() {
        File configFile = LauncherConfigFile;
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
     * 【应用启动器设置】
     **/
    //可以将启动器设置对象应用到启动其中。
    private void ApplyLauncherSetting(SettingJson Setting) {
        try {
            setting_downloadtype.setSelection(getSpinnerFitString(setting_downloadtype, Setting.getDownloadType()));
            editText_javaArgs.setText(Setting.getConfigurations().getJavaArgs());
            editText_minecraftArgs.setText(Setting.getConfigurations().getMinecraftArgs());
            editText_maxMemory.setText((Integer.valueOf(Setting.getConfigurations().getMaxMemory())).toString());
            setting_notcheckJvm.setChecked(Setting.getConfigurations().isNotCheckJvm());
            setting_notcheckMinecraft.setChecked(Setting.getConfigurations().isNotCheckGame());
            Language = Setting.getLanguage();

            if (Setting.getLocalization().equals("public")) {
                DATA_PATH = MCINABOX_DATA_PUBLIC;
                radioButton_gamedir_public.setChecked(true);
                radioButton_gamedir_private.setChecked(false);
            } else {
                DATA_PATH = MCINABOX_DATA_PRIVATE;
                radioButton_gamedir_public.setChecked(false);
                radioButton_gamedir_private.setChecked(true);
            }


        } catch (NullPointerException e) {
            //如果读入的数据缺少参数，则删除掉并重新初始化。
            Toast.makeText(this, getString(R.string.tips_launcher_load_bad), Toast.LENGTH_SHORT).show();
            Log.e("ApplyLauncherSetting ", e.toString());
            LauncherConfigFile.delete();
            initLauncher();
        }

    }

    /**
     * 【从配置文件载入启动器配置并应用】
     **/
    //可以读取上一次使用的配置文件并应用于启动器
    //必须在启动器启动时执行一次，以设定启动的配置
    private void initLauncher() {
        //启动器初始化
        CheckMcinaBoxDir();
        SettingJson ConfigFile = CheckLauncherSettingFile();
        ApplyLauncherSetting(ConfigFile);
        //以下为载入启动器全局配置之后才能完成的设定
        ReflashLocalVersionList();
        ReflashLocalUserList(false);
        ReflashLocalKeyboardList();
        ReflashRuntimePackList();
        ReflashForgeInstallerList();
        RefreshRuntimePackInfo();
        GetAvailableMemories();
    }


    private void InstallRuntime() {
        String packagePath = null;
        if(spinner_runtimepacks.getSelectedItem() == null){
            Toast.makeText(this, getString(R.string.tips_runtime_notfound), Toast.LENGTH_SHORT).show();
            return;
        }else{
            packagePath = MCINABOX_DATA_RUNTIME + "/" + spinner_runtimepacks.getSelectedItem().toString();
        }
        final String mpackagePath = packagePath;
        Log.e("PackagePack",packagePath);

        new Thread() {
            @Override
            public void run() {
                File packageFile = new File(mpackagePath);
                if (!packageFile.exists()) {
                    Message msg_1 = new Message();
                    msg_1.what = 4;
                    handler.sendMessage(msg_1);
                    return;
                }
                Message msg_2 = new Message();
                Message msg_3 = new Message();
                msg_2.what = 5;
                handler.sendMessage(msg_2);
                Utils.extractTarXZ(mpackagePath, getDir("runtime", 0));
                if (Utils.setExecutable(getDir("runtime", 0))) {
                    msg_3.what = 6;
                    handler.sendMessage(msg_3);
                } else {
                    msg_3.what = 7;
                    handler.sendMessage(msg_3);
                }
            }
        }.start();


    }

    ArrayList<String> runtimelist = new ArrayList<String>();
    private void ReflashRuntimePackList(){
        ArrayList<String> packlist = new ArrayList<String>();
        File file = new File(MCINABOX_DATA_RUNTIME+"/");
        File[] files = file.listFiles();
        if (files == null) {
            //nothing.
            if(spinner_runtimepacks.getAdapter() != null){
                runtimelist.clear();
                ((BaseAdapter)spinner_runtimepacks.getAdapter()).notifyDataSetChanged();
            }
        } else {
            for (File targetFile : files) {
                packlist.add(targetFile.getName());
            }
            if (spinner_runtimepacks.getAdapter() == null){
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.runtimelist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_runtimepacks.setAdapter(adapter);
            }else{
                this.runtimelist.clear();
                this.runtimelist.addAll(packlist);
                ((BaseAdapter)spinner_runtimepacks.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    ArrayList<String> forgeInstallerList = new ArrayList<>();
    private void ReflashForgeInstallerList(){
        ArrayList<String> packlist = new ArrayList<String>();
        File file = new File(FORGEINSTALLER_HOME+"/");
        File[] files = file.listFiles();
        if (files == null) {
            //nothing.
            if(spinner_forgeinstaller.getAdapter() != null){
                forgeInstallerList.clear();
                ((BaseAdapter)spinner_forgeinstaller.getAdapter()).notifyDataSetChanged();
            }
        } else {
            for (File targetFile : files) {
                packlist.add(targetFile.getName());
            }
            if (spinner_forgeinstaller.getAdapter() == null){
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.forgeInstallerList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_forgeinstaller.setAdapter(adapter);
            }else{
                this.forgeInstallerList.clear();
                this.forgeInstallerList.addAll(packlist);
                ((BaseAdapter)spinner_forgeinstaller.getAdapter()).notifyDataSetChanged();
            }
        }
    }


    private boolean CheckUsersData(SettingJson setting){
        return (tipslist.size() == 0);
    }

    /**【在View下方显示Tipper】**/
    private void ShowPopupTipperUnderView(View arg0){
        TipperListAdapter tipperListAdapter = new TipperListAdapter(getApplication(), tipslist);
        listView_tipper.setAdapter(tipperListAdapter);
        int[] location = new int[2];
        arg0.getLocationInWindow(location);
        bubbleLayout_tipper.setArrowDirection(ArrowDirection.TOP);
        popupWindow.showAtLocation(arg0, Gravity.NO_GRAVITY, location[0], arg0.getHeight() + location[1]);
    }

    private TimerTask TipperTask = new TimerTask() {
        @Override
        public void run() {
            ArrayList<Integer> tip_indexs = new ArrayList<>();
            SettingJson setting = SaveLauncherSettingToFile(LauncherConfigFile);
            Message msg_1 = new Message();
            msg_1.what = 1;
            handler.sendMessage(msg_1);

            boolean User_isSelected = false;
            boolean Keyboard_isSelected = false;
            boolean Minecraft_isSelected = false;
            boolean Runtime_isImported = true;

            //检查用户是否选择
            SettingJson.Accounts[] accounts = setting.getAccounts();
            for(SettingJson.Accounts p1 : accounts){
                if(p1.isSelected()){
                    User_isSelected = true;
                }
            }

            //检查键盘模版是否选择
            if(setting_keyboard.getSelectedItem() != null){
                Keyboard_isSelected = true;
            }

            //检查游戏版本是否选择
            if(spinner_choice_version.getSelectedItem() != null){
                Minecraft_isSelected = true;
            }

            //检查运行库是否导入
            for(String p1 : MCINABOX_RUNTIME_FILES) {
                if (!FileTool.isFileExists(p1)) {
                    Runtime_isImported = false;
                }
            }

            //检查内存大小设置是否正确
            if(!editText_maxMemory.getText().toString().equals("") && Integer.parseInt((String) editText_maxMemory.getText().toString()) >= 128 && Integer.parseInt((String) editText_maxMemory.getText().toString()) <= 1024){
                //nothing
            }else{
                tip_indexs.add(5);
            }

            if(User_isSelected && Keyboard_isSelected && Minecraft_isSelected && Runtime_isImported ){
                if(listView_tipper.getAdapter() == null || listView_tipper.getAdapter().getCount() != 0){
                    tipslist = new ArrayList<TipperListBean>(){};
                    Message msg_2 = new Message();
                    msg_2.what = 3;
                    handler.sendMessage(msg_2);
                }
                return;
            }else{
                Message msg_2 = new Message();
                msg_2.what = 2;
                handler.sendMessage(msg_2);
                if(!User_isSelected){
                    tip_indexs.add(1);
                }
                if(!Keyboard_isSelected){
                    tip_indexs.add(3);
                }
                if(!Minecraft_isSelected){
                    tip_indexs.add(2);
                }
                if(!Runtime_isImported){
                    tip_indexs.add(4);
                }
            }

            if(tip_indexs.size() != 0){
                ArrayList<TipperListBean> tipperlist = new ArrayList<TipperListBean>();
                for (int index : tip_indexs){
                    TipperListBean tmp = new TipperListBean();
                    tmp.setContext(getApplication());
                    tmp.setTipper_index(index);
                    tipperlist.add(tmp);
                }
                tipslist = tipperlist;
            }

        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1:
                    ReflashLocalVersionList();
                    ReflashLocalUserList(false);
                    ReflashLocalKeyboardList();
                    ReflashRuntimePackList();
                    ReflashForgeInstallerList();
                    RefreshRuntimePackInfo();
                    GetAvailableMemories();
                    break;
                case 2:
                    launcher_info.setBackground(getResources().getDrawable(R.drawable.ic_info_red_500_24dp));
                    break;
                case 3:
                    launcher_info.setBackground(getResources().getDrawable(R.drawable.ic_info_outline_blue_500_24dp));
                    break;
                case 4:
                    Toast.makeText(getApplication(), getString(R.string.tips_runtime_notfound), Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(getApplication(), getString(R.string.tips_runtime_installing), Toast.LENGTH_SHORT).show();
                    ImportRuntime.setClickable(false);
                    break;
                case 6:
                    Toast.makeText(getApplication(), getString(R.string.tips_runtime_install_success), Toast.LENGTH_SHORT).show();
                    ImportRuntime.setClickable(true);
                    break;
                case 7:
                    Toast.makeText(getApplication(), getString(R.string.tips_runtime_install_fail) + " " + getString(R.string.tips_runtime_install_fail_exeable), Toast.LENGTH_SHORT).show();
                    ImportRuntime.setClickable(true);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**【刷新运行库信息】**/
    private void RefreshRuntimePackInfo(){
        String info = com.aof.mcinabox.launcher.JsonUtils.getPackInformation(this);
        if (info.equals("")){
            runtime_info.setText(getString(R.string.tips_import_runtime));
        }else{
            runtime_info.setText(info);
        }
    }

    private void GetAvailableMemories(){
        ((TextView)findViewById(R.id.game_setting_text_memory)).setText(MemoryUtils.getTotalMemory(getApplication()));
    }

    private void SetMCinaBoxNoMedia(){
        File file = new File(MCINABOX_DATA_PUBLIC + "/.nomedia");
        File file2 = new File(MCINABOX_DATA_PRIVATE + "/.nomedia");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!file2.exists()){
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void RemoveTmpFloder(){
        FileTool.deleteDir(MCINABOX_TEMP);
        FileTool.makeFloder(MCINABOX_TEMP);
    }

    public void ReflashOnlineGameListWhenClick(){
        if(listview_minecraft_manifest.getAdapter() == null){
            gamelist_button_reflash.performClick();
        }else{
            //nothing
        }
    }

    //下载监听器
    private int totalProcess = 0; //0 ~ 4
    private int finishCount = 0;
    private String minecraftId ="";
    private FileDownloadListener downloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            if(totalProcess == 1 || totalProcess == 3){
                ChangeDownloadPrcess(totalProcess,soFarBytes*100/totalBytes);
            }
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            //如果完成的任务为清单文件下载，就刷新版本列表
            finishCount++;

            if(task.getFilename().equals("version_manifest.json")){
                downloadTasks.clear();
                ReflashOnlineVersionList();
                finishCount = 0;
                return;
            }

            if(finishCount == downloadTasks.size() && totalProcess != 4){
                finishCount = 0;
                downloadTasks.clear();
                totalProcess++;
                StartDownloadMinecraft(totalProcess,minecraftId);
            }else if(finishCount == downloadTasks.size() && (totalProcess == 4 || totalProcess == 6)){
                finishCount = 0;
                downloadTasks.clear();
                totalProcess =0;
                ChangeDownloadPrcess(5,100);
            }
            if(totalProcess == 2||totalProcess == 4||totalProcess == 5){
                ChangeDownloadPrcess(totalProcess,(finishCount*100)/downloadTasks.size());
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            //任务全部取消
            Log.e("Downloader",e.toString());
            Toast.makeText(getApplication(), getString(R.string.tips_download_failed), Toast.LENGTH_SHORT).show();
            download_cancle.performClick();
        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    };

    //并行方式执行下载队列
    private void StartDownloadQueueSet(FileDownloadQueueSet queueSet,ArrayList<BaseDownloadTask> downloadTasks){
        //queueSet.disableCallbackProgressTimes();
        queueSet.downloadTogether(downloadTasks);
        queueSet.start();
    }

    private void StartDownloadDialog(String id){
        //init
        downloaderDialog.findViewById(R.id.dialog_download_finish).setVisibility(View.GONE);
        downloaderDialog.findViewById(R.id.dialog_total_count).setVisibility(View.VISIBLE);
        downloaderDialog.findViewById(R.id.dialog_download_ok).setClickable(false);
        downloader_target_version.setText(id);
        downloader_current_task.setText("");
        downloader_total_process.setProgress(0);
        downloader_current_process.setProgress(0);
        downloader_current_count.setText("0%");
        downloader_total_count.setText("0/0");
        minecraftId = id;
        finishCount = 0;
        downloadTasks.clear();
        //show
        downloaderDialog.show();
        totalProcess = 1;
        ChangeDownloadPrcess(1,0);
        //auto download files
        StartDownloadMinecraft(1,id);

    }
    private void ChangeDownloadPrcess(int taskId,int currentProcess){
        String task = "",totalCount = "";
        int totalProcess = 0;
        switch(taskId){
            case 1:
                task = getString(R.string.tips_download_version_json);
                totalCount = "1/4";
                totalProcess = 25;
                break;
            case 2:
                task = getString(R.string.tips_download_version_libraries);
                totalCount = "2/4";
                totalProcess = 50;
                break;
            case 3:
                task = getString(R.string.tips_download_assets_index);
                totalCount = "3/4";
                totalProcess = 75;
                break;
            case 4:
                task = getString(R.string.tips_download_assets_object);
                totalCount = "4/4";
                totalProcess = 100;
                break;
            case 5:
                task = getString(R.string.tips_download_finish);
                totalCount = "4/4";
                totalProcess = 100;
                download_ok.setClickable(true);
                downloaderDialog.findViewById(R.id.dialog_download_finish).setVisibility(View.VISIBLE);
                downloaderDialog.findViewById(R.id.dialog_total_count).setVisibility(View.GONE);
                break;
        }
        downloader_current_task.setText(task);
        downloader_total_count.setText(totalCount);
        downloader_total_process.setProgress(totalProcess);
        downloader_current_count.setText(currentProcess + "%");
        downloader_current_process.setProgress(currentProcess);
    }

    private void StartDownloadMinecraft(int totalProcess,String id){
        switch(totalProcess){
            case 1:
                downloadTasks.add(mDownloadMinecraft.createVersionJsonDownloadTask(id));
                StartDownloadQueueSet(queueSet,downloadTasks);
                break;
            case 2:
                downloadTasks.add(mDownloadMinecraft.createVersionJarDownloadTask(id));
                downloadTasks.addAll(mDownloadMinecraft.createLibrariesDownloadTask(id));
                StartDownloadQueueSet(queueSet,downloadTasks);
                break;
            case 3:
                downloadTasks.add(mDownloadMinecraft.createAssetIndexDownloadTask(id));
                StartDownloadQueueSet(queueSet,downloadTasks);
                break;
            case 4:
                downloadTasks.addAll(mDownloadMinecraft.createAssetObjectsDownloadTask(id));
                StartDownloadQueueSet(queueSet,downloadTasks);
                break;
            case 5:
                downloadTasks.addAll(mDownloadMinecraft.createForgeDownloadTask(id));
                StartDownloadQueueSet(queueSet,downloadTasks);
                break;
        }
    }

    private void ChangeLauncherLanguage(String language){
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = LanguageUtils.getLocaleFromConfig(language);
        resources.updateConfiguration(config, dm);
        ReStartLauncher();
    }

    private void ReStartLauncher(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }

    private void installForgeFromInstaller(){
        String filename;
        if(spinner_forgeinstaller.getSelectedItem() != null){
            filename = spinner_forgeinstaller.getSelectedItem().toString();
        }else{
            return;
        }
        ForgeInstaller installer = new ForgeInstaller(getApplication());
        try {
            installer.unzipForgeInstaller(filename);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplication(), getString(R.string.tips_unzip_failed), Toast.LENGTH_SHORT).show();
            return;
        }
        String id = installer.makeForgeData();

        //init
        downloaderDialog.findViewById(R.id.dialog_download_finish).setVisibility(View.GONE);
        downloaderDialog.findViewById(R.id.dialog_total_count).setVisibility(View.VISIBLE);
        downloaderDialog.findViewById(R.id.dialog_download_ok).setClickable(false);
        downloader_target_version.setText(id);
        downloader_current_task.setText("");
        downloader_total_process.setProgress(0);
        downloader_current_process.setProgress(0);
        downloader_current_count.setText("0%");
        downloader_total_count.setText("0/0");
        finishCount = 0;
        downloadTasks.clear();
        //show
        downloaderDialog.show();
        totalProcess = 6;
        ChangeDownloadPrcess(6,0);
        //auto download files
        StartDownloadMinecraft(5,id);
    }

}

