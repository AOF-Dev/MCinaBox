package com.aof.mcinabox.launcher.uis;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.aof.mcinabox.R;
import com.aof.mcinabox.launcher.json.SettingJson;

public class GamedirUI extends StandUI {

    public GamedirUI(Activity context) {
        super(context);
        initUI();
    }

    public GamedirUI(Activity context, SettingJson setting) {
        this(context);
        refreshUI(setting);
    }

    private LinearLayout layout_gamedir;
    private RadioButton buttonPublic, buttonPrivate;


    @Override
    public void initUI() {
        layout_gamedir = mContext.findViewById(R.id.layout_gamedir);
        buttonPublic = layout_gamedir.findViewById(R.id.radiobutton_gamedir_public);
        buttonPrivate = layout_gamedir.findViewById(R.id.radiobutton_gamedir_private);
    }

    @Override
    public void refreshUI(SettingJson setting) {
        if (setting.getLocalization().equals("private")) {
            switchRadioButton(buttonPrivate);
        } else if (setting.getLocalization().equals("public")) {
            switchRadioButton(buttonPublic);
        } else {
            switchRadioButton(buttonPublic);
        }
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
