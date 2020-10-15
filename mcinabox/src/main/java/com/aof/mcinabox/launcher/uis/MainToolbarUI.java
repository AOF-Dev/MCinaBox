package com.aof.mcinabox.launcher.uis;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.lang.LanguageDialog;
import com.aof.mcinabox.launcher.setting.support.SettingJson;
import com.aof.utils.dialog.DialogUtils;
import com.aof.utils.dialog.support.DialogSupports;

public class MainToolbarUI extends BaseUI {

    public MainToolbarUI(Context context) {
        super(context);
    }

    private Toolbar layout_toolbar;
    private Button buttonBack;
    private TextView textPosition;
    private Button buttonHome;
    private Button buttonLanguage;
    private Button buttonRefresh;
    private Button buttonInfo;
    private SettingJson setting;

    @Override
    public void onCreate() {
        super.onCreate();
        setting = MainActivity.Setting;
        layout_toolbar = MainActivity.CURRENT_ACTIVITY.findViewById(R.id.layout_toolbar_main);
        buttonBack = layout_toolbar.findViewById(R.id.toolbar_button_backfromhere);
        textPosition = layout_toolbar.findViewById(R.id.main_text_showstate);
        buttonHome = layout_toolbar.findViewById(R.id.toolbar_button_backhome);
        buttonLanguage = layout_toolbar.findViewById(R.id.toolbar_button_language);
        buttonRefresh = layout_toolbar.findViewById(R.id.toolbar_button_refresh);
        buttonInfo = layout_toolbar.findViewById(R.id.toolbar_button_taskinfo);
        setToolbarAsActionbar();

        for (View v : new View[]{buttonInfo, buttonRefresh, buttonLanguage, buttonHome, buttonBack}) {
            v.setOnClickListener(clickListener);
        }
        refreshUI();
    }

    @Override
    public void refreshUI() {
        if (MainActivity.CURRENT_ACTIVITY.mTipperManager != null && MainActivity.CURRENT_ACTIVITY.mTipperManager.getTipCounts() != 0) {
            buttonInfo.setVisibility(View.VISIBLE);
        } else {
            buttonInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void saveUIConfig() {

    }

    @Override
    public void setUIVisiability(int visiability) {
        //Do not change its visiability.
    }

    @Override
    public int getUIVisiability() {
        return layout_toolbar.getVisibility();
    }

    public void setCurrentPosition(String position) {
        textPosition.setText(position);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == buttonRefresh) {
                DialogUtils.createBothChoicesDialog(mContext, mContext.getString(R.string.title_warn), mContext.getString(R.string.tips_going_to_restart_app), mContext.getString(R.string.title_continue), mContext.getString(R.string.title_cancel), new DialogSupports() {
                    @Override
                    public void runWhenPositive() {
                        MainActivity.CURRENT_ACTIVITY.restarter();
                    }
                });
            }
            if (v == buttonBack) {
                MainActivity.CURRENT_ACTIVITY.backFromHere();
            }
            if (v == buttonHome) {
                MainActivity.CURRENT_ACTIVITY.switchUIs(MainActivity.CURRENT_ACTIVITY.mUiManager.uiStartGame, mContext.getString(R.string.title_home));
            }
            if (v == buttonLanguage) {
                new LanguageDialog(mContext).show();
            }
            if (v == buttonInfo) {
                MainActivity.CURRENT_ACTIVITY.mTipperManager.showTipper(buttonInfo);
            }
        }
    };

    private void setToolbarAsActionbar() {
        MainActivity.CURRENT_ACTIVITY.setSupportActionBar(layout_toolbar);
    }
}
