package com.aof.mcinabox.gamecontroller.input.screen;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aof.mcinabox.R;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.OnscreenInput;
import com.aof.mcinabox.gamecontroller.input.screen.button.MouseButton;
import com.aof.mcinabox.utils.DisplayUtils;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;

import java.util.Timer;
import java.util.TimerTask;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_BUTTON;

public class OnscreenMouse implements OnscreenInput {

    public final static int SHOW_ALL = 0;
    public final static int SHOW_IN_GAME = 1;
    public final static int SHOW_OUT_GAME = 2;
    public static final int DEFAULT_WHEEL_SPEED = 100; //ms
    private final static int type = MOUSE_BUTTON;
    private final static String TAG = "OnscreenMouse";
    private final static int widthDp = 100;
    private final static int heightDp = 110;
    private static final int MIN_HOLDING_TIME = 500; //ms
    private final static int INDEX_BUTTON_WHEEL_UP = 1;
    private final static int INDEX_BUTTON_WHEEL_DOWN = 2;
    private final boolean moveable = false;
    private final int REFRESH_DELAY = 0; //ms
    private final int[] viewPos = new int[2];
    private Context mContext;
    private int show;
    private int screenWidth;
    private int screenHeight;
    private LinearLayout onscreenMouse;
    private Button moveButton;
    private MouseButton mouseButton_pri;
    private MouseButton mouseButton_sec;
    private MouseButton mouseButton_wheel_up;
    private MouseButton mouseButton_wheel_down;
    private Controller mController;
    private MouseButton mouseButton_middle;
    private OnscreenMouseConfigDialog configDialog;
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            MouseButton mb;
            switch (msg.what) {
                case INDEX_BUTTON_WHEEL_UP:
                    mb = mouseButton_wheel_up;
                    break;
                case INDEX_BUTTON_WHEEL_DOWN:
                    mb = mouseButton_wheel_down;
                    break;
                default:
                    return;
            }
            mController.sendKey(new BaseKeyEvent(TAG, mb.getMouseName(), true, type, null));
            mController.sendKey(new BaseKeyEvent(TAG, mb.getMouseName(), false, type, null));
            super.handleMessage(msg);
        }
    };
    private int REFRESH_PERIOD = 50; //ms
    private boolean enable;
    private long EVENT_DOWN_TIME;
    private boolean hasHeld = false;
    private Timer mTimer;

    private int posX;
    private int posY;

    @Override
    public boolean load(Context context, Controller controller) {
        this.mContext = context;
        this.mController = controller;
        screenWidth = mController.getConfig().getScreenWidth();
        screenHeight = mController.getConfig().getScreenHeight();

        onscreenMouse = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.virtual_mouse, null);
        mController.addContentView(onscreenMouse, new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(mContext, widthDp), DisplayUtils.getPxFromDp(mContext, heightDp)));

        mouseButton_pri = onscreenMouse.findViewById(R.id.onscreen_mouse_pri);
        mouseButton_sec = onscreenMouse.findViewById(R.id.onscreen_mouse_sec);
        mouseButton_middle = onscreenMouse.findViewById(R.id.onscreen_mouse_middle);
        mouseButton_wheel_up = onscreenMouse.findViewById(R.id.onscreen_mouse_wheel_up);
        mouseButton_wheel_down = onscreenMouse.findViewById(R.id.onscreen_mouse_wheel_down);
        moveButton = onscreenMouse.findViewById(R.id.onscreen_mouse_move);

        //设定监听器
        View[] views = new View[]{mouseButton_pri, mouseButton_sec, mouseButton_middle, mouseButton_wheel_up, mouseButton_wheel_down, moveButton};
        for (View v : views) {
            v.setOnTouchListener(this);
        }

        //设定配置器
        configDialog = new OnscreenMouseConfigDialog(context, this);

        return true;
    }

    @Override
    public void setUiMoveable(boolean moveable) {
        if (moveable) {
            moveButton.setVisibility(View.VISIBLE);
        } else {
            moveButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setUiVisibility(int visiablity) {
        onscreenMouse.setVisibility(visiablity);
    }

    @Override
    public boolean isEnabled() {
        return this.enable;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enable = enabled;
        updateUI();
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        updateUI();
    }

    @Override
    public void runConfigure() {
        configDialog.show();
    }

    private TimerTask createTask(final int index) {
        return new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = index;
                handler.sendMessage(msg);
            }
        };
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (v == mouseButton_wheel_up || v == mouseButton_wheel_down) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    hasHeld = false;
                    this.EVENT_DOWN_TIME = event.getDownTime();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getEventTime() - EVENT_DOWN_TIME >= MIN_HOLDING_TIME && !hasHeld) {
                        int index;
                        hasHeld = true;
                        if (v == mouseButton_wheel_up) {
                            index = INDEX_BUTTON_WHEEL_UP;
                            mController.sendKey(new BaseKeyEvent(TAG, mouseButton_wheel_up.getMouseName(), false, type, null));
                        } else {
                            mController.sendKey(new BaseKeyEvent(TAG, mouseButton_wheel_down.getMouseName(), false, type, null));
                            index = INDEX_BUTTON_WHEEL_DOWN;
                        }
                        mTimer = new Timer();
                        mTimer.schedule(createTask(index), REFRESH_DELAY, REFRESH_PERIOD);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    if (hasHeld) {
                        mTimer.cancel();
                        return true;
                    }
            }
        }

        if (v instanceof MouseButton) {
            sendKeyEvent(v, event);
            return true;
        }

        if (v == moveButton) {
            moveViewByTouch(onscreenMouse, event);
            return true;
        }

        return false;
    }

    @Override
    public boolean unload() {
        onscreenMouse.setVisibility(View.INVISIBLE);
        ViewGroup vg = (ViewGroup) onscreenMouse.getParent();
        vg.removeView(onscreenMouse);
        return false;
    }

    @Override
    public float[] getPos() {
        return (new float[]{posX, posY});
    }

    @Override
    public void setMargins(int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams p = onscreenMouse.getLayoutParams();
        ((ViewGroup.MarginLayoutParams) p).setMargins(left, top, 0, 0);
        onscreenMouse.setLayoutParams(p);
        this.posX = left;
        this.posY = top;
    }

    @Override
    public int[] getSize() {
        return new int[]{onscreenMouse.getLayoutParams().width, onscreenMouse.getLayoutParams().height};
    }

    @Override
    public View[] getViews() {
        return new View[]{this.onscreenMouse};
    }

    @Override
    public int getUiVisiability() {
        return onscreenMouse.getVisibility();
    }

    private void sendKeyEvent(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            mController.sendKey(new BaseKeyEvent(TAG, ((MouseButton) v).getMouseName(), true, type, null));
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            mController.sendKey(new BaseKeyEvent(TAG, ((MouseButton) v).getMouseName(), false, type, null));
        }
    }

    private void moveViewByTouch(View p2, MotionEvent p3) {
        switch (p3.getAction()) {
            case MotionEvent.ACTION_DOWN:
                viewPos[0] = (int) p3.getRawX();
                viewPos[1] = (int) p3.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) p3.getRawX() - viewPos[0];
                int dy = (int) p3.getRawY() - viewPos[1];
                int l = p2.getLeft() + dx;
                int b = p2.getBottom() + dy;
                int r = p2.getRight() + dx;
                int t = p2.getTop() + dy;
                //下面判断移动是否超出屏幕
                if (l < 0) {
                    l = 0;
                    r = l + p2.getWidth();
                }
                if (t < 0) {
                    t = 0;
                    b = t + p2.getHeight();
                }
                if (r > screenWidth) {
                    r = screenWidth;
                    l = r - p2.getWidth();
                }
                if (b > screenHeight) {
                    b = screenHeight;
                    t = b - p2.getHeight();
                }
                p2.layout(l, t, r, b);
                viewPos[0] = (int) p3.getRawX();
                viewPos[1] = (int) p3.getRawY();
                p2.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                setMargins(p2.getLeft(), p2.getTop(), 0, 0);
                break;
            default:
                break;
        }

    }

    public void setAlpha(float a) {
        onscreenMouse.setAlpha(a);
    }

    public void setSize(int width, int height) {
        ViewGroup.LayoutParams p = onscreenMouse.getLayoutParams();
        p.width = width;
        p.height = height;
        //控件重绘
        onscreenMouse.requestLayout();
        onscreenMouse.invalidate();
    }

    public void saveConfig() {
        configDialog.saveConfigToFile();
    }

    public void setWheelSpeed(int speed) {
        this.REFRESH_PERIOD = speed;
    }

    private void updateUI() {
        if (enable) {
            if (mController.isGrabbed()) {
                if (show == SHOW_ALL || show == SHOW_IN_GAME) {
                    this.setUiVisibility(View.VISIBLE);
                } else {
                    this.setUiVisibility(View.GONE);
                }
            } else {
                if (show == SHOW_ALL || show == SHOW_OUT_GAME) {
                    this.setUiVisibility(View.VISIBLE);
                } else {
                    this.setUiVisibility(View.GONE);
                }
            }
        } else {
            setUiVisibility(View.GONE);
        }
    }

    public int getShowStat() {
        return this.show;
    }

    public void setShowStat(int s) {
        this.show = s;
        updateUI();
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onResumed() {

    }

    @Override
    public Controller getController() {
        return this.mController;
    }

    private static class OnscreenMouseConfigDialog extends Dialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Dialog.OnCancelListener, CompoundButton.OnCheckedChangeListener {

        private final static String TAG = "OnscreenMouseConfigDialog";
        private final static int DEFAULT_ALPHA_PROGRESS = 40;
        private final static int DEFAULT_SIZE_PROGRESS = 50;
        private final static int DEFAULT_WHEEL_SPEED_PROGRESS = 0;
        private final static int MAX_ALPHA_PROGRESS = 100;
        private final static int MIN_ALPHA_PROGRESS = 0;
        private final static int MAX_SIZE_PROGRESS = 100;
        private final static int MIN_SIZE_PROGRESS = -50;
        private final static int MAX_WHEEL_SPEED_PROGRESS = 9;
        private final static int MIN_SHEEL_SPEED_PROGRESS = 1;
        private final static String spFileName = "input_onscreenmouse_config";
        private final static int spMode = Context.MODE_PRIVATE;
        private final static String sp_alpha_name = "alpha";
        private final static String sp_size_name = "size";
        private final static String sp_wheel_speed_name = "wheel_speed";
        private final static String sp_pos_x_name = "pos_x";
        private final static String sp_pos_y_name = "pos_y";
        private final static String sp_show_name = "show";
        private final Context mContext;
        private final OnscreenInput mInput;
        private Button buttonOK;
        private Button buttonCancel;
        private Button buttonRestore;
        private SeekBar seekbarAlpha;
        private SeekBar seekbarSize;
        private SeekBar seekbarWheelSpeed;
        private TextView textAlpha;
        private TextView textSize;
        private TextView textWheelSpeed;
        private RadioButton rbtAll;
        private RadioButton rbtInGame;
        private RadioButton rbtOutGame;
        private int originalAlphaProgress;
        private int originalSizeProgress;
        private int originalWheelSpeedProgress;
        private int originalMarginLeft;
        private int originalMarginTop;
        private int originalShow;
        private int originalInputWidth;
        private int originalInputHeight;
        private int screenWidth;
        private int screenHeight;

        public OnscreenMouseConfigDialog(@NonNull Context context, OnscreenInput input) {
            super(context);
            setContentView(R.layout.dialog_onscreen_mouse_config);
            mContext = context;
            mInput = input;
            init();
        }

        private void init() {
            this.setCanceledOnTouchOutside(false);
            this.setOnCancelListener(this);

            buttonOK = this.findViewById(R.id.input_onscreen_mouse_dialog_button_ok);
            buttonCancel = this.findViewById(R.id.input_onscreen_mouse_dialog_button_cancel);
            buttonRestore = this.findViewById(R.id.input_onscreen_mouse_dialog_button_restore);
            seekbarAlpha = this.findViewById(R.id.input_onscreen_mouse_dialog_seekbar_alpha);
            seekbarSize = this.findViewById(R.id.input_onscreen_mouse_dialog_seekbar_size);
            seekbarWheelSpeed = this.findViewById(R.id.input_onscreen_mouse_dialog_seekbar_wheelspeed);
            textAlpha = this.findViewById(R.id.input_onscreen_mouse_dialog_text_alpha);
            textSize = this.findViewById(R.id.input_onscreen_mouse_dialog_text_size);
            textWheelSpeed = this.findViewById(R.id.input_onscreen_mouse_dialog_text_wheelspeed);
            rbtAll = this.findViewById(R.id.input_onscreen_mouse_dialog_rbt_all);
            rbtInGame = this.findViewById(R.id.input_onscreen_mouse_dialog_rbt_in_game);
            rbtOutGame = this.findViewById(R.id.input_onscreen_mouse_dialog_rbt_out_game);

            for (View v : new View[]{buttonOK, buttonCancel, buttonRestore}) {
                v.setOnClickListener(this);
            }
            for (SeekBar s : new SeekBar[]{seekbarAlpha, seekbarSize, seekbarWheelSpeed}) {
                s.setOnSeekBarChangeListener(this);
            }
            for (RadioButton rbt : new RadioButton[]{rbtAll, rbtInGame, rbtOutGame}) {
                rbt.setOnCheckedChangeListener(this);
            }

            originalInputWidth = mInput.getSize()[0];
            originalInputHeight = mInput.getSize()[1];
            screenWidth = mInput.getController().getConfig().getScreenWidth();
            screenHeight = mInput.getController().getConfig().getScreenHeight();

            //初始化控件属性
            this.seekbarAlpha.setMax(MAX_ALPHA_PROGRESS);
            this.seekbarSize.setMax(MAX_SIZE_PROGRESS);
            this.seekbarWheelSpeed.setMax(MAX_WHEEL_SPEED_PROGRESS);

            loadConfigFromFile();

        }

        @Override
        public void onCancel(DialogInterface dialog) {

            if (dialog == this) {
                seekbarAlpha.setProgress(originalAlphaProgress);
                seekbarSize.setProgress(originalSizeProgress);
                seekbarWheelSpeed.setProgress(originalWheelSpeedProgress);
                mInput.setMargins(originalMarginLeft, originalMarginTop, 0, 0);
                switch (originalShow) {
                    case OnscreenMouse.SHOW_ALL:
                        rbtAll.setChecked(true);
                        break;
                    case OnscreenMouse.SHOW_IN_GAME:
                        rbtInGame.setChecked(true);
                        break;
                    case OnscreenMouse.SHOW_OUT_GAME:
                        rbtOutGame.setChecked(true);
                        break;
                }
            }

        }

        @Override
        public void onClick(View v) {

            if (v == buttonCancel) {
                this.cancel();
            }

            if (v == buttonOK) {
                this.dismiss();
            }

            if (v == buttonRestore) {

                DialogUtils.createBothChoicesDialog(mContext, mContext.getString(R.string.title_warn), mContext.getString(R.string.tips_are_you_sure_to_restore_setting), mContext.getString(R.string.title_ok), mContext.getString(R.string.title_cancel), new DialogSupports() {
                    @Override
                    public void runWhenPositive() {
                        restoreConfig();
                    }
                });

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void show() {
            super.show();
            originalAlphaProgress = seekbarAlpha.getProgress();
            originalSizeProgress = seekbarSize.getProgress();
            originalWheelSpeedProgress = seekbarWheelSpeed.getProgress();
            originalMarginLeft = (int) mInput.getPos()[0];
            originalMarginTop = (int) mInput.getPos()[1];
            originalShow = ((OnscreenMouse) mInput).getShowStat();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == seekbarAlpha) {
                int p = progress + MIN_ALPHA_PROGRESS;
                String str = p + "%";
                textAlpha.setText(str);
                //设置透明度
                float alpha = 1 - p * 0.01f;
                ((OnscreenMouse) mInput).setAlpha(alpha);
            }

            if (seekBar == seekbarSize) {
                int p = progress + MIN_SIZE_PROGRESS;
                textSize.setText(String.valueOf(p));
                //设置大小
                int centerX = (int) (mInput.getPos()[0] + mInput.getSize()[0] / 2);
                int centerY = (int) (mInput.getPos()[1] + mInput.getSize()[1] / 2);
                int tmpWidth = (int) ((1 + p * 0.01f) * originalInputWidth);
                int tmpHeight = (int) ((1 + p * 0.01f) * originalInputHeight);
                ((OnscreenMouse) mInput).setSize(tmpWidth, tmpHeight);
                //调整位置
                adjustPos(centerX, centerY);
            }

            if (seekBar == seekbarWheelSpeed) {
                int p = progress + MIN_SHEEL_SPEED_PROGRESS;
                textWheelSpeed.setText(String.valueOf(p));
                //设置滚轮速度
                ((OnscreenMouse) mInput).setWheelSpeed(OnscreenMouse.DEFAULT_WHEEL_SPEED / p);
            }
        }

        @Override
        public void onStop() {
            super.onStop();
            saveConfigToFile();
        }

        private void restoreConfig() {
            seekbarAlpha.setProgress(DEFAULT_ALPHA_PROGRESS);
            seekbarSize.setProgress(DEFAULT_SIZE_PROGRESS);
            seekbarWheelSpeed.setProgress(DEFAULT_WHEEL_SPEED_PROGRESS);
            rbtAll.setChecked(true);
        }

        private void adjustPos(int originalCenterX, int originalCenterY) {
            int viewWidth = mInput.getSize()[0];
            int viewHeight = mInput.getSize()[1];
            int marginLeft = originalCenterX - viewWidth / 2;
            int margeinTop = originalCenterY - viewHeight / 2;

            //左边界检测
            if (marginLeft < 0) {
                marginLeft = 0;
            }
            //上边界检测
            if (margeinTop < 0) {
                margeinTop = 0;
            }
            //右边界检测
            if (marginLeft + viewWidth > screenWidth) {
                marginLeft = screenWidth - viewWidth;
            }
            //下边界检测
            if (margeinTop + viewHeight > screenHeight) {
                margeinTop = screenHeight - viewHeight;
            }

            mInput.setMargins(marginLeft, margeinTop, 0, 0);
        }

        private void loadConfigFromFile() {
            SharedPreferences sp = mContext.getSharedPreferences(spFileName, spMode);

            //先设定一个最大值，防止Seebar的监听器无法监听到事件
            seekbarAlpha.setProgress(MAX_ALPHA_PROGRESS);
            seekbarSize.setProgress(MAX_SIZE_PROGRESS);
            seekbarWheelSpeed.setProgress(MAX_WHEEL_SPEED_PROGRESS);
            //设定存储的数据
            seekbarAlpha.setProgress(sp.getInt(sp_alpha_name, DEFAULT_ALPHA_PROGRESS));
            seekbarSize.setProgress(sp.getInt(sp_size_name, DEFAULT_SIZE_PROGRESS));
            seekbarWheelSpeed.setProgress(sp.getInt(sp_wheel_speed_name, DEFAULT_WHEEL_SPEED_PROGRESS));
            mInput.setMargins(sp.getInt(sp_pos_x_name, 0), sp.getInt(sp_pos_y_name, 0), 0, 0);
            switch (sp.getInt(sp_show_name, OnscreenMouse.SHOW_ALL)) {
                case OnscreenMouse.SHOW_ALL:
                    rbtAll.setChecked(true);
                    break;
                case OnscreenMouse.SHOW_IN_GAME:
                    rbtInGame.setChecked(true);
                    break;
                case OnscreenMouse.SHOW_OUT_GAME:
                    rbtOutGame.setChecked(true);
                    break;
            }
        }

        public void saveConfigToFile() {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(spFileName, spMode).edit();
            editor.putInt(sp_alpha_name, seekbarAlpha.getProgress());
            editor.putInt(sp_size_name, seekbarSize.getProgress());
            editor.putInt(sp_wheel_speed_name, seekbarWheelSpeed.getProgress());
            editor.putInt(sp_pos_x_name, (int) mInput.getPos()[0]);
            editor.putInt(sp_pos_y_name, (int) mInput.getPos()[1]);
            editor.putInt(sp_show_name, ((OnscreenMouse) mInput).getShowStat());
            editor.apply();
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == rbtAll) {
                if (isChecked) {
                    ((OnscreenMouse) mInput).setShowStat(OnscreenMouse.SHOW_ALL);
                }
            }

            if (buttonView == rbtInGame) {
                if (isChecked) {
                    ((OnscreenMouse) mInput).setShowStat(OnscreenMouse.SHOW_IN_GAME);
                }
            }

            if (buttonView == rbtOutGame) {
                if (isChecked) {
                    ((OnscreenMouse) mInput).setShowStat(OnscreenMouse.SHOW_OUT_GAME);
                }
            }
        }
    }

}

