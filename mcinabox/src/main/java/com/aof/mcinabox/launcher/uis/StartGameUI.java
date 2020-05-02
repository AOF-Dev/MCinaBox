package com.aof.mcinabox.launcher.uis;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.aof.mcinabox.R;
import com.aof.mcinabox.ReadyToStart;
import com.aof.mcinabox.launcher.JsonUtils;
import com.aof.mcinabox.launcher.json.SettingJson;
import com.aof.mcinabox.launcher.version.LocalVersionListBean;
import com.aof.mcinabox.utils.FileTool;
import com.aof.mcinabox.utils.PathTool;

import java.io.File;
import java.util.ArrayList;

import static com.aof.sharedmodule.Data.DataPathManifest.MCINABOX_DATA_PRIVATE;
import static com.aof.sharedmodule.Data.DataPathManifest.MCINABOX_DATA_PUBLIC;
import static com.aof.sharedmodule.Data.DataPathManifest.MCINABOX_FILE_JSON;
import static com.aof.sharedmodule.Data.DataPathManifest.MCINABOX_VERSION;

public class StartGameUI extends StandUI {

    public StartGameUI(Activity context) {
        super(context);
        initUI();
        preInitUI();
    }

    public StartGameUI(Activity context, SettingJson setting) {
        this(context);
        refreshUI(setting);
    }

    private LinearLayout layout_startgame;
    private LinearLayout buttonStartGame;
    private Spinner listVersions;

    private View[] views;


    @Override
    public void initUI() {

        layout_startgame = mContext.findViewById(R.id.layout_startgame);
        buttonStartGame = layout_startgame.findViewById(R.id.main_button_startgame);
        listVersions = layout_startgame.findViewById(R.id.spinner_choice_version);

        views = new View[]{buttonStartGame};
        for (View v : views) {
            v.setOnClickListener(clickListener);
        }

    }

    @Override
    public void refreshUI(SettingJson setting) {
        refreshLocalVersionList(setting);
    }

    @Override
    public SettingJson saveUIConfig(SettingJson setting) {
        saveLastVersion(setting);
        return setting;
    }

    @Override
    public void setUIVisiability(int visiability) {
        layout_startgame.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return layout_startgame.getVisibility();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == buttonStartGame) {
                startMinecraft();
            }
        }
    };

    public void preInitUI(){
        //These initial should not be applied after the UI has been created.
        SettingJson setting = JsonUtils.getSettingFromFile(MCINABOX_FILE_JSON);
        if(setting.getLastVersion() == null){
            return;
        }
        if (!(setting.getLastVersion().equals(""))) {
            setConfigureToVersionlist(setting.getLastVersion(), listVersions);
        }
    }


    /**
     * 【刷新本地游戏列表】
     * Refresh loacl verion list.
     **/
    private ArrayList<String> versionIdList;
    private void refreshLocalVersionList(SettingJson setting) {

        PathTool pathTool = new PathTool(setting.getLocalization(), true);
        ArrayList<String> versionIdList = new ArrayList<String>();
        ArrayList<LocalVersionListBean> mlocalversionList = new ArrayList<LocalVersionListBean>();

        ArrayList<String> versionIdListTmp;
        try {
            versionIdListTmp = FileTool.listChildDirFromTargetDir(pathTool.getMINECRAFT_VERSION_DIR());
        } catch (NullPointerException e) {
            e.printStackTrace();
            versionIdListTmp = new ArrayList<String>() {
            };
        }

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

        if (listVersions.getAdapter() == null) {
            this.versionIdList = versionIdList;
            ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, this.versionIdList);
            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            listVersions.setAdapter(mAdapter);
        } else {
            this.versionIdList.clear();
            this.versionIdList.addAll(versionIdList);
            ((BaseAdapter) listVersions.getAdapter()).notifyDataSetChanged();
        }
    }

    /**
     * 【启动Minecraft】
     * Start to launch Minecraft.
     **/
    private void startMinecraft() {
        String dataPath;
        if (JsonUtils.getSettingFromFile(MCINABOX_FILE_JSON).getLocalization().equals("private")) {
            dataPath = MCINABOX_DATA_PRIVATE;
        } else {
            dataPath = MCINABOX_DATA_PUBLIC;
        }
        ReadyToStart starter = new ReadyToStart(mContext, MCINABOX_VERSION, dataPath, listVersions.getSelectedItem().toString(), JsonUtils.getSettingFromFile(MCINABOX_FILE_JSON).getKeyboard());
        if (checkConfig()) {
            starter.StartGame();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.tips_check_setting), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 【检查启动状态】
     * Check the state of the Launcher.
     * If return false, launch task will be abort.
     **/
    private boolean checkConfig() {
        //TODO:根据Tipper
        return true;
    }

    /**
     * 【保存选中的版本号】
     * Save the last seleced version into Setting file.
     **/
    private void saveLastVersion(SettingJson setting) {
        if (listVersions.getSelectedItem() != null) {
            setting.setLastVersion(listVersions.getSelectedItem().toString());
        } else {
            setting.setLastVersion("");
        }
    }

    /**
     * 【匹配选中的版本】
     * Selected the last version that launched.
     **/
    private void setConfigureToVersionlist(String id, Spinner list) {
        int pos = Utils.getItemPosByString(id, list);
        if (pos != -1) {
            list.setSelection(pos);
        }
    }
}
