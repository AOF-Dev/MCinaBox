package com.aof.mcinabox.launcher.uis;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aof.mcinabox.R;
import com.aof.mcinabox.activity.OldMainActivity;
import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.utils.DisplayUtils;
import com.aof.mcinabox.utils.FileTool;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.views.LineTextView;

import java.io.IOException;

import cosine.boat.LoadMe;

public class LogUI extends BaseUI implements View.OnClickListener {

    private LinearLayout layout_log;
    private TextView logView;
    private ScrollView scrollView;
    private Animation showAnim;
    private LinearLayout buttonRefreshFromFile;
    private LinearLayout buttonRefreshFromThread;

    public LogUI(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.layout_log = OldMainActivity.CURRENT_ACTIVITY.get().findViewById(R.id.layout_log);
        this.scrollView = new ScrollView(mContext);
        this.scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.scrollView.setBackgroundColor(Color.parseColor("#7F8E8E8E"));
        this.layout_log.addView(scrollView);
        this.logView = new LineTextView(mContext);
        this.logView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        this.logView.setTextSize(DisplayUtils.getPxFromSp(mContext, 3));
        this.logView.setTextColor(Color.WHITE);
        this.logView.setTextIsSelectable(true);
        this.scrollView.addView(logView);
        this.buttonRefreshFromFile = layout_log.findViewById(R.id.log_button_refresh_from_file);
        this.buttonRefreshFromThread = layout_log.findViewById(R.id.log_button_refresh_from_this);
        for(View view : new View[]{buttonRefreshFromFile, buttonRefreshFromThread}){
            view.setOnClickListener(this);
        }
        showAnim = AnimationUtils.loadAnimation(mContext, R.anim.layout_show);
    }

    @Override
    public void refreshUI() {

    }

    @Override
    public void saveUIConfig() {

    }

    @Override
    public void setUIVisibility(int visibility) {
        if (logView.getText().toString().equals("") && visibility == View.VISIBLE) {
            layout_log.startAnimation(showAnim);
            showLog(logView);
        }
        layout_log.setVisibility(visibility);
    }

    @Override
    public int getUIVisibility() {
        return layout_log.getVisibility();
    }

    private void showLog(TextView view){
        if (LoadMe.mReceiver != null && LoadMe.mReceiver.get() != null && !LoadMe.mReceiver.get().getLogs().equals("")) {
            view.setText(LoadMe.mReceiver.get().getLogs());
        } else {
            try {
                view.setText(FileTool.readToString(AppManifest.BOAT_LOG_FILE));
            } catch (IOException e){
                e.printStackTrace();
                view.setText(e.toString());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v == buttonRefreshFromFile){
            try {
                logView.setText(FileTool.readToString(AppManifest.BOAT_LOG_FILE));
            } catch (IOException e) {
                e.printStackTrace();
                DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), mContext.getString(R.string.tips_no_log), mContext.getString(R.string.title_ok), null);
            }
        }

        if(v == buttonRefreshFromThread){
            if (LoadMe.mReceiver != null && LoadMe.mReceiver.get() != null){
                logView.setText(LoadMe.mReceiver.get().getLogs());
            }else{
                DialogUtils.createSingleChoiceDialog(mContext, mContext.getString(R.string.title_error), mContext.getString(R.string.tips_process_is_not_running), mContext.getString(R.string.title_ok), null);
            }
        }
    }
}
