package com.aof.sharedmodule.Dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.aof.sharedmodule.R;

public class Helper extends Dialog {

    public Helper(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_controller_help);
    }

}
