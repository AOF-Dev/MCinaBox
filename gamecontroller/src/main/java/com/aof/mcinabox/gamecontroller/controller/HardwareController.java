package com.aof.mcinabox.gamecontroller.controller;

import android.content.Context;
import android.hardware.input.InputManager;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.aof.mcinabox.definitions.id.AppEvent;
import com.aof.mcinabox.gamecontroller.client.ClientInput;
import com.aof.mcinabox.gamecontroller.codes.AndroidKeyMap;
import com.aof.mcinabox.gamecontroller.codes.Translation;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.HwInput;
import com.aof.mcinabox.gamecontroller.input.Input;
import com.aof.mcinabox.gamecontroller.input.otg.JoyStick;
import com.aof.mcinabox.gamecontroller.input.otg.Keyboard;
import com.aof.mcinabox.gamecontroller.input.otg.Mouse;
import com.aof.mcinabox.gamecontroller.input.otg.Phone;

import java.util.ArrayList;

public class HardwareController extends BaseController implements AppEvent, View.OnHoverListener, HwController {

    private AndroidKeyMap androidKeyMap = new AndroidKeyMap();
    private HwInput keyboard;
    private HwInput phone;
    private HwInput mouse;
    private HwInput joystick;
    private Context mContext;
    private Translation mTranslation;
    private final static String TAG = "HardwareController";

    public HardwareController(Context context, ClientInput client, int transType) {
        super(context, client);
        this.mContext = context;
        checkInputDevices();

        //初始化键值翻译器
        this.mTranslation = new Translation(transType);

        //初始化Input
        keyboard = new Keyboard();
        phone = new Phone();
        mouse = new Mouse();
        joystick = new JoyStick();

        //添加Input
        for (Input i : new Input[]{keyboard, phone, mouse, joystick}) {
            addInput(i);
            //直接启用
            i.setEnable(true);
        }


    }

    @Override
    public void sendKey(BaseKeyEvent event) {
        toLog(event);
        switch (event.getType()) {
            case KEYBOARD_BUTTON:
            case MOUSE_BUTTON:
                String KeyName = event.getKeyName();
                String[] strs = KeyName.split(MARK_KEYNAME_SPLIT);
                for (String str : strs) {
                    sendKeyEvent(new BaseKeyEvent(event.getTag(), str, event.isPressed(), event.getType(), event.getPointer()));
                }
                break;
            case MOUSE_POINTER:
                sendKeyEvent(event);
            case TYPE_WORDS:
                sendKeyEvent(event);
                break;
            default:
                break;
        }
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
                    client.setMousePoniter(e.getPointer()[0], e.getPointer()[1]);
                }
                break;
            case TYPE_WORDS:
                client.typeWords(e.getChars());
            default:
        }
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        return false;
    }

    //写按键事件的分配方式
    //注意每一种输入方式的优先级
    @Override
    public void dispatchKeyEvent(KeyEvent event) {
        if (event == null) return;
        if ((event.getDevice().getSources() & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE) {
            for (HwInput hwi : new HwInput[]{mouse}) {
                if (hwi.isEnable() && hwi.onKey(event)) {
                    return;
                }
            }
        } else if ((event.getDevice().getSources() & InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD) {
            for (HwInput hwi : new HwInput[]{phone, keyboard}) {
                if (hwi.isEnable() && hwi.onKey(event)) {
                    return;
                }
            }
        }
    }

    @Override
    public void dispatchMotionKeyEvent(MotionEvent event) {
        if ((event.getDevice().getSources() & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE) {
            for (HwInput hwi : new HwInput[]{mouse}) {
                if (hwi.isEnable() && hwi.onMotionKey(event)) {
                    return;
                }
            }
        } else if ((event.getDevice().getSources() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
            for (HwInput hwi : new HwInput[]{joystick}) {
                if (hwi.isEnable() && hwi.onMotionKey(event)) {
                    return;
                }
            }
        }
    }

    private void checkInputDevices() {
        InputManager inputManager = (InputManager) mContext.getSystemService(Context.INPUT_SERVICE);
        int[] inputDeviceIds = inputManager.getInputDeviceIds();
        ArrayList<InputDevice> inputDevices = new ArrayList<>();
        for (int id : inputDeviceIds) {
            inputDevices.add(inputManager.getInputDevice(id));
        }
        StringBuilder stb = new StringBuilder("Devices:\n");
        for (InputDevice i : inputDevices) {
            stb.append(String.format("name: %s \n information: %s \n", i.getName(), i.toString()));
        }
        Log.e(TAG, stb.toString());
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
            default:
                info = "Unknown Type: ";
        }
        Log.e(event.getTag(), info);
    }
}
