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

import com.aof.mcinabox.R;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.OnscreenInput;
import com.aof.mcinabox.utils.DisplayUtils;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;
import com.kongqw.rockerlibrary.view.RockerView;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MARK_KEYNAME_SPLIT_STRING;
import static com.aof.mcinabox.gamecontroller.definitions.map.KeyMap.KEYMAP_KEY_A;
import static com.aof.mcinabox.gamecontroller.definitions.map.KeyMap.KEYMAP_KEY_D;
import static com.aof.mcinabox.gamecontroller.definitions.map.KeyMap.KEYMAP_KEY_S;
import static com.aof.mcinabox.gamecontroller.definitions.map.KeyMap.KEYMAP_KEY_W;

public class OnscreenJoystick implements OnscreenInput, RockerView.OnShakeListener {

    private LinearLayout onscreenJoystick;
    private RockerView joystick;
    private Button buttonMove;
    private boolean moveable;
    private OnscreenJoystickConfigDialog configDialog;

    private Context mContext;
    private Controller mController;

    private int screenWidth;
    private int screenHeight;

    private boolean enable;

    public final static int SHOW_ALL = 0;
    public final static int SHOW_IN_GAME = 1;
    public final static int SHOW_OUT_GAME = 2;
    private int show;

    private final static int type = KEYBOARD_BUTTON;

    private final static int widthDp = 200;
    private final static int heightDp = 200;


    private final static String TAG = "OnscreenJoystick";


    @Override
    public void setUiMoveable(boolean moveable) {
        this.moveable = moveable;
        if (moveable) {
            buttonMove.setVisibility(View.VISIBLE);
        } else {
            buttonMove.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setUiVisibility(int visiablity) {
        this.onscreenJoystick.setVisibility(visiablity);
    }

    @Override
    public float[] getPos() {
        return (new float[]{onscreenJoystick.getX(), onscreenJoystick.getY()});
    }

    @Override
    public boolean isEnable() {
        return this.enable;
    }

    @Override
    public void setMargins(int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams p = onscreenJoystick.getLayoutParams();
        ((ViewGroup.MarginLayoutParams) p).setMargins(left, top, 0, 0);
        onscreenJoystick.setLayoutParams(p);
    }

    @Override
    public int[] getSize() {
        return new int[]{onscreenJoystick.getLayoutParams().width, onscreenJoystick.getLayoutParams().height};

    }

    @Override
    public View[] getViews() {
        return new View[]{onscreenJoystick};
    }

    @Override
    public int getUiVisiability() {
        return onscreenJoystick.getVisibility();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == buttonMove) {
            if (moveable) {
                moveViewByTouch(onscreenJoystick, event);
            }
        }
        return false;
    }

    private final int[] viewPos = new int[2];

    private void moveViewByTouch(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                viewPos[0] = (int) e.getRawX();
                viewPos[1] = (int) e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) e.getRawX() - viewPos[0];
                int dy = (int) e.getRawY() - viewPos[1];
                int l = v.getLeft() + dx;
                int b = v.getBottom() + dy;
                int r = v.getRight() + dx;
                int t = v.getTop() + dy;
                //下面判断移动是否超出屏幕
                if (l < 0) {
                    l = 0;
                    r = l + v.getWidth();
                }
                if (t < 0) {
                    t = 0;
                    b = t + v.getHeight();
                }
                if (r > screenWidth) {
                    r = screenWidth;
                    l = r - v.getWidth();
                }
                if (b > screenHeight) {
                    b = screenHeight;
                    t = b - v.getHeight();
                }
                v.layout(l, t, r, b);
                viewPos[0] = (int) e.getRawX();
                viewPos[1] = (int) e.getRawY();
                v.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                ViewGroup.LayoutParams p = v.getLayoutParams();
                ((ViewGroup.MarginLayoutParams) p).setMargins(v.getLeft(), v.getTop(), 0, 0);
                v.setLayoutParams(p);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean load(Context context, Controller controller) {
        this.mContext = context;
        this.mController = controller;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        onscreenJoystick = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.virtual_joystick, null);
        mController.addContentView(onscreenJoystick, new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(mContext, widthDp), DisplayUtils.getPxFromDp(mContext, heightDp)));

        joystick = onscreenJoystick.findViewById(R.id.joystick_rocker);
        buttonMove = onscreenJoystick.findViewById(R.id.joystick_move);

        //设定监听
        buttonMove.setOnTouchListener(this);
        joystick.setOnShakeListener(RockerView.DirectionMode.DIRECTION_8, this);

        //设定配置器
        configDialog = new OnscreenJoystickConfigDialog(mContext, this);

        return true;
    }

    @Override
    public boolean unload() {
        onscreenJoystick.setVisibility(View.INVISIBLE);
        ViewGroup vg = (ViewGroup) onscreenJoystick.getParent();
        vg.removeView(onscreenJoystick);
        return true;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        updateUI();
    }

    @Override
    public void runConfigure() {
        configDialog.show();
    }

    @Override
    public void saveConfig() {
        configDialog.saveConfigToFile();
    }

    @Override
    public void onStart() {

    }

    private String lastKeyName = "";

    @Override
    public void direction(RockerView.Direction direction) {
        String keyName;
        switch (direction) {
            case DIRECTION_UP:
                keyName = KEYMAP_KEY_W;
                break;
            case DIRECTION_DOWN:
                keyName = KEYMAP_KEY_S;
                break;
            case DIRECTION_LEFT:
                keyName = KEYMAP_KEY_A;
                break;
            case DIRECTION_RIGHT:
                keyName = KEYMAP_KEY_D;
                break;
            case DIRECTION_UP_LEFT:
                keyName = KEYMAP_KEY_W + MARK_KEYNAME_SPLIT_STRING + KEYMAP_KEY_A;
                break;
            case DIRECTION_UP_RIGHT:
                keyName = KEYMAP_KEY_W + MARK_KEYNAME_SPLIT_STRING + KEYMAP_KEY_D;
                break;
            case DIRECTION_DOWN_LEFT:
                keyName = KEYMAP_KEY_S + MARK_KEYNAME_SPLIT_STRING + KEYMAP_KEY_A;
                break;
            case DIRECTION_DOWN_RIGHT:
                keyName = KEYMAP_KEY_S + MARK_KEYNAME_SPLIT_STRING + KEYMAP_KEY_D;
                break;
            default:
                return;
        }

        if (lastKeyName == null || lastKeyName.equals("") || !lastKeyName.equals(keyName)) {
            this.sendKeyEvent(keyName, true);
            if (lastKeyName != null && !lastKeyName.equals("")) {
                this.sendKeyEvent(lastKeyName, false);
            }
            this.lastKeyName = keyName;
        }

    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
        updateUI();
    }

    private void updateUI() {
        if (enable) {
            if (mController.getGrabbed()) {
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

    public void setShowStat(int s) {
        this.show = s;
        updateUI();
    }

    public int getShowStat() {
        return this.show;
    }

    @Override
    public void onFinish() {
        if (lastKeyName != null && !lastKeyName.equals("")) {
            this.sendKeyEvent(lastKeyName, false);
        }
    }

    private void sendKeyEvent(String keyName, boolean pressed) {
        mController.sendKey(new BaseKeyEvent(TAG, keyName, pressed, type, null));
    }

    public void setSize(int s) {
        ViewGroup.LayoutParams p = onscreenJoystick.getLayoutParams();
        p.height = s;
        p.width = s;
        //控件重绘
        onscreenJoystick.requestLayout();
        onscreenJoystick.invalidate();
        joystick.refreshDrawableState();
        joystick.invalidate();
    }

    public void setAlpha(float a) {
        onscreenJoystick.setAlpha(a);
    }

    private static class OnscreenJoystickConfigDialog extends Dialog implements View.OnClickListener, Dialog.OnCancelListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

        private final static String TAG = "OnscreenJoystickConfigDialog";
        private final static int DEFAULT_ALPHA_PROCESS = 40;
        private final static int DEFAULT_SIZE_PROGRESS = 50;
        private final static int MIN_SIZE_PROGRESS = -50;
        private final static int MAX_SIZE_PROGRESS = 100;
        private final static int MAX_ALPHA_PROGRESS = 100;
        private final static int MIN_ALPHA_PROGRESS = 0;
        private final static String spFileName = "input_onscreenjoystick_config";
        private final static int spMode = Context.MODE_PRIVATE;
        private final static String sp_alpha_name = "alpha";
        private final static String sp_size_name = "size";
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
        private TextView textAlpha;
        private TextView textSize;
        private RadioButton rbtAll;
        private RadioButton rbtInGame;
        private RadioButton rbtOutGame;
        private int originalAlphaProgress;
        private int originalSizeProgress;
        private int originalShow;
        private int originalMarginLeft;
        private int originalMarginTop;
        private int originalInputSize;
        private int screenWidth;
        private int screenHeight;

        public OnscreenJoystickConfigDialog(Context context, OnscreenInput input) {
            super(context);
            setContentView(R.layout.dialog_onscreen_joystick_config);
            this.mContext = context;
            this.mInput = input;
            init();
        }

        private void init() {
            this.setCanceledOnTouchOutside(false);
            this.setOnCancelListener(this);

            buttonOK = findViewById(R.id.input_onscreen_joystick_dialog_button_ok);
            buttonCancel = findViewById(R.id.input_onscreen_joystick_dialog_button_cancel);
            buttonRestore = findViewById(R.id.input_onscreen_joystick_dialog_button_restore);
            seekbarAlpha = findViewById(R.id.input_onscreen_joystick_dialog_seekbar_alpha);
            seekbarSize = findViewById(R.id.input_onscreen_joystick_dialog_seekbar_size);
            textAlpha = findViewById(R.id.input_onscreen_joystick_dialog_text_alpha);
            textSize = findViewById(R.id.input_onscreen_joystick_dialog_text_size);
            rbtAll = this.findViewById(R.id.input_onscreen_joystick_dialog_rbt_all);
            rbtInGame = this.findViewById(R.id.input_onscreen_joystick_dialog_rbt_in_game);
            rbtOutGame = this.findViewById(R.id.input_onscreen_joystick_dialog_rbt_out_game);

            //设定监听
            for (View v : new View[]{buttonOK, buttonCancel, buttonRestore}) {
                v.setOnClickListener(this);
            }
            for (SeekBar s : new SeekBar[]{seekbarSize, seekbarAlpha}) {
                s.setOnSeekBarChangeListener(this);
            }
            for (RadioButton rbt : new RadioButton[]{rbtAll, rbtInGame, rbtOutGame}) {
                rbt.setOnCheckedChangeListener(this);
            }

            originalInputSize = mInput.getSize()[0];
            screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;

            //初始化控件属性
            this.seekbarAlpha.setMax(MAX_ALPHA_PROGRESS);
            this.seekbarSize.setMax(MAX_SIZE_PROGRESS);

            loadConfigFromFile();

        }

        @Override
        public void onCancel(DialogInterface dialog) {
            if (dialog == this) {
                seekbarAlpha.setProgress(originalAlphaProgress);
                seekbarSize.setProgress(originalSizeProgress);
                mInput.setMargins(originalMarginLeft, originalMarginTop, 0, 0);
                switch (originalShow) {
                    case OnscreenJoystick.SHOW_ALL:
                        rbtAll.setChecked(true);
                        break;
                    case OnscreenJoystick.SHOW_IN_GAME:
                        rbtInGame.setChecked(true);
                        break;
                    case OnscreenJoystick.SHOW_OUT_GAME:
                        rbtOutGame.setChecked(true);
                        break;
                }
            }
        }

        @Override
        public void show() {
            super.show();
            originalAlphaProgress = seekbarAlpha.getProgress();
            originalSizeProgress = seekbarSize.getProgress();
            originalMarginLeft = (int) mInput.getPos()[0];
            originalMarginTop = (int) mInput.getPos()[1];
            originalShow = ((OnscreenJoystick) mInput).getShowStat();
        }

        @Override
        public void onStop() {
            super.onStop();
            saveConfigToFile();
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
            if (seekBar == seekbarAlpha) {
                int p = progress + MIN_ALPHA_PROGRESS;
                String str = p + "%";
                textAlpha.setText(str);
                //调整透明度
                float alpha = 1 - p * 0.01f;
                ((OnscreenJoystick) mInput).setAlpha(alpha);
            }
            if (seekBar == seekbarSize) {
                int p = progress + MIN_SIZE_PROGRESS;
                textSize.setText(String.valueOf(p));
                //调整大小
                int centerX = (int) (mInput.getPos()[0] + mInput.getSize()[0] / 2);
                int centerY = (int) (mInput.getPos()[1] + mInput.getSize()[1] / 2);
                int size = (int) ((1 + p * 0.01f) * originalInputSize);
                ((OnscreenJoystick) mInput).setSize(size);
                //调整位置
                adjustPos(centerX, centerY);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

        private void loadConfigFromFile() {
            SharedPreferences sp = mContext.getSharedPreferences(spFileName, spMode);

            //先设定一个最大值，防止Seebar的监听器无法监听到事件
            seekbarAlpha.setProgress(MAX_ALPHA_PROGRESS);
            seekbarSize.setProgress(MAX_SIZE_PROGRESS);
            //设定存储的数据
            seekbarAlpha.setProgress(sp.getInt(sp_alpha_name, DEFAULT_ALPHA_PROCESS));
            seekbarSize.setProgress(sp.getInt(sp_size_name, DEFAULT_SIZE_PROGRESS));
            mInput.setMargins(sp.getInt(sp_pos_x_name, 0), sp.getInt(sp_pos_y_name, 0), 0, 0);
            switch (sp.getInt(sp_show_name, OnscreenJoystick.SHOW_ALL)) {
                case OnscreenJoystick.SHOW_ALL:
                    rbtAll.setChecked(true);
                    break;
                case OnscreenJoystick.SHOW_IN_GAME:
                    rbtInGame.setChecked(true);
                    break;
                case OnscreenJoystick.SHOW_OUT_GAME:
                    rbtOutGame.setChecked(true);
                    break;
            }
        }

        public void saveConfigToFile() {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(spFileName, spMode).edit();
            editor.putInt(sp_alpha_name, seekbarAlpha.getProgress());
            editor.putInt(sp_size_name, seekbarSize.getProgress());
            if (mInput.getUiVisiability() == View.VISIBLE) {
                editor.putInt(sp_pos_x_name, (int) mInput.getPos()[0]);
                editor.putInt(sp_pos_y_name, (int) mInput.getPos()[1]);
            }
            editor.putInt(sp_show_name, ((OnscreenJoystick) mInput).getShowStat());
            editor.apply();
        }

        private void restoreConfig() {
            seekbarAlpha.setProgress(DEFAULT_ALPHA_PROCESS);
            seekbarSize.setProgress(DEFAULT_SIZE_PROGRESS);
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

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == rbtAll) {
                if (isChecked) {
                    ((OnscreenJoystick) mInput).setShowStat(OnscreenJoystick.SHOW_ALL);
                }
            }

            if (buttonView == rbtInGame) {
                if (isChecked) {
                    ((OnscreenJoystick) mInput).setShowStat(OnscreenJoystick.SHOW_IN_GAME);
                }
            }

            if (buttonView == rbtOutGame) {
                if (isChecked) {
                    ((OnscreenJoystick) mInput).setShowStat(OnscreenJoystick.SHOW_OUT_GAME);
                }
            }
        }
    }

}

