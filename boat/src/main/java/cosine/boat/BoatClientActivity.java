package cosine.boat;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.telephony.mbms.MbmsErrors;
import android.util.Log;
import android.view.MotionEvent;
import android.os.Bundle;
import android.app.NativeActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.nio.ByteBuffer;
import android.widget.LinearLayout;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import  org.lwjgl.input.Keyboard;
import java.io.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.aof.sharedmodule.Button.CrossButton;
import com.aof.sharedmodule.Button.ItemButton;
import com.aof.sharedmodule.Button.MouseButton;
import com.aof.sharedmodule.Button.QwertButton;
import com.google.gson.Gson;
import com.aof.sharedmodule.Model.ArgsModel;
import com.aof.sharedmodule.Tools.ColorUtils;
import com.aof.sharedmodule.Button.GameButton;
import cosine.boat.AdaptMCinaBoxApp.KeyTool;
import com.aof.sharedmodule.Model.KeyboardJsonModel;
import static org.lwjgl.glfw.GLFW.*;

public class BoatClientActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, TextWatcher, TextView.OnEditorActionListener {

    private ArgsModel argsModel;
    private ArrayList<GameButton> KeyboardList;
    private LinearLayout QwertKeyboard;
    private LinearLayout CrossKey;
    private LinearLayout MouseKey;
    private LinearLayout SwitcherBar;
    private int screenWidth,screenHeight;
    private PopupWindow popupWindow;
    private RelativeLayout base;
    private LinearLayout itemBar;
    private ImageView mouseCursor;
    private EditText inputScanner;
    public boolean mode = false;
    private MyHandler mHandler;
    private int initialX;
    private int initialY;
    private int baseX;
    private int baseY;
    private HashMap<Object,int[]> layoutsPos;
    private CrossButton[] crosskeychildren;
    private int[] tempCrossKey;
    private CheckBox checkbox_qwertkeyboard,checkbox_crosskey,checkbox_mousekey,checkbox_virtualkeyboard,checkbox_otg,checkbox_joystick;
    private CheckBox[] toolerBarChildren;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //获取参数对象
        argsModel = (ArgsModel) getIntent().getSerializableExtra("LauncherConfig");
        //获取屏幕的长宽像素
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        //初始化界面
        InitWindowsAndScreenKeyboard();
        //初始化hanlder
        mHandler = new MyHandler();
        //初始化Map
        layoutsPos = new HashMap<Object,int[]>();

    }

    @Override
    protected void onPause() {
        super.onPause();
        popupWindow.dismiss();
    }

/*
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        super.surfaceCreated(holder);
        System.out.println("Surface is created!");


        new Thread() {
            @Override
            public void run() {
                ArgsModel argsModel = (ArgsModel) getIntent().getSerializableExtra("LauncherConfig");
                LauncherConfig config = LauncherConfig.fromFile(getIntent().getExtras().getString("config"));
                LoadMe.exec(config, BoatClientActivity.this);
                Message msg = new Message();
                msg.what = -1;
                mHandler.sendMessage(msg);

            }
        }.start();
    }*/

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    BoatClientActivity.this.mouseCursor.setVisibility(View.INVISIBLE);
                    BoatClientActivity.this.itemBar.setVisibility(View.VISIBLE);
                    BoatClientActivity.this.mode = true;
                    break;
                case 1:
                    BoatClientActivity.this.mouseCursor.setVisibility(View.VISIBLE);
                    BoatClientActivity.this.itemBar.setVisibility(View.INVISIBLE);
                    BoatClientActivity.this.mode = false;
                    break;
                default:
                    BoatClientActivity.this.finish();
                    break;
            }
        }
    }

    private Button findButton(int id) {
        Button b = (Button) base.findViewById(id);
        b.setOnTouchListener(this);
        return b;
    }

    @Override
    public void onClick(View p1) {

        if (p1 == inputScanner) {
            inputScanner.setSelection(1);
        }
    }

    public void setCursorMode(int mode) {
        Message msg = new Message();
        msg.what = mode;
        mHandler.sendMessage(msg);
    }

    @Override
    public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {

    }

    @Override
    public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {

    }

    @Override
    public void afterTextChanged(Editable p1) {

        String newText = p1.toString();
        if (newText.length() < 1) {

            BoatInputEventSender.setKey(GLFW_KEY_BACKSPACE, true, 0);
            BoatInputEventSender.setKey(GLFW_KEY_BACKSPACE, false, 0);
            inputScanner.setText(">");
            inputScanner.setSelection(1);
        }
        if (newText.length() > 1) {
            for (int i = 1; i < newText.length(); i++) {
                BoatInputEventSender.setKey(0, true, newText.charAt(i));
                BoatInputEventSender.setKey(0, false, newText.charAt(i));
            }

            inputScanner.setText(">");
            inputScanner.setSelection(1);
        }
    }

    @Override
    public boolean onEditorAction(TextView p1, int p2, KeyEvent p3) {

        BoatInputEventSender.setKey(GLFW_KEY_ENTER, true, '\n');
        BoatInputEventSender.setKey(GLFW_KEY_ENTER, false, '\n');
        return false;
    }

    @Override
    public boolean onTouch(View p1, MotionEvent p2) {

        Log.e("TouchedView","ID: "+p1.getId());
        Log.e("MotionEvent",p2.getAction()+"");

        if (p1 == inputScanner) {
            inputScanner.setSelection(1);
            return false;
        }

        //物品栏手势
        if(p1 instanceof ItemButton){
            OnTouchItemButton((ItemButton) p1,p2);
            return false;
        }

        //全键盘手势
        if((p1 instanceof QwertButton) && !((QwertButton) p1).getButtonName().equals("Move")){
            OnTouchQwertKeyboard((QwertButton)p1,p2);
            return false;
        }

        //移动全键盘
        if((p1 instanceof QwertButton) && ((QwertButton)p1).getButtonName().equals("Move")){
            OnMoveQwertKeyboard(p1,p2);
            return true;
        }

        //十字键手势
        if(p1 instanceof CrossButton){
            Log.e("StartTouchCross","true");
            OnTouchCrossKey(p1,p2,true);
            return false;
        }

        //移动十字键
        if(p1.getId() == R.id.crosskey_move){
            OnMoveCrossKey((Button)p1,p2);
            return true;
        }

        //自定义虚拟按键手势
        for(GameButton gameButton : KeyboardList){
            if(p1 == gameButton){
                OnTouchVirtualKeyboard(gameButton,p2);
                return  false;
            }
        }

        //屏幕鼠标手势
        if(p1 instanceof MouseButton){
            OnTouchMouseKey((MouseButton) p1,p2);
            return false;
        }

        //移动屏幕鼠标
        if(p1.getId() == R.id.mousekey_move){
            OnMoveMouseKey(p1,p2);
            return true;
        }


        //鼠标指针
        if (p1 == base) {
            OnTouchVirtualMouse(p2);
            return true;
        }

        return false;

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            popupWindow.showAtLocation(BoatClientActivity.this.getWindow().getDecorView(), Gravity.TOP | Gravity.LEFT, 0, 0);
        }
    }


    /**【初始化界面和布局】**/
    public void InitWindowsAndScreenKeyboard(){
        //初始化一个悬浮窗口
        popupWindow = new PopupWindow();
        popupWindow.setWidth(LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LayoutParams.MATCH_PARENT);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setFocusable(true);

        //设定界面
        base = (RelativeLayout) LayoutInflater.from(BoatClientActivity.this).inflate(R.layout.overlay, null);
        base.setOnTouchListener(this);
        mouseCursor = base.findViewById(R.id.mouse_cursor);
        itemBar = base.findViewById(R.id.item_bar);
        inputScanner = base.findViewById(R.id.input_scanner);
        inputScanner.setFocusable(true);
        inputScanner.addTextChangedListener(this);
        inputScanner.setOnEditorActionListener(this);
        inputScanner.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN | EditorInfo.IME_ACTION_DONE);
        inputScanner.setSelection(1);
        QwertKeyboard = base.findViewById(R.id.QwertKeyboard);
        CrossKey = base.findViewById(R.id.CrossKey);
        MouseKey = base.findViewById(R.id.MouseKey);
        crosskeychildren = new CrossButton[]{CrossKey.findViewById(R.id.crosskey_up_left), CrossKey.findViewById(R.id.crosskey_up_right), CrossKey.findViewById(R.id.crosskey_down_left), CrossKey.findViewById(R.id.crosskey_down_right)};
        SwitcherBar = base.findViewById(R.id.SwitcherBar);
        checkbox_qwertkeyboard = SwitcherBar.findViewById(R.id.checkbox_QwertKeyboard);
        checkbox_crosskey = SwitcherBar.findViewById(R.id.checkbox_CrossKey);
        checkbox_virtualkeyboard = SwitcherBar.findViewById(R.id.checkbox_VirtualKeyboard);
        checkbox_mousekey = SwitcherBar.findViewById(R.id.checkbox_MouseKey);
        checkbox_joystick = SwitcherBar.findViewById(R.id.checkbox_Joystick);
        checkbox_otg = SwitcherBar.findViewById(R.id.checkbox_Otg);
        toolerBarChildren = new CheckBox[]{checkbox_qwertkeyboard,checkbox_crosskey,checkbox_mousekey,checkbox_virtualkeyboard,checkbox_otg,checkbox_joystick};
        //设定checkbox监听
        for(CheckBox checkBox:toolerBarChildren){
            checkBox.setOnCheckedChangeListener(checkedlistener);
        }

        //设定虚拟鼠标
        for(int i =0; i < MouseKey.getChildCount();i++){
            if(MouseKey.getChildAt(i) instanceof Button){
                MouseKey.getChildAt(i).setOnTouchListener(this);
            }else {
                for (int a = 0; a < ((LinearLayout) MouseKey.getChildAt(i)).getChildCount(); a++) {
                    (((LinearLayout) MouseKey.getChildAt(i)).getChildAt(a)).setOnTouchListener(this);
                }
            }
        }

        //设定物品栏
        for(int i = 0;i < itemBar.getChildCount();i++){
            itemBar.getChildAt(i).setOnTouchListener(this);
        }

        //计算并设定物品栏大小
        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int scale = 1;
        while (width / (scale + 1) >= 320 && height / (scale + 1) >= 240) {
            scale++;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) itemBar.getLayoutParams();
        lp.height = 20 * scale;
        lp.width = 20 * scale * 9;
        itemBar.setLayoutParams(lp);

        //添加虚拟键盘
        KeyboardList = InitFromFile();
        for(GameButton gameButton : KeyboardList){
            gameButton.bringToFront();
            base.addView(gameButton);
            gameButton.setOnTouchListener(this);
        }

        //设定QwertKeyboard全键盘的监听
        for(int i = 0;i < ((LinearLayout)base.findViewById(R.id.QwertKeyboard)).getChildCount();i++){
            for(int a = 0;a < ((LinearLayout)((LinearLayout)base.findViewById(R.id.QwertKeyboard)).getChildAt(i)).getChildCount();a++){
                if(((LinearLayout)((LinearLayout)base.findViewById(R.id.QwertKeyboard)).getChildAt(i)).getChildAt(a) instanceof LinearLayout){
                    for(int b = 0;b <((LinearLayout)((LinearLayout)((LinearLayout)base.findViewById(R.id.QwertKeyboard)).getChildAt(i)).getChildAt(a)).getChildCount() ;b++){
                        ((LinearLayout)((LinearLayout)((LinearLayout)base.findViewById(R.id.QwertKeyboard)).getChildAt(i)).getChildAt(a)).getChildAt(b).setOnTouchListener(this);
                    }
                }else {
                    ((LinearLayout) ((LinearLayout) base.findViewById(R.id.QwertKeyboard)).getChildAt(i)).getChildAt(a).setOnTouchListener(this);
                }
            }
        }

        //设定CrossKey十字键的监听
        for(int i = 0;i < ((LinearLayout)base.findViewById(R.id.CrossKey)).getChildCount();i++){
            if(((LinearLayout)base.findViewById(R.id.CrossKey)).getChildAt(i) instanceof Button){
                ((LinearLayout)base.findViewById(R.id.CrossKey)).getChildAt(i).setOnTouchListener(this);
            }else{
                for(int a = 0;a < ((LinearLayout)((LinearLayout)base.findViewById(R.id.CrossKey)).getChildAt(i)).getChildCount();a++){
                    for(int b = 0;b< ((LinearLayout)(((LinearLayout)((LinearLayout)base.findViewById(R.id.CrossKey)).getChildAt(i)).getChildAt(a))).getChildCount();b++){
                        ((LinearLayout)(((LinearLayout)((LinearLayout)base.findViewById(R.id.CrossKey)).getChildAt(i)).getChildAt(a))).getChildAt(b).setOnTouchListener(this);
                    }
                }
            }
        }


        //显示布局到悬浮窗
        popupWindow.setContentView(base);

        //代码动态添加Qwert全键盘
        //QwertKeyboard = (LinearLayout) getLayoutInflater().inflate(R.layout.virtual_keyboard,null);
        //base.addView(QwertKeyboard);

    }


    /**【从文件获取虚拟键盘】**/
    public ArrayList<GameButton> InitFromFile(){
        InputStream inputStream;
        Gson gson = new Gson();
        File jsonFile = new File(argsModel.getKeyboardFilePath());
        Log.e("InitFromFile",argsModel.getKeyboardFilePath());
        ArrayList<GameButton> keyboardList = new ArrayList<GameButton>();
        if(!jsonFile.exists()){
            Toast.makeText(this, "找不到键盘模板", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            inputStream = new FileInputStream(jsonFile);
            Reader reader = new InputStreamReader(inputStream);
            KeyboardJsonModel[] jsonArray = new Gson().fromJson(reader, KeyboardJsonModel[].class);
            List<KeyboardJsonModel> tempList1 = Arrays.asList(jsonArray);
            ArrayList<KeyboardJsonModel> tempList2 = new ArrayList<KeyboardJsonModel>(tempList1);
            if(tempList2.size() != 0){
                Toast.makeText(this, "导入成功", Toast.LENGTH_SHORT).show();
                for(KeyboardJsonModel targetModel : tempList2){
                    GameButton gameButton = GetButtonFromModel(targetModel.getKeyName(),targetModel.getKeySizeW(),targetModel.getKeySizeH(),targetModel.getKeyLX(),targetModel.getKeyLY(),targetModel.getKeyMain(),targetModel.getSpecialOne(),targetModel.getSpecialTwo(),targetModel.isAutoKeep(),targetModel.isHide(),targetModel.isMult(),targetModel.getMainPos(),targetModel.getSpecialOnePos(),targetModel.getSpecialTwoPos(),targetModel.getColorhex(),targetModel.getCornerRadius());
                    keyboardList.add(gameButton);
                }
            }else{
                Toast.makeText(this, "导入失败", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return keyboardList;
    }

    public GameButton GetButtonFromModel(String KeyName, int KeySizeW,int KeySizeH, int KeyLX, int KeyLY, String KeyMain, String SpecialOne, String SpecialTwo, boolean isAutoKeep, boolean isHide, boolean isMult,int MainPos,int SpecialOnePos,int SpecialTwoPos,String colorhex,int conerRadius){
        GameButton KeyButton = new GameButton(getApplicationContext());
        //设置外观以及基本属性
        KeyButton.setText(KeyName);
        KeyButton.setLayoutParams(new ViewGroup.LayoutParams(getPxFromDp(this,KeySizeW),getPxFromDp(this,KeySizeH) ));
        KeyButton.setX(getPxFromDp(this,KeyLX));
        KeyButton.setY(getPxFromDp(this,KeyLY));
        KeyButton.setKeyLX_dp(KeyLX);
        KeyButton.setKeyLY_dp(KeyLY);
        KeyButton.setKeySizeW(KeySizeW);
        KeyButton.setKeySizeH(KeySizeH);
        KeyButton.setKeep(isAutoKeep);
        KeyButton.setHide(isHide);
        KeyButton.setSpecialOne(SpecialOne);
        KeyButton.setSpecialTwo(SpecialTwo);
        KeyButton.setKeyMain(KeyMain);
        KeyButton.setMult(isMult);
        KeyButton.setClickable(true);
        KeyButton.setId(KeyButton.hashCode());
        KeyButton.setGravity(Gravity.CENTER);
        KeyButton.setMainPos(MainPos);
        KeyButton.setSpecialOnePos(SpecialOnePos);
        KeyButton.setSpecialTwoPos(SpecialTwoPos);
        KeyButton.setCornerRadius(conerRadius);
        KeyButton.setColorHex(colorhex);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setColor(ColorUtils.hex2Int(colorhex));
        gradientDrawable.setCornerRadius(conerRadius);
        KeyButton.setBackground(gradientDrawable);

        //设置键索引
        KeyButton.setMainIndex(KeyTool.IndexKeyMap(KeyMain));
        if(isMult){
            KeyButton.setSpecialOneIndex(KeyTool.IndexKeyMap(SpecialOne));
            KeyButton.setSpecialTwoIndex(KeyTool.IndexKeyMap(SpecialTwo));
        }
        return KeyButton;
    }

    public static int getPxFromDp(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getDpFromPx(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return ((int) ((pxValue - 0.5f)/scale))+1;
    }

    private void OnTouchVirtualKeyboard(GameButton gameButton,MotionEvent p2){
        if(p2.getActionMasked() == MotionEvent.ACTION_DOWN){
            if(gameButton.isMult()){
                Log.e("VirtualKey-Mult","KeyName: " + gameButton.getKeyMain() + " " + gameButton.getSpecialOne() + " " + gameButton.getSpecialTwo() +" KeyIndex: " + gameButton.getMainIndex() + " " + gameButton.getSpecialOneIndex() + " " + gameButton.getSpecialTwoIndex()  + " Status: " + "pressed");
                BoatInputEventSender.setKey(gameButton.getMainIndex(), true, 0);
                BoatInputEventSender.setKey(gameButton.getSpecialOneIndex(), true, 0);
                BoatInputEventSender.setKey(gameButton.getSpecialTwoIndex(), true, 0);
            }else{
                Log.e("VirtualKey-Single","KeyName: " + gameButton.getKeyMain() + " KeyIndex: " + gameButton.getMainIndex() + " Status: " + "pressed");
                BoatInputEventSender.setKey(gameButton.getMainIndex(), true, 0);
            }
        }else if (p2.getActionMasked() == MotionEvent.ACTION_UP){
            if(gameButton.isMult()){
                Log.e("VirtualKey-Mult","KeyName: " + gameButton.getKeyMain() + " " + gameButton.getSpecialOne() + " " + gameButton.getSpecialTwo() +" KeyIndex: " + gameButton.getMainIndex() + " " + gameButton.getSpecialOneIndex() + " " + gameButton.getSpecialTwoIndex()  + " Status: " + "uped");
                BoatInputEventSender.setKey(gameButton.getMainIndex(), false, 0);
                BoatInputEventSender.setKey(gameButton.getSpecialOneIndex(), false, 0);
                BoatInputEventSender.setKey(gameButton.getSpecialTwoIndex(), false, 0);
            }else{
                Log.e("VirtualKey-Single","KeyName: " + gameButton.getKeyMain() + " KeyIndex: " + gameButton.getMainIndex() + " Status: " + "uped");
                BoatInputEventSender.setKey(gameButton.getMainIndex(), false, 0);
            }
        }
    }

    private void OnTouchVirtualMouse(MotionEvent p2){
        if (mode) {
            switch (p2.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = (int) p2.getX();
                    initialY = (int) p2.getY();
                case MotionEvent.ACTION_MOVE:
                    BoatInputEventSender.setPointer(baseX + (int) p2.getX() - initialX, baseY + (int) p2.getY() - initialY);
                    break;
                case MotionEvent.ACTION_UP:
                    baseX += ((int) p2.getX() - initialX);
                    baseY += ((int) p2.getY() - initialY);

                    BoatInputEventSender.setPointer(baseX, baseY);
                    break;
                default:
                    break;
            }
        } else {
            baseX = (int) p2.getX();
            baseY = (int) p2.getY();
            BoatInputEventSender.setPointer(baseX, baseY);
        }
        mouseCursor.setX(p2.getX());
        mouseCursor.setY(p2.getY());
    }

    private void OnTouchMouseKey(MouseButton p1,MotionEvent p2){
        if(p2.getActionMasked() == MotionEvent.ACTION_DOWN){
            Log.e("ItemButton","MouseName: " + p1.getMouseName() + " MouseIndex: " + p1.getMouseIndex() + " pressed");
            BoatInputEventSender.setMouseButton(p1.getMouseIndex(), true);
        }else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
            Log.e("ItemButton","MouseName: " + p1.getMouseName() + " MouseIndex: " + p1.getMouseIndex() + " uped");
            BoatInputEventSender.setMouseButton(p1.getMouseIndex(), false);
        }
    }

    private void OnTouchItemButton(ItemButton p1,MotionEvent p2){
        if(p2.getActionMasked() == MotionEvent.ACTION_DOWN){
            Log.e("ItemButton","KeyName: " + p1.getButtonName() + " KeyIndex: " + p1.getButtonIndex() + " pressed");
            BoatInputEventSender.setKey(p1.getButtonIndex(),true,0);
        }else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
            Log.e("ItemButton","KeyName: " + p1.getButtonName() + " KeyIndex: " + p1.getButtonIndex() + " uped");
            BoatInputEventSender.setKey(p1.getButtonIndex(),false,0);
        }
    }

    private void OnTouchQwertKeyboard(QwertButton p1,MotionEvent p2){
        if(p2.getActionMasked() == MotionEvent.ACTION_DOWN){
            Log.e("QwertKeyboard","KeyName: " + p1.getButtonName() + " KeyIndex: " + p1.getButtonIndex() + " pressed");
            BoatInputEventSender.setKey(p1.getButtonIndex(),true,0);
        }else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
            Log.e("QwertKeyboard","KeyName: " + p1.getButtonName() + " KeyIndex: " + p1.getButtonIndex() + " uped");
            BoatInputEventSender.setKey(p1.getButtonIndex(),false,0);
        }
    }

    private void OnTouchCrossKey(CrossButton p1,MotionEvent p2){
        if(p2.getActionMasked() == MotionEvent.ACTION_DOWN){
            Log.e("CrossKey","KeyName: " + p1.getButtonName() + " KeyIndex: " + p1.getButtonIndex() + " " + p1.getButtonIndexSec() + " pressed");
            BoatInputEventSender.setKey(p1.getButtonIndex(),true,0);
            BoatInputEventSender.setKey(p1.getButtonIndexSec(),true,0);
        }else if(p2.getActionMasked() == MotionEvent.ACTION_UP){
            Log.e("CrossKey","KeyName: " + p1.getButtonName() + " KeyIndex: " + p1.getButtonIndex() + " " + p1.getButtonIndexSec() + " uped");
            BoatInputEventSender.setKey(p1.getButtonIndex(),false,0);
            BoatInputEventSender.setKey(p1.getButtonIndexSec(),false,0);
        }
    }

    private void OnTouchCrossKey(View p1,MotionEvent p2,boolean a){
        int[] Indexs = ApplyCrossKeyByTouchPosition(CrossKey.findViewById(R.id.crosskey_shift),CrossKey.findViewById(R.id.crosskey_parent),p2);
        switch (p2.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.e("Action","Down");
                for(int temp:Indexs){
                    Log.e("OnTouchCrossKey","ACTION_DOWN " + temp);
                }
                tempCrossKey = Indexs;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("Action","Move");
                SendDownOrUpToCrossKey(Indexs);
                break;
            case MotionEvent.ACTION_UP:
                Log.e("Action","Up");
                for(int temp:Indexs){
                    Log.e("OnTouchCrossKey","ACTION_UP " + temp);
                }
                tempCrossKey = null;
            default:
                break;
        }
    }

    private void OnMoveMouseKey(View p1,MotionEvent p2){
        MoveViewByTouch(p1,MouseKey,p2);
    }

    private void OnMoveCrossKey(Button p1,MotionEvent p2){
        MoveViewByTouch(p1,CrossKey,p2);
    }

    private void OnMoveQwertKeyboard(View p1,MotionEvent p2){
        MoveViewByTouch(p1,QwertKeyboard,p2);
    }

    private void MoveViewByTouch(View p1,View p2, MotionEvent p3){
        switch(p3.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!layoutsPos.containsKey(p2)){
                    layoutsPos.put(p2,(new int[]{(int)p3.getRawX(),(int)p3.getRawY()}));
                }else{
                    layoutsPos.remove(p2);
                    layoutsPos.put(p2,(new int[]{(int)p3.getRawX(),(int)p3.getRawY()}));
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) p3.getRawX() - layoutsPos.get(p2)[0];
                int dy = (int) p3.getRawY() - layoutsPos.get(p2)[1];
                int l = p2.getLeft() + dx;
                int b = p2.getBottom() + dy;
                int r = p2.getRight() + dx;
                int t = p2.getTop() + dy;
                //下面判断移动是否超出屏幕
                if(l < 0){
                    l = 0;
                    r = l + p2.getWidth();
                }
                if(t < 0){
                    t = 0;
                    b = t+ p2.getHeight();
                }
                if(r > screenWidth){
                    r = screenWidth;
                    l = r - p2.getWidth();
                }
                if(b > screenHeight){
                    b = screenHeight;
                    t = b - p2.getHeight();
                }
                p2.layout(l,t,r,b);
                layoutsPos.remove(p2);
                layoutsPos.put(p2,(new int[]{(int)p3.getRawX(),(int)p3.getRawY()}));
                p2.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
    }

    //p1传入中间位置按键 p2传入corsskey p3传入触摸事件
    public int[] ApplyCrossKeyByTouchPosition(View p1,View p2,MotionEvent p3){
        int[] initPos = new int[2];
        p2.getLocationOnScreen(initPos);
        int[] changPos = {(int) p3.getRawX() - initPos[0],(int) p3.getRawY() - initPos[1]};
        int[] targetPos = new int[2];
        p1.getLocationOnScreen(targetPos);
        Log.e("CrossKeyTouchDebug","TouchX: " + p3.getRawX() + " TouchY: " + p3.getRawY());
        Log.e("CrossKeyTochDebug","ChangeX " + changPos[0] + " ChangeY: " + changPos[1]);
        //自左向右，第一列
        if(changPos[0] < targetPos[0] - initPos[0] && changPos[0] >= 0){
            if(changPos[1] < targetPos[1] - initPos[1] && changPos[1] >= 0){
                //左上
                Log.e("CrossKey","Up-Left");
                if(p3.getAction() != MotionEvent.ACTION_MOVE) {
                    ReflectCrossKeyToScreen(new View[]{}, p3);
                }
                return (new int[]{GLFW_KEY_W,GLFW_KEY_A});
            }else if(changPos[1] <= targetPos[1] + p1.getHeight() - initPos[1] && changPos[1] >= targetPos[1] -initPos[1]){
                //左中
                Log.e("CrossKey","Center-Left");
                ReflectCrossKeyToScreen(new View[]{crosskeychildren[0],crosskeychildren[2]},p3);
                return (new int[]{GLFW_KEY_A});
            }else if(changPos[1] > 0 && changPos[1] > changPos[1] + p1.getHeight() - initPos[1] && changPos[1] <= p2.getHeight()){
                //左下
                Log.e("CrossKey","Down-Left");
                if(p3.getAction() != MotionEvent.ACTION_MOVE) {
                    ReflectCrossKeyToScreen(new View[]{}, p3);
                }
                return (new int[]{GLFW_KEY_S,GLFW_KEY_A});
            }else{
                SendDownOrUpToCrossKey(new int[]{});
                MotionEvent p4 = p3;
                p4.setAction(MotionEvent.ACTION_UP);
                ReflectCrossKeyToScreen(new View[]{},p4);
            }
            //第二列
        }else if(changPos[0] <= targetPos[0] + p1.getWidth() - initPos[0] && changPos[0] >= targetPos[0] - initPos[0]){
            if(changPos[1] < targetPos[1] - initPos[1] && changPos[1] >= 0){
                //上
                Log.e("CrossKey","Up");
                ReflectCrossKeyToScreen(new View[]{crosskeychildren[0],crosskeychildren[1]},p3);
                return (new int[]{GLFW_KEY_W});
            }else if(changPos[1] <= targetPos[1] + p1.getHeight() - initPos[1] && changPos[1] >= targetPos[1] -initPos[1]){
                //中
                Log.e("CrossKey","Center");
                ReflectCrossKeyToScreen(new View[]{},p3);
                if(p3.getAction() == MotionEvent.ACTION_MOVE){
                    return(new int[]{});
                }else{
                    return (new int[]{GLFW_KEY_LEFT_SHIFT});
                }
            }else if(changPos[1] > 0 && changPos[1] > changPos[1] + p1.getHeight() - initPos[1] && changPos[1] <= p2.getHeight()){
                //下
                Log.e("CrossKey","Down");
                ReflectCrossKeyToScreen(new View[]{crosskeychildren[2],crosskeychildren[3]},p3);
                return (new int[]{GLFW_KEY_S});
            }else{
                SendDownOrUpToCrossKey(new int[]{});
                p3.setAction(MotionEvent.ACTION_UP);
                ReflectCrossKeyToScreen(new View[]{},p3);
            }
            //第三列
        }else if(changPos[0] > targetPos[0] + p1.getWidth() - initPos[0] && changPos[0] <= p2.getWidth()){
            if(changPos[1] < targetPos[1] - initPos[1] && changPos[1] >= 0){
                //右上
                Log.e("CrossKey","Up-Right");
                if(p3.getAction() != MotionEvent.ACTION_MOVE) {
                    ReflectCrossKeyToScreen(new View[]{}, p3);
                }
                return (new int[]{GLFW_KEY_W,GLFW_KEY_D});
            }else if(changPos[1] <= targetPos[1] + p1.getHeight() - initPos[1] && changPos[1] >= targetPos[1] -initPos[1]){
                //右中
                Log.e("CrossKey","Right");
                ReflectCrossKeyToScreen(new View[]{crosskeychildren[1],crosskeychildren[3]},p3);
                return (new int[]{GLFW_KEY_D});
            }else if(changPos[1] > 0 && changPos[1] > changPos[1] + p1.getHeight() - initPos[1] && changPos[1] <= p2.getHeight()){
                //右下
                Log.e("CrossKey","Down-Right");
                if(p3.getAction() != MotionEvent.ACTION_MOVE) {
                    ReflectCrossKeyToScreen(new View[]{}, p3);
                }
                return (new int[]{GLFW_KEY_S,GLFW_KEY_D});
            }else{
                SendDownOrUpToCrossKey(new int[]{});
                MotionEvent p4 = p3;
                p4.setAction(MotionEvent.ACTION_UP);
                ReflectCrossKeyToScreen(new View[]{},p4);
            }
        }else{
            SendDownOrUpToCrossKey(new int[]{});
            MotionEvent p4 = p3;
            p4.setAction(MotionEvent.ACTION_UP);
            ReflectCrossKeyToScreen(new View[]{},p4);
        }
        return (new int[]{});
    }

    private void ReflectCrossKeyToScreen(View[] views,MotionEvent p1){
        switch(p1.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                for(View v1:crosskeychildren){
                    v1.setVisibility(View.INVISIBLE);
                    for(View v2:views){
                        if(v1 == v2){
                            v1.setVisibility(View.VISIBLE);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                for(View v1:crosskeychildren){
                    v1.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private void SendDownOrUpToCrossKey(int[] downKeys){
        if(tempCrossKey == null){
            tempCrossKey = downKeys;
        }else if(Arrays.equals(tempCrossKey,downKeys)){
            Log.e("CrossKeyDebug","KeepPressed.");
            return;
        }else{
            for(int temp:tempCrossKey){
                Log.e("CrossKeyDebug","Release Index: " + temp);
            }
            tempCrossKey = downKeys;
        }
        for(int temp: downKeys){
            Log.e("CrossKeyDebug","Catch Index: "+temp);
        }
    }



    private CompoundButton.OnCheckedChangeListener checkedlistener = new CompoundButton.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView,boolean ischecked){
            if (buttonView.getId() == R.id.checkbox_QwertKeyboard) {
                if(ischecked){
                    QwertKeyboard.setVisibility(View.VISIBLE);
                }else{
                    QwertKeyboard.setVisibility(View.GONE);
                }
            }else if(buttonView.getId() == R.id.checkbox_CrossKey){
                if(ischecked){
                    CrossKey.setVisibility(View.VISIBLE);
                }else{
                    CrossKey.setVisibility(View.GONE);
                }
            }else if(buttonView.getId() == R.id.checkbox_VirtualKeyboard){
                if(ischecked){
                    for(GameButton button:KeyboardList){
                        button.setVisibility(View.VISIBLE);
                    }
                }else{
                    for(GameButton button:KeyboardList){
                        button.setVisibility(View.GONE);
                    }
                }
            }else if(buttonView.getId() == R.id.checkbox_MouseKey){
                if(ischecked){
                    MouseKey.setVisibility(View.VISIBLE);
                }else{
                    MouseKey.setVisibility(View.GONE);
                }
            }else if(buttonView.getId() == R.id.checkbox_Otg){

            }
        }
    };

}

