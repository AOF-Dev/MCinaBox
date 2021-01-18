package com.aof.mcinabox.gamecontroller.controller;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.aof.mcinabox.R;
import com.aof.mcinabox.gamecontroller.ckb.support.CustomizeKeyboardMaker;
import com.aof.mcinabox.gamecontroller.client.Client;
import com.aof.mcinabox.gamecontroller.codes.Translation;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.Input;
import com.aof.mcinabox.gamecontroller.input.OnscreenInput;
import com.aof.mcinabox.gamecontroller.input.log.DebugInfo;
import com.aof.mcinabox.gamecontroller.input.screen.CrossKeyboard;
import com.aof.mcinabox.gamecontroller.input.screen.CustomizeKeyboard;
import com.aof.mcinabox.gamecontroller.input.screen.InputBox;
import com.aof.mcinabox.gamecontroller.input.screen.ItemBar;
import com.aof.mcinabox.gamecontroller.input.screen.OnscreenJoystick;
import com.aof.mcinabox.gamecontroller.input.screen.OnscreenKeyboard;
import com.aof.mcinabox.gamecontroller.input.screen.OnscreenMouse;
import com.aof.mcinabox.gamecontroller.input.screen.OnscreenTouchpad;
import com.aof.mcinabox.utils.DisplayUtils;
import com.aof.mcinabox.utils.dialog.DialogUtils;
import com.aof.mcinabox.utils.dialog.support.DialogSupports;

import java.util.HashMap;
import java.util.Objects;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MARK_KEYNAME_SPLIT;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_POINTER;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_POINTER_INC;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.TYPE_WORDS;


public class VirtualController extends BaseController implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    //sp
    private final static String spFileName = "virtualcontroller_config";
    private final static int spMode = Context.MODE_PRIVATE;
    private final static String sp_enable_crosskeyboard = "enable_mcpe_keyboard";
    private final static String sp_enable_ckb = "enable_customize_keyboard";
    private final static String sp_enable_itembar = "enable_mcpe_itembar";
    private final static String sp_enable_onscreenkeyboard = "enable_pc_keyboard";
    private final static String sp_enable_onscreenmouse = "enable_pc_mouse";
    private final static String sp_enable_onscreentouchpad = "enable_touchpad";
    private final static String sp_enable_joystick = "enable_mcpe_joystick";
    private final static String sp_enable_inputbox = "enable_inputbox";
    private final static String sp_enable_debuginfo = "enable_debuginfo";
    private final static String sp_first_loadder = "first_loaded";

    //Dialog的控件
    private final String TAG = "VirtualController";
    private final Translation mTranslation;
    private int screenWidth;
    private int screenHeight;
    public OnscreenInput crossKeyboard;
    public OnscreenInput itemBar;
    public OnscreenInput onscreenKeyboard;
    public OnscreenInput onscreenMouse;
    public OnscreenInput custmoizeKeyboard;
    public OnscreenInput onscreenTouchpad;
    public OnscreenInput inputBox;
    public OnscreenInput onscreenJoystick;
    public Input debugInfo;
    private DragFloatActionButton dButton;
    private VirtualControllerSetting settingDialog;
    private ImageButton buttonCustomizeKeyboard;
    private SwitchCompat switchCustomizeKeyboard;
    private ImageButton buttonPCKeyboard;
    private SwitchCompat switchPCKeyboard;
    private ImageButton buttonPCMouse;
    private SwitchCompat switchPCMouse;
    private ImageButton buttonPEKeyboard;
    private SwitchCompat switchPEKeyboard;
    private ImageButton buttonPEJoystick;
    private SwitchCompat switchPEJoystick;
    private ImageButton buttonPEItembar;
    private SwitchCompat switchPEItembar;
    private ImageButton buttonTouchpad;
    private SwitchCompat switchTouchpad;
    private ImageButton buttonInputBox;
    private SwitchCompat switchInputBox;
    private ImageButton buttonDebugInfo;
    private SwitchCompat switchDebugInfo;
    private Button buttonOK;
    private CheckBox checkboxLock;
    private Button buttonResetPos;

    //绑定
    private HashMap<View, Input> bindingViews;

    public VirtualController(Client client, int transType) {
        super(client, true);

        //初始化键值翻译器
        this.mTranslation = new Translation(transType);

        screenWidth = this.getConfig().getScreenWidth();
        screenHeight = this.getConfig().getScreenHeight();

        //初始化
        init();

    }

    @Override
    public void saveConfig() {
        super.saveConfig();
        this.saveConfigToFile();
    }

    public void init() {
        //初始化Setting对话框
        settingDialog = new VirtualControllerSetting(context);

        //初始化控制器
        onscreenTouchpad = new OnscreenTouchpad();
        crossKeyboard = new CrossKeyboard();
        itemBar = new ItemBar();
        onscreenKeyboard = new OnscreenKeyboard();
        onscreenMouse = new OnscreenMouse();
        custmoizeKeyboard = new CustomizeKeyboard();
        onscreenJoystick = new OnscreenJoystick();
        inputBox = new InputBox();
        debugInfo = new DebugInfo();

        //注册控制器
        this.addInput(onscreenTouchpad);
        this.addInput(debugInfo);
        this.addInput(crossKeyboard);
        this.addInput(itemBar);
        this.addInput(onscreenKeyboard);
        this.addInput(onscreenMouse);
        this.addInput(custmoizeKeyboard);
        this.addInput(onscreenJoystick);
        this.addInput(inputBox);

        //全部隐藏
        for (Input i : inputs) {
            i.setEnabled(false);
        }

        //添加悬浮配置按钮
        dButton = new DragFloatActionButton(context);
        dButton.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(context, 30), DisplayUtils.getPxFromDp(context, 30)));
        dButton.setBackground(ContextCompat.getDrawable(context, R.drawable.background_floatbutton));
        dButton.setTodo(new ArrangeRule() {
            @Override
            public void run() {
                settingDialog.show();
            }
        });
        dButton.setY((float) (screenHeight / 2));
        client.addContentView(dButton, dButton.getLayoutParams());

        //初始化Dialog的控件

        buttonCustomizeKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_button_customize_keyboard);
        buttonPCKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pc_keyboard);
        buttonPCMouse = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pc_mouse);
        buttonPEKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pe_keyboard);
        buttonPEJoystick = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pe_joystick);
        buttonPEItembar = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pe_itembar);
        buttonTouchpad = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pc_touchpad);
        buttonInputBox = settingDialog.findViewById(R.id.virtual_controller_dialog_button_inputbox);
        buttonDebugInfo = settingDialog.findViewById(R.id.virtual_controller_dialog_button_debug_info);

        switchCustomizeKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_customize_keyboard);
        switchPCKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pc_keyboard);
        switchPCMouse = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pc_mouse);
        switchPEKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pe_keyboard);
        switchPEJoystick = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pe_joystick);
        switchPEItembar = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pe_itembar);
        switchTouchpad = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pc_touchpad);
        switchInputBox = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_inputbox);
        switchDebugInfo = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_debug_info);

        buttonOK = settingDialog.findViewById(R.id.virtual_controller_dialog_button_ok);
        checkboxLock = settingDialog.findViewById(R.id.virtual_controller_dialog_checkbox_lock);
        buttonResetPos = settingDialog.findViewById(R.id.virtual_controller_dialog_button_reset_pos);

        //给Dialog布局添加监听

        for (View v : new View[]{buttonCustomizeKeyboard, buttonOK, buttonResetPos, buttonPCKeyboard, buttonPCMouse, buttonPEKeyboard, buttonPEJoystick, buttonPEItembar, buttonTouchpad, buttonInputBox, buttonDebugInfo}) {
            v.setOnClickListener(this);
        }

        for (SwitchCompat s : new SwitchCompat[]{switchCustomizeKeyboard, switchPCKeyboard, switchPCMouse, switchPEKeyboard, switchPEJoystick, switchPEItembar, switchTouchpad, switchInputBox, switchDebugInfo}) {
            s.setOnCheckedChangeListener(this);
        }

        checkboxLock.setOnCheckedChangeListener(this);

        //绑定
        bindViewWithInput();

        //加载配置文件
        loadConfigFromFile();
    }

    public void bindViewWithInput() {
        //绑定Input对象与ImageButton和Switch
        bindingViews = new HashMap<>();
        bindingViews.put(buttonCustomizeKeyboard, custmoizeKeyboard);
        bindingViews.put(switchCustomizeKeyboard, custmoizeKeyboard);
        bindingViews.put(buttonPCKeyboard, onscreenKeyboard);
        bindingViews.put(switchPCKeyboard, onscreenKeyboard);
        bindingViews.put(buttonPCMouse, onscreenMouse);
        bindingViews.put(switchPCMouse, onscreenMouse);
        bindingViews.put(buttonPEKeyboard, crossKeyboard);
        bindingViews.put(switchPEKeyboard, crossKeyboard);
        bindingViews.put(buttonPEJoystick, onscreenJoystick);
        bindingViews.put(switchPEJoystick, onscreenJoystick);
        bindingViews.put(buttonPEItembar, itemBar);
        bindingViews.put(switchPEItembar, itemBar);
        bindingViews.put(buttonTouchpad, onscreenTouchpad);
        bindingViews.put(switchTouchpad, onscreenTouchpad);
        bindingViews.put(buttonInputBox, inputBox);
        bindingViews.put(switchInputBox, inputBox);
        bindingViews.put(buttonDebugInfo, debugInfo);
        bindingViews.put(switchDebugInfo, debugInfo);
    }

    @Override
    public void sendKey(BaseKeyEvent e) {
        //日志输出
        toLog(e);
        //事件分配
        switch (e.getType()) {
            case KEYBOARD_BUTTON:
            case MOUSE_BUTTON:
                String KeyName = e.getKeyName();
                String[] strs = KeyName.split(MARK_KEYNAME_SPLIT);
                for (String str : strs) {
                    //Log.e(e.getTag(),"切分: " + str + " 总大小: " + strs.length );
                    sendKeyEvent(new BaseKeyEvent(e.getTag(), str, e.isPressed(), e.getType(), e.getPointer()));
                }
                break;
            case MOUSE_POINTER:
            case MOUSE_POINTER_INC:
            case TYPE_WORDS:
                sendKeyEvent(e);
                break;
            default:
                break;
        }

    }

    private void toLog(BaseKeyEvent event) {
        String info;
        switch (event.getType()) {
            case KEYBOARD_BUTTON:
                info = "Type: " + event.getType() + " KeyName: " + event.getKeyName() + " Pressed: " + event.isPressed();
                break;
            case MOUSE_BUTTON:
                info = "Type: " + event.getType() + " MouseName " + event.getKeyName() + " Pressed: " + event.isPressed();
                break;
            case MOUSE_POINTER:
                info = "Type: " + event.getType() + " PointerX: " + event.getPointer()[0] + " PointerY: " + event.getPointer()[1];
                break;
            case TYPE_WORDS:
                info = "Type: " + event.getType() + " Char: " + event.getChars();
                break;
            case MOUSE_POINTER_INC:
                info = "Type: " + event.getType() + " IncX: " + event.getPointer()[0] + " IncY: " + event.getPointer()[1];
                break;
            default:
                info = "Unknown: " + event.toString();
        }
        Log.e(event.getTag(), info);
    }

    //事件发送
    private void sendKeyEvent(BaseKeyEvent e) {
        switch (e.getType()) {
            case KEYBOARD_BUTTON:
                client.setKey(mTranslation.trans(e.getKeyName()), e.isPressed());
                break;
            case MOUSE_BUTTON:
                client.setMouseButton(mTranslation.trans(e.getKeyName()), e.isPressed());
                break;
            case MOUSE_POINTER:
                if (e.getPointer() != null) {
                    client.setPointer(e.getPointer()[0], e.getPointer()[1]);
                }
                break;
            case TYPE_WORDS:
                typeWords(e.getChars());
                break;
            case MOUSE_POINTER_INC:
                if (e.getPointer() != null) {
                    client.setPointerInc(e.getPointer()[0], e.getPointer()[1]);
                }
            default:
        }
    }

    @Override
    public void onClick(View v) {

        if (v instanceof ImageButton && bindingViews.containsKey(v)) {
            Objects.requireNonNull(bindingViews.get(v)).runConfigure();
            return;
        }

        if (v == buttonOK) {
            saveConfigToFile();
            settingDialog.dismiss();
            return;
        }

        if (v == buttonResetPos) {
            DialogUtils.createBothChoicesDialog(context, context.getString(R.string.title_note), context.getString(R.string.tips_are_you_sure_to_auto_config_layout), context.getString(R.string.title_ok), context.getString(R.string.title_cancel), new DialogSupports() {
                @Override
                public void runWhenPositive() {
                    resetAllPosOnScreen();
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (buttonView instanceof SwitchCompat && bindingViews.containsKey(buttonView)) {
            (Objects.requireNonNull(bindingViews.get(buttonView))).setEnabled(isChecked);
        }
        if (buttonView == checkboxLock) {
            for (Input i : inputs) {
                if (i instanceof OnscreenInput)
                    ((OnscreenInput) i).setUiMoveable(isChecked);
            }
        }

    }

    //根据X,Y的比例，计算Input主控件中心位置在Activity的主View中的位置
    private int[] calculateMarginsOnScreen(OnscreenInput i, float leftScale, float topScale) {
        int viewWidth;
        int viewHeight;
        int leftMargin;
        int topMargin;

        if (i.getSize() == null) {
            return null;
        } else {
            viewWidth = i.getSize()[0];
            viewHeight = i.getSize()[1];
        }

        leftMargin = (int) (screenWidth * leftScale - viewWidth / 2);
        topMargin = (int) (screenHeight * topScale - viewHeight / 2);

        //超出右边界
        if (leftMargin + viewWidth > screenWidth) {
            leftMargin = screenWidth - viewWidth;
        }
        //超出下边界
        if (topMargin + viewHeight > screenHeight) {
            topMargin = screenHeight - viewHeight;
        }
        //超出左边界
        if (leftMargin < 0) {
            leftMargin = 0;
        }
        //超出上边界
        if (topMargin < 0) {
            topMargin = 0;
        }

        //Log.e(TAG,"屏幕宽度 " + screenWidth + " 屏幕高度 " + screenHeight + '\n' + "左侧比例 " + leftScale + " 顶部比例 " + topScale + '\n' + "左侧边距大小 " + leftMargin + " 顶部边距大小 " +topMargin);

        return new int[]{leftMargin, topMargin};
    }

    private void resetAllPosOnScreen() {
        int[] i;

        i = calculateMarginsOnScreen(onscreenKeyboard, 0.5f, 0.5f);
        onscreenKeyboard.setMargins(i[0], i[1], 0, 0);
        i = calculateMarginsOnScreen(onscreenMouse, 0.8f, 0.7f);
        onscreenMouse.setMargins(i[0], i[1], 0, 0);
        i = calculateMarginsOnScreen(crossKeyboard, 0.2f, 0.7f);
        crossKeyboard.setMargins(i[0], i[1], 0, 0);
        i = calculateMarginsOnScreen(itemBar, 0.5f, 1);
        itemBar.setMargins(i[0], i[1], 0, 0);
    }

    private void saveConfigToFile() {
        SharedPreferences.Editor editor = context.getSharedPreferences(spFileName, spMode).edit();
        editor.putBoolean(sp_enable_ckb, switchCustomizeKeyboard.isChecked());
        editor.putBoolean(sp_enable_onscreenkeyboard, switchPCKeyboard.isChecked());
        editor.putBoolean(sp_enable_onscreenmouse, switchPCMouse.isChecked());
        editor.putBoolean(sp_enable_itembar, switchPEItembar.isChecked());
        editor.putBoolean(sp_enable_joystick, switchPEJoystick.isChecked());
        editor.putBoolean(sp_enable_onscreentouchpad, switchTouchpad.isChecked());
        editor.putBoolean(sp_enable_crosskeyboard, switchPEKeyboard.isChecked());
        editor.putBoolean(sp_enable_inputbox, switchInputBox.isChecked());
        editor.putBoolean(sp_enable_debuginfo, switchDebugInfo.isChecked());
        if (!context.getSharedPreferences(spFileName, spMode).contains(sp_first_loadder)) {
            editor.putBoolean(sp_first_loadder, false);
        }
        editor.apply();

    }

    private void loadConfigFromFile() {
        SharedPreferences sp = context.getSharedPreferences(spFileName, spMode);
        switchCustomizeKeyboard.setChecked(sp.getBoolean(sp_enable_ckb, true));
        switchPCKeyboard.setChecked(sp.getBoolean(sp_enable_onscreenkeyboard, false));
        switchPCMouse.setChecked(sp.getBoolean(sp_enable_onscreenmouse, false));
        switchPEKeyboard.setChecked(sp.getBoolean(sp_enable_crosskeyboard, true));
        switchPEItembar.setChecked(sp.getBoolean(sp_enable_itembar, true));
        switchPEJoystick.setChecked(sp.getBoolean(sp_enable_joystick, false));
        switchTouchpad.setChecked(sp.getBoolean(sp_enable_onscreentouchpad, true));
        switchInputBox.setChecked(sp.getBoolean(sp_enable_inputbox, false));
        switchDebugInfo.setChecked(sp.getBoolean(sp_enable_debuginfo, false));
        if (!sp.contains(sp_first_loadder)) {
            resetAllPosOnScreen();
            ((CustomizeKeyboard)custmoizeKeyboard).mManager.loadKeyboard(new CustomizeKeyboardMaker(context).createDefaultKeyboard());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        saveConfigToFile();
    }

    private static class VirtualControllerSetting extends Dialog {
        public VirtualControllerSetting(@NonNull Context context) {
            super(context);
            setContentView(R.layout.dialog_controller_functions);
        }
    }

    private static class DragFloatActionButton extends LinearLayout implements ViewGroup.OnTouchListener {

        private static final String TAG = "DragButton";
        private int parentHeight;
        private int parentWidth;

        private int lastX;
        private int lastY;

        private boolean isDrag;
        private ViewGroup parent;

        private ArrangeRule aRule;


        public DragFloatActionButton(Context context) {
            super(context);
            this.setOnTouchListener(this);
        }

        public DragFloatActionButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public boolean performClick() {
            super.performClick();
            return false;
        }

        public void behave(MotionEvent event) {
            int rawX = (int) event.getRawX();
            int rawY = (int) event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isDrag = false;
                    this.setAlpha(0.9f);
                    getParent().requestDisallowInterceptTouchEvent(true);
                    lastX = rawX;
                    lastY = rawY;
                    if (getParent() != null) {
                        parent = (ViewGroup) getParent();
                        parentHeight = parent.getHeight();
                        parentWidth = parent.getWidth();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    this.setAlpha(0.9f);
                    int dx = rawX - lastX;
                    int dy = rawY - lastY;
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    if (distance > 2 && !isDrag) {
                        isDrag = true;
                    }

                    float x = getX() + dx;
                    float y = getY() + dy;
                    //检测是否到达边缘 左上右下
                    x = x < 0 ? 0 : x > parentWidth - getWidth() ? parentWidth - getWidth() : x;
                    y = getY() < 0 ? 0 : getY() + getHeight() > parentHeight ? parentHeight - getHeight() : y;
                    setX(x);
                    setY(y);
                    lastX = rawX;
                    lastY = rawY;
                    break;
                case MotionEvent.ACTION_UP:
                    if (isDrag) {
                        //恢复按压效果
                        setPressed(false);
                        moveHide(rawX);
                    } else {
                        //执行点击操作
                        startTodo();
                    }
                    break;
            }
        }

        private void moveHide(int rawX) {
            if (rawX >= parentWidth / 2) {
                //靠右吸附
                ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), parentWidth - getWidth());
                oa.setInterpolator(new DecelerateInterpolator());
                oa.setDuration(500);
                oa.start();
            } else {
                //靠左吸附
                ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
                oa.setInterpolator(new DecelerateInterpolator());
                oa.setDuration(500);
                oa.start();
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v == this) {
                this.behave(event);
                return true;
            }
            return false;
        }

        public void setTodo(ArrangeRule ar) {
            this.aRule = ar;
        }

        public void startTodo() {
            if (aRule != null) {
                aRule.run();
            }
        }
    }

    private static class ArrangeRule {
        public void run() {
            // Override this method.
        }
    }
}
