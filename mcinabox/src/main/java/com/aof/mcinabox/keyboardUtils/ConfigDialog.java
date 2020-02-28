package com.aof.mcinabox.keyboardUtils;

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

    boolean isCanceledOnTouchOutside;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
        if(isCanceledOnTouchOutside){
            super.setCanceledOnTouchOutside(cancel);
        }else {
            super.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        }
    }
}
