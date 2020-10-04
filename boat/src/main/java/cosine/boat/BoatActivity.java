package cosine.boat;

import android.app.Activity;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.os.Bundle;
import android.app.NativeActivity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;
import android.view.View;
import android.os.Handler;
import android.os.Message;
import android.widget.RelativeLayout;
import android.view.SurfaceHolder;
import com.aof.mcinabox.definitions.id.AppEvent;
import com.aof.mcinabox.definitions.models.BoatArgs;
import com.aof.mcinabox.gamecontroller.client.ClientInput;
import com.aof.mcinabox.gamecontroller.controller.BaseController;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.controller.HardwareController;
import com.aof.mcinabox.gamecontroller.controller.HwController;
import com.aof.mcinabox.gamecontroller.controller.VirtualController;
import java.util.ArrayList;

public class BoatActivity extends NativeActivity implements View.OnClickListener, View.OnTouchListener, ClientInput, AppEvent {

    private BoatArgs boatArgs;
    private PopupWindow popupWindow;
    private RelativeLayout baseLayout;
    private BaseController virtualController;
    private BaseController hardwareController;
    private BoatHandler mHandler;
    private final static String TAG = "BoatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setCallback(this);
        boatArgs = (BoatArgs) getIntent().getSerializableExtra("LauncherConfig");

        //设置悬浮窗口以及基本LinearLayout
        popupWindow = new PopupWindow();
        popupWindow.setWidth(LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(false);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        baseLayout = new RelativeLayout(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        baseLayout.setLayoutParams(layoutParams);
        baseLayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        popupWindow.setContentView(baseLayout);

        //添加控制器
        virtualController = new VirtualController(this,this, KEYMAP_TO_X);
        hardwareController = new HardwareController(this,this, KEYMAP_TO_X);
        //设定当前Activity
        BoatInput.mActivity = this;

        //初始化Handler
        mHandler = new BoatHandler();

    }

    @Override
    protected void onPause() {

        super.onPause();
        popupWindow.dismiss();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            popupWindow.showAtLocation(BoatActivity.this.getWindow().getDecorView(), Gravity.TOP | Gravity.LEFT, 0, 0);
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        super.surfaceCreated(holder);
        System.out.println("Surface is created!");

        new Thread() {
            @Override
            public void run() {
                BoatArgs boatArgs = (BoatArgs) getIntent().getSerializableExtra("LauncherConfig");
                LoadMe.exec(boatArgs);
                Message msg = new Message();
                msg.what = -1;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    public void setCursorMode(int mode) {
        Message msg = new Message();
        msg.what = mode;
        mHandler.sendMessage(msg);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
    }

    private class BoatHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BoatInput.CursorDisabled:
                    for (Controller c : new Controller[]{hardwareController, virtualController}) {
                        c.setInputMode(MARK_INPUT_MODE_CATCH);
                    }
                    break;
                case BoatInput.CursorEnabled:
                    for (Controller c : new Controller[]{hardwareController, virtualController}) {
                        c.setInputMode(MARK_INPUT_MODE_ALONE);
                    }
                    break;
                default:
                    BoatActivity.this.finish();
                    break;
            }
        }
    }

    @Override
    public void onClick(View p1) {

    }

    @Override
    public boolean onTouch(View p1, MotionEvent p2) {
        return false;
    }

    //重写 addContentView(View, ViewGroup.MarginLayoutParams) 方法实现NativeActivity动态添加View的功能
    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (params instanceof RelativeLayout.LayoutParams) {
            this.baseLayout.addView(view, params);
        } else {
            RelativeLayout.LayoutParams tparams = new RelativeLayout.LayoutParams(params.width, params.height);
            this.baseLayout.addView(view, tparams);
        }
        bringControllerToFront();
    }

    @Override
    public void typeWords(String str) {
        //TODO:根据字符串输入字符
        char[] cs = str.toCharArray();
        for (char c : cs) {
            BoatInput.setKey(0, c, true);
            BoatInput.setKey(0, c, false);
        }
    }

    private ArrayList<View> cvs = new ArrayList<>();

    @Override
    public void addControllerView(View v) {
        if (!cvs.contains(v)) {
            cvs.add(v);
        }
        this.addView(v);
    }

    public void bringControllerToFront(){
        for(View v : cvs){
           v.bringToFront();
        }
    }

    @Override
    public void setKey(int keyCode, boolean pressed) {
        BoatInput.setKey(keyCode, 0, pressed);
    }

    @Override
    public void setMouseButton(int mouseCode, boolean pressed) {
        BoatInput.setMouseButton(mouseCode, pressed);
    }

    @Override
    public void setMousePoniter(int x, int y) {
        BoatInput.setPointer(x, y);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void addView(View v) {
        if (v.getLayoutParams() == null) {
            return;
        }
        if (v.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            this.baseLayout.addView(v);
        } else {
            this.addContentView(v, v.getLayoutParams());
        }
        bringControllerToFront();
    }

    private void stopControllers() {
        for (Controller c : new Controller[]{hardwareController, virtualController}) {
            c.onStop();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        stopControllers();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public void onKey(KeyEvent event){
        ((HwController)hardwareController).dispatchKeyEvent(event);
    }

}


