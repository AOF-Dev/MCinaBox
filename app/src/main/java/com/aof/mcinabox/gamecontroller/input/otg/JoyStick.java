package com.aof.mcinabox.gamecontroller.input.otg;

import android.content.Context;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.input.HwInput;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_POINTER;

public class JoyStick implements HwInput {

    private final static String TAG = "JoyStick";

    private Context mContext;
    private Controller mController;
    private boolean enable;

    private static final int type1 = KEYBOARD_BUTTON;
    private static final int type2 = MOUSE_POINTER;

    @Override
    public boolean onKey(KeyEvent event) {
        return false;
    }

    @Override
    public boolean onMotionKey(MotionEvent event) {
        Log.e(TAG, event.toString());
        Log.e(TAG, event.getDevice().toString());
        return true;
    }

    @Override
    public int getSource() {
        return InputDevice.SOURCE_JOYSTICK;
    }

    @Override
    public boolean load(Context context, Controller controller) {
        this.mContext = context;
        this.mController = controller;
        return true;
    }

    @Override
    public boolean unload() {
        return true;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {

    }

    @Override
    public void runConfigure() {

    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean isEnable() {
        return this.enable;
    }
}
