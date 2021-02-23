package com.aof.mcinabox.gamecontroller.input.log;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.definitions.manifest.AppManifest;
import com.aof.mcinabox.gamecontroller.input.Input;
import com.aof.mcinabox.utils.DisplayUtils;
import com.aof.mcinabox.utils.FileTool;
import com.aof.mcinabox.views.LineTextView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import cosine.boat.LoadMe;

public class DebugInfo implements Input, View.OnClickListener {
    private final static String TAG = "DebugInfo";
    private Context mContext;
    private Controller mController;
    private boolean isEnabled;

    //private Button switchButton;
    private LogView mLogView;
    private LoadMe.LogReceiver mReceiver;
    //private boolean isShowInfo = true;

    @Override
    public boolean load(Context context, Controller controller) {
        this.mContext = context;
        this.mController = controller;
        //this.switchButton = new Button(mContext);

        /*switchButton.setBackground(ContextCompat.getDrawable(mContext, R.drawable.background_floatbutton));
        switchButton.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(mContext, 30), DisplayUtils.getPxFromDp(mContext, 30)));
        mController.addView(switchButton);
        switchButton.setX(mController.getConfig().getScreenWidth() - switchButton.getLayoutParams().width - DisplayUtils.getPxFromDp(mContext, 30));
        switchButton.setY(0);
        switchButton.setOnClickListener(this);
         */

        mLogView = new LogView(mContext);
        mLogView.setLayoutParams(new ViewGroup.LayoutParams(mController.getConfig().getScreenWidth() - DisplayUtils.getPxFromDp(mContext, 10), mController.getConfig().getScreenHeight() / 2 - DisplayUtils.getPxFromDp(mContext, 30)));
        mController.addView(mLogView);
        mLogView.setX(0);
        mLogView.setY(mController.getConfig().getScreenHeight() - mLogView.getLayoutParams().height);

        if (LoadMe.mReceiver == null || LoadMe.mReceiver.get() == null) {
            mReceiver = new LoadMe.LogReceiver() {
                final StringBuilder stringBuilder = new StringBuilder();

                @Override
                public void pushLog(String log) {
                    mLogView.appendLog(log);
                    stringBuilder.append(log);
                    writeLog(log);
                }

                @Override
                public String getLogs() {
                    return stringBuilder.toString();
                }
            };
            LoadMe.mReceiver = new WeakReference<>(mReceiver);
        }

        return true;
    }

    @Override
    public boolean unload() {
        ViewGroup vg = (ViewGroup) mLogView.getParent();
        vg.removeView(mLogView);
        //vg.removeView(switchButton);
        LoadMe.mReceiver = null;
        return true;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {

    }

    @Override
    public void runConfigure() {

    }

    @Override
    public void saveConfig() {

    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        if(enabled){
            mLogView.setVisibility(View.VISIBLE);
        }else{
            mLogView.setVisibility(View.GONE);
        }
        /*
        if (enabled && isShowInfo) {
            switchButton.setVisibility(View.VISIBLE);
            mLogView.setVisibility(View.VISIBLE);
        } else if (!enabled) {
            switchButton.setVisibility(View.GONE);
            mLogView.setVisibility(View.GONE);
        } else {
            switchButton.setVisibility(View.VISIBLE);
        }
         */

    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onResumed() {

    }

    @Override
    public Controller getController() {
        return mController;
    }

    @Override
    public void onClick(View v) {
        /*if (v == switchButton) {
            if (!isShowInfo) {
                mLogView.setVisibility(View.VISIBLE);
                isShowInfo = true;
            } else {
                mLogView.setVisibility(View.GONE);
                isShowInfo = false;
            }
        }
         */
    }

    private boolean firstWrite = true;
    private boolean isWrite = true;
    private void writeLog(String log){
        if(!isWrite)
            return;
        File logFile = new File(AppManifest.BOAT_LOG_FILE);
        if(!logFile.exists()) {
            try {
                if(!logFile.createNewFile()){
                    isWrite = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (firstWrite) {
            FileTool.writeData(logFile.getAbsolutePath(), log);
            firstWrite = false;
        } else {
            FileTool.addStringLineToFile(log, logFile);
        }


    }

    public class LogView extends ScrollView {

        private final TextView mTextView;

        public LogView(@NonNull Context context) {
            super(context);
            this.setBackground(getViewBackground());
            this.mTextView = new LineTextView(context);

            mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            addView(mTextView);
            mTextView.setTextColor(Color.WHITE);
            mTextView.setTextIsSelectable(true);
            mTextView.setTextSize(DisplayUtils.getPxFromSp(mContext, 2));
            mTextView.setLineSpacing(0, 1f);
        }

        public void appendLog(String str) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    if (mTextView != null) {
                        LogView.this.mTextView.append(str);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toBottom(LogView.this, mTextView);
                            }
                        }, 50);
                    }
                }
            });
        }

        private void toBottom(final ScrollView scrollView, final View view) {
            int offset = view.getHeight()
                    - scrollView.getHeight();
            if (offset < 0) {
                offset = 0;
            }
            scrollView.scrollTo(0, offset);
        }

        private LayerDrawable getViewBackground() {
            int radiusSize = 0;
            int mainColor = Color.parseColor("#7f5B5B5B");

            float[] outerR = new float[]{radiusSize, radiusSize, radiusSize, radiusSize, radiusSize, radiusSize, radiusSize, radiusSize};
            RoundRectShape rectShape = new RoundRectShape(outerR, null, null);
            ShapeDrawable shapeDrawable = new ShapeDrawable();
            shapeDrawable.setShape(rectShape);
            shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
            shapeDrawable.getPaint().setColor(mainColor);

            Drawable[] layers = new Drawable[]{shapeDrawable};

            return new LayerDrawable(layers);
        }
    }
}
