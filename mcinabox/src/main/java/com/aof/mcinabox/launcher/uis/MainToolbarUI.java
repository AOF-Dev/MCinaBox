package com.aof.mcinabox.launcher.uis;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.SettingJson;
import com.aof.mcinabox.launcher.tipper.Tipper;

public class MainToolbarUI extends BaseUI {

    public MainToolbarUI(Activity context) {
        super(context);
        initUI();
    }

    public MainToolbarUI(Activity context, SettingJson setting) {
        this(context);
        refreshUI(setting);
    }

    private Toolbar layout_toolbar;
    private Button buttonBack;
    private TextView textPosition;
    private Button buttonHome;
    private Button buttonLanguage;
    private Button buttonRefresh;
    private Button buttonInfo;

    private Tipper tipper;

    private View[] views;


    @Override
    public void initUI() {
        layout_toolbar = mContext.findViewById(R.id.layout_toolbar_main);
        buttonBack = layout_toolbar.findViewById(R.id.toolbar_button_backfromhere);
        textPosition = layout_toolbar.findViewById(R.id.main_text_showstate);
        buttonHome = layout_toolbar.findViewById(R.id.toolbar_button_backhome);
        buttonLanguage = layout_toolbar.findViewById(R.id.toolbar_button_language);
        buttonRefresh = layout_toolbar.findViewById(R.id.toolbar_button_refresh);
        buttonInfo = layout_toolbar.findViewById(R.id.toolbar_button_taskinfo);
        tipper = new Tipper(mContext);
        setToolbarAsActionbar();

        views = new View[]{buttonInfo,buttonRefresh,buttonLanguage,buttonHome,buttonBack};
        for(View v : views){
            v.setOnClickListener(clickListener);
        }
    }

    @Override
    public void refreshUI(SettingJson setting) {
        refreshTaskInfo(setting);
        tipper.refreshTipper(setting,this);
    }

    @Override
    public SettingJson saveUIConfig(SettingJson setting) {
        return setting;
    }

    @Override
    public void setUIVisiability(int visiability) {
        layout_toolbar.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return layout_toolbar.getVisibility();
    }

    public void setCurrentPosition(String position){
        textPosition.setText(position);
    }

    private void refreshTaskInfo(SettingJson setting){

    }

    public void setTaskInfoBackground(int id){
        buttonInfo.setBackground(mContext.getResources().getDrawable(id));
    }

    private View.OnClickListener clickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(v == buttonRefresh){
                ((MainActivity)mContext).refreshLauncher(null,true);
            }
            if(v == buttonBack){
                ((MainActivity)mContext).backFromHere();
            }
            if(v == buttonHome){
                ((MainActivity)mContext).switchUIs(((MainActivity)mContext).uiStartGame,mContext.getString(R.string.title_home));
            }
            if(v == buttonInfo){
                tipper.showTipper(v);
            }
            if(v == buttonLanguage){
                ((MainActivity)mContext).dialogLanguage.show();
            }
        }
    };

    private void setToolbarAsActionbar(){
        ((MainActivity)mContext).setSupportActionBar(layout_toolbar);
    }
}
