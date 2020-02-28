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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aof.mcinabox.initUtils.LauncherSettingModel;
import com.aof.mcinabox.ioUtils.FileTool;
import com.aof.mcinabox.jsonUtils.AnaliesMinecraftAssetJson;
import com.aof.mcinabox.jsonUtils.AnaliesMinecraftVersionJson;
import com.aof.mcinabox.jsonUtils.AnaliesVersionManifestJson;
import com.aof.mcinabox.jsonUtils.ListVersionManifestJson;
import com.aof.mcinabox.jsonUtils.ModelMinecraftAssetsJson;
import com.aof.mcinabox.jsonUtils.ModelMinecraftVersionJson;
import com.aof.mcinabox.loaclVersionUtil.LocalVersionListAdapter;
import com.aof.mcinabox.loaclVersionUtil.LocalVersionListBean;
import com.aof.mcinabox.userUtil.UserListAdapter;
import com.aof.mcinabox.userUtil.UserListBean;
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
import java.util.Set;
import java.util.UUID;

import cosine.boat.LauncherActivity;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    public Button[] launcherBts;
    public Button button_user, button_gameselected, button_gamelist, button_gamedir, button_launchersetting, button_launchercontrol, toolbar_button_backhome, toolbar_button_backfromhere;


    public RadioGroup radioGroup_version_type;
    public RadioButton radioButton_type_release, radioButton_type_snapshot, radioButton_type_old;
    public RadioButton radioButton_gamedir_public, radioButton_gamedir_private;

    public Spinner setting_java, setting_opengl, setting_openal, setting_lwjgl, setting_runtime, setting_downloadtype, setting_keyboard,spinner_choice_version;

    public Switch setting_notcheckJvm, setting_notcheckMinecraft, setting_notenableKeyboard, setting_enableOtg;

    public LinearLayout[] launcherBts2;
    public LinearLayout gamelist_button_reflash, gamelist_button_installnewgame, gamelist_button_backfrom_installnewversion, gamelist_button_setting, main_button_startgame, gamelist_button_download;

    public View[] launcherLins;
    public View layout_user, layout_gamelist, layout_gameselected, layout_gamedir, layout_launchersetting, layout_gamelist_installversion, layout_gamelist_setting, layout_startgame;

    public ListView listview_minecraft_manifest, listview_user,listView_localversion;

    public ListVersionManifestJson.Version[] versionList;

    public EditText editText_maxMemory, editText_javaArgs, editText_minecraftArgs;


    //用于存储当前显示的layout的id值
    public int layout_here_Id = R.id.layout_fictionlist;

    public TextView logText, main_text_showstate, gamelist_text_show_slectedversion;
    private BroadcastReceiver broadcastReceiver1;
    private BroadcastReceiver broadcastReceiver2;
    private BroadcastReceiver broadcastReceiver3;

    public ListVersionManifestJson.Version selectedVersion;
    public ModelMinecraftVersionJson selectedVersionJson;
    public int selectedVersionPos = -1;
    public String MCinaBox_HomePath, MCinaBox_PublicPath, MCinaBox_PrivatePath;
    public File publicConfigFile, privateConfigFile;
    public ReadyToStart toStart;
    //加载当前设置，也加载另一份设置
    public LauncherSettingModel anotherSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Activity生命周期开始，执行初始化
        super.onCreate(savedInstanceState);


        //显示activity_main为当前Activity布局
        setContentView(R.layout.activity_main);

        //使用Toolbar作为Actionbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //请求软件所需的权限
        requestPermission();

        //MCinaBox的全局目录
        MCinaBox_PublicPath = "/sdcard/MCinaBox";
        MCinaBox_PrivatePath = getExternalFilesDir(null).getPath() + "/MCinaBox";
        //MCinaBox_HomePath = MCinaBox_PublicPath;
        publicConfigFile = new File(MCinaBox_PublicPath + "/mcinabox.json");
        privateConfigFile = new File(MCinaBox_PrivatePath + "/mcinabox.json");


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
        launcherBts = new Button[]{radioButton_gamedir_public, radioButton_gamedir_private, button_user, button_gameselected, button_gamelist, button_gamedir, button_launchersetting, button_launchercontrol, toolbar_button_backhome, toolbar_button_backfromhere};
        for (Button button : launcherBts) {
            button.setOnClickListener(listener);
        }

        if (Build.VERSION.SDK_INT >= 29) {
            radioButton_gamedir_public.setClickable(false);
        }


        gamelist_button_reflash = findViewById(R.id.gamelist_button_reflash);
        gamelist_button_installnewgame = findViewById(R.id.gamelist_button_installnewgame);
        gamelist_button_backfrom_installnewversion = findViewById(R.id.gamelist_button_backfrom_installnewversion);
        gamelist_button_setting = findViewById(R.id.gamelist_button_setting);
        gamelist_button_download = findViewById(R.id.gamelist_button_download);
        main_button_startgame = findViewById(R.id.main_button_startgame);
        launcherBts2 = new LinearLayout[]{main_button_startgame, gamelist_button_download, gamelist_button_reflash, gamelist_button_installnewgame, gamelist_button_backfrom_installnewversion, gamelist_button_setting};
        for (LinearLayout button : launcherBts2) {
            button.setOnClickListener(listener);
        }

        radioGroup_version_type = findViewById(R.id.radiogroup_version_type);
        radioButton_type_release = findViewById(R.id.radiobutton_type_release);
        radioButton_type_snapshot = findViewById(R.id.radiobutton_type_snapshot);
        radioButton_type_old = findViewById(R.id.radiobutton_type_old);
        radioGroup_version_type.setOnCheckedChangeListener(this);


        setting_java = findViewById(R.id.setting_spinner_java);
        setting_opengl = findViewById(R.id.setting_spinner_opengl);
        setting_openal = findViewById(R.id.setting_spinner_openal);
        setting_keyboard = findViewById(R.id.setting_spinner_keyboard);
        setting_lwjgl = findViewById(R.id.setting_spinner_lwjgl);
        setting_downloadtype = findViewById(R.id.setting_spinner_downloadtype);
        setting_runtime = findViewById(R.id.setting_spinner_runtime);
        spinner_choice_version = findViewById(R.id.spinner_choice_version);

        editText_javaArgs = findViewById(R.id.setting_edit_javaargs);
        editText_minecraftArgs = findViewById(R.id.setting_edit_minecraftargs);
        editText_maxMemory = findViewById(R.id.setting_edit_maxmemory);

        setting_notcheckJvm = findViewById(R.id.setting_switch_notcheckjvm);
        setting_notcheckMinecraft = findViewById(R.id.setting_switch_notcheckminecraft);
        setting_notenableKeyboard = findViewById(R.id.setting_switch_notenable_keyboard);
        setting_enableOtg = findViewById(R.id.setting_switch_enable_otg);

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

        //测试user_list
        listview_user = findViewById(R.id.list_user);


        //测试localversion_list
        listView_localversion = findViewById(R.id.list_local_version);

        //初始化LogTextView控件
        logText = findViewById(R.id.logTextView);
        gamelist_text_show_slectedversion = findViewById(R.id.gamelist_text_show_selectedversion);

        main_text_showstate = findViewById(R.id.main_text_showstate);

        //载入启动器配置文件
        initLauncher(null);
        //以下为载入启动器之后才能完成的设定
        ReflashLocalVersionList();
        ReflashLocalUserList();

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
                    main_text_showstate.setText(getString(R.string.main_text_user));
                    break;
                case R.id.main_button_gameselected:
                    SetOnlyVisibleTargetView(layout_gameselected);
                    main_text_showstate.setText(getString(R.string.main_text_gameselected));
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
                    //main_text_showstate.setText(getString(R.string.main_text_launchercontrol));
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
                            /*
                            //处理完成后给handler发送消息
                            Message msg = new Message();
                            msg.what = COMPLETED;
                            handler.sendMessage(msg);
                            */
                            Looper.loop();
                        }
                    };
                    syncTask.start();
                    break;
                case R.id.gamelist_button_installnewgame:
                    SetOnlyVisibleTargetView(layout_gamelist_installversion);
                    main_text_showstate.setText(getString(R.string.main_text_gamelist_installversion) + " - " + getString(R.string.main_text_gamelist));
                    break;
                case R.id.gamelist_button_backfrom_installnewversion:
                    SetOnlyVisibleTargetView(layout_gamelist);
                    main_text_showstate.setText(getString(R.string.main_text_gamelist));
                    break;
                case R.id.gamelist_button_setting:
                    SetOnlyVisibleTargetView(layout_gamelist_setting);
                    main_text_showstate.setText(getString(R.string.main_text_gamelist_setting) + " - " + getString(R.string.main_text_gamelist));
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
                    SaveLauncherSettingToFile(false,privateConfigFile);
                    MCinaBox_HomePath = MCinaBox_PublicPath;
                    initLauncher(publicConfigFile);
                    break;
                case R.id.radiobutton_gamedir_private:
                    radioButton_gamedir_private.setChecked(true);
                    radioButton_gamedir_public.setChecked(false);
                    SaveLauncherSettingToFile(false,publicConfigFile);
                    MCinaBox_HomePath = MCinaBox_PrivatePath;
                    initLauncher(privateConfigFile);

                    break;

                case R.id.main_button_startgame:
                    //Intent intent_start = new Intent(getApplicationContext(), LauncherActivity.class);
                    //startActivity(intent_start);
                    SaveLauncherSettingToFile(true,new File(MCinaBox_HomePath + "/mcinabox.json"));
                    if (spinner_choice_version.getSelectedItem() != null && !spinner_choice_version.getSelectedItem().equals("")){
                        Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
                        toStart = new ReadyToStart("0.1.0",MCinaBox_HomePath,MCinaBox_PrivatePath,spinner_choice_version.getSelectedItem().toString());
                        toStart.StartGame();
                    }else{
                        Toast.makeText(getApplicationContext(), "请选择游戏版本", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.spinner_choice_version:
                    ReflashLocalVersionList();
                    break;
                default:
                    break;
            }
        }
    };

    //用于接收子线程传来的ui变化
    /*
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == COMPLETED) {
                loadSpinnerVersionList();
            }
        }
    };*/

    /**【下载清单列表文件】**/
    private long DownloadVersionList() {
        //适配SDK29 TODO：适配SDK29的文件操作
        if(Build.VERSION.SDK_INT >= 29){
            DownloadMinecraft downloadTask = new DownloadMinecraft(GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(),0),GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(),1), "/sdcard/Downloads",MCinaBox_PrivatePath);
            return (downloadTask.UpdateVersionManifestJson(this));
        }

        Toast.makeText(this, "当前路径 "+MCinaBox_HomePath, Toast.LENGTH_SHORT).show();
        DownloadMinecraft downloadTask = new DownloadMinecraft(GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(),0), GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(),1),MCinaBox_HomePath,MCinaBox_PrivatePath);
        return (downloadTask.UpdateVersionManifestJson(this));
    }

    /**【下载从网络版本列表中选择的版本】**/
    private void DownloadSelectedVersion() {
        DownloadMinecraft downloadTask = new DownloadMinecraft(GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(),0),GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(),1), MCinaBox_HomePath,MCinaBox_PrivatePath);
        long taskId;
        ListVersionManifestJson.Version targetVer = null;
        for (ListVersionManifestJson.Version version : versionList) {
            if (version.getId().equals(listview_minecraft_manifest.getItemAtPosition(selectedVersionPos))) {
                targetVer = version;
            }
        }
        if (targetVer == null) {
            Toast.makeText(this, "下载失败 未找到对应版本", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "开始下载 " + targetVer.getId(), Toast.LENGTH_SHORT).show();
        taskId = downloadTask.DownloadMinecraftVersionJson(targetVer.getId(), targetVer.getUrl(), this);
        listener2(taskId);
        selectedVersion = targetVer;
    }

    /**【下载依赖库文件】**/
    private void DownloadVersionLibraries() {
        DownloadMinecraft downloadTask = new DownloadMinecraft(GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(),0),GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(),1), MCinaBox_HomePath,MCinaBox_PrivatePath);
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
        long taskId = downloadTask.DownloadMinecraftAssetJson(version.getAssetIndex().getId(),version.getAssetIndex().getUrl(),this);
        listener3(taskId);

    }

    /**【下载资源文件】**/
    //可以获取minecraft游戏资源文件
    //请保证使用前村咋对应的资源索引文件
    private void DownloadVersionAssets(){
        DownloadMinecraft downloadTask = new DownloadMinecraft(GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(),0),GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(),1),MCinaBox_HomePath,MCinaBox_PrivatePath);

        ModelMinecraftAssetsJson assets = new AnaliesMinecraftAssetJson().getModelMinecraftAssetsJson(downloadTask.getMINECRAFT_ASSETS_DIR()+"objects/indexes/"+selectedVersionJson.getAssetIndex().getId()+".json");
        Set<String> keySets = assets.getObjects().keySet();
        Toast.makeText(this, "共有 "+ keySets.size() +" 个资源文件需要下载", Toast.LENGTH_SHORT).show();
        //利用了Iterator迭代器
        Iterator<String> it =keySets.iterator();
        while(it.hasNext()) {
            //得到每一个key
            String key = it.next();
            //通过key获取对应的value
            downloadTask.DownloadMinecraftAssetFile(assets.getObjects().get(key).getHash(),this);
        }
    }

    /**【更新网络版本列表】**/
    //可以通过版本类型将版本分类并更新列表
    //使用前必须保证更新一次版本清单文件
    private void ReflashOnlineVersionList() {
        DownloadMinecraft downloadTask = new DownloadMinecraft(GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(),0),GetDownloadServerUrl(setting_downloadtype.getSelectedItem().toString(),1), MCinaBox_HomePath,MCinaBox_PrivatePath);
        //获取实例化后的versionList
        versionList = new AnaliesVersionManifestJson().getVersionList(downloadTask.getMINECRAFT_TEMP() + "version_manifest.json");
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

    /**【主界面切换】**/
    //可以隐藏其他全部的主界面(数组launcherLins内的)，只显示传入的界面
    //实现界面切换的目的
    private void SetOnlyVisibleTargetView(View view) {
        for (View tempview : launcherLins) {
            tempview.setVisibility(View.INVISIBLE);
        }
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            layout_here_Id = view.getId();
        } else {
            return;
        }
    }

    /**【监听版本清单更新】**/
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
                    Toast.makeText(getApplicationContext(), "版本清单更新完成", Toast.LENGTH_LONG).show();
                }
            }
        };
        registerReceiver(broadcastReceiver1, intentFilter);
    }

    /**【监听version.json的下载】**/
    //可以监听version.json的下载，如果下载成功，将会自动执行依赖库的下载
    private void listener2(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    DownloadVersionLibraries();
                }
            }
        };
        registerReceiver(broadcastReceiver2, intentFilter);
    }

    /**【监听资源索引文件的下载】**/
    //可以监听资源索引文件的下载，如果下成功，将会自动执行资源文件的下载
    private void listener3(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    DownloadVersionAssets();
                }
            }
        };
        registerReceiver(broadcastReceiver2, intentFilter);
    }

    /**【当Activity销毁时】**/
    @Override
    public void onDestroy() {
        super.onDestroy();
        SaveLauncherSettingToFile(true,null);
        if (broadcastReceiver1 != null) {
            unregisterReceiver(broadcastReceiver1);
        }
        if (broadcastReceiver2 != null) {
            unregisterReceiver(broadcastReceiver2);
        }
    }

    /**【当版本类型发生变化时】**/
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
            Toast.makeText(this, "暂无游戏清单数据", Toast.LENGTH_SHORT).show();
        }
    }

    /**【设定全局返回键的执行逻辑和顶部指示器】**/
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


    /**【从配置文件载入启动器配置并应用】**/
    //可以读取上一次使用的配置文件并应用于启动器
    //必须在启动器启动时执行一次，以设定启动的配置
    public void initLauncher(File targetFile) {
        File configFile;

        if (Build.VERSION.SDK_INT >= 29) {
            configFile = privateConfigFile;
            MCinaBox_HomePath = MCinaBox_PrivatePath;
        } else if (publicConfigFile.exists() && CheckConfigIsUsing(publicConfigFile)) {
            configFile = publicConfigFile;
            MCinaBox_HomePath = MCinaBox_PublicPath;
        } else if (privateConfigFile.exists() && CheckConfigIsUsing(privateConfigFile)) {
            configFile = privateConfigFile;
            MCinaBox_HomePath = MCinaBox_PrivatePath;
        } else {
            configFile = publicConfigFile;
            MCinaBox_HomePath = MCinaBox_PublicPath;
        }

        if (targetFile != null) {
            configFile = targetFile;
        }

        Gson gson = new Gson();
        InputStream inputStream;
        Reader reader;
        LauncherSettingModel settingModel = null;

        //检查目录结构是否正常
        CheckMcinaBoxDir();

        //检测启动器配置文件是否存在
        if (!configFile.exists()) {
            //如果不存在，就创建一个空文件
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                //如果创建失败，就退出程序
                e.printStackTrace();
                Toast.makeText(this, "启动器配置模板创建失败", Toast.LENGTH_SHORT).show();
                Log.e("initLauncher ", e.toString());
                finish();
            }
            //初始化模板并写出配置文件
            LauncherSettingModel newModel = new LauncherSettingModel();
            String jsonString = gson.toJson(newModel);
            try {
                FileWriter jsonWriter = new FileWriter(configFile);
                BufferedWriter out = new BufferedWriter(jsonWriter);
                out.write(jsonString);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "启动器配置模板创建失败", Toast.LENGTH_SHORT).show();
                Log.e("initLauncher ", e.toString());
                finish();
            }
            Toast.makeText(this, "已为启动器创建配置模板", Toast.LENGTH_SHORT).show();
        } else {
            //如果文件存在，就读入配置文件
            try {
                inputStream = new FileInputStream(configFile);
                reader = new InputStreamReader(inputStream);
                settingModel = new Gson().fromJson(reader, LauncherSettingModel.class);
                if (settingModel == null) {
                    Toast.makeText(this, "启动器初始化失败", Toast.LENGTH_SHORT).show();
                    finish();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "读入启动器模板失败", Toast.LENGTH_SHORT).show();
                Log.e("initLauncher ", e.toString());
                finish();
            }
            //根据读入的结果应用启动器配置文件
            try {
                setting_java.setSelection(getSpinnerFitString(setting_java, settingModel.getConfigurations().getJava()));
//                setting_opengl.setSelection(getSpinnerFitString(setting_opengl, settingModel.getConfigurations().getOpengl()));
                setting_openal.setSelection(getSpinnerFitString(setting_openal, settingModel.getConfigurations().getOpenal()));
                setting_lwjgl.setSelection(getSpinnerFitString(setting_lwjgl, settingModel.getConfigurations().getLwjgl()));
                setting_runtime.setSelection(getSpinnerFitString(setting_runtime, settingModel.getConfigurations().getRuntime()));
                setting_downloadtype.setSelection(getSpinnerFitString(setting_downloadtype, settingModel.getDownloadType()));
                //setting_keyboard.setSelection(getSpinnerFitString(setting_keyboard,settingModel.getKeyboard()));TODO:keyboard需要单独读取本地数据来处理

                editText_javaArgs.setText(settingModel.getConfigurations().getJavaArgs());
                editText_minecraftArgs.setText(settingModel.getConfigurations().getMinecraftArgs());
                editText_maxMemory.setText((new Integer(settingModel.getConfigurations().getMaxMemory())).toString());

                setting_notcheckJvm.setChecked(settingModel.getConfigurations().isNotCheckJvm());
                setting_notcheckMinecraft.setChecked(settingModel.getConfigurations().isNotCheckGame());
                setting_notenableKeyboard.setChecked(settingModel.getConfigurations().isNotEnableVirtualKeyboard());
                setting_enableOtg.setChecked(settingModel.getConfigurations().isEnableOtg());
                if (configFile.getPath().equals(publicConfigFile.getPath())) {
                    MCinaBox_HomePath = MCinaBox_PublicPath;
                    radioButton_gamedir_public.setChecked(true);
                    radioButton_gamedir_private.setChecked(false);
                } else {
                    MCinaBox_HomePath = MCinaBox_PrivatePath;
                    radioButton_gamedir_public.setChecked(false);
                    radioButton_gamedir_private.setChecked(true);
                }
            } catch (NullPointerException e) {
                //如果读入的数据缺少参数，则删除掉并重新初始化。
                Toast.makeText(this, "配置文件损坏", Toast.LENGTH_SHORT).show();
                Log.e("!!!!", e.toString());
                configFile.delete();
                initLauncher(null);
            }

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
            Toast.makeText(this, "启动器初始化失败 " + tag, Toast.LENGTH_SHORT).show();
        }
        return pos;
    }


    private int getKeyboardSpinnerFitString(Spinner spinner, String tag) {
        int pos = -1;
        return pos;
    }


    /**【保存启动器配置到配置文件】**/
    //可以得到现在的启动器配置并将其写入到本地配置文件 mcinabox.json 中
    //必须在关闭启动器时执行一次，以保存启动器配置
    //可以根据情况传入isContinueUsing参数，来设定配置文件的isUsing参数
    //但是你必须保证，如果你已经同时启用了共有目录和私有目录，不要将两个模板的isUsing设定为相同的值
    //你还可以传入configFile参数，来将配置存储到特定的配置文件中
    private LauncherSettingModel SaveLauncherSettingToFile(boolean isContinueUsing,File configFile) {
        if(configFile == null){
            configFile = new File(MCinaBox_HomePath + "/mcinabox.json");
        }
        Gson gson = new Gson();

        //存储全部启动器设置到模板对象中
        LauncherSettingModel settingModel = new LauncherSettingModel();
        LauncherSettingModel.Configurations configurations = settingModel.getConfigurations();

        //将当前所有用户的信息存入模板对象
        LauncherSettingModel.Accounts[] accounts = new LauncherSettingModel.Accounts[listview_user.getAdapter().getCount()];
        for(int i = 0;i < listview_user.getAdapter().getCount();i++){
            LauncherSettingModel.Accounts account = new LauncherSettingModel().newAccounts;
            UserListBean user = (UserListBean) listview_user.getAdapter().getItem(i);
            account.setSelected(user.isIsSelected());
            account.setUsername(user.getUser_name());
            account.setType(user.getUser_model());
            account.setUuid(UUID.nameUUIDFromBytes((user.getUser_name()).getBytes()).toString());
            accounts[i] = account;
        }
        settingModel.setAccounts(accounts);
        //给模板对象设定参数
        settingModel.setDownloadType((String) setting_downloadtype.getSelectedItem());
        settingModel.setKeyboard((String) setting_keyboard.getSelectedItem());
        settingModel.setUsing(isContinueUsing);
        configurations.setJava((String) setting_java.getSelectedItem());
        configurations.setOpengl((String) setting_opengl.getSelectedItem());
        configurations.setOpenal((String) setting_openal.getSelectedItem());
        configurations.setLwjgl((String) setting_lwjgl.getSelectedItem());
        configurations.setRuntime((String) setting_runtime.getSelectedItem());
        configurations.setMaxMemory(Integer.parseInt((String) editText_maxMemory.getText().toString()));
        configurations.setJavaArgs(editText_javaArgs.getText().toString());
        configurations.setMinecraftArgs(editText_minecraftArgs.getText().toString());
        configurations.setNotCheckGame(setting_notcheckMinecraft.isChecked());
        configurations.setNotCheckJvm(setting_notcheckJvm.isChecked());
        configurations.setNotEnableVirtualKeyboard(setting_notenableKeyboard.isChecked());
        configurations.setEnableOtg(setting_enableOtg.isChecked());

        //写出模板对象到启动器配置文件
        String jsonString = gson.toJson(settingModel);
        try {
            FileWriter jsonWriter = new FileWriter(configFile);
            BufferedWriter out = new BufferedWriter(jsonWriter);
            out.write(jsonString);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "启动器配置保存失败", Toast.LENGTH_SHORT).show();
            Log.e("saveLauncher ", e.toString());
        }
        //返回一个模板对象
        return settingModel;
    }


    /**【根据下载文件的类型和下载源类型得到一个下载地址】*/
    //可以通过下载源类型和下载文件类型得到一个下载地址
    //在调用DownloadMinecraft时必须使用本方法，来传入正确的文件下载服务器地址
    //FileType含义: 0 - "version_manifext.json""version.json""assetIndex.json" 1 - "assets/objects/*"
    public String GetDownloadServerUrl(String DownloadType,int FileType) {
        String url;
        switch(FileType){
            case 0:
                switch (DownloadType) {
                    case "bmclapi":
                    case "mcbbs":
                    case "official":
                    default:
                        url = "https://launchermeta.mojang.com";
                        break;
                }
                break;
            case 1:
                switch (DownloadType) {
                    case "bmclapi":
                    case "mcbbs":
                    case "official":
                    default:
                        url = "http://resources.download.minecraft.net";
                        break;
                }
                break;
            default:
                url = null;
                break;
        }
        return url;
    }


    /**【检查目标配置文件是否正在被使用】**/
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
        return settingModel.isUsing();
    }

    /**【检查MCinaBox的目录结构是否正常】**/
    //可以检查MCinaBox必要的目录结构，如果目录结构不完整将自动创建目录
    //必须在启动时执行一次，如果目录结构不完整将会导致启动器崩溃
    public void CheckMcinaBoxDir(){
        FileTool.checkFilePath(new File(MCinaBox_HomePath),true);
        FileTool.checkFilePath(new File(MCinaBox_HomePath+"/.minecraft/Temp"),true);
        FileTool.checkFilePath(new File(MCinaBox_PrivatePath+"/Keyboardmodel"),true);
    }

    /**【刷新本地游戏列表】**/
    //可以根据游戏目录下的version文件夹和文件夹下是否存在同名jar,json文件来判断是否有这一版本
    //然后将这些版本存入列表中并执行刷新
    //必须在启动器配置时使用一次，以显示本地游戏列表
    //也可再根据情况使用。
    public void ReflashLocalVersionList(){
        DownloadMinecraft pathTool = new DownloadMinecraft(MCinaBox_HomePath,MCinaBox_PrivatePath);
        ArrayList<String> versionIdListTmp = FileTool.listChildDirFromTargetDir(pathTool.getMINECRAFT_VERSION_DIR());
        ArrayList<String> versionIdList = new ArrayList<String>();
        ArrayList<LocalVersionListBean> loaclversionListBeans = new ArrayList<LocalVersionListBean>();
        for(String fileName : versionIdListTmp){
            if((new File(pathTool.getMINECRAFT_VERSION_DIR() + fileName + "/" + fileName + ".jar")).exists() && (new File(pathTool.getMINECRAFT_VERSION_DIR() + fileName + "/" + fileName + ".json")).exists()){
                versionIdList.add(fileName);
            }
        }
        for(String fileName : versionIdList){
            LocalVersionListBean localVersionListBean = new LocalVersionListBean();
            localVersionListBean.setVersion_Id(fileName);
            loaclversionListBeans.add(localVersionListBean);
        }
        LocalVersionListAdapter localversionlistadapter = new LocalVersionListAdapter(this,loaclversionListBeans);
        listView_localversion.setAdapter(localversionlistadapter);

        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, versionIdList);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_choice_version.setAdapter(mAdapter);
    }


    /**【刷新本地用户列表】**/
    //可以根据启动器设置，配置用户列表并刷新
    //必须在启动器配置时使用一次，以显示用户列表
    //也可再根据情况使用，使用前请保证启动器设置模板为最新状态
    public void ReflashLocalUserList(){
        LauncherSettingModel.Accounts[] accounts = GetLauncherSettingFromFile().getAccounts();
        ArrayList<UserListBean> userlist = new ArrayList<UserListBean>();
        for(LauncherSettingModel.Accounts account : accounts){
            UserListBean user = new UserListBean();
            user.setUser_name(account.getUsername());
            user.setUser_model(account.getType());
            user.setIsSelected(account.isSelected());
            user.setContext(this);
            userlist.add(user);
        }
        UserListAdapter userlistadapter = new UserListAdapter(this, userlist);
        listview_user.setAdapter(userlistadapter);

    }



    /**【获取启动器设置对象】**/
    //可以根据当前的目录读入启动器设置的到启动器设置模板
    //如果想得到当前状态下的模板，请保证使用该方法前已经保存过启动器状态
    public LauncherSettingModel GetLauncherSettingFromFile(){
        Gson gson = new Gson();
        InputStream inputStream;
        Reader reader = null;
        LauncherSettingModel launcherSetting;
        File configFile = new File(MCinaBox_HomePath + "/mcinabox.json");
        try {
            inputStream = new FileInputStream(configFile);
            reader = new InputStreamReader(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        launcherSetting = new Gson().fromJson(reader, LauncherSettingModel.class);
        return launcherSetting;
    }

}
