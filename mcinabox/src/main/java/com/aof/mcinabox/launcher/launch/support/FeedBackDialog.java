package com.aof.mcinabox.launcher.launch.support;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.aof.mcinabox.R;

public class FeedBackDialog extends Dialog {

    private TextView textProgress;
    private Context mContext;

    public FeedBackDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        setContentView(R.layout.dialog_launch_feedback);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        init();
    }

    private void init() {
        this.textProgress = findViewById(R.id.dialog_launch_feedback_text_current);
    }

    public void setProgressName(String name) {
        FeedBackDialog.this.textProgress.setText(name);
    }

}
