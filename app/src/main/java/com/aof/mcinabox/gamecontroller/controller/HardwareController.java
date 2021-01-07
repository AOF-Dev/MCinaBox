package com.aof.mcinabox.gamecontroller.controller;

import android.content.Context;
import android.hardware.input.InputManager;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.aof.mcinabox.gamecontroller.client.Client;
import com.aof.mcinabox.gamecontroller.codes.AndroidKeyMap;
import com.aof.mcinabox.gamecontroller.codes.Translation;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.HwInput;
import com.aof.mcinabox.gamecontroller.input.Input;
import com.aof.mcinabox.gamecontroller.input.otg.GamePad;
import com.aof.mcinabox.gamecontroller.input.otg.Keyboard;
import com.aof.mcinabox.gamecontroller.input.otg.Mouse;
import com.aof.mcinabox.gamecontroller.input.otg.Phone;

import java.util.ArrayList;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MARK_KEYNAME_SPLIT;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_POINTER;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_POINTER_INC;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.TYPE_WORDS;

public class HardwareController extends BaseController implements HwController {

    private final static String TAG = "HardwareController";
    private final AndroidKeyMap androidKeyMap = new AndroidKeyMap();
    public HwInput keyboard;
    public HwInput phone;
    public HwInput mouse;
    public HwInput gamepad;
    private final Translation mTranslation;

    public HardwareController(Client client, int transType) {
        super(client);
        checkInputDevices();

        //初始化键值翻译器
        this.mTranslation = new Translation(transType);

        //初始化Input
        keyboard = new Keyboard();
        phone = new Phone();
        mouse = new Mouse();
        gamepad = new GamePad();

        //添加Input
        for (Input i : new Input[]{keyboard, phone, mouse, gamepad}) {
            addInput(i);
            //直接启用
            i.setEnabled(true);
        }


    }

    @Override
    public void sendKey(BaseKeyEvent event) {
        toLog(event);
        switch (event.getType()) {
            case KEYBOARD_BUTTON:
            case MOUSE_BUTTON:
                String KeyName = event.getKeyName();
                if(KeyName == null) return;
                String[] strs = KeyName.split(MARK_KEYNAME_SPLIT);
                for (String str : strs) {
                    sendKeyEvent(new BaseKeyEvent(event.getTag(), str, event.isPressed(), event.getType(), event.getPointer()));
                }
                break;
            case MOUSE_POINTER:
            case MOUSE_POINTER_INC:
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
                break;
            default:
        }
    }

    //写按键事件的分配方式
    //注意每一种输入方式的优先级
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event == null) return false;
        if ((event.getDevice().getSources() & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE && (event.getDevice().getSources() & (InputDevice.SOURCE_KEYBOARD | InputDevice.SOURCE_JOYSTICK)) == 0) {
            for (HwInput hwi : new HwInput[]{mouse}) {
                if (hwi.isEnabled() && hwi.onKey(event)) {
                    return true;
                }
            }
        } else if ((event.getDevice().getSources() & InputDevice.SOURCE_KEYBOARD) != 0 && (event.getDevice().getKeyboardType() == InputDevice.KEYBOARD_TYPE_ALPHABETIC)) {
            for (HwInput hwi : new HwInput[]{phone, keyboard}) {
                if (hwi.isEnabled() && hwi.onKey(event)) {
                    return true;
                }
            }
        } else if (((event.getDevice().getSources() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) && ((event.getDevice().getSources() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
            for (HwInput hwi : new HwInput[]{gamepad}) {
                if (hwi.isEnabled() && hwi.onKey(event)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean dispatchMotionKeyEvent(MotionEvent event) {
        if ((event.getDevice().getSources() & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE && (event.getDevice().getSources() & (InputDevice.SOURCE_KEYBOARD | InputDevice.SOURCE_JOYSTICK)) == 0) {
            for (HwInput hwi : new HwInput[]{mouse}) {
                if (hwi.isEnabled() && hwi.onMotionKey(event)) {
                    return true;
                }
            }
        } else if (((event.getDevice().getSources() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) && ((event.getDevice().getSources() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
            for (HwInput hwi : new HwInput[]{gamepad}) {
                if (hwi.isEnabled() && hwi.onMotionKey(event)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkInputDevices() {
        InputManager inputManager = (InputManager) client.getActivity().getSystemService(Context.INPUT_SERVICE);
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
            case MOUSE_POINTER_INC:
                info = "Type: " + event.getType() + " IncX: " + event.getPointer()[0] + " IncY: " + event.getPointer()[1];
                break;
            default:
                info = "Unknown: " + event.toString();
        }
        Log.e(event.getTag(), info);
    }
}
