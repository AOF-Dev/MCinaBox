package com.aof.mcinabox.launcher.keyboard;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;


import com.aof.mcinabox.R;

public class ConfigDialog extends Dialog {

    public ConfigDialog(Context context,int id,boolean isCanceledOnTouchOutside){
        super(context,R.style.ConfigDialog_editor);
        this.isCanceledOnTouchOutside = isCanceledOnTouchOutside;
        setContentView(id);
    }

    private boolean isCanceledOnTouchOutside;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
            super.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
    }
}
