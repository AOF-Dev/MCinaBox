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
import com.aof.mcinabox.definitions.id.AppEvent;
import com.aof.mcinabox.gamecontroller.R;
import com.aof.mcinabox.gamecontroller.client.ClientInput;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.Input;
import com.aof.mcinabox.gamecontroller.input.OnscreenInput;
import com.aof.mcinabox.gamecontroller.input.screen.CrossKeyboard;
import com.aof.mcinabox.gamecontroller.input.screen.CustomizeKeyboard;
import com.aof.mcinabox.gamecontroller.input.screen.InputBox;
import com.aof.mcinabox.gamecontroller.input.screen.ItemBar;
import com.aof.mcinabox.gamecontroller.input.screen.OnscreenJoystick;
import com.aof.mcinabox.gamecontroller.input.screen.OnscreenKeyboard;
import com.aof.mcinabox.gamecontroller.input.screen.OnscreenMouse;
import com.aof.mcinabox.gamecontroller.input.screen.OnscreenTouchpad;
import com.aof.mcinabox.gamecontroller.codes.Translation;
import com.aof.utils.DisplayUtils;
import com.aof.utils.dialog.DialogUtils;
import com.aof.utils.dialog.support.DialogSupports;
import java.util.HashMap;
import java.util.Objects;


public class VirtualController extends BaseController implements AppEvent , View.OnClickListener , CompoundButton.OnCheckedChangeListener {

    private String TAG = "VirtualController";
    private Translation mTranslation;
    private Context mContext;

    private OnscreenInput crossKeyboard;
    private OnscreenInput itemBar;
    private OnscreenInput onscreenKeyboard;
    private OnscreenInput onscreenMouse;
    private OnscreenInput custmoizeKeyboard;
    private OnscreenInput onscreenTouchpad;
    private OnscreenInput inputBox;
    private OnscreenInput onscreenJoystick;

    private DragFloatActionButton dButton;

    private VirtualControllerSetting settingDialog;

    //Dialog的控件

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

    private Button buttonOK;

    private CheckBox checkboxLock;
    private Button buttonResetPos;

    //绑定
    private HashMap<View, Input> bindingViews;

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
    private final static String sp_first_loadder = "first_loaded";

    int screenWidth;
    int screenHeight;

    public VirtualController(Context context , ClientInput client, int transType) {
        super(context,client);
        mContext = context;

        //初始化键值翻译器
        this.mTranslation = new Translation(transType);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        //初始化
        init();

    }

    @Override
    public void saveConfig(){
        super.saveConfig();
        this.saveConfigToFile();
    }

    private void init(){
        //初始化Setting对话框
        settingDialog = new VirtualControllerSetting(context);
        settingDialog.create();

        //初始化控制器
        onscreenTouchpad = new OnscreenTouchpad();
        crossKeyboard = new CrossKeyboard();
        itemBar = new ItemBar();
        onscreenKeyboard = new OnscreenKeyboard();
        onscreenMouse = new OnscreenMouse();
        custmoizeKeyboard = new CustomizeKeyboard();
        onscreenJoystick = new OnscreenJoystick();
        inputBox = new InputBox();

        //注册控制器
        this.addInput(onscreenTouchpad);
        this.addInput(crossKeyboard);
        this.addInput(itemBar);
        this.addInput(onscreenKeyboard);
        this.addInput(onscreenMouse);
        this.addInput(custmoizeKeyboard);
        this.addInput(onscreenJoystick);
        this.addInput(inputBox);

        //全部隐藏
        for(Input i : inputs){
            i.setEnable(false);
        }

        //添加悬浮配置按钮
        dButton = new DragFloatActionButton(context);
        dButton.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(mContext,30), DisplayUtils.getPxFromDp(mContext,30)));
        dButton.setBackground(ContextCompat.getDrawable(mContext,R.drawable.background_floatbutton));
        dButton.setTodo(new ArrangeRule(){
            @Override
            public void run(){
                settingDialog.show();
            }
        });
        dButton.setY((float) (screenHeight / 2));
        client.addControllerView(dButton);

        //初始化Dialog的控件

        buttonCustomizeKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_button_customize_keyboard);
        buttonPCKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pc_keyboard);
        buttonPCMouse = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pc_mouse);
        buttonPEKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pe_keyboard);
        buttonPEJoystick = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pe_joystick);
        buttonPEItembar = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pe_itembar);
        buttonTouchpad = settingDialog.findViewById(R.id.virtual_controller_dialog_button_pc_touchpad);
        buttonInputBox = settingDialog.findViewById(R.id.virtual_controller_dialog_button_inputbox);

        switchCustomizeKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_customize_keyboard);
        switchPCKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pc_keyboard);
        switchPCMouse = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pc_mouse);
        switchPEKeyboard = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pe_keyboard);
        switchPEJoystick = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pe_joystick);
        switchPEItembar = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pe_itembar);
        switchTouchpad = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_pc_touchpad);
        switchInputBox = settingDialog.findViewById(R.id.virtual_controller_dialog_switch_inputbox);

        buttonOK = settingDialog.findViewById(R.id.virtual_controller_dialog_button_ok);
        checkboxLock = settingDialog.findViewById(R.id.virtual_controller_dialog_checkbox_lock);
        buttonResetPos = settingDialog.findViewById(R.id.virtual_controller_dialog_button_reset_pos);

        //给Dialog布局添加监听

        for(View v : new View[]{buttonCustomizeKeyboard,buttonOK,buttonResetPos,buttonPCKeyboard , buttonPCMouse , buttonPEKeyboard , buttonPEJoystick , buttonPEItembar ,buttonTouchpad, buttonInputBox}){
            v.setOnClickListener(this);
        }

        for(SwitchCompat s : new SwitchCompat[]{switchCustomizeKeyboard,switchPCKeyboard , switchPCMouse , switchPEKeyboard , switchPEJoystick , switchPEItembar , switchTouchpad, switchInputBox}){
            s.setOnCheckedChangeListener(this);
        }

        checkboxLock.setOnCheckedChangeListener(this);

        //绑定Input对象与ImageButton和Switch
        bindingViews = new HashMap<>();
        bindingViews.put(buttonCustomizeKeyboard,custmoizeKeyboard);
        bindingViews.put(switchCustomizeKeyboard,custmoizeKeyboard);
        bindingViews.put(buttonPCKeyboard,onscreenKeyboard);
        bindingViews.put(switchPCKeyboard,onscreenKeyboard);
        bindingViews.put(buttonPCMouse,onscreenMouse);
        bindingViews.put(switchPCMouse,onscreenMouse);
        bindingViews.put(buttonPEKeyboard,crossKeyboard);
        bindingViews.put(switchPEKeyboard,crossKeyboard);
        bindingViews.put(buttonPEJoystick,onscreenJoystick);
        bindingViews.put(switchPEJoystick,onscreenJoystick);
        bindingViews.put(buttonPEItembar,itemBar);
        bindingViews.put(switchPEItembar,itemBar);
        bindingViews.put(buttonTouchpad,onscreenTouchpad);
        bindingViews.put(switchTouchpad,onscreenTouchpad);
        bindingViews.put(buttonInputBox,inputBox);
        bindingViews.put(switchInputBox,inputBox);

        //加载配置文件
        loadConfigFromFile();
    }

    @Override
    public void sendKey(BaseKeyEvent e) {
        //日志输出
        toLog(e);
        //事件分配
        switch (e.getType()){
            case KEYBOARD_BUTTON:
            case MOUSE_BUTTON:
                String KeyName = e.getKeyName();
                String[] strs = KeyName.split(MARK_KEYNAME_SPLIT);
                for(String str : strs){
                    //Log.e(e.getTag(),"切分: " + str + " 总大小: " + strs.length );
                    sendKeyEvent(new BaseKeyEvent(e.getTag(),str,e.isPressed(),e.getType(),e.getPointer()));
                }
                break;
            case MOUSE_POINTER:
                sendKeyEvent(e);
            case TYPE_WORDS:
                sendKeyEvent(e);
                break;
            default:
                break;
        }

    }

    private void toLog(BaseKeyEvent event){
        String info;
        switch (event.getType()){
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
            default:
                info = "Unknown Type: ";
        }
        Log.e(event.getTag(),info);
    }

    //事件发送
    private void sendKeyEvent(BaseKeyEvent e){
        switch (e.getType()){
            case KEYBOARD_BUTTON:
                client.setKey(mTranslation.trans(e.getKeyName()),e.isPressed());
                break;
            case MOUSE_BUTTON:
                client.setMouseButton(mTranslation.trans(e.getKeyName()),e.isPressed());
                break;
            case MOUSE_POINTER:
                if(e.getPointer() != null){
                    client.setMousePoniter(e.getPointer()[0],e.getPointer()[1]);
                }
                break;
            case TYPE_WORDS:
                client.typeWords(e.getChars());
            default:
        }
    }

    @Override
    public void onClick(View v) {

        if(v instanceof ImageButton && bindingViews.containsKey(v)){
            Objects.requireNonNull(bindingViews.get(v)).runConfigure();
            return;
        }

        if(v == buttonOK){
            saveConfigToFile();
            settingDialog.dismiss();
            return;
        }

        if(v == buttonResetPos){
            DialogUtils.createBothChoicesDialog(context,"自动配置布局","你确定要自动配置布局吗？此操作将更改您的控制器，并自动计算可能合适的布局位置。","确定","取消",new DialogSupports(){
                @Override
                public void runWhenPositive(){
                    resetAllPosOnScreen();
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(buttonView instanceof SwitchCompat && bindingViews.containsKey(buttonView)){
            if(isChecked){
                (Objects.requireNonNull(bindingViews.get(buttonView))).setEnable(true);
            }else{
                (Objects.requireNonNull(bindingViews.get(buttonView))).setEnable(false);
            }
        }
        if(buttonView == checkboxLock){
            if(isChecked){
                for(Input i : inputs){
                    ((OnscreenInput)i).setUiMoveable(true);
                }
            }else{
                for(Input i : inputs){
                    ((OnscreenInput)i).setUiMoveable(false);
                }
            }
        }

    }

    //根据X,Y的比例，计算Input主控件中心位置在Activity的主View中的位置
    private int[] calculateMarginsOnScreen(OnscreenInput i, float leftScale , float topScale){
        int viewWidth;
        int viewHeight;
        int leftMargin;
        int topMargin;

        if(i.getSize() == null){
            return null;
        }else{
            viewWidth = i.getSize()[0];
            viewHeight = i.getSize()[1];
        }

        leftMargin = (int)(screenWidth * leftScale - viewWidth / 2);
        topMargin = (int)(screenHeight * topScale - viewHeight / 2);

        //超出右边界
        if(leftMargin + viewWidth > screenWidth){
            leftMargin = screenWidth - viewWidth;
        }
        //超出下边界
        if(topMargin + viewHeight > screenHeight){
            topMargin = screenHeight - viewHeight;
        }
        //超出左边界
        if(leftMargin < 0){
            leftMargin = 0;
        }
        //超出上边界
        if(topMargin < 0){
            topMargin = 0;
        }

        //Log.e(TAG,"屏幕宽度 " + screenWidth + " 屏幕高度 " + screenHeight + '\n' + "左侧比例 " + leftScale + " 顶部比例 " + topScale + '\n' + "左侧边距大小 " + leftMargin + " 顶部边距大小 " +topMargin);

        return new int[]{leftMargin , topMargin};
    }

    private void resetAllPosOnScreen(){
        int[] i;

        i = calculateMarginsOnScreen(onscreenKeyboard, 0.5f , 0.5f);
        onscreenKeyboard.setMargins(i[0],i[1],0,0);
        i = calculateMarginsOnScreen(onscreenMouse, 0.8f,0.7f);
        onscreenMouse.setMargins(i[0],i[1],0,0);
        i = calculateMarginsOnScreen(crossKeyboard,0.2f,0.7f);
        crossKeyboard.setMargins(i[0],i[1],0,0);
        i = calculateMarginsOnScreen(itemBar, 0.5f , 1);
        itemBar.setMargins(i[0],i[1],0,0);
    }

    private void saveConfigToFile(){
        SharedPreferences.Editor editor = mContext.getSharedPreferences(spFileName,spMode).edit();
        editor.putBoolean(sp_enable_ckb,switchCustomizeKeyboard.isChecked());
        editor.putBoolean(sp_enable_onscreenkeyboard,switchPCKeyboard.isChecked());
        editor.putBoolean(sp_enable_onscreenmouse,switchPCMouse.isChecked());
        editor.putBoolean(sp_enable_itembar,switchPEItembar.isChecked());
        editor.putBoolean(sp_enable_joystick,switchPEJoystick.isChecked());
        editor.putBoolean(sp_enable_onscreentouchpad,switchTouchpad.isChecked());
        editor.putBoolean(sp_enable_crosskeyboard,switchPEKeyboard.isChecked());
        editor.putBoolean(sp_enable_inputbox,switchInputBox.isChecked());
        if(!mContext.getSharedPreferences(spFileName,spMode).contains(sp_first_loadder)){
            editor.putBoolean(sp_first_loadder,false);
        }
        editor.apply();

    }

    private void loadConfigFromFile(){
        SharedPreferences sp = mContext.getSharedPreferences(spFileName,spMode);
        switchCustomizeKeyboard.setChecked(sp.getBoolean(sp_enable_ckb,true));
        switchPCKeyboard.setChecked(sp.getBoolean(sp_enable_onscreenkeyboard,false));
        switchPCMouse.setChecked(sp.getBoolean(sp_enable_onscreenmouse,false));
        switchPEKeyboard.setChecked(sp.getBoolean(sp_enable_crosskeyboard,true));
        switchPEItembar.setChecked(sp.getBoolean(sp_enable_itembar,true));
        switchPEJoystick.setChecked(sp.getBoolean(sp_enable_joystick,false));
        switchTouchpad.setChecked(sp.getBoolean(sp_enable_onscreentouchpad,true));
        switchInputBox.setChecked(sp.getBoolean(sp_enable_inputbox,false));
        if(!sp.contains(sp_first_loadder)){
            buttonResetPos.performClick();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        saveConfigToFile();
    }

}

class VirtualControllerSetting extends Dialog {

    public VirtualControllerSetting(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_controller_functions);
    }
}

class DragFloatActionButton extends LinearLayout implements ViewGroup.OnTouchListener {

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

    @Override
    public boolean performClick(){
        super.performClick();
        return false;
    }

    public DragFloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void behave(MotionEvent event){
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                this.setAlpha(0.9f);
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX = rawX;
                lastY = rawY;
                if(getParent() != null){
                    parent = (ViewGroup) getParent();
                    parentHeight = parent.getHeight();
                    parentWidth = parent.getWidth();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                this.setAlpha(0.9f);
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                int distance = (int) Math.sqrt(dx *dx + dy*dy);
                if(distance > 2 && !isDrag){
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
                if(isDrag){
                    //恢复按压效果
                    setPressed(false);
                    moveHide(rawX);
                }else{
                    //执行点击操作
                    startTodo();
                }
                break;
        }
    }

    private void moveHide(int rawX){
        if(rawX >= parentWidth / 2){
            //靠右吸附
            ObjectAnimator oa = ObjectAnimator.ofFloat(this,"x",getX(),parentWidth - getWidth());
            oa.setInterpolator(new DecelerateInterpolator());
            oa.setDuration(500);
            oa.start();
        }else{
            //靠左吸附
            ObjectAnimator oa = ObjectAnimator.ofFloat(this,"x",getX(),0);
            oa.setInterpolator(new DecelerateInterpolator());
            oa.setDuration(500);
            oa.start();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v == this){
            this.behave(event);
            return true;
        }
        return false;
    }

    public void setTodo(ArrangeRule ar){
        this.aRule = ar;
    }

    public void startTodo(){
        if(aRule != null){
            aRule.run();
        }
    }
}

class ArrangeRule{
    public void run(){
        // Override this method.
    }
}
