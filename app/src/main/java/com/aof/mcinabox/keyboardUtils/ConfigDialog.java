package com.aof.mcinabox.keyboardUtils;

import android.app.Dialog;
import android.content.Context;

import com.aof.mcinabox.R;

public class ConfigDialog extends Dialog {
    public ConfigDialog(Context context){
        super(context,R.style.ConfigDialog_editor);
        setContentView(R.layout.dialog_configkey);
    }
}
