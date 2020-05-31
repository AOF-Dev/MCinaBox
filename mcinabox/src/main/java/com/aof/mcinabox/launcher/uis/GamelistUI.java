package com.aof.mcinabox.launcher.uis;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.SettingJson;
import com.aof.mcinabox.launcher.version.LocalVersionListAdapter;
import com.aof.mcinabox.launcher.version.LocalVersionListBean;
import com.aof.mcinabox.utils.FileTool;
import com.aof.mcinabox.utils.PathTool;

import java.io.File;
import java.util.ArrayList;

public class GamelistUI extends BaseUI {

    public GamelistUI(Activity context,SettingJson setting){
        super(context);
        initUI(setting);
        refreshUI(setting);
    }

    private LinearLayout layout_gamelist;
    private LinearLayout buttonInstallGame;
    private LinearLayout buttonRefreshList;
    private LinearLayout buttonGameSetting;
    private ListView listLocalVersions;
    private Animation showAnim;

    private View[] views;

    @Override
    public void initUI(SettingJson setting) {
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
        layout_gamelist = mContext.findViewById(R.id.layout_gamelist);
        buttonInstallGame = layout_gamelist.findViewById(R.id.gamelist_button_installnewgame);
        buttonRefreshList = layout_gamelist.findViewById(R.id.gamelist_button_reflash_locallist);
        buttonGameSetting = layout_gamelist.findViewById(R.id.gamelist_button_setting);
        listLocalVersions = layout_gamelist.findViewById(R.id.list_local_version);

        views = new View[]{buttonGameSetting,buttonInstallGame,buttonRefreshList};
        for(View v : views){
            v.setOnClickListener(clickListener);
        }
    }

    @Override
    public void refreshUI(SettingJson setting) {
        refreshLocalVersionList(setting);
    }

    @Override
    public SettingJson saveUIConfig(SettingJson setting) {
        return setting;
    }

    @Override
    public void setUIVisiability(int visiability) {
        if(visiability == View.VISIBLE){
            layout_gamelist.startAnimation(showAnim);
        }
        layout_gamelist.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return layout_gamelist.getVisibility();
    }

    private View.OnClickListener clickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(v == buttonGameSetting) {
                ((MainActivity) mContext).switchUIs(((MainActivity) mContext).uiGameSetting, mContext.getString(R.string.title_setting_minecraft) + " - " + mContext.getString(R.string.title_gamelist));
            }
            if(v == buttonInstallGame){
                ((MainActivity)mContext).switchUIs(((MainActivity) mContext).uiInstallVersion,mContext.getString(R.string.title_install_newversion) + " - " + mContext.getString(R.string.title_gamelist));
            }
            if(v == buttonRefreshList){

            }
        }

    };

    /**
     * 【刷新本地游戏列表】
     * Refresh the local Version list.
     * These versions are from your sdcard.
     **/
    private ArrayList<LocalVersionListBean> localversionList;

    private ArrayList<String> versionIdList;

    public void refreshLocalVersionList(SettingJson setting) {
        PathTool pathTool = new PathTool(setting.getLocalization(),true);
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

        if(listLocalVersions.getAdapter() == null){
            this.localversionList = mlocalversionList;
            LocalVersionListAdapter localversionlistadapter = new LocalVersionListAdapter(mContext, this.localversionList);
            listLocalVersions.setAdapter(localversionlistadapter);
        }else{
            this.localversionList.clear();
            this.localversionList.addAll(mlocalversionList);
            ((BaseAdapter)listLocalVersions.getAdapter()).notifyDataSetChanged();
        }

    }


}
