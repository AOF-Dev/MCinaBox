package com.aof.mcinabox.launcher.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;

public abstract class BaseDialog extends Dialog {

    public BaseDialog(Activity mContext, int layoutID) {
        super(mContext, R.style.StandDialog);
        setContentView(layoutID);
        this.mContext = mContext;
    }

    public BaseDialog(Activity mContext, View view){
        super(mContext,R.style.StandDialog);
        setContentView(view);
        this.mContext = mContext;
    }

    //This is Activity.
    public Activity mContext;
    @Override
    abstract protected void onCreate(Bundle savedInstanceState);
}
