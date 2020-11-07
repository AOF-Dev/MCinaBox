package com.aof.mcinabox.gamecontroller.input.otg;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.aof.mcinabox.gamecontroller.codes.Translation;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.definitions.map.KeyMap;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.HwInput;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.ANDROID_TO_KEYMAP;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;

public class Phone implements HwInput {

    private final static String TAG = "Phone";
    private Translation mTrans;
    private Controller mController;
    private final int type = KEYBOARD_BUTTON;
    private Context mContext;
    private boolean enable = false;

    @Override
    public boolean load(Context context, Controller controller) {
        mTrans = new Translation(ANDROID_TO_KEYMAP);
        this.mContext = context;
        this.mController = controller;
        return false;
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

    @Override
    public boolean onKey(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    adjustAudio(AudioManager.ADJUST_RAISE);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    adjustAudio(AudioManager.ADJUST_LOWER);
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (!event.getDevice().isVirtual()) {
                    return false;
                }
                sendKeyEvent(KeyMap.KEYMAP_KEY_ESC, event);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onMotionKey(MotionEvent event) {
        return false;
    }

    @Override
    public int getSource() {
        return InputDevice.SOURCE_KEYBOARD;
    }

    private void sendKeyEvent(KeyEvent event) {
        sendKeyEvent(mTrans.trans(event.getKeyCode()), event);
    }

    private void sendKeyEvent(String keyName, KeyEvent event) {
        boolean pressed;
        switch (event.getAction()) {
            case KeyEvent.ACTION_UP:
                pressed = false;
                break;
            case KeyEvent.ACTION_DOWN:
                pressed = true;
                break;
            default:
                return;

        }
        mController.sendKey(new BaseKeyEvent(TAG, keyName, pressed, type, null));
    }

    private void adjustAudio(int direction) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI);
    }
}
