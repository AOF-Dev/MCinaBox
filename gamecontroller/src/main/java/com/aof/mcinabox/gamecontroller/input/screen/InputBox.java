package com.aof.mcinabox.gamecontroller.input.screen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aof.mcinabox.definitions.id.key.KeyMode;
import com.aof.mcinabox.definitions.map.KeyMap;
import com.aof.mcinabox.gamecontroller.R;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.OnscreenInput;
import com.aof.utils.DisplayUtils;
import com.aof.utils.dialog.DialogUtils;
import com.aof.utils.dialog.support.DialogSupports;

public class InputBox implements OnscreenInput, TextWatcher, TextView.OnEditorActionListener, KeyMap, View.OnClickListener {

    private Context mContext;
    private Controller mController;
    private LinearLayout inputBox;
    private EditText editScanner;

    private int screenWidth;
    private int screenHeight;

    private final static int type_1 = TYPE_WORDS;
    private final static int type_2 = KEYBOARD_BUTTON;
    private final static String TAG = "InputBox";

    public final static int SHOW_ALL = 0;
    public final static int SHOW_IN_GAME = 1;
    public final static int SHOW_OUT_GAME = 2;
    private int show;

    private final static int widthDp = 100; //dp
    private final static int heightDp = 20; //dp

    private InputBoxConfigDialog configDialog;

    private boolean enable;


    @Override
    public void setUiMoveable(boolean moveable) {
        //to do nothing.
    }

    @Override
    public boolean isEnable() {
        return this.enable;
    }

    @Override
    public void setEnable(boolean e) {
        this.enable = e;
        updateUI();
    }

    @Override
    public void setUiVisibility(int visibility) {
        inputBox.setVisibility(visibility);
    }

    @Override
    public float[] getPos() {
        return new float[]{inputBox.getX(), inputBox.getY()};
    }

    @Override
    public void setMargins(int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams p = inputBox.getLayoutParams();
        ((ViewGroup.MarginLayoutParams) p).setMargins(left, top, 0, 0);
        inputBox.setLayoutParams(p);
    }

    @Override
    public int[] getSize() {
        return new int[]{inputBox.getLayoutParams().width, inputBox.getLayoutParams().height};
    }

    @Override
    public View[] getViews() {
        return new View[]{this.inputBox};
    }

    @Override
    public int getUiVisiability() {
        return inputBox.getVisibility();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private int[] pos = new int[2];

    private void moveViewByTouch(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pos[0] = (int) e.getRawX();
                pos[1] = (int) e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) e.getRawX() - pos[0];
                int dy = (int) e.getRawY() - pos[1];
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
                pos[0] = (int) e.getRawX();
                pos[1] = (int) e.getRawY();
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

        inputBox = (LinearLayout) LayoutInflater.from(context).inflate(com.aof.mcinabox.gamecontroller.R.layout.char_input_scanner, null);
        controller.addContentView(inputBox, new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(mContext, widthDp), DisplayUtils.getPxFromDp(mContext, heightDp)));
        editScanner = inputBox.findViewById(R.id.input_scanner);

        //设定监听
        editScanner.setFocusable(true);
        editScanner.addTextChangedListener(this);
        editScanner.setOnEditorActionListener(this);
        editScanner.setOnClickListener(this);
        editScanner.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN | EditorInfo.IME_ACTION_DONE);
        editScanner.setSelection(1);

        //设定配置器
        this.configDialog = new InputBoxConfigDialog(mContext, this);

        return true;
    }

    @Override
    public boolean unload() {
        inputBox.setVisibility(View.INVISIBLE);
        ViewGroup vg = (ViewGroup) inputBox.getParent();
        vg.removeView(inputBox);
        return true;
    }

    @Override
    public void setInputMode(int inputMode) {
        //重置显示状态
        updateUI();
    }

    private void updateUI() {
        if (enable) {
            switch (mController.getInputMode()) {
                case KeyMode.MARK_INPUT_MODE_ALONE:
                    if (show == SHOW_ALL || show == SHOW_OUT_GAME) {
                        this.setUiVisibility(View.VISIBLE);
                    } else {
                        this.setUiVisibility(View.GONE);
                    }
                    break;
                case KeyMode.MARK_INPUT_MODE_CATCH:
                    if (show == SHOW_ALL || show == SHOW_IN_GAME) {
                        this.setUiVisibility(View.VISIBLE);
                    } else {
                        this.setUiVisibility(View.GONE);
                    }
                    break;
            }
        } else {
            setUiVisibility(View.GONE);
        }
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String newText = s.toString();
        if (newText.length() < 1) {
            this.sendKey(KEYMAP_KEY_BACKSPACE, true);
            this.sendKey(KEYMAP_KEY_BACKSPACE, false);
        }
        if (newText.length() > 1) {
            for (int i = 1; i < newText.length(); i++) {
                sendChars(String.valueOf(newText.charAt(i)));
            }
        }
        if (newText.length() != 1) {
            editScanner.setText(">");
            editScanner.setSelection(1);
        }

    }

    private void sendKey(String keyName, boolean pressed) {
        mController.sendKey(new BaseKeyEvent(TAG, keyName, pressed, type_2, null));
    }

    private void sendChars(String str) {
        mController.sendKey(new BaseKeyEvent(TAG, null, false, type_1, null).setChars(str));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        sendKey(KEYMAP_KEY_ENTER, true);
        sendChars(String.valueOf('\n'));
        sendKey(KEYMAP_KEY_ENTER, false);
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == editScanner) {
            editScanner.setSelection(1);
        }
    }

    public void setAlpha(float a) {
        inputBox.setAlpha(a);
    }

    public void setSize(int width, int height) {
        ViewGroup.LayoutParams p = inputBox.getLayoutParams();
        p.width = width;
        p.height = height;
        //控件重绘
        inputBox.requestLayout();
        inputBox.invalidate();
    }

    public void setShowStat(int s) {
        this.show = s;
        updateUI();
    }

    public int getShowStat() {
        return this.show;
    }

}

class InputBoxConfigDialog extends Dialog implements View.OnClickListener, Dialog.OnCancelListener, SeekBar.OnSeekBarChangeListener, RadioButton.OnCheckedChangeListener {

    private OnscreenInput mInput;
    private Context mContext;

    private SeekBar seekbarSize;
    private SeekBar seekbarAlpha;
    private TextView textSize;
    private TextView textAlpha;
    private Button buttonOK;
    private Button buttonCancel;
    private Button buttonRestore;
    private Button buttonMoveLeft;
    private Button buttonMoveRight;
    private Button buttonMoveUp;
    private Button buttonMoveDown;
    private RadioButton rbtAll;
    private RadioButton rbtInGame;
    private RadioButton rbtOutGame;

    private int originalAlphaProgress;
    private int originalSizeProgress;
    private int originalMarginLeft;
    private int originalMarginTop;
    private int originalShow;

    private int originalInputWidth;
    private int originalInputHeight;
    private int screenWidth;
    private int screenHeight;

    private final static int DEFAULT_MOVE_DISTANCE = 10;
    private final static int MARK_MOVE_UP = 1;
    private final static int MARK_MOVE_DOWN = 2;
    private final static int MARK_MOVE_LEFT = 3;
    private final static int MARK_MOVE_RIGHT = 4;

    private final static int DEFAULT_ALPHA_PROGRESS = 60;
    private final static int DEFAULT_SIZE_PROGRESS = 50;

    private final static int MAX_ALPHA_PROGRESS = 100;
    private final static int MIN_ALPHA_PROGRESS = 0;
    private final static int MAX_SIZE_PROGRESS = 100;
    private final static int MIN_SIZE_PROGRESS = -50;

    private final static String spFileName = "input_inputbox_config";
    private final static int spMode = Context.MODE_PRIVATE;
    private final static String sp_alpha_name = "alpha";
    private final static String sp_size_name = "size";
    private final static String sp_pos_x_name = "pos_x";
    private final static String sp_pos_y_name = "pos_y";
    private final static String sp_show_name = "show";

    private final static String TAG = "InputBoxConfigDialog";


    public InputBoxConfigDialog(Context context, OnscreenInput input) {
        super(context);
        setContentView(R.layout.dialog_inputbox_config);
        this.mContext = context;
        this.mInput = input;
        init();
    }

    private void init() {
        this.setCanceledOnTouchOutside(false);
        this.setOnCancelListener(this);

        seekbarSize = this.findViewById(R.id.input_inputbox_dialog_seekbar_size);
        seekbarAlpha = this.findViewById(R.id.input_inputbox_dialog_seekbar_alpha);
        textSize = this.findViewById(R.id.input_inputbox_dialog_text_size);
        textAlpha = this.findViewById(R.id.input_inputbox_dialog_text_alpha);
        buttonOK = this.findViewById(R.id.input_inputbox_dialog_button_ok);
        buttonCancel = this.findViewById(R.id.input_inputbox_dialog_button_cancel);
        buttonRestore = this.findViewById(R.id.input_inputbox_dialog_button_restore);
        buttonMoveLeft = this.findViewById(R.id.input_inputbox_dialog_button_move_left);
        buttonMoveRight = this.findViewById(R.id.input_inputbox_dialog_button_move_right);
        buttonMoveUp = this.findViewById(R.id.input_inputbox_dialog_button_move_up);
        buttonMoveDown = this.findViewById(R.id.input_inputbox_dialog_button_move_down);
        rbtAll = this.findViewById(R.id.input_inputbox_dialog_rbt_all);
        rbtInGame = this.findViewById(R.id.input_inputbox_dialog_rbt_in_game);
        rbtOutGame = this.findViewById(R.id.input_inputbox_dialog_rbt_out_game);

        //设定监听
        for (View v : new View[]{buttonOK, buttonCancel, buttonRestore, buttonMoveDown, buttonMoveUp, buttonMoveLeft, buttonMoveRight}) {
            v.setOnClickListener(this);
        }
        for (SeekBar s : new SeekBar[]{seekbarAlpha, seekbarSize}) {
            s.setOnSeekBarChangeListener(this);
        }
        for (RadioButton rbt : new RadioButton[]{rbtAll, rbtInGame, rbtOutGame}) {
            rbt.setOnCheckedChangeListener(this);
        }

        //获取数据
        originalInputWidth = mInput.getSize()[0];
        originalInputHeight = mInput.getSize()[1];
        screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;

        //初始化控件属性
        this.seekbarAlpha.setMax(MAX_ALPHA_PROGRESS);
        this.seekbarSize.setMax(MAX_SIZE_PROGRESS);

        //加载配置文件
        loadConfigFromFile();

    }

    @Override
    public void show() {
        super.show();
        originalAlphaProgress = seekbarAlpha.getProgress();
        originalSizeProgress = seekbarSize.getProgress();
        originalMarginLeft = (int) mInput.getPos()[0];
        originalMarginTop = (int) mInput.getPos()[1];
        originalShow = ((InputBox) mInput).getShowStat();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (dialog == this) {
            seekbarAlpha.setProgress(originalAlphaProgress);
            seekbarSize.setProgress(originalSizeProgress);
            mInput.setMargins(originalMarginLeft, originalMarginTop, 0, 0);
            switch (originalShow) {
                case InputBox.SHOW_ALL:
                    rbtAll.setChecked(true);
                    break;
                case InputBox.SHOW_IN_GAME:
                    rbtInGame.setChecked(true);
                    break;
                case InputBox.SHOW_OUT_GAME:
                    rbtOutGame.setChecked(true);
                    break;
            }
        }
    }

    private void restoreConfig() {
        seekbarAlpha.setProgress(DEFAULT_ALPHA_PROGRESS);
        seekbarSize.setProgress(DEFAULT_SIZE_PROGRESS);
        rbtAll.setChecked(true);
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
        if (v == buttonMoveUp) {
            moveInputBoxByButton(MARK_MOVE_UP);
        }
        if (v == buttonMoveDown) {
            moveInputBoxByButton(MARK_MOVE_DOWN);
        }
        if (v == buttonMoveLeft) {
            moveInputBoxByButton(MARK_MOVE_LEFT);
        }
        if (v == buttonMoveRight) {
            moveInputBoxByButton(MARK_MOVE_RIGHT);
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
            ((InputBox) mInput).setAlpha(alpha);
        }

        if (seekBar == seekbarSize) {
            int p = progress + MIN_SIZE_PROGRESS;
            textSize.setText(String.valueOf(p));
            //设置大小
            int centerX = (int) (mInput.getPos()[0] + mInput.getSize()[0] / 2);
            int centerY = (int) (mInput.getPos()[1] + mInput.getSize()[1] / 2);
            int tmpWidth = (int) ((1 + p * 0.01f) * originalInputWidth);
            int tmpHeight = (int) ((1 + p * 0.01f) * originalInputHeight);
            ((InputBox) mInput).setSize(tmpWidth, tmpHeight);
            //调整位置
            adjustPos(centerX, centerY);
        }
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

    private void moveInputBoxByButton(int mark) {
        float posX = mInput.getPos()[0];
        float posY = mInput.getPos()[1];

        int marginLeft = (int) posX;
        int marginTop = (int) posY;

        int viewWidth = mInput.getSize()[0];
        int viewHeight = mInput.getSize()[1];

        //获得移动后的边距
        switch (mark) {
            case MARK_MOVE_UP:
                marginTop -= DEFAULT_MOVE_DISTANCE;
                break;
            case MARK_MOVE_DOWN:
                marginTop += DEFAULT_MOVE_DISTANCE;
                break;
            case MARK_MOVE_LEFT:
                marginLeft -= DEFAULT_MOVE_DISTANCE;
                break;
            case MARK_MOVE_RIGHT:
                marginLeft += DEFAULT_MOVE_DISTANCE;
                break;
            default:
                break;
        }

        //边缘检测
        //上边界
        if (marginTop < 0) {
            marginTop = 0;
        }
        //下边界
        if (marginTop + viewHeight > screenHeight) {
            marginTop = screenHeight - viewHeight;
        }
        //左边界
        if (marginLeft < 0) {
            marginLeft = 0;
        }
        //右边界
        if (marginLeft + viewWidth > screenWidth) {
            marginLeft = screenWidth - viewWidth;
        }

        mInput.setMargins(marginLeft, marginTop, 0, 0);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStop() {
        super.onStop();
        saveConfigToFile();
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
        switch (sp.getInt(sp_show_name, InputBox.SHOW_ALL)) {
            case InputBox.SHOW_ALL:
                rbtAll.setChecked(true);
                break;
            case InputBox.SHOW_IN_GAME:
                rbtInGame.setChecked(true);
                break;
            case InputBox.SHOW_OUT_GAME:
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
        editor.putInt(sp_show_name, ((InputBox) mInput).getShowStat());
        editor.apply();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView == rbtAll) {
            if (isChecked) {
                ((InputBox) mInput).setShowStat(InputBox.SHOW_ALL);
            }
        }

        if (buttonView == rbtInGame) {
            if (isChecked) {
                ((InputBox) mInput).setShowStat(InputBox.SHOW_IN_GAME);
            }
        }

        if (buttonView == rbtOutGame) {
            if (isChecked) {
                ((InputBox) mInput).setShowStat(InputBox.SHOW_OUT_GAME);
            }
        }

    }
}
