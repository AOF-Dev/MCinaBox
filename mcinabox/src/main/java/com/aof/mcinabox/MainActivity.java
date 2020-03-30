package com.aof.mcinabox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aof.mcinabox.Launcher.LauncherSettingModel;
import com.aof.mcinabox.RuntimePack.AnaliesRuntimeJson;
import com.aof.mcinabox.Tipper.TipperListAdapter;
import com.aof.mcinabox.Tipper.TipperListBean;
import com.aof.mcinabox.Utils.FileTool;
import com.aof.mcinabox.Utils.MemoryUtils;
import com.aof.mcinabox.Utils.PathTool;
import com.aof.mcinabox.Version.AnaliesMinecraftAssetJson;
import com.aof.mcinabox.Version.AnaliesMinecraftVersionJson;
import com.aof.mcinabox.Version.AnaliesVersionManifestJson;
import com.aof.mcinabox.Version.ListVersionManifestJson;
import com.aof.mcinabox.Version.ModelMinecraftAssetsJson;
import com.aof.mcinabox.Version.ModelMinecraftVersionJson;
import com.aof.mcinabox.Keyboard.ConfigDialog;
import com.aof.mcinabox.Version.LocalVersionListAdapter;
import com.aof.mcinabox.Version.LocalVersionListBean;
import com.aof.mcinabox.User.UserListAdapter;
import com.aof.mcinabox.User.UserListBean;
import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import cosine.boat.Utils;

import static com.aof.mcinabox.DataPathManifest.*;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    public Button[] launcherBts;
    public Button button_user, button_gameselected, button_gamelist, button_gamedir, button_launchersetting, button_launchercontrol, toolbar_button_backhome, toolbar_button_backfromhere,ImportRuntime;
    public RadioGroup radioGroup_version_type;
    public RadioButton radioButton_type_release, radioButton_type_snapshot, radioButton_type_old;
    public RadioButton radioButton_gamedir_public, radioButton_gamedir_private;
    public Spinner setting_downloadtype, setting_keyboard, spinner_choice_version,spinner_runtimepacks;
    public Switch setting_notcheckJvm, setting_notcheckMinecraft;
    public LinearLayout[] launcherBts2;
    public LinearLayout gamelist_button_reflash, gamelist_button_installnewgame, gamelist_button_backfrom_installnewversion, gamelist_button_setting, main_button_startgame, gamelist_button_download, user_button_adduser, gamelist_button_reflash_locallist, user_button_reflash_userlist;
    public View[] launcherLins;
    public View layout_user, layout_gamelist, layout_gameselected, layout_gamedir, layout_launchersetting, layout_gamelist_installversion, layout_gamelist_setting, layout_startgame;
    public ListView listview_minecraft_manifest, listview_user, listView_localversion, listView_tipper;
    public ListVersionManifestJson.Version[] versionList;
    public EditText editText_maxMemory, editText_javaArgs, editText_minecraftArgs;
    //用于存储当前显示的layout的id值
    public int layout_here_Id = R.id.layout_fictionlist;
    public TextView logText, main_text_showstate, gamelist_text_show_slectedversion;
    private BroadcastReceiver broadcastReceiver1;
    public ListVersionManifestJson.Version selectedVersion;
    public ModelMinecraftVersionJson selectedVersionJson;
    public int selectedVersionPos = -1;
    public File LauncherConfigFile;
    public ReadyToStart toStart;
    public Button dialog_button_confrom_createuser, dialog_button_cancle_createuser;
    public EditText dialog_editText_username, dialog_editText_access;
    public CheckBox dialog_checkBox_usermodel;
    public ConfigDialog userCreateDialog;
    public String DATA_PATH;
    public Animation ShowAnim,HideAnim;
    public Button launcher_refresh,launcher_info;
    public BubbleLayout bubbleLayout_tipper;
    public PopupWindow popupWindow;
    public Timer timer_tipper = new Timer();
    public ArrayList<TipperListBean> tipslist;
    public TextView runtime_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Activity生命周期开始，执行初始化
        super.onCreate(savedInstanceState);

        //显示activity_main为当前Activity布局
        setContentView(R.layout.activity_main);

        //使用Toolbar作为Actionbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //初始化用户创建界面
        userCreateDialog = new ConfigDialog(MainActivity.this, R.layout.dialog_createuser, true);
        dialog_button_confrom_createuser = userCreateDialog.findViewById(R.id.dialog_button_confirm_createuser);
        dialog_button_cancle_createuser = userCreateDialog.findViewById(R.id.dialog_button_cancle_createuser);
        dialog_editText_username = userCreateDialog.findViewById(R.id.dialog_edittext_input_username);
        dialog_editText_access = userCreateDialog.findViewById(R.id.dialog_edittext_input_access);
        dialog_checkBox_usermodel = userCreateDialog.findViewById(R.id.dialog_checkbox_online_model);

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
        radioButton_gamedir_public = findViewById(R.id.radiobutton_gamedir_public);
        radioButton_gamedir_private = findViewById(R.id.radiobutton_gamedir_private);
        ImportRuntime = findViewById(R.id.launchersetting_button_import);
        launcher_info = findViewById(R.id.toolbar_button_taskinfo);
        launcher_refresh = findViewById(R.id.toolbar_button_reflash);
        launcherBts = new Button[]{launcher_refresh,launcher_info,radioButton_gamedir_public, radioButton_gamedir_private, button_user, button_gameselected, button_gamelist, button_gamedir, button_launchersetting, button_launchercontrol, toolbar_button_backhome, toolbar_button_backfromhere, dialog_button_confrom_createuser, dialog_button_cancle_createuser,ImportRuntime};
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

        //测试user_list
        listview_user = findViewById(R.id.list_user);

        //测试localversion_list
        listView_localversion = findViewById(R.id.list_local_version);

        //动画
        ShowAnim = AnimationUtils.loadAnimation(this,R.anim.layout_show);
        HideAnim = AnimationUtils.loadAnimation(this,R.anim.layout_hide);

        //初始化LogTextView控件
        logText = findViewById(R.id.logTextView);
        gamelist_text_show_slectedversion = findViewById(R.id.gamelist_text_show_selectedversion);

        main_text_showstate = findViewById(R.id.main_text_showstate);


        /*
            控件初始化完成，进入启动器的全局配置阶段
         */

        //配置MCinaBox的全局目录
        LauncherConfigFile = new File(MCINABOX_HOME + "/mcinabox.json");
        DATA_PATH = "";

        //请求软件所需的权限
        requestPermission();

        //载入启动器配置文件
        initLauncher();

        //执行自动刷新
        timer_tipper.schedule(TipperTask,1000,3000);

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
            case R.id.toolbar_action1:
                //ToolBar菜单的按键监听
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestPermission() {
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
                    //这里使用了多线程
                    Thread syncTask = new Thread() {
                        @Override
                        public void run() {
                            // 执行操作
                            Looper.prepare();
                            long Id = DownloadVersionList();
                            listener1(Id);
                            Looper.loop();
                        }
                    };
                    syncTask.start();
                    break;
                case R.id.gamelist_button_installnewgame:
                    SetOnlyVisibleTargetView(layout_gamelist_installversion);
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
                default:
                    break;
            }
        }
    };


    /**
     * 【下载清单列表文件】
     **/
    private long DownloadVersionList() {
        DownloadMinecraft downloadTask = new DownloadMinecraft(GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(), 0), GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(), 1), DATA_PATH);
        return (downloadTask.UpdateVersionManifestJson(this));
    }

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
        DownloadMinecraft downloadTask = new DownloadMinecraft(GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(), 0), GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(), 1), DATA_PATH);
        long taskId;
        ListVersionManifestJson.Version targetVer = null;
        for (ListVersionManifestJson.Version version : versionList) {
            if (version.getId().equals(listview_minecraft_manifest.getItemAtPosition(selectedVersionPos))) {
                targetVer = version;
            }
        }
        if (targetVer == null) {
            Toast.makeText(this, getString(R.string.tips_online_version_notfound), Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, getString(R.string.tips_online_version_downloadstart) + " " + targetVer.getId(), Toast.LENGTH_SHORT).show();
        taskId = downloadTask.DownloadMinecraftVersionJson(targetVer.getId(), targetVer.getUrl(), this);
        listener2(taskId);
        selectedVersion = targetVer;
    }

    /**
     * 【下载依赖库文件】
     **/
    private void DownloadVersionLibraries() {
        DownloadMinecraft downloadTask = new DownloadMinecraft(GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(), 0), GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(), 1), DATA_PATH);
        ModelMinecraftVersionJson version = new AnaliesMinecraftVersionJson().getModelMinecraftVersionJson(downloadTask.getMINECRAFT_VERSION_DIR() + selectedVersion.getId() + "/" + selectedVersion.getId() + ".json");
        //执行minecraft jar文件的下载
        downloadTask.DownloadMinecraftJar(version.getId(), version.getDownloads().getClient().getUrl(), this);
        //执行minecraft 依赖库的下载
        for (int i = 0; i < version.getLibraries().length; i++) {
            if (version.getLibraries()[i].getDownloads().getArtifact() != null) {
                downloadTask.DownloadMinecraftDependentLibraries(version.getLibraries()[i].getDownloads().getArtifact().getPath(), version.getLibraries()[i].getDownloads().getArtifact().getUrl(), this);
            }
        }
        //执行资源索引文件的下载
        selectedVersionJson = version;
        long taskId = downloadTask.DownloadMinecraftAssetJson(version.getAssetIndex().getId(), version.getAssetIndex().getUrl(), this);
        listener3(taskId);

    }

    /**
     * 【下载资源文件】
     **/
    //可以获取minecraft游戏资源文件
    //请保证使用前存在对应的资源索引文件
    private void DownloadVersionAssets() {
        DownloadMinecraft downloadTask = new DownloadMinecraft(GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(), 0), GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(), 1), DATA_PATH);
        ModelMinecraftAssetsJson assets = new AnaliesMinecraftAssetJson().getModelMinecraftAssetsJson(downloadTask.getMINECRAFT_ASSETS_DIR() + "indexes/" + selectedVersionJson.getAssetIndex().getId() + ".json");
        Set<String> keySets = assets.getObjects().keySet();
        //Toast.makeText(this, "共有 " + keySets.size() + " 个资源文件需要下载", Toast.LENGTH_SHORT).show();
        //利用了Iterator迭代器
        Iterator<String> it = keySets.iterator();
        while (it.hasNext()) {
            //得到每一个key
            String key = it.next();
            //通过key获取对应的value
            downloadTask.DownloadMinecraftAssetFile(assets.getObjects().get(key).getHash(), this);
        }
    }

    /**
     * 【更新网络版本列表】
     **/
    //可以通过版本类型将版本分类并更新列表
    //使用前必须保证更新一次版本清单文件
    private void ReflashOnlineVersionList() {
        DownloadMinecraft downloadTask = new DownloadMinecraft(GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(), 0), GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(), 1), DATA_PATH);
        //获取实例化后的versionList
        versionList = new AnaliesVersionManifestJson().getVersionList(MCINABOX_TEMP + "/version_manifest.json");
        String[] nameList;

        ArrayList<ListVersionManifestJson.Version> version_type_release = new ArrayList<ListVersionManifestJson.Version>() {
        };
        ArrayList<ListVersionManifestJson.Version> version_type_snapsht = new ArrayList<ListVersionManifestJson.Version>() {
        };
        ArrayList<ListVersionManifestJson.Version> version_type_old = new ArrayList<ListVersionManifestJson.Version>() {
        };

        for (ListVersionManifestJson.Version version : versionList) {
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
     * 【监听版本清单更新】
     **/
    //可以监听版本清单的更新，如果更新成功，将会自动刷新网络版本列表
    private void listener1(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {

                    ReflashOnlineVersionList();
                    Toast.makeText(getApplicationContext(), getString(R.string.tips_online_version_refresh_finish), Toast.LENGTH_LONG).show();
                }
            }
        };
        registerReceiver(broadcastReceiver1, intentFilter);
    }

    /**
     * 【监听version.json的下载】
     **/
    //可以监听version.json的下载，如果下载成功，将会自动执行依赖库的下载
    private void listener2(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    DownloadVersionLibraries();
                }
            }
        };
        registerReceiver(broadcastReceiver1, intentFilter);
    }

    /**
     * 【监听资源索引文件的下载】
     **/
    //可以监听资源索引文件的下载，如果下成功，将会自动执行资源文件的下载
    private void listener3(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    Thread syncTask = new Thread() {
                        @Override
                        public void run() {
                            // 执行操作
                            Looper.prepare();
                            DownloadVersionAssets();
                            Looper.loop();
                        }
                    };
                    syncTask.start();
                }
            }
        };
        registerReceiver(broadcastReceiver1, intentFilter);
    }

    /**
     * 【当Activity销毁时】
     **/
    @Override
    public void onDestroy() {
        super.onDestroy();
        SaveLauncherSettingToFile(LauncherConfigFile);
        if (broadcastReceiver1 != null) {
            unregisterReceiver(broadcastReceiver1);
        }
    }

    /**【当Activity停止时】**/
    @Override
    public void onStop(){
        super.onStop();
        SaveLauncherSettingToFile(LauncherConfigFile);
    }

    /**【当Activity】**/

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


    private int getKeyboardSpinnerFitString(Spinner spinner, String tag) {
        int pos = -1;
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
    private LauncherSettingModel SaveLauncherSettingToFile(File configFile) {
        if (configFile == null) {
            configFile = new File(MCINABOX_FILE_JSON);
        }
        Gson gson = new Gson();

        //存储全部启动器设置到模板对象中
        LauncherSettingModel settingModel = new LauncherSettingModel();
        LauncherSettingModel.Configurations configurations = settingModel.getConfigurations();

        //将当前所有用户的信息存入模板对象
        LauncherSettingModel.Accounts[] accounts;
        if (listview_user.getAdapter() == null) {
            accounts = new LauncherSettingModel.Accounts[0];
        } else {
            accounts = new LauncherSettingModel.Accounts[listview_user.getAdapter().getCount()];
            for (int i = 0; i < listview_user.getAdapter().getCount(); i++) {
                LauncherSettingModel.Accounts account = new LauncherSettingModel().newAccounts;
                UserListBean user = (UserListBean) listview_user.getAdapter().getItem(i);
                account.setSelected(user.isIsSelected());
                account.setUsername(user.getUser_name());
                account.setType(user.getUser_model());
                account.setUuid(UUID.nameUUIDFromBytes((user.getUser_name()).getBytes()).toString());
                account.setAccessToken("0");
                accounts[i] = account;
            }
        }
        settingModel.setAccounts(accounts);

        //给模板对象设定参数
        settingModel.setDownloadType((String) setting_downloadtype.getSelectedItem());
        settingModel.setKeyboard((String) setting_keyboard.getSelectedItem());
        configurations.setMaxMemory(Integer.parseInt((String) editText_maxMemory.getText().toString()));
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
     * 【根据下载文件的类型和下载源类型得到一个下载地址】
     */
    //可以通过下载源类型和下载文件类型得到一个下载地址
    //在调用DownloadMinecraft时必须使用本方法，来传入正确的文件下载服务器地址
    //FileType含义: 0 - "version_manifext.json""version.json""assetIndex.json" 1 - "assets/objects/*"
    public String GetDownloadServerUrl(String DownloadType, int FileType) {
        String url;
        switch (FileType) {
            case 0:
                switch (DownloadType) {
                    case "bmclapi":
                    case "mcbbs":
                    case "official":
                    default:
                        url = DOWNLOAD_SOURCE_OFFICIAL_MINECRAFT;
                        break;
                }
                break;
            case 1:
                switch (DownloadType) {
                    case "bmclapi":
                    case "mcbbs":
                    case "official":
                    default:
                        url = DOWNLOAD_SOURCE_OFFICIAL_RESOURCES;
                        break;
                }
                break;
            default:
                url = null;
                break;
        }
        return url;
    }


    /**
     * 【检查目标配置文件是否正在被使用】
     * 【已弃用】
     **/
    //可以检查目标配置文件是否是上一次保存时使用的
    //必须在启动时使用一次，来确定上一次使用的配置文件是哪一个目录类型中的
    public boolean CheckConfigIsUsing(File file) {
        Gson gson = new Gson();
        InputStream inputStream;
        Reader reader;
        LauncherSettingModel settingModel = null;
        try {
            inputStream = new FileInputStream(file);
            reader = new InputStreamReader(inputStream);
            settingModel = new Gson().fromJson(reader, LauncherSettingModel.class);
            if (settingModel == null) {
                return false;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //return settingModel.isUsing();
        return false;
    }

    /**
     * 【检查MCinaBox的目录结构是否正常】
     **/
    //可以检查MCinaBox必要的目录结构，如果目录结构不完整将自动创建目录
    //必须在启动时执行一次，如果目录结构不完整将会导致启动器崩溃
    public void CheckMcinaBoxDir() {
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
    public ArrayList<LocalVersionListBean> localversionList;
    public ArrayList<String> versionIdList;

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
            if ((new File(pathTool.getMINECRAFT_VERSION_DIR() + fileName + "/" + fileName + ".jar")).exists() && (new File(pathTool.getMINECRAFT_VERSION_DIR() + fileName + "/" + fileName + ".json")).exists()) {
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
    ArrayList<UserListBean> userlist = new ArrayList<UserListBean>(){};
    public ArrayList<UserListBean> ReflashLocalUserList(boolean isSaveBeforeReflash) {
        if (isSaveBeforeReflash) {
            SaveLauncherSettingToFile(LauncherConfigFile);
        }
        LauncherSettingModel.Accounts[] accounts = CheckLauncherSettingFile().getAccounts();
        ArrayList<UserListBean> tmp = new ArrayList<UserListBean>(){};
        if (accounts == null) {
            userlist = new ArrayList<UserListBean>(){};
        } else {
            for (LauncherSettingModel.Accounts account : accounts) {
                UserListBean user = new UserListBean();
                user.setUser_name(account.getUsername());
                user.setUser_model(account.getType());
                user.setIsSelected(account.isSelected());
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
    ArrayList<String> KeyboardList = new ArrayList<String>();
    public void ReflashLocalKeyboardList() {
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
    public void CreateNewUser() {
        ReflashLocalUserList(true);
        ArrayList<UserListBean> userlist = ReflashLocalUserList(false);
        UserListBean newUser = new UserListBean();
        String username = dialog_editText_username.getText().toString();
        String access = dialog_editText_access.getText().toString();
        String usermodel;
        if (dialog_checkBox_usermodel.isChecked()) {
            usermodel = "online";
        } else {
            usermodel = "offline";
        }

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
        newUser.setUser_name(username);
        newUser.setUser_model(usermodel);
        newUser.setIsSelected(false);
        userlist.add(newUser);
        UserListAdapter userlistadapter = new UserListAdapter(this, userlist);
        listview_user.setAdapter(userlistadapter);
        Toast.makeText(this, getString(R.string.tips_add_success), Toast.LENGTH_SHORT).show();
        ReflashLocalUserList(true);
    }

    /**
     * 【检查启动器模板】
     **/
    //可以检查启动器设置文件是否存在
    //若不存在则先创建新的文件
    //若存在则直接读入文件并返回一个启动器设置对象
    public LauncherSettingModel CheckLauncherSettingFile() {
        File configFile = LauncherConfigFile;
        Gson gson = new Gson();
        InputStream inputStream;
        Reader reader;
        LauncherSettingModel settingModel = null;

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
            settingModel = new LauncherSettingModel();
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
                settingModel = new Gson().fromJson(reader, LauncherSettingModel.class);
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
    public void ApplyLauncherSetting(LauncherSettingModel Setting) {
        try {
            setting_downloadtype.setSelection(getSpinnerFitString(setting_downloadtype, Setting.getDownloadType()));
            editText_javaArgs.setText(Setting.getConfigurations().getJavaArgs());
            editText_minecraftArgs.setText(Setting.getConfigurations().getMinecraftArgs());
            editText_maxMemory.setText((Integer.valueOf(Setting.getConfigurations().getMaxMemory())).toString());
            setting_notcheckJvm.setChecked(Setting.getConfigurations().isNotCheckJvm());
            setting_notcheckMinecraft.setChecked(Setting.getConfigurations().isNotCheckGame());

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
    public void initLauncher() {
        //启动器初始化
        CheckMcinaBoxDir();
        LauncherSettingModel ConfigFile = CheckLauncherSettingFile();
        ApplyLauncherSetting(ConfigFile);
        //以下为载入启动器全局配置之后才能完成的设定
        ReflashLocalVersionList();
        ReflashLocalUserList(false);
        ReflashLocalKeyboardList();
        ReflashRuntimePackList();
        RefreshRuntimePackInfo();
        GetAvailableMemories();
    }


    public void InstallRuntime() {
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
    public void ReflashRuntimePackList(){
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
    public boolean CheckUsersData(LauncherSettingModel setting){
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

    public TimerTask TipperTask = new TimerTask() {
        @Override
        public void run() {
            ArrayList<Integer> tip_indexs = new ArrayList<>();
            LauncherSettingModel setting = SaveLauncherSettingToFile(LauncherConfigFile);
            Message msg_1 = new Message();
            msg_1.what = 1;
            handler.sendMessage(msg_1);

            boolean User_isSelected = false;
            boolean Keyboard_isSelected = false;
            boolean Minecraft_isSelected = false;
            boolean Runtime_isImported = true;

            //检查用户是否选择
            LauncherSettingModel.Accounts[] accounts = setting.getAccounts();
            for(LauncherSettingModel.Accounts p1 : accounts){
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

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1:
                    ReflashLocalVersionList();
                    ReflashLocalUserList(false);
                    ReflashLocalKeyboardList();
                    ReflashRuntimePackList();
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

    /**【刷新运行库信息】**/
    public void RefreshRuntimePackInfo(){
        String info = (new AnaliesRuntimeJson()).GetInformation(getApplication());
        if (info == null || info.equals("")){
            runtime_info.setText(getString(R.string.tips_import_runtime));
        }else{
            runtime_info.setText(info);
        }
    }

    public void GetAvailableMemories(){
        ((TextView)findViewById(R.id.game_setting_text_memory)).setText(MemoryUtils.getTotalMemory(getApplication()));
    }

}
