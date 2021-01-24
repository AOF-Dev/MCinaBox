package com.aof.mcinabox.gamecontroller.input.otg;

import android.content.Context;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.aof.mcinabox.gamecontroller.codes.AndroidKeyMap;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.HwInput;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;

public class Keyboard implements HwInput {

    private Context mContext;
    private Controller mController;
    private AndroidKeyMap androidKeyMap;

    private boolean isEnabled;

    private final static String TAG = "OtgKeyboard";
    private final static int type = KEYBOARD_BUTTON;

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
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
    public void setGrabCursor(boolean isGrabbed) {

    }

    @Override
    public void runConfigure() {

    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    private void sendKeyEvent(String keyName, boolean pressed) {
        mController.sendKey(new BaseKeyEvent(TAG, keyName, pressed, type, null));
    }

    @Override
    public boolean onKey(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_MUTE) {
            return false;
        }
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                if (event.getRepeatCount() == 0) {
                    this.sendKeyEvent((String) androidKeyMap.translate(event.getKeyCode()), true);
                }
                break;
            case KeyEvent.ACTION_UP:
                this.sendKeyEvent((String) androidKeyMap.translate(event.getKeyCode()), false);
                break;
        }
        return true;
    }

    @Override
    public boolean onMotionKey(MotionEvent event) {
        return false;
    }

    @Override
    public int getSource() {
        return InputDevice.SOURCE_KEYBOARD;
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
}
