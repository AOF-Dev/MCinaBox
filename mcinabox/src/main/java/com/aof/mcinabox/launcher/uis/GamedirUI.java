package com.aof.mcinabox.launcher.uis;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.SettingJson;

public class GamedirUI extends BaseUI {

    public GamedirUI(Activity context, SettingJson setting) {
        super(context);
        initUI(setting);
        refreshUI(setting);
    }

    private LinearLayout layout_gamedir;
    private RadioButton buttonPublic, buttonPrivate;
    private Animation showAnim;
    private View[] views;


    @Override
    public void initUI(SettingJson setting) {
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
        layout_gamedir = mContext.findViewById(R.id.layout_gamedir);
        buttonPublic = layout_gamedir.findViewById(R.id.radiobutton_gamedir_public);
        buttonPrivate = layout_gamedir.findViewById(R.id.radiobutton_gamedir_private);
        views = new View[]{buttonPrivate,buttonPublic};
        if (setting.getLocalization().equals("private")) {
            switchRadioButton(buttonPrivate);
        } else if (setting.getLocalization().equals("public")) {
            switchRadioButton(buttonPublic);
        } else {
            switchRadioButton(buttonPublic);
        }
        for(View v:views){
            v.setOnClickListener(clickListener);
        }
    }

    @Override
    public void refreshUI(SettingJson setting) {

    }

    @Override
    public SettingJson saveUIConfig(SettingJson setting) {
        if (buttonPrivate.isChecked()) {
            setting.setLocalization("private");
        } else if (buttonPublic.isChecked()) {
            setting.setLocalization("public");
        }
        return setting;
    }

    @Override
    public void setUIVisiability(int visiability) {
        if(visiability == View.VISIBLE){
            layout_gamedir.startAnimation(showAnim);
        }
        layout_gamedir.setVisibility(visiability);
    }

    @Override
    public int getUIVisiability() {
        return layout_gamedir.getVisibility();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == buttonPrivate) {
                switchRadioButton(buttonPrivate);
            }
            if (v == buttonPublic) {
                switchRadioButton(buttonPublic);
            }
        }
    };

    /**
     * 【切换单选按钮】
     * Change the state of radiobuttons.
     **/
    private void switchRadioButton(RadioButton v) {
        if (v == buttonPrivate) {
            buttonPrivate.setChecked(true);
            buttonPublic.setChecked(false);
        } else {
            buttonPrivate.setChecked(false);
            buttonPublic.setChecked(true);
        }
    }
}
