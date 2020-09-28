package com.aof.mcinabox.gamecontroller.input.otg;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.aof.mcinabox.definitions.id.AppEvent;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.OtgInput;
import com.aof.mcinabox.gamecontroller.codes.AndroidKeyMap;

public class Keyboard implements OtgInput , AppEvent {

    private Context mContext;
    private Controller mController;
    private AndroidKeyMap androidKeyMap;

    private final static String TAG = "OtgKeyboard";
    private final static int type = KEYBOARD_BUTTON;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //对于任何按键，只对第一次的onKeyDown回调做出响应
        //以免长按或短时间内多次按下使pressed被多次执行
        if(event.getRepeatCount() == 0){
            this.sendKeyEvent(androidKeyMap.translate(keyCode),true);
        }
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        this.sendKeyEvent(androidKeyMap.translate(keyCode),true);
        return false;
    }

    @Override
    public boolean isEnable() {
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        return false;
    }

    @Override
    public boolean load(Context context, Controller controller) {

        this.mContext = context;
        this.mController = controller;
        this.androidKeyMap = new AndroidKeyMap();

        return true;
    }

    @Override
    public boolean unload() {
        return true;
    }

    @Override
    public void setInputMode(int inputMode) {
        // to do nothing.
    }

    @Override
    public void runConfigure() {
        Log.e(TAG,"Run Configure.");
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void setEnable(boolean enable) {

    }

    private void sendKeyEvent(String keyName , boolean pressed){
        mController.sendKey(new BaseKeyEvent(TAG,keyName,pressed,type,null));
    }

}
