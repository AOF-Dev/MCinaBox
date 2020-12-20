package com.aof.mcinabox.gamecontroller.input.otg;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.aof.mcinabox.R;
import com.aof.mcinabox.gamecontroller.codes.Translation;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.definitions.map.KeyMap;
import com.aof.mcinabox.gamecontroller.definitions.map.MouseMap;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.HwInput;
import com.aof.mcinabox.utils.DisplayUtils;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.ANDROID_TO_KEYMAP;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_POINTER;

public class Mouse implements HwInput {

    private final static String TAG = "Mouse";
    private final static int type2 = MOUSE_BUTTON;
    private final static int type1 = MOUSE_POINTER;
    private final static int CURSOR_SIZE = 16; //dp
    private final static int CURSOR_EXTRA = 2; //D (x)

    private Context mContext;
    private Controller mController;
    private boolean enable = false;
    private boolean grabbed = false;
    private Translation mTrans;
    private Object mCapturedPointerListener;
    private ImageView cursor;
    private int grabbed_x = 0;
    private int grabbed_y = 0;
    private int screenWidth;
    private int screenHeight;

    @Override
    public int getSource() {
        return InputDevice.SOURCE_MOUSE;
    }

    @Override
    public boolean load(Context context, Controller controller) {
        this.mContext = context;
        this.mController = controller;
        //初始化键值翻译器
        mTrans = new Translation(ANDROID_TO_KEYMAP);
        //设定鼠标监听器（SDK >= 26）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mCapturedPointerListener = new View.OnCapturedPointerListener(){
                @Override
                public boolean onCapturedPointer(View view, MotionEvent event) {
                    Mouse.this.onMotionKey(event);
                    return true;
                }
            };
            mController.getClient().getViewsParent().setOnCapturedPointerListener((View.OnCapturedPointerListener) mCapturedPointerListener);
        }
        //添加鼠标图标
        cursor = new ImageView(mContext);
        cursor.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(mContext, CURSOR_SIZE), DisplayUtils.getPxFromDp(mContext, CURSOR_SIZE)));
        cursor.setImageResource(R.drawable.cursor);
        mController.getClient().addView(cursor);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        return true;
    }

    @Override
    public boolean unload() {
        return false;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        this.grabbed = isGrabbed;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //如果sdk版本大于O，则对鼠标获取或释放进行相应的操作
            View v = mController.getClient().getViewsParent();
            if(!v.hasPointerCapture()){
                //捕获模式下，请求鼠标独占
                v.setFocusable(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.requestPointerCapture();
                    }
                }, 200);
            }
            //鼠标图标的显示或隐藏
            if(grabbed)
                cursor.setVisibility(View.INVISIBLE);
            else
                cursor.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
        if(grabbed)
            cursor.setVisibility(View.INVISIBLE);
        else
            cursor.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean isEnable() {
        return this.enable;
    }


    //私有事件封装
    private void sendPointer(int x, int y) {
        mController.sendKey(new BaseKeyEvent(TAG, null, false, type1, new int[]{x, y}));
    }

    private void sendKeyEvent(String keyName, boolean pressed, int type){
        if(keyName == null) return;
        mController.sendKey(new BaseKeyEvent(TAG, keyName, pressed, type,null));
    }

    //配置信息处理
    @Override
    public void runConfigure() {

    }

    @Override
    public void saveConfig() {

    }

    //事件分发处理
    @Override
    public boolean onKey(KeyEvent event) {
        Log.e(TAG, event.toString());
        Log.e(TAG, event.getDevice().toString());
        whenKeyPress(event);
        return true;
    }



    @Override
    public boolean onMotionKey(MotionEvent event) {
        //Log.e(TAG, event.toString());
        //Log.e(TAG, event.getDevice().toString());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            doMotion(event, grabbed);
        }
        return true;
    }

    //主要的控制逻辑处理
    private void doMotion(MotionEvent event, boolean grabbed){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }

        switch(event.getActionMasked()){
            case MotionEvent.ACTION_BUTTON_PRESS:
                sendKeyEvent(mapCovert(event.getActionButton()), true, type2);
                break;
            case MotionEvent.ACTION_BUTTON_RELEASE:
                sendKeyEvent(mapCovert(event.getActionButton()), false, type2);
                break;
            case MotionEvent.ACTION_SCROLL:
                String keyName;
                if(event.getAxisValue(MotionEvent.AXIS_VSCROLL) > 0){
                    keyName = MouseMap.MOUSEMAP_WHEEL_UP;
                }else{
                    keyName = MouseMap.MOUSEMAP_WHEEL_DOWN;
                }
                sendKeyEvent(keyName, true, type2);
                sendKeyEvent(keyName, false, type2);
                break;
            case MotionEvent.ACTION_HOVER_ENTER:
            case MotionEvent.ACTION_HOVER_MOVE:
            case MotionEvent.ACTION_HOVER_EXIT:
                sendPointer((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if(grabbed){
                    sendPointer((int)event.getAxisValue(MotionEvent.AXIS_X) + mController.getPointer()[0], (int)event.getAxisValue(MotionEvent.AXIS_Y) + mController.getPointer()[1]);
                }else{
                    int x, y;
                    x = grabbed_x + (int)event.getAxisValue(MotionEvent.AXIS_X) * CURSOR_EXTRA;
                    y = grabbed_y + (int)event.getAxisValue(MotionEvent.AXIS_Y) * CURSOR_EXTRA;
                    if(x < 0 || y < 0 || x > screenWidth || y > screenHeight)
                        return;
                    else{
                        cursor.setX(x);
                        cursor.setY(y);
                        sendPointer(x, y);
                        this.grabbed_x = x;
                        this.grabbed_y = y;
                    }

                }
                break;
        }

    }

    private String mapCovert(int actionButton){
        switch(actionButton){
            case MotionEvent.BUTTON_PRIMARY:
                return MouseMap.MOUSEMAP_BUTTON_LEFT;
            case MotionEvent.BUTTON_TERTIARY:
                return MouseMap.MOUSEMAP_BUTTON_MIDDLE;
            case MotionEvent.BUTTON_SECONDARY:
                return MouseMap.MOUSEMAP_BUTTON_RIGHT;
            default:
                return null;
        }
    }

    public void whenKeyPress(KeyEvent event){
        //鼠标不处于捕获状态时，才能够产生KeyEvent事件
        // TODO:这里不确定是否会产生KeyEvent，一般来说产生的是TouchEvent，需要测试
        boolean pressed = false;
        switch(event.getAction()){
            case KeyEvent.ACTION_DOWN:
            case KeyEvent.ACTION_UP:
                pressed = true;
                sendKeyEvent(mTrans.trans(event.getKeyCode()),pressed, type2);
                break;
            default:
        }
    }

}
