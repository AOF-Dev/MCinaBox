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
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.aof.mcinabox.jsonUtils.AnaliesMinecraftVersionJson;
import com.aof.mcinabox.jsonUtils.AnaliesVersionManifestJson;
import com.aof.mcinabox.jsonUtils.ListVersionManifestJson;
import com.aof.mcinabox.jsonUtils.ModelMinecraftVersionJson;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
Button[] launcherBts;
Button button_user,button_gameselected,button_gamelist,button_gamedir,button_launchersetting,button_launchercontrol,button8,toolbar_button_backhome,toolbar_button_backfromhere;


RadioGroup radioGroup_version_type;
RadioButton radioButton_type_release,radioButton_type_snapshot,radioButton_type_old;
RadioButton radioButton_gamedir_public,radioButton_gamedir_private;

Spinner setting_java,setting_opengl,setting_openal,setting_lwjgl,setting_runtime,setting_downloadtype,setting_keyboard;

Switch setting_notcheckJvm,setting_notcheckMinecraft,setting_notenableKeyboard;

LinearLayout[] launcherBts2;
LinearLayout gamelist_button_reflash,gamelist_button_installnewgame,gamelist_button_backfrom_installnewversion,gamelist_button_setting;

View[] launcherLins;
View layout_user,layout_gamelist,layout_gameselected,layout_gamedir,layout_launchersetting,layout_gamelist_installversion,layout_gamelist_setting;

ListView listview_minecraft_manifest,listview_user;

DownloadMinecraft downloadTask = new DownloadMinecraft();
ListVersionManifestJson.Version[] versionList;
ModelMinecraftVersionJson minecraftVersionJson;
Spinner spinnerVersionList;

EditText editText_maxMemory,editText_javaArgs,editText_minecraftArgs;

int targetPos;
int layout_here_Id = R.id.layout_fictionlist;

TextView logText,main_text_showstate;
private BroadcastReceiver broadcastReceiver1;
private BroadcastReceiver broadcastReceiver2;

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

        //给界面的按键设置按键监听
        button_user = findViewById(R.id.main_button_user);
        button_gameselected = findViewById(R.id.main_button_gameselected);
        button_gamelist = findViewById(R.id.main_button_gamelist);
        button_gamedir = findViewById(R.id.main_button_gamedir);
        button_launchersetting = findViewById(R.id.main_button_launchersetting);
        button_launchercontrol = findViewById(R.id.main_button_launchercontrol);
        button8 = findViewById(R.id.main_linear3_download1);
        toolbar_button_backhome = findViewById(R.id.toolbar_button_backhome);
        toolbar_button_backfromhere = findViewById(R.id.toolbar_button_backfromhere);
        radioButton_gamedir_public = findViewById(R.id.radiobutton_gamedir_public);
        radioButton_gamedir_private = findViewById(R.id.radiobutton_gamedir_private);
        launcherBts = new Button[]{radioButton_gamedir_public,radioButton_gamedir_private,button_user,button_gameselected,button_gamelist,button_gamedir,button_launchersetting,button_launchercontrol,button8,toolbar_button_backhome,toolbar_button_backfromhere};
        for(Button button : launcherBts ){
            button.setOnClickListener(listener);
        }

        gamelist_button_reflash = findViewById(R.id.gamelist_button_reflash);
        gamelist_button_installnewgame = findViewById(R.id.gamelist_button_installnewgame);
        gamelist_button_backfrom_installnewversion = findViewById(R.id.gamelist_button_backfrom_installnewversion);
        gamelist_button_setting = findViewById(R.id.gamelist_button_setting);
        launcherBts2 = new LinearLayout[]{gamelist_button_reflash,gamelist_button_installnewgame,gamelist_button_backfrom_installnewversion,gamelist_button_setting};
        for(LinearLayout button : launcherBts2){
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

        editText_javaArgs = findViewById(R.id.setting_edit_javaargs);
        editText_minecraftArgs = findViewById(R.id.setting_edit_minecraftargs);
        editText_maxMemory = findViewById(R.id.setting_edit_maxmemory);

        setting_notcheckJvm = findViewById(R.id.setting_switch_notcheckjvm);
        setting_notcheckMinecraft = findViewById(R.id.setting_switch_notcheckminecraft);
        setting_notenableKeyboard = findViewById(R.id.setting_switch_notenable_keyboard);

        //将所有的linearlayout和scrollview布局都作为view处理
        layout_user = findViewById(R.id.layout_user);
        layout_gameselected = findViewById(R.id.layout_gameselected);
        layout_gamelist = findViewById(R.id.layout_gamelist);
        layout_gamedir = findViewById(R.id.layout_gamedir);
        layout_launchersetting = findViewById(R.id.layout_launchersetting);
        layout_gamelist_installversion = findViewById(R.id.layout_gamelist_installversion);
        layout_gamelist_setting = findViewById(R.id.layout_gamelist_setting);
        launcherLins = new View[] {layout_user,layout_gameselected,layout_gamelist,layout_gamedir,layout_launchersetting,layout_gamelist_installversion,layout_gamelist_setting};

        //初始化ListView控件
        listview_minecraft_manifest = findViewById(R.id.list_minecraft_manifest);

        listview_user = findViewById(R.id.list_user);
        ArrayList<UserListBean> userlist = new ArrayList<UserListBean>();
        for(int i=0;i<=10;i++){
            userlist.add(new UserListBean());
        }
        UserListAdapter adapter = new UserListAdapter(this,userlist);
        listview_user.setAdapter(adapter);

        //初始化LogTextView控件
        logText = findViewById(R.id.logTextView);

        main_text_showstate = findViewById(R.id.main_text_showstate);

        //载入启动器配置文件
        initLauncher();

    }

    //重写boolean onCreatOptionsMenu(Menu menu)方法实现Toolbar的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
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

    /*public void startFloatingService(View view) {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "请授权本地存储权限", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName())), 0);
        }
    }*/

    public void requestPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if ( !ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    //Button数组launchbts中的按键监听
    private View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch(arg0.getId()){
                case R.id.main_button_user:
                    //具体点击操作的逻辑
                    setVisibleLinearLayout(layout_user);
                    main_text_showstate.setText(getString(R.string.main_text_user));
                    break;
                case R.id.main_button_gameselected:
                    setVisibleLinearLayout(layout_gameselected);
                    main_text_showstate.setText(getString(R.string.main_text_gameselected));
                    break;
                case R.id.main_button_gamelist:
                    setVisibleLinearLayout(layout_gamelist);
                    main_text_showstate.setText(getString(R.string.main_text_gamelist));
                    break;
                case R.id.main_button_gamedir:
                    setVisibleLinearLayout(layout_gamedir);
                    main_text_showstate.setText(getString(R.string.main_text_gamedir));
                    break;
                case R.id.main_button_launchersetting:
                    setVisibleLinearLayout(layout_launchersetting);
                    main_text_showstate.setText(getString(R.string.main_text_launchersetting));
                    break;
                case R.id.main_button_launchercontrol:
                    //main_text_showstate.setText(getString(R.string.main_text_launchercontrol));
                    //页面跳转
                    Intent intent = new Intent(getApplicationContext(),VirtualKeyBoardActivity.class);
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
                    button8.setClickable(true);
                    break;
                case R.id.gamelist_button_installnewgame:
                    setVisibleLinearLayout(layout_gamelist_installversion);
                    main_text_showstate.setText(getString(R.string.main_text_gamelist_installversion)+" - "+getString(R.string.main_text_gamelist));
                    break;
                case R.id.gamelist_button_backfrom_installnewversion:
                    setVisibleLinearLayout(layout_gamelist);
                    main_text_showstate.setText(getString(R.string.main_text_gamelist));
                    break;
                case R.id.gamelist_button_setting:
                    setVisibleLinearLayout(layout_gamelist_setting);
                    main_text_showstate.setText(getString(R.string.main_text_gamelist_setting)+" - "+getString(R.string.main_text_gamelist));
                    break;
                case R.id.main_linear3_download1:
                    DownloadVersionFirst();
                    break;
                case R.id.toolbar_button_backhome:
                    setVisibleLinearLayout(null);
                    main_text_showstate.setText(getString(R.string.main_text_defaultlayout));
                    break;
                case R.id.toolbar_button_backfromhere:
                    setBackFromHere(layout_here_Id);
                    break;
                case R.id.radiobutton_gamedir_public:
                    radioButton_gamedir_public.setChecked(true);
                    radioButton_gamedir_private.setChecked(false);
                    break;
                case R.id.radiobutton_gamedir_private:
                    radioButton_gamedir_private.setChecked(true);
                    radioButton_gamedir_public.setChecked(false);
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

    //下载Minecraft清单列表
    private long DownloadVersionList(){
        downloadTask.setInformation("https://launchermeta.mojang.com", "/MCinaBox/.minecraft/");
        return (downloadTask.UpdateVersionManifestJson(this));
    }
    private void DownloadVersionFirst(){
        long taskId;
        taskId = downloadTask.DownloadMinecraftVersionJson(versionList[targetPos].getId(),versionList[targetPos].getUrl(),this);
        listener2(taskId);
    }
    private void DownloadVersionSecond(){
        //获取实例化后的versionList
        minecraftVersionJson = new AnaliesMinecraftVersionJson().getModelMinecraftVersionJson(downloadTask.getMINECRAFT_VERSION_DIR()+versionList[targetPos].getId()+"/"+versionList[targetPos].getId()+".json");
        //Toast.makeText(getApplicationContext(),minecraftVersionJson.getLibraries().length+"",Toast.LENGTH_SHORT).show();
        //先做一个输出测试一下解析结果是否正确

        /*StringBuffer s2 = new StringBuffer("");
        for(int i = 0;i<=minecraftVersionJson.getLibraries().length-1;i++){
            StringBuffer s1 = new StringBuffer(minecraftVersionJson.getLibraries()[i].getDownloads().containsKey("path"));
            s2.append(s1);
        }
        logText.setText(s2);
         */
        logText.setText(""+minecraftVersionJson.getLibraries()[1].getDownloads().size());


        //TODO:未正确获取path参数
        //测试一下url参数
        //downloadTask.DownloadMinecraftDependentLibraries(minecraftVersionJson.getLibraries()[0].getPath(), minecraftVersionJson.getLibraries()[0].getUrl(), this);

    }

    //测试json解析功能
    /*
    private void testJson(){

        try {
            //将version_manifest.json文件加入输入流
            InputStream inputStream = new FileInputStream(new File(downloadTask.getMINECRAFT_TEMP()+"version_manifest.json"));
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            //使用Gson将ListVersionManifestJson实例化
            ListVersionManifestJson listVersionManifestJson = gson.fromJson(reader, ListVersionManifestJson.class);

            ListVersionManifestJson.Version[] result = listVersionManifestJson.getVersions();
            String testid = result[0].getId();

            Toast.makeText(getApplicationContext(),testid,Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }*/

    //更新List
    private void loadSpinnerVersionList(){
        //获取实例化后的versionList
        versionList = new AnaliesVersionManifestJson().getVersionList(downloadTask.getMINECRAFT_TEMP()+"version_manifest.json");
        String[] nameList;

        ArrayList<ListVersionManifestJson.Version> version_type_release = new ArrayList<ListVersionManifestJson.Version>(){};
        ArrayList<ListVersionManifestJson.Version> version_type_snapsht = new ArrayList<ListVersionManifestJson.Version>(){};
        ArrayList<ListVersionManifestJson.Version> version_type_old = new ArrayList<ListVersionManifestJson.Version>(){};

        for(ListVersionManifestJson.Version version : versionList){
            switch(version.getType()){
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
                case  "old_alpha":
                    version_type_old.add(version);
                    break;
            }
        }

        switch (radioGroup_version_type.getCheckedRadioButtonId()){
            default:
                nameList = new String[0];
                break;
            case R.id.radiobutton_type_release:
                nameList = new String[version_type_release.size()];
                for(int i=0;i<version_type_release.size();i++){
                    nameList[i] = version_type_release.get(i).getId();
                }
                break;
            case R.id.radiobutton_type_snapshot:
                nameList = new String[version_type_snapsht.size()];
                for(int i=0;i<version_type_snapsht.size();i++){
                    nameList[i] = version_type_snapsht.get(i).getId();
                }
                break;
            case R.id.radiobutton_type_old:
                nameList = new String[version_type_old.size()];
                for(int i=0;i<version_type_old.size();i++){
                    nameList[i] = version_type_old.get(i).getId();
                }
                break;
        }

        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, nameList);
        //绑定 Adapter到控件
        listview_minecraft_manifest.setAdapter(adapter);

       /* listview_minecraft_manifest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                targetPos = pos;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });*/
    }

    //主界面逻辑，显示分界面
    private void setVisibleLinearLayout(View view){
        for(View tempview : launcherLins){
            tempview.setVisibility(View.INVISIBLE);
        }
        if(view != null) {
            view.setVisibility(View.VISIBLE);
            layout_here_Id = view.getId();
        }else{
            return;
        }
    }

    //DownloadManager下载完成事件的广播监听
    private void listener1(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    loadSpinnerVersionList();
                    Toast.makeText(getApplicationContext(), "版本清单更新完成", Toast.LENGTH_LONG).show();
                }
            }
        };
        registerReceiver(broadcastReceiver1, intentFilter);
    }

    private void listener2(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    Toast.makeText(getApplicationContext(), "中断1", Toast.LENGTH_LONG).show();
                    DownloadVersionSecond();
                }
            }
        };
        registerReceiver(broadcastReceiver2, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveLauncher();
        if(broadcastReceiver1 != null){
            unregisterReceiver(broadcastReceiver1);
        }
        if(broadcastReceiver2 != null){
            unregisterReceiver(broadcastReceiver2);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup_version_type, int checkedId) {
        if(versionList != null) {
            loadSpinnerVersionList();
            switch (checkedId) {
                case R.id.radiobutton_type_release:
                    Toast.makeText(this, "稳定版", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.radiobutton_type_snapshot:
                    Toast.makeText(this, "测试版", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.radiobutton_type_old:
                    Toast.makeText(this, "远古版", Toast.LENGTH_SHORT).show();
                    break;
            }
        }else{
            Toast.makeText(this, "暂无游戏清单数据", Toast.LENGTH_SHORT).show();
        }
    }

    //给返回按键设计返回逻辑
    public void setBackFromHere(int location){
        switch(location){
            default:
                setVisibleLinearLayout(null);
                main_text_showstate.setText(getString(R.string.main_text_defaultlayout));
                break;
            case R.id.layout_user:
            case R.id.layout_gameselected:
            case R.id.layout_gamelist:
            case R.id.layout_gamedir:
            case R.id.layout_launchersetting:
                setVisibleLinearLayout(null);
                main_text_showstate.setText(getString(R.string.main_text_defaultlayout));
                layout_here_Id = R.id.layout_fictionlist;
                break;
            case R.id.layout_gamelist_installversion:
            case R.id.layout_gamelist_setting:
                setVisibleLinearLayout(layout_gamelist);
                main_text_showstate.setText(getString(R.string.main_text_gamelist));
                layout_here_Id = R.id.layout_gamelist;
                break;
            case R.id.layout_fictionlist:
                finish();
                break;
        }
    }


    //用于启动启动器时载入启动器配置
    public void initLauncher(){
        File configFile = new File("/sdcard/MCinaBox/mcinabox.json");
        Gson gson = new Gson();
        InputStream inputStream;
        Reader reader;
        LauncherSettingModel settingModel = null;

        //检测启动器配置文件是否存在
        if(!configFile.exists()){
            //如果不存在，就创建一个空文件
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                //如果创建失败，就退出程序
                e.printStackTrace();
                Toast.makeText(this, "启动器配置模板创建失败", Toast.LENGTH_SHORT).show();
                Log.e("initLauncher ",e.toString());
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
                Log.e("initLauncher ",e.toString());
                finish();
            }
            Toast.makeText(this, "已为启动器创建配置模板", Toast.LENGTH_SHORT).show();
        }else{
            //如果文件存在，就读入配置文件
            try {
                inputStream = new FileInputStream(configFile);
                reader = new InputStreamReader(inputStream);
                settingModel = new Gson().fromJson(reader, LauncherSettingModel.class);
                if (settingModel == null){
                    Toast.makeText(this, "启动器初始化失败", Toast.LENGTH_SHORT).show();
                    finish();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "读入启动器模板失败", Toast.LENGTH_SHORT).show();
                Log.e("initLauncher ",e.toString());
                finish();
            }
            //根据读入的结果应用启动器配置文件

            setting_java.setSelection(getSpinnerFitString(setting_java,settingModel.getConfigurations().getJava()));
            setting_opengl.setSelection(getSpinnerFitString(setting_opengl,settingModel.getConfigurations().getOpengl()));
            setting_openal.setSelection(getSpinnerFitString(setting_openal,settingModel.getConfigurations().getOpenal()));
            setting_lwjgl.setSelection(getSpinnerFitString(setting_lwjgl,settingModel.getConfigurations().getLwjgl()));
            setting_runtime.setSelection(getSpinnerFitString(setting_runtime,settingModel.getConfigurations().getRuntime()));
            setting_downloadtype.setSelection(getSpinnerFitString(setting_downloadtype,settingModel.getDownloadType()));
            //setting_keyboard.setSelection(getSpinnerFitString(setting_keyboard,settingModel.getKeyboard()));TODO:keyboard需要单独读取本地数据来处理

            editText_javaArgs.setText(settingModel.getConfigurations().getJavaArgs());
            editText_minecraftArgs.setText(settingModel.getConfigurations().getMinecraftArgs());
            editText_maxMemory.setText(settingModel.getConfigurations().getMaxMemory()+"");

            setting_notcheckJvm.setChecked(settingModel.getConfigurations().isNotCheckJvm());
            setting_notcheckMinecraft.setChecked(settingModel.getConfigurations().isNotCheckGame());
            setting_notenableKeyboard.setChecked(settingModel.getConfigurations().isNotEnableVirtualKeyboard());

            if(settingModel.getLocalization().equals("public")){
                radioButton_gamedir_public.setChecked(true);
                radioButton_gamedir_private.setChecked(false);
            }else if(settingModel.getLocalization().equals("private")){
                radioButton_gamedir_private.setChecked(true);
                radioButton_gamedir_public.setChecked(false);
            }


        }

    }


    //将字符串与Spinner中的字符串进行匹配，然后返回匹配的位置上的id
    public int getSpinnerFitString(Spinner spinner,String tag) {
        int pos = -1;
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            if (tag.equals(spinner.getItemAtPosition(i))) {
                pos = i;
            }
        }
        if(pos == -1){
            //TODO:需要自动删除错误的模板，再关闭程序，以免下次启动仍遇到问题。
            Toast.makeText(this, "启动器初始化失败 "+tag,Toast.LENGTH_SHORT).show();
        }
        return pos;
    }

    public int getKeyboardSpinnerFitString(Spinner spinner,String tag){
        int pos = -1;
        return pos;
    }


    //用于在退出或者启动Minecraft时保存启动器配置
    public void saveLauncher(){
        File configFile = new File("/sdcard/MCinaBox/mcinabox.json");
        Gson gson = new Gson();

        //存储全部启动器设置到模板对象中
        LauncherSettingModel settingModel = new LauncherSettingModel();
        LauncherSettingModel.Configurations configurations = settingModel.getConfigurations();
        LauncherSettingModel.Accounts[] accounts = settingModel.getAccounts();

        settingModel.setDownloadType((String)setting_downloadtype.getSelectedItem());
        settingModel.setKeyboard((String)setting_keyboard.getSelectedItem());
        if(radioButton_gamedir_public.isChecked()){
            settingModel.setLocalization("public");
        }else if(radioButton_gamedir_private.isChecked()){
            settingModel.setLocalization("private");
        }
        configurations.setJava((String)setting_java.getSelectedItem());
        configurations.setOpengl((String)setting_opengl.getSelectedItem());
        configurations.setOpenal((String)setting_openal.getSelectedItem());
        configurations.setLwjgl((String)setting_lwjgl.getSelectedItem());
        configurations.setRuntime((String)setting_runtime.getSelectedItem());
        configurations.setMaxMemory(Integer.parseInt((String)editText_maxMemory.getText().toString()));
        configurations.setJavaArgs(editText_javaArgs.getText().toString());
        configurations.setMinecraftArgs(editText_minecraftArgs.getText().toString());
        configurations.setNotCheckGame(setting_notcheckMinecraft.isChecked());
        configurations.setNotCheckJvm(setting_notcheckJvm.isChecked());
        configurations.setNotEnableVirtualKeyboard(setting_notenableKeyboard.isChecked());

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
            Log.e("saveLauncher ",e.toString());
        }

    }



}
