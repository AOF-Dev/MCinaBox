package com.aof.mcinabox.launcher.uis;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.launcher.launch.LaunchManager;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.uis.support.Utils;
import com.aof.mcinabox.launcher.version.VersionManager;

import java.util.ArrayList;
import java.util.Arrays;

public class StartGameUI extends BaseUI implements Spinner.OnItemSelectedListener {

    public StartGameUI(Context context) {
        super(context);
    }

    private LinearLayout layout_startgame;
    private LinearLayout buttonStartGame;
    private Spinner listVersions;
    private SettingJson setting;

    private final View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == buttonStartGame) {
                startMinecraft();
            }
        }
    };

    @Override
    public void refreshUI() {
        refreshLocalVersionList();
    }

    @Override
    public void saveUIConfig() {
    }

    @Override
    public void setUIVisiability(int visiability) {
        layout_startgame.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return layout_startgame.getVisibility();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setting = OldMainActivity.Setting;
        layout_startgame = OldMainActivity.CURRENT_ACTIVITY.get().findViewById(R.id.layout_startgame);
        buttonStartGame = layout_startgame.findViewById(R.id.main_button_startgame);
        listVersions = layout_startgame.findViewById(R.id.spinner_choice_version);

        //设定属性
        refreshLocalVersionList();
        if(setting.getLastVersion() != null &&  !setting.getLastVersion().equals("")){
            setConfigureToVersionlist(setting.getLastVersion(), listVersions);
        }

        //设定监听器
        for (View v : new View[]{buttonStartGame}) {
            v.setOnClickListener(clickListener);
        }
        listVersions.setOnItemSelectedListener(this);

    }


    /**
     * 【刷新本地游戏列表】
     **/
    private ArrayList<String> versionIdList;
    private void refreshLocalVersionList() {
        if(listVersions.getAdapter() == null){
            versionIdList = new ArrayList<>();
            versionIdList.addAll(Arrays.asList(VersionManager.getVersionsList()));
            listVersions.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, this.versionIdList));
        }else{
            versionIdList.clear();
            versionIdList.addAll(Arrays.asList(VersionManager.getVersionsList()));
            ((BaseAdapter)listVersions.getAdapter()).notifyDataSetChanged();
        }
    }

    /**
     * 【启动Minecraft】
     **/
    private void startMinecraft() {
        new LaunchManager(mContext).launchMinecraft(OldMainActivity.Setting ,LaunchManager.LAUNCH_PRECHECK);
    }

    /**
     * 【匹配选中的版本】
     **/
    private void setConfigureToVersionlist(String id, Spinner list) {
        int pos = Utils.getItemPosByString(id, list);
        if (pos != -1) {
            list.setSelection(pos);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == listVersions){
            setting.setLastVersion((String) listVersions.getItemAtPosition(position));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if(parent == listVersions){
            setting.setLastVersion("");
        }
    }
}
