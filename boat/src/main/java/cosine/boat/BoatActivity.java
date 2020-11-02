package cosine.boat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aof.mcinabox.definitions.models.BoatArgs;
import com.aof.mcinabox.gamecontroller.client.ClientInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.aof.mcinabox.definitions.id.key.KeyMode.MARK_INPUT_MODE_ALONE;
import static com.aof.mcinabox.definitions.id.key.KeyMode.MARK_INPUT_MODE_CATCH;

public class BoatActivity extends AppCompatActivity implements SurfaceHolder.Callback, ClientInput {
    private final static String TAG = "BoatActivity";

    private SurfaceView surfaceView;
    private BoatArgs boatArgs;
    private RelativeLayout baseLayout;
    public static IController controllerInterface;
    private BoatHandler mHandler;
    private Timer mTimer;
    private final static int REFRESH_P = 5000; //ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nOnCreate();
        setContentView(R.layout.activity_boat);

        surfaceView = findViewById(R.id.surface_view);
        baseLayout = findViewById(R.id.base_layout);
        surfaceView.getHolder().addCallback(this);

        boatArgs = (BoatArgs) getIntent().getSerializableExtra("LauncherConfig");

        //初始化Handler
        mHandler = new BoatHandler(getMainLooper());

        //启动定时器
        mTimer = new Timer();
        mTimer.schedule(createTimerTask(), REFRESH_P, REFRESH_P);

        controllerInterface.onActivityCreate(this);
    }

    private native void nOnCreate();

    @Override
    protected void onDestroy() {
        nOnDestroy();
        super.onDestroy();
    }

    private native void nOnDestroy();

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private TimerTask createTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                controllerInterface.saveConfig();
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        //取消定时器
        mTimer.cancel();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //启动定时器
        mTimer = new Timer();
        mTimer.schedule(createTimerTask(), REFRESH_P, REFRESH_P);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        nSurfaceCreated(holder.getSurface());
        if (nIsLoaded()) {
            new Thread() {
                @Override
                public void run() {
                    BoatArgs boatArgs = (BoatArgs) getIntent().getSerializableExtra("LauncherConfig");
                    LoadMe.exec(boatArgs);
                }
            }.start();
        } else {
            // TODO: Something went wrong during initialization. Alert the user.
        }
    }

    private native void nSurfaceCreated(Surface surface);

    private native boolean nIsLoaded();

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: format = " + format + ", width = " + width + ", height = " + height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        nSurfaceCreated(holder.getSurface());
        stopControllers();
    }

    private native void nSurfaceDestroyed(Surface surface);

    public void setCursorMode(int mode) {
        Message msg = new Message();
        msg.what = mode;
        mHandler.sendMessage(msg);
    }

    private class BoatHandler extends Handler {
        public BoatHandler(@NonNull Looper looper) {
            super(looper);
        }

        public BoatHandler(@NonNull Looper looper, @Nullable Callback callback) {
            super(looper, callback);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BoatInput.CURSOR_DISABLED:
                    controllerInterface.setInputMode(MARK_INPUT_MODE_CATCH);
                    break;
                case BoatInput.CURSOR_ENABLED:
                    controllerInterface.setInputMode(MARK_INPUT_MODE_ALONE);
                    break;
                default:
                    BoatActivity.this.finish();
                    break;
            }
        }
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
        // TODO:根据字符串输入字符
        char[] cs = str.toCharArray();
        for (char c : cs) {
            BoatInput.setKey(0, c, true);
            BoatInput.setKey(0, c, false);
        }
    }

    private List<View> cvs = new ArrayList<>();

    @Override
    public void addControllerView(View v) {
        if (!cvs.contains(v)) {
            cvs.add(v);
        }
        this.addView(v);
    }

    @Override
    public int[] getPointer() {
        return BoatInput.getPointer();
    }

    public void bringControllerToFront() {
        for (View v : cvs) {
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
        controllerInterface.onStop();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        controllerInterface.dispatchKeyEvent(event);
        return true;
    }


    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        controllerInterface.dispatchMotionKeyEvent(event);
        return true;
    }

    @Override
    public ViewGroup getViewsParent() {
        return this.baseLayout;
    }

    static {
        System.loadLibrary("boat");
    }

    public interface IController {
        void onActivityCreate(BoatActivity boatActivity);
        void saveConfig();
        void setInputMode(int inputMode);
        void onStop();
        void dispatchKeyEvent(KeyEvent event);
        void dispatchMotionKeyEvent(MotionEvent event);
    }
}
