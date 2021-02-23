package com.aof.mcinabox.utils.dialog.support;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aof.mcinabox.R;

public class TaskDialog extends Dialog {

    private TextView textTotalTaskName;
    private TextView textCurrentTaskName;
    private Context mContext;

    public TaskDialog(@NonNull Context context, boolean cancelable) {
        super(context);
        setContentView(R.layout.dialog_task);
        textTotalTaskName = findViewById(R.id.dialog_task_text_total_task_name);
        textCurrentTaskName = findViewById(R.id.dialog_task_text_current_task_name);
        this.mContext = context;
        setCancelable(cancelable);
    }

    public TaskDialog setTotalTaskName(String taskname){
        textTotalTaskName.setText(taskname);
        return this;
    }

    public TaskDialog setCurrentTaskName(String taskname){
        textCurrentTaskName.setText(taskname);
        return this;
    }

    public TextView getTextTotalTaskName(){
        return textTotalTaskName;
    }

    public TextView getTextCurrentTaskName(){
        return textCurrentTaskName;
    }
}

