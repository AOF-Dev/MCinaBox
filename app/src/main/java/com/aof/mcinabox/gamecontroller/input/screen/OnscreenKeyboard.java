package com.aof.mcinabox.gamecontroller.input.screen;

import android.annotation.SuppressLint;
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
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.OnscreenInput;
import com.aof.mcinabox.gamecontroller.input.screen.button.QwertButton;
import com.aof.mcinabox.utils.ColorUtils;
import com.aof.mcinabox.utils.DisplayUtils;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.TYPE_WORDS;

public class OnscreenKeyboard implements OnscreenInput {

    public final static int SHOW_ALL = 0;
    public final static int SHOW_IN_GAME = 1;
    public final static int SHOW_OUT_GAME = 2;
    private final static String TAG = "OnscreenKeyboard";
    private final static int type = KEYBOARD_BUTTON;
    private final static int type_2 = TYPE_WORDS;
    private final static int widthDp = 460;
    private final static int heightDp = 130;
    private final static String colorHexEnable = "#149CFF";
    private final static String colorHexDisable = "#000000";
    private Context mContext;
    private Controller mController;
    private LinearLayout onscreenKeyboard;
    private boolean moveable = false;
    private boolean enable;
    private int show;
    private QwertButton qButtonLShift;
    private QwertButton qButtonRShift;
    private QwertButton qButtonCaps;
    private TextView textSignalCaps;
    private TextView textSignalType;
    private boolean enableCaps = false;
    private boolean enableType = false;
    private boolean enableShift = false;
    private OnscreenKeyboardConfigDialog configDialog;
    private final int[] OnscreenKeyboardPos = new int[2];
    private int screenWidth;
    private int screenHeight;

    @Override
    public boolean load(Context context, Controller controller) {
        this.mContext = context;
        this.mController = controller;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        onscreenKeyboard = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.virtual_keyboard, null);
        mController.addContentView(onscreenKeyboard, new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(mContext, widthDp), DisplayUtils.getPxFromDp(mContext, heightDp)));

        //设置监听器
        for (int a = 0; a < onscreenKeyboard.getChildCount(); a++) {
            if (onscreenKeyboard.getChildAt(a) instanceof LinearLayout) {
                for (int b = 0; b < ((LinearLayout) onscreenKeyboard.getChildAt(a)).getChildCount(); b++) {
                    ((LinearLayout) onscreenKeyboard.getChildAt(a)).getChildAt(b).setOnTouchListener(this);
                }
            }
        }

        //设定配置器
        configDialog = new OnscreenKeyboardConfigDialog(mContext, this);

        //信号
        qButtonCaps = onscreenKeyboard.findViewById(R.id.virtual_keyboard_button_caps);
        qButtonLShift = onscreenKeyboard.findViewById(R.id.virtual_keyboard_button_lshift);
        qButtonRShift = onscreenKeyboard.findViewById(R.id.virtual_keyboard_button_Rshift);
        textSignalCaps = onscreenKeyboard.findViewById(R.id.virtual_keyboard_signal_text_caps);
        textSignalType = onscreenKeyboard.findViewById(R.id.virtual_keyboard_signal_enable_type);
        textSignalType.setOnTouchListener(this);
        textSignalType.setClickable(true);
        textSignalCaps.setClickable(true);

        onscreenKeyboard.setOnTouchListener(this);

        return true;
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
    public void setUiMoveable(boolean moveable) {
        this.moveable = moveable;
    }

    @Override
    public void setUiVisibility(int visiablity) {
        onscreenKeyboard.setVisibility(visiablity);
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        updateUI();
    }

    @Override
    public void runConfigure() {
        configDialog.show();
    }

    private void performCaps(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            if (enableCaps) {
                enableCaps = false;
                textSignalCaps.setTextColor(ColorUtils.hex2Int(colorHexDisable));
            } else {
                enableCaps = true;
                textSignalCaps.setTextColor(ColorUtils.hex2Int(colorHexEnable));
            }
        }
    }

    private void performShift(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                enableShift = true;
                break;
            case MotionEvent.ACTION_UP:
                enableShift = false;
                break;
        }
    }

    private void performType(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            if (enableType) {
                enableType = false;
                textSignalType.setTextColor(ColorUtils.hex2Int(colorHexDisable));
            } else {
                enableType = true;
                textSignalType.setTextColor(ColorUtils.hex2Int(colorHexEnable));
            }
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent e) {

        //过滤一下信号用按键
        if (v == qButtonCaps) {
            performCaps(e);
            //直接截获事件
            return true;
        }

        if (v == qButtonLShift || v == qButtonRShift) {
            if (enableType) {
                //如果启用输入，则截获事件
                performShift(e);
                return true;
            } else {
                //如果没有启用输入，则不截获事件
            }
        }

        if (v == textSignalType) {
            performType(e);
            return true;
        }
        //过滤完成

        if (v instanceof QwertButton) {
            if (enableType) {
                sendCharEvent((QwertButton) v, e);
            } else {
                sendKeyEvent(v, e);
            }
            return true;
        }

        if (v == onscreenKeyboard && moveable) {
            moveViewByTouch(v, e);
            return true;
        }
        return false;
    }

    @Override
    public boolean unload() {
        onscreenKeyboard.setVisibility(View.INVISIBLE);
        ViewGroup vg = (ViewGroup) onscreenKeyboard.getParent();
        vg.removeView(onscreenKeyboard);
        return true;
    }

    @Override
    public float[] getPos() {
        return (new float[]{onscreenKeyboard.getX(), onscreenKeyboard.getY()});
    }

    @Override
    public void setMargins(int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams p = onscreenKeyboard.getLayoutParams();
        ((ViewGroup.MarginLayoutParams) p).setMargins(left, top, 0, 0);
        onscreenKeyboard.setLayoutParams(p);
    }

    @Override
    public int[] getSize() {
        return new int[]{onscreenKeyboard.getLayoutParams().width, onscreenKeyboard.getLayoutParams().height};
    }

    @Override
    public View[] getViews() {
        return new View[]{this.onscreenKeyboard};
    }

    @Override
    public int getUiVisiability() {
        return onscreenKeyboard.getVisibility();
    }

    private void sendKeyEvent(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            mController.sendKey(new BaseKeyEvent(TAG, ((QwertButton) v).getButtonName(), true, type, null));
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            mController.sendKey(new BaseKeyEvent(TAG, ((QwertButton) v).getButtonName(), false, type, null));
        }
    }

    private void sendCharEvent(QwertButton qb, MotionEvent e) {
        //支持输入模式的按键必须有char_none属性，如果没有就将其过滤并传递至sendKeyEvent
        if (qb.getCharNone() == null) {
            sendKeyEvent(qb, e);
            return;
        }
        if (e.getAction() == MotionEvent.ACTION_UP) {
            //Shift信号的优先级高于Caps, Caps 高于 None
            if (enableShift) {
                mController.typeWords(qb.getCharShift());
            } else if (enableCaps) {
                mController.typeWords(qb.getCharCaps());
            } else {
                mController.typeWords(qb.getCharNone());
            }
        }
    }

    private void moveViewByTouch(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                OnscreenKeyboardPos[0] = (int) e.getRawX();
                OnscreenKeyboardPos[1] = (int) e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) e.getRawX() - OnscreenKeyboardPos[0];
                int dy = (int) e.getRawY() - OnscreenKeyboardPos[1];
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
                OnscreenKeyboardPos[0] = (int) e.getRawX();
                OnscreenKeyboardPos[1] = (int) e.getRawY();
                v.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                ViewGroup.LayoutParams p = onscreenKeyboard.getLayoutParams();
                ((ViewGroup.MarginLayoutParams) p).setMargins(onscreenKeyboard.getLeft(), onscreenKeyboard.getTop(), 0, 0);
                onscreenKeyboard.setLayoutParams(p);
                break;
            default:
                break;
        }
    }

    public void setAlpha(float a) {
        onscreenKeyboard.setAlpha(a);
    }

    public void setSize(int width, int height) {
        ViewGroup.LayoutParams p = onscreenKeyboard.getLayoutParams();
        p.width = width;
        p.height = height;
        //控件重绘
        onscreenKeyboard.requestLayout();
        onscreenKeyboard.invalidate();
    }

    @Override
    public void saveConfig() {
        configDialog.saveConfigToFile();
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

    private static class OnscreenKeyboardConfigDialog extends Dialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Dialog.OnCancelListener, CompoundButton.OnCheckedChangeListener {

        private final static String TAG = "OnscreenKeyboardConfigDialog";
        private final static int DEFAULT_ALPHA_PROGRESS = 40;
        private final static int DEFAULT_SIZE_PROGRESS = 50;
        private final static int MAX_ALPHA_PROGRESS = 100;
        private final static int MIN_ALPHA_PROGRESS = 0;
        private final static int MAX_SIZE_PROGRESS = 100;
        private final static int MIN_SIZE_PROGRESS = -50;
        private final static String spFileName = "input_onscreenkeyboard_config";
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
        private int originalInputWidth;
        private int originalInputHeight;
        private int screenWidth;
        private int screenHeight;
        private int originalAlphaProgress;
        private int originalSizeProgress;
        private int originalMarginLeft;
        private int originalMarginTop;
        private int originalShow;

        public OnscreenKeyboardConfigDialog(@NonNull Context context, OnscreenInput input) {
            super(context);
            setContentView(R.layout.dialog_onscreen_keyboard_config);
            mContext = context;
            mInput = input;
            init();
        }

        private void init() {
            this.setCanceledOnTouchOutside(false);
            this.setOnCancelListener(this);

            buttonOK = this.findViewById(R.id.input_onscreen_keyboard_dialog_button_ok);
            buttonCancel = this.findViewById(R.id.input_onscreen_keyboard_dialog_button_cancel);
            buttonRestore = this.findViewById(R.id.input_onscreen_keyboard_dialog_button_restore);
            seekbarAlpha = this.findViewById(R.id.input_onscreen_keyboard_dialog_seekbar_alpha);
            seekbarSize = this.findViewById(R.id.input_onscreen_keyboard_dialog_seekbar_size);
            textAlpha = this.findViewById(R.id.input_onscreen_keyboard_dialog_text_alpha);
            textSize = this.findViewById(R.id.input_onscreen_keyboard_dialog_text_size);
            rbtAll = this.findViewById(R.id.input_onscreen_keyboard_dialog_rbt_all);
            rbtInGame = this.findViewById(R.id.input_onscreen_keyboard_dialog_rbt_in_game);
            rbtOutGame = this.findViewById(R.id.input_onscreen_keyboard_dialog_rbt_out_game);

            for (View v : new View[]{buttonOK, buttonCancel, buttonRestore}) {
                v.setOnClickListener(this);
            }
            for (SeekBar s : new SeekBar[]{seekbarSize, seekbarAlpha}) {
                s.setOnSeekBarChangeListener(this);
            }
            for (RadioButton rbt : new RadioButton[]{rbtAll, rbtInGame, rbtOutGame}) {
                rbt.setOnCheckedChangeListener(this);
            }

            originalInputWidth = mInput.getSize()[0];
            originalInputHeight = mInput.getSize()[1];
            screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;

            //初始化控件属性
            this.seekbarAlpha.setMax(MAX_ALPHA_PROGRESS);
            this.seekbarSize.setMax(MAX_SIZE_PROGRESS);

            loadConfigFromFile();
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
                //设置透明度
                float alpha = 1 - p * 0.01f;
                ((OnscreenKeyboard) mInput).setAlpha(alpha);
            }

            if (seekBar == seekbarSize) {
                int p = progress + MIN_SIZE_PROGRESS;
                textSize.setText(String.valueOf(p));
                //设置大小
                int centerX = (int) (mInput.getPos()[0] + mInput.getSize()[0] / 2);
                int centerY = (int) (mInput.getPos()[1] + mInput.getSize()[1] / 2);
                int tmpWidth = (int) ((1 + p * 0.01f) * originalInputWidth);
                int tmpHeight = (int) ((1 + p * 0.01f) * originalInputHeight);
                ((OnscreenKeyboard) mInput).setSize(tmpWidth, tmpHeight);
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

        @Override
        public void onCancel(DialogInterface dialog) {

            if (dialog == this) {
                seekbarAlpha.setProgress(originalAlphaProgress);
                seekbarSize.setProgress(originalSizeProgress);
                mInput.setMargins(originalMarginLeft, originalMarginTop, 0, 0);
                switch (originalShow) {
                    case OnscreenKeyboard.SHOW_ALL:
                        rbtAll.setChecked(true);
                        break;
                    case OnscreenKeyboard.SHOW_IN_GAME:
                        rbtInGame.setChecked(true);
                        break;
                    case OnscreenKeyboard.SHOW_OUT_GAME:
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
            originalShow = ((OnscreenKeyboard) mInput).getShowStat();
        }

        @Override
        public void onStop() {
            super.onStop();
            saveConfigToFile();
        }

        private void restoreConfig() {
            seekbarAlpha.setProgress(DEFAULT_ALPHA_PROGRESS);
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

        private void loadConfigFromFile() {
            SharedPreferences sp = mContext.getSharedPreferences(spFileName, spMode);

            //先设定一个最大值，防止Seebar的监听器无法监听到事件
            seekbarAlpha.setProgress(MAX_ALPHA_PROGRESS);
            seekbarSize.setProgress(MAX_SIZE_PROGRESS);
            //设定存储的数据
            seekbarAlpha.setProgress(sp.getInt(sp_alpha_name, DEFAULT_ALPHA_PROGRESS));
            seekbarSize.setProgress(sp.getInt(sp_size_name, DEFAULT_SIZE_PROGRESS));
            mInput.setMargins(sp.getInt(sp_pos_x_name, 0), sp.getInt(sp_pos_y_name, 0), 0, 0);
            switch (sp.getInt(sp_show_name, OnscreenKeyboard.SHOW_ALL)) {
                case OnscreenKeyboard.SHOW_ALL:
                    rbtAll.setChecked(true);
                    break;
                case OnscreenKeyboard.SHOW_IN_GAME:
                    rbtInGame.setChecked(true);
                    break;
                case OnscreenKeyboard.SHOW_OUT_GAME:
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
            editor.putInt(sp_show_name, ((OnscreenKeyboard) mInput).getShowStat());
            editor.apply();
        }


        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == rbtAll) {
                if (isChecked) {
                    ((OnscreenKeyboard) mInput).setShowStat(OnscreenKeyboard.SHOW_ALL);
                }
            }

            if (buttonView == rbtInGame) {
                if (isChecked) {
                    ((OnscreenKeyboard) mInput).setShowStat(OnscreenKeyboard.SHOW_IN_GAME);
                }
            }

            if (buttonView == rbtOutGame) {
                if (isChecked) {
                    ((OnscreenKeyboard) mInput).setShowStat(OnscreenKeyboard.SHOW_OUT_GAME);
                }
            }
        }
    }

}

