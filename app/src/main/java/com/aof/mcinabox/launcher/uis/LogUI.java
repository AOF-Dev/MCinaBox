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
import com.aof.mcinabox.views.LineTextView;

import java.io.IOException;

import cosine.boat.LoadMe;

public class LogUI extends BaseUI {

    private LinearLayout layout_log;
    private TextView logView;
    private ScrollView scrollView;
    private Animation showAnim;

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
        if (visibility == View.VISIBLE) {
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
        if (LoadMe.mReceiver != null && !LoadMe.mReceiver.getLogs().equals("")) {
            view.setText(LoadMe.mReceiver.getLogs());
        } else {
            try {
                view.setText(FileTool.readToString(AppManifest.BOAT_LOG_FILE));
            } catch (IOException e){
                e.printStackTrace();
                view.setText(e.toString());
            }
        }
    }
}
