package com.aof.mcinabox.keyboardUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;


import com.aof.mcinabox.R;

public class ConfigDialog extends Dialog {

    public ConfigDialog(Context context,int id){
        super(context,R.style.ConfigDialog_editor);
        setContentView(id);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

}
