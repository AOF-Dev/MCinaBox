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
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aof.mcinabox.jsonUtils.AnaliesMinecraftVersionJson;
import com.aof.mcinabox.jsonUtils.AnaliesVersionManifestJson;
import com.aof.mcinabox.jsonUtils.ListVersionManifestJson;
import com.aof.mcinabox.jsonUtils.ModelMinecraftVersionJson;

public class MainActivity extends AppCompatActivity {
Button[] launcherBts;
Button button_user,button_gameselected,button_gamelist,button_gamedir,button_launchersetting,button_launchercontrol,button7,button8;
Button testButton;
LinearLayout[] launcherLins;
LinearLayout layout_user,layout_gameselected,layout_gamelist,layout_gamedir,layout_launchersetting,layout_launchercontrol;
DownloadMinecraft downloadTask = new DownloadMinecraft();
ListVersionManifestJson.Version[] versionList;
ModelMinecraftVersionJson minecraftVersionJson;
Spinner spinnerVersionList;
int targetPos;
TextView logText;
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
        button7 = findViewById(R.id.main_linear3_flash1);
        button8 = findViewById(R.id.main_linear3_download1);
        testButton = findViewById(R.id.test);
        launcherBts = new Button[]{button_user,button_gameselected,button_gamelist,button_gamedir,button_launchersetting,button_launchercontrol,button7,button8,testButton,};
        for(Button button : launcherBts ){
            button.setOnClickListener(listener);
        }

        //给linearlayout设置对象数组
        layout_user = findViewById(R.id.layout_user);
        layout_gamelist = findViewById(R.id.layout_gamelist);
        layout_gamedir = findViewById(R.id.layout_gamedir);
        layout_launchersetting = findViewById(R.id.layout_launchersetting);
        layout_launchercontrol = findViewById(R.id.layout_launchercontrol);
        launcherLins = new LinearLayout[] {layout_user,layout_gamelist,layout_gamedir,layout_launchersetting,layout_launchercontrol,layout_gameselected};
        //初始化Spinner控件
        spinnerVersionList = findViewById(R.id.main_linear3_spinner);

        //初始化LogTextView控件
        logText = findViewById(R.id.logTextView);

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
                    setVisibleLinearLyout(layout_user);
                    break;
                case R.id.main_button_gameselected:
                    setVisibleLinearLyout(layout_gameselected);
                    break;
                case R.id.main_button_gamelist:
                    setVisibleLinearLyout(layout_gamelist);
                    break;
                case R.id.main_button_gamedir:
                    setVisibleLinearLyout(layout_gamedir);
                    break;
                case R.id.main_button_launchersetting:
                    setVisibleLinearLyout(layout_launchersetting);
                    break;
                case R.id.main_button_launchercontrol:
                    setVisibleLinearLyout(layout_launchercontrol);
                    //页面跳转
                    Intent intent = new Intent(getApplicationContext(),VirtualKeyBoardActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_linear3_flash1:
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
                case R.id.main_linear3_download1:
                    DownloadVersionFirst();
                    break;
                case R.id.test:
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

    //更新Spinner
    private void loadSpinnerVersionList(){
        //获取实例化后的versionList
        versionList = new AnaliesVersionManifestJson().getVersionList(downloadTask.getMINECRAFT_TEMP()+"version_manifest.json");
        final String[] versions = new String[versionList.length];
        //将versionList中的id值拷贝到一个String数组中作为数据源
        for(int i = 0;i < versionList.length;i++){
            versions[i] = versionList[i].getId();
        }
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, versions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //绑定 Adapter到控件
        spinnerVersionList .setAdapter(adapter);
        spinnerVersionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                targetPos = pos;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    //主界面逻辑，显示分界面
    private void setVisibleLinearLyout(LinearLayout layout){

        for(LinearLayout tempLayout : launcherLins){
            tempLayout.setVisibility(View.INVISIBLE);
        }
        layout.setVisibility(View.VISIBLE);
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
        unregisterReceiver(broadcastReceiver1);
        unregisterReceiver(broadcastReceiver2);
    }

}
