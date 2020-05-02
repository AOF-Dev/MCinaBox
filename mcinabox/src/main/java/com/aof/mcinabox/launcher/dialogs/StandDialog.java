package com.aof.mcinabox.launcher.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import com.aof.mcinabox.MainActivity;
import com.aof.mcinabox.R;

public abstract class StandDialog extends Dialog {

    public StandDialog(Activity mContext, int layoutID) {
        super(mContext, R.style.StandDialog);
        setContentView(layoutID);
        this.mContext = mContext;
    }

    //This is Activity.
    public Activity mContext;
    @Override
    abstract protected void onCreate(Bundle savedInstanceState);
}
