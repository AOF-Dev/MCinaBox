package com.aof.mcinabox.gamecontroller.input.screen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.aof.mcinabox.gamecontroller.definitions.map.KeyMap;
import com.aof.mcinabox.gamecontroller.definitions.map.MouseMap;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.OnscreenInput;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_POINTER;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_POINTER_INC;

public class OnscreenTouchpad implements OnscreenInput, KeyMap, MouseMap {

    public final static int TOUCHPAD_MODE_SLIDE = 1;
    public final static int TOUCHPAD_MODE_POINT = 2;
    public final static int DEFAULT_HOLDING_DELAY = 500;
    private final static String TAG = "OnscreenTouchpad";
    private final static int type_1 = MOUSE_BUTTON;
    private final static int type_2 = MOUSE_POINTER;
    private final static int type_3 = MOUSE_POINTER_INC;
    private final static int MAX_MOVE_DISTANCE = 5;
    private final static long MIN_SHLDING_TIME = 100;
    private Context mContext;
    private Controller mController;
    private LinearLayout onscreenTouchpad;
    private Button touchpad;
    private int touchpadMode = TOUCHPAD_MODE_POINT;
    private int inputSpeedLevel = 0; //-5 ~ 10 || 减少50% ~  增加100%
    private OnscreenTouchpadConfigDialog configDialog;
    private boolean enable;
    private int cursorDownPosX;
    private int cursorDownPosY;
    private long MIN_HOLDING_TIME = 500;
    private boolean performClick;
    private boolean hasPerformLeftClick;
    private long cursorDownTime;
    private int posX;
    private int posY;

    @Override
    public boolean load(Context context, Controller controller) {

        this.mContext = context;
        this.mController = controller;

        onscreenTouchpad = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.virtual_touchpad, null);
        mController.addContentView(onscreenTouchpad, new ViewGroup.LayoutParams(mController.getConfig().getScreenWidth(), mController.getConfig().getScreenHeight()));
        touchpad = onscreenTouchpad.findViewById(R.id.touchpad_button);

        touchpad.setOnTouchListener(this);

        //设定配置器
        configDialog = new OnscreenTouchpadConfigDialog(mContext, this);

        return true;
    }

    @Override
    public void setUiMoveable(boolean moveable) {
        // to do nothing.
    }

    @Override
    public void setUiVisibility(int visiablity) {
        onscreenTouchpad.setVisibility(visiablity);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == touchpad) {
            locateCursor(event);
            performMouseClick(event);
            return true;
        }
        return false;
    }

    private int initX = 0;
    private int initY = 0;
    private void locateCursor(MotionEvent event) {
        if(mController.isGrabbed()){
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE:
                    sendPointer((int) ((event.getX() - initX) * (1 + inputSpeedLevel * 0.1f)), (int) ((event.getY() - initY) * (1 + inputSpeedLevel * 0.1f)), type_3);
                    break;
                default:
                    break;
            }
            initX = (int) event.getX();
            initY = (int) event.getY();
        }else{
            switch (touchpadMode) {
                case TOUCHPAD_MODE_POINT:
                    sendPointer((int) event.getX(), (int) event.getY(), type_2);
                    break;
                case TOUCHPAD_MODE_SLIDE:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_MOVE:
                            sendPointer((int) ((event.getX() - initX) * (1 + inputSpeedLevel * 0.1f)), (int) ((event.getY() - initY) * (1 + inputSpeedLevel * 0.1f)), type_3);
                            break;
                        default:
                            break;
                    }
                    initX = (int) event.getX();
                    initY = (int) event.getY();
                    break;
                default:
                    break;
            }
        }
    }

    public void performMouseClick(MotionEvent event) {
        if (mController.isGrabbed()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cursorDownTime = System.currentTimeMillis();
                    cursorDownPosX = (int) event.getX();
                    cursorDownPosY = (int) event.getY();
                    performClick = true;
                    hasPerformLeftClick = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    long currentTime = System.currentTimeMillis();
                    if (Math.abs(cursorDownPosX - event.getX()) > MAX_MOVE_DISTANCE || Math.abs(cursorDownPosY - event.getY()) > MAX_MOVE_DISTANCE) {
                        performClick = false;
                    }
                    if (currentTime - cursorDownTime >= MIN_HOLDING_TIME && !hasPerformLeftClick && performClick) {
                        hasPerformLeftClick = true;
                        sendMouseEvent(MOUSEMAP_BUTTON_LEFT, true);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!hasPerformLeftClick && performClick && System.currentTimeMillis() - cursorDownTime <= MIN_SHLDING_TIME) {
                        sendMouseEvent(MOUSEMAP_BUTTON_RIGHT, true);
                        sendMouseEvent(MOUSEMAP_BUTTON_RIGHT, false);
                    }
                    if (hasPerformLeftClick) {
                        sendMouseEvent(MOUSEMAP_BUTTON_LEFT, false);
                        hasPerformLeftClick = false;
                    }
                    break;
            }
        } else {
            switch (this.touchpadMode) {
                case TOUCHPAD_MODE_POINT:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            sendMouseEvent(MOUSEMAP_BUTTON_LEFT, true);
                            break;
                        case MotionEvent.ACTION_UP:
                            sendMouseEvent(MOUSEMAP_BUTTON_LEFT, false);
                            break;
                    }
                    break;
                case TOUCHPAD_MODE_SLIDE:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            cursorDownPosX = (int) event.getRawX();
                            cursorDownPosY = (int) event.getRawY();
                            cursorDownTime = event.getDownTime();
                            performClick = true;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (Math.abs(cursorDownPosX - event.getRawX()) > MAX_MOVE_DISTANCE || Math.abs(cursorDownPosY - event.getRawY()) > MAX_MOVE_DISTANCE) {
                                performClick = false;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (performClick) {
                                if (event.getEventTime() - cursorDownTime < MIN_HOLDING_TIME) {
                                    sendMouseEvent(MOUSEMAP_BUTTON_LEFT, true);
                                    try {
                                        Thread.sleep(20);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    sendMouseEvent(MOUSEMAP_BUTTON_LEFT, false);
                                } else {
                                    sendMouseEvent(MOUSEMAP_BUTTON_RIGHT, true);
                                    sendMouseEvent(MOUSEMAP_BUTTON_RIGHT, false);
                                }
                            }
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public boolean unload() {
        onscreenTouchpad.setVisibility(View.INVISIBLE);
        ViewGroup vg = (ViewGroup) onscreenTouchpad.getParent();
        vg.removeView(onscreenTouchpad);
        return true;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
    }

    @Override
    public void runConfigure() {
        this.configDialog.show();
    }

    @Override
    public float[] getPos() {
        return (new float[]{posX, posY});
    }

    @Override
    public void setMargins(int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams p = touchpad.getLayoutParams();
        ((ViewGroup.MarginLayoutParams) p).setMargins(left, top, 0, 0);
        touchpad.setLayoutParams(p);
        this.posX = left;
        this.posY = top;
    }

    @Override
    public int[] getSize() {
        return new int[]{touchpad.getWidth(), touchpad.getHeight()};
    }

    @Override
    public View[] getViews() {
        return new View[]{this.onscreenTouchpad};
    }

    @Override
    public int getUiVisiability() {
        return onscreenTouchpad.getVisibility();
    }

    public void setInputSpeedLevel(int level) {
        this.inputSpeedLevel = level;
    }

    public int getTouchpadMode() {
        return this.touchpadMode;
    }

    public void setTouchpadMode(int mode) {
        this.touchpadMode = mode;
    }

    private void sendPointer(int x, int y, int type) {
        mController.sendKey(new BaseKeyEvent(TAG, null, false, type, new int[]{x, y}));
    }

    private void sendMouseEvent(String name, boolean pressed) {
        mController.sendKey(new BaseKeyEvent(TAG, name, pressed, type_1, null));
    }

    @Override
    public void saveConfig() {
        //to do nothing.
    }

    public void setHoldingDelay(long delay) {
        this.MIN_HOLDING_TIME = delay;
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

    private void updateUI() {
        if (enable) {
            setUiVisibility(View.VISIBLE);
        } else {
            setUiVisibility(View.GONE);
        }
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

    private static class OnscreenTouchpadConfigDialog extends Dialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Dialog.OnCancelListener, RadioButton.OnCheckedChangeListener {


        private final static String TAG = "OnscreenTouchConfigDialog";
        private final static int DEFAULT_SPEED_PROGRESS = 5;
        private final static int MAX_SPEED_PROGRESS = 15;
        private final static int MIN_SPEED_PROGRESS = -5;
        private final static int MAX_DELAY_PROGRESS = 900;
        private final static int MIN_DELAY_PROGRESS = 100;
        private final static int DEFAULT_DELAY_PROGRESS = OnscreenTouchpad.DEFAULT_HOLDING_DELAY - MIN_DELAY_PROGRESS;
        private final static String spFileName = "input_onscreentouchpad_config";
        private final static int spMode = Context.MODE_PRIVATE;
        private final static String sp_speed_name = "speed";
        private final static String sp_touchpad_mode = "touchpad_mode";
        private final static String sp_delay_name = "delay";
        private final Context mContext;
        private final OnscreenInput mInput;
        private SeekBar seekbarSpeed;
        private SeekBar seekbarDelay;
        private TextView textSpeed;
        private TextView textDelay;
        private RadioButton radioSlide;
        private RadioButton radioPoint;
        private Button buttonOK;
        private Button buttonCancel;
        private Button buttonRestore;
        private int originalSpeedProgress;
        private int originalTouchpadMode;
        private int originalDelayProgress;

        public OnscreenTouchpadConfigDialog(@NonNull Context context, OnscreenInput input) {
            super(context);
            this.setContentView(R.layout.dialog_onscreen_touchpad_config);
            this.mContext = context;
            this.mInput = input;
            init();
        }

        private void init() {
            this.setCanceledOnTouchOutside(false);
            this.setOnCancelListener(this);

            this.seekbarSpeed = this.findViewById(R.id.input_onscreen_touchpad_dialog_seekbar_speed);
            this.seekbarDelay = this.findViewById(R.id.input_onscreen_touchpad_dialog_seekbar_delay);
            this.textSpeed = this.findViewById(R.id.input_onscreen_touchpad_dialog_text_speed);
            this.textDelay = this.findViewById(R.id.input_onscreen_touchpad_dialog_text_delay);
            this.radioSlide = this.findViewById(R.id.input_onscreen_touchpad_dialog_radio_slide);
            this.radioPoint = this.findViewById(R.id.input_onscreen_touchpad_dialog_radio_point);
            this.buttonOK = this.findViewById(R.id.input_onscreen_touchpad_dialog_button_ok);
            this.buttonCancel = this.findViewById(R.id.input_onscreen_touchpad_dialog_button_cancel);
            this.buttonRestore = this.findViewById(R.id.input_onscreen_touchpad_dialog_button_restore);

            for (View v : new View[]{buttonOK, buttonCancel, buttonRestore}) {
                v.setOnClickListener(this);
            }
            for (RadioButton r : new RadioButton[]{radioPoint, radioSlide}) {
                r.setOnCheckedChangeListener(this);
            }
            for (SeekBar s : new SeekBar[]{seekbarSpeed, seekbarDelay}) {
                s.setOnSeekBarChangeListener(this);
            }
            //初始化控件属性
            this.seekbarSpeed.setMax(MAX_SPEED_PROGRESS);
            this.seekbarDelay.setMax(MAX_DELAY_PROGRESS);

            loadConfigFromFile();

        }

        @Override
        public void onCancel(DialogInterface dialog) {

            if (dialog == this) {
                seekbarSpeed.setProgress(originalSpeedProgress);
                seekbarDelay.setProgress(originalDelayProgress);
                switch (originalSpeedProgress) {
                    case OnscreenTouchpad.TOUCHPAD_MODE_POINT:
                        radioPoint.setChecked(true);
                        break;
                    case OnscreenTouchpad.TOUCHPAD_MODE_SLIDE:
                        radioSlide.setChecked(true);
                        break;
                }
            }

        }

        @Override
        public void onClick(View v) {

            if (v == buttonOK) {
                this.dismiss();
            }

            if (v == buttonCancel) {
                this.cancel();
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
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (seekBar == seekbarSpeed) {
                int p = progress + MIN_SPEED_PROGRESS;
                String str = String.valueOf(p);
                textSpeed.setText(str);
                //设置速度等级
                ((OnscreenTouchpad) mInput).setInputSpeedLevel(p);
            }

            if (seekBar == seekbarDelay) {
                int p = progress + MIN_DELAY_PROGRESS;
                String str = p + "ms";
                textDelay.setText(str);
                //设置延迟
                ((OnscreenTouchpad) mInput).setHoldingDelay(p);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (buttonView == radioPoint) {
                if (isChecked) {
                    ((OnscreenTouchpad) mInput).setTouchpadMode(OnscreenTouchpad.TOUCHPAD_MODE_POINT);
                }
            }

            if (buttonView == radioSlide) {
                if (isChecked) {
                    ((OnscreenTouchpad) mInput).setTouchpadMode(OnscreenTouchpad.TOUCHPAD_MODE_SLIDE);
                }
            }

        }

        @Override
        public void onStop() {
            super.onStop();
            saveConfigToFile();
        }

        @Override
        public void show() {
            super.show();
            originalSpeedProgress = seekbarSpeed.getProgress();
            originalDelayProgress = seekbarDelay.getProgress();
            if (radioSlide.isChecked()) {
                originalTouchpadMode = OnscreenTouchpad.TOUCHPAD_MODE_SLIDE;
            } else if (radioPoint.isChecked()) {
                originalTouchpadMode = OnscreenTouchpad.TOUCHPAD_MODE_POINT;
            }
        }

        private void restoreConfig() {
            seekbarSpeed.setProgress(DEFAULT_SPEED_PROGRESS);
            seekbarDelay.setProgress(DEFAULT_DELAY_PROGRESS);
            radioPoint.setChecked(true);
        }

        private void loadConfigFromFile() {
            SharedPreferences sp = mContext.getSharedPreferences(spFileName, spMode);

            //先设定一个最大值，防止Seebar的监听器无法监听到事件
            seekbarSpeed.setProgress(MAX_SPEED_PROGRESS);
            seekbarDelay.setProgress(MAX_DELAY_PROGRESS);
            //设定存储的数据
            seekbarSpeed.setProgress(sp.getInt(sp_speed_name, DEFAULT_SPEED_PROGRESS));
            switch (sp.getInt(sp_touchpad_mode, OnscreenTouchpad.TOUCHPAD_MODE_POINT)) {
                case OnscreenTouchpad.TOUCHPAD_MODE_POINT:
                    ((OnscreenTouchpad) mInput).setTouchpadMode(OnscreenTouchpad.TOUCHPAD_MODE_POINT);
                    this.radioPoint.setChecked(true);
                    break;
                case OnscreenTouchpad.TOUCHPAD_MODE_SLIDE:
                    ((OnscreenTouchpad) mInput).setTouchpadMode(OnscreenTouchpad.TOUCHPAD_MODE_SLIDE);
                    this.radioSlide.setChecked(true);
                    break;
            }
            seekbarDelay.setProgress(sp.getInt(sp_delay_name, DEFAULT_DELAY_PROGRESS));
        }

        private void saveConfigToFile() {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(spFileName, spMode).edit();
            editor.putInt(sp_speed_name, seekbarSpeed.getProgress());
            if (this.radioSlide.isChecked()) {
                editor.putInt(sp_touchpad_mode, OnscreenTouchpad.TOUCHPAD_MODE_SLIDE);
            } else if (this.radioPoint.isChecked()) {
                editor.putInt(sp_touchpad_mode, OnscreenTouchpad.TOUCHPAD_MODE_POINT);
            }
            editor.putInt(sp_delay_name, seekbarDelay.getProgress());
            editor.apply();
        }
    }

}
