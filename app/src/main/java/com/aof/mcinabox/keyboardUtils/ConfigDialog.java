package com.aof.mcinabox.keyboardUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.aof.mcinabox.R;
import com.aof.mcinabox.VirtualKeyBoardActivity;

public class ConfigDialog extends Dialog {

    public ConfigDialog(Context context){
        super(context,R.style.ConfigDialog_editor);
        setContentView(R.layout.dialog_configkey);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

}
