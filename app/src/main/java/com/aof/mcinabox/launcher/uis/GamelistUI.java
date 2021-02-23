package com.aof.mcinabox.launcher.uis;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.mcinabox.launcher.version.VersionManager;
import com.aof.mcinabox.launcher.version.support.LocalVersionListAdapter;
import com.aof.mcinabox.launcher.version.support.LocalVersionListBean;

import java.util.ArrayList;

public class GamelistUI extends BaseUI {

    public GamelistUI(Context context){
        super(context);
    }

    private LinearLayout layout_gamelist;
    private LinearLayout buttonInstallGame;
    private LinearLayout buttonRefreshList;
    private LinearLayout buttonGameSetting;
    private ListView listLocalVersions;
    private Animation showAnim;
    private SettingJson setting;

    private final View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == buttonGameSetting) {
                OldMainActivity.CURRENT_ACTIVITY.get().switchUIs(OldMainActivity.CURRENT_ACTIVITY.get().mUiManager.uiGameSetting, mContext.getString(R.string.title_game_global_setting) + " - " + mContext.getString(R.string.title_game_list));
            }
            if (v == buttonInstallGame) {
                OldMainActivity.CURRENT_ACTIVITY.get().switchUIs(OldMainActivity.CURRENT_ACTIVITY.get().mUiManager.uiInstallVersion, mContext.getString(R.string.title_install_new_version) + " - " + mContext.getString(R.string.title_game_list));
            }
            if (v == buttonRefreshList) {
                refreshLocalVersionList();
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
    public void setUIVisibility(int visibility) {
        if(visibility == View.VISIBLE){
            layout_gamelist.startAnimation(showAnim);
        }
        layout_gamelist.setVisibility(visibility);
    }

    @Override
    public int getUIVisibility() {
        return layout_gamelist.getVisibility();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setting = OldMainActivity.Setting;
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
        layout_gamelist = OldMainActivity.CURRENT_ACTIVITY.get().findViewById(R.id.layout_gamelist);
        buttonInstallGame = layout_gamelist.findViewById(R.id.gamelist_button_installnewgame);
        buttonRefreshList = layout_gamelist.findViewById(R.id.gamelist_button_reflash_locallist);
        buttonGameSetting = layout_gamelist.findViewById(R.id.gamelist_button_setting);
        listLocalVersions = layout_gamelist.findViewById(R.id.list_local_version);

        for (View v : new View[]{buttonGameSetting, buttonInstallGame, buttonRefreshList}) {
            v.setOnClickListener(clickListener);
        }
        refreshUI();
    }

    /**
     * 【刷新本地游戏列表】
     **/
    private ArrayList<LocalVersionListBean> beans;

    public void refreshLocalVersionList() {
        if (beans == null) {
            beans = new ArrayList<>();
            beans.addAll(VersionManager.getVersionBeansList());
            listLocalVersions.setAdapter(new LocalVersionListAdapter(mContext, beans));
        }else{
            beans.clear();
            beans.addAll(VersionManager.getVersionBeansList());
            ((BaseAdapter)listLocalVersions.getAdapter()).notifyDataSetChanged();
        }
    }


}
