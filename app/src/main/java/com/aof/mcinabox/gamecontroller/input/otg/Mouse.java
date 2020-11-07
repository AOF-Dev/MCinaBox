package com.aof.mcinabox.gamecontroller.input.otg;

import android.content.Context;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.HwInput;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_POINTER;

public class Mouse implements HwInput {

    private final static String TAG = "Mouse";
    private final static int type2 = MOUSE_BUTTON;
    private final static int type1 = MOUSE_POINTER;

    private Context mContext;
    private Controller mController;
    private boolean enable;

    @Override
    public boolean onKey(KeyEvent event) {
        Log.e(TAG, event.toString());
        Log.e(TAG, event.getDevice().toString());
        return true;
    }

    @Override
    public boolean onMotionKey(MotionEvent event) {
        Log.e(TAG, event.toString());
        Log.e(TAG, event.getDevice().toString());
        switch (event.getAction()) {
            case MotionEvent.ACTION_HOVER_MOVE:
                sendPointer((int) event.getX(), (int) event.getY());
                break;
        }
        return true;
    }

    @Override
    public int getSource() {
        return InputDevice.SOURCE_MOUSE;
    }

    @Override
    public boolean load(Context context, Controller controller) {
        this.mContext = context;
        this.mController = controller;
        return true;
    }

    @Override
    public boolean unload() {
        return false;
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

    private void sendPointer(int x, int y) {
        mController.sendKey(new BaseKeyEvent(TAG, null, false, type1, new int[]{x, y}));
    }

}
