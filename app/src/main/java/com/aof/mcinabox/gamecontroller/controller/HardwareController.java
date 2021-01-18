package com.aof.mcinabox.gamecontroller.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

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
    private USBDeviceReceiver mUsbReceiver;

    private final static int INPUT_DEVICE_MOUSE = 101;
    private final static int INPUT_DEVICE_KEYBOARD = 102;
    private final static int INPUT_DEVICE_GAMEPAD = 103;
    private final static int INPUT_DEVICE_UNKNOWN = 109;

    public HardwareController(Client client, int transType) {
        super(client, false);
        //printInputDevices();

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
        }

        //按需启用Input
        refreshInputs();

        //注册广播接收器
        registerReceiver();

    }

    @Override
    public void sendKey(BaseKeyEvent event) {
        toLog(event);
        switch (event.getType()) {
            case KEYBOARD_BUTTON:
            case MOUSE_BUTTON:
                String KeyName = event.getKeyName();
                if (KeyName == null) return;
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event == null) return false;
        switch (distinguishInputDevices(event.getDevice())) {
            case HardwareController.INPUT_DEVICE_MOUSE:
                for (HwInput hwi : new HwInput[]{mouse}) {
                    if (hwi.isEnabled() && hwi.onKey(event)) {
                        return true;
                    }
                }
                break;
            case HardwareController.INPUT_DEVICE_KEYBOARD:
                for (HwInput hwi : new HwInput[]{phone, keyboard}) {
                    if (hwi.isEnabled() && hwi.onKey(event)) {
                        return true;
                    }
                }
                break;
            case HardwareController.INPUT_DEVICE_GAMEPAD:
                for (HwInput hwi : new HwInput[]{gamepad}) {
                    if (hwi.isEnabled() && hwi.onKey(event)) {
                        return true;
                    }
                }
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public boolean dispatchMotionKeyEvent(MotionEvent event) {
        if (event == null) return false;
        switch (distinguishInputDevices(event.getDevice())) {
            case HardwareController.INPUT_DEVICE_MOUSE:
                for (HwInput hwi : new HwInput[]{mouse}) {
                    if (hwi.isEnabled() && hwi.onMotionKey(event)) {
                        return true;
                    }
                }
                break;
            case HardwareController.INPUT_DEVICE_GAMEPAD:
                for (HwInput hwi : new HwInput[]{gamepad}) {
                    if (hwi.isEnabled() && hwi.onMotionKey(event)) {
                        return true;
                    }
                }
                break;
            default:
                return false;
        }
        return false;
    }

    private void printInputDevices() {
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

    private void refreshInputs() {
        InputManager inputManager = (InputManager) client.getActivity().getSystemService(Context.INPUT_SERVICE);
        int[] inputDeviceIds = inputManager.getInputDeviceIds();
        for (Input i : this.inputs) {
            i.setEnabled(false);
        }
        for (int id : inputDeviceIds) {
            switch (distinguishInputDevices(inputManager.getInputDevice(id))) {
                case HardwareController.INPUT_DEVICE_MOUSE:
                    this.mouse.setEnabled(true);
                    break;
                case HardwareController.INPUT_DEVICE_GAMEPAD:
                    this.gamepad.setEnabled(true);
                    break;
                case HardwareController.INPUT_DEVICE_KEYBOARD:
                    this.keyboard.setEnabled(true);
                    this.phone.setEnabled(true);
                    break;
                default:
            }
        }
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

    public static int distinguishInputDevices(InputDevice device) {
        if ((device.getSources() & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE && (device.getSources() & (InputDevice.SOURCE_KEYBOARD | InputDevice.SOURCE_JOYSTICK)) == 0) {
            return HardwareController.INPUT_DEVICE_MOUSE;
        } else if ((device.getSources() & InputDevice.SOURCE_KEYBOARD) != 0 && (device.getKeyboardType() == InputDevice.KEYBOARD_TYPE_ALPHABETIC)) {
            return HardwareController.INPUT_DEVICE_KEYBOARD;
        } else if (((device.getSources() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) && ((device.getSources() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK)) {
            return HardwareController.INPUT_DEVICE_GAMEPAD;
        } else return HardwareController.INPUT_DEVICE_UNKNOWN;
    }

    @Override
    public void onStop() {
        super.onStop();
        //注销广播接收器
        unregisterReceiver();
    }

    @Override
    public void onResumed() {
        super.onResumed();
        //注册广播接收器
        registerReceiver();
    }

    private void registerReceiver() {
        if (mUsbReceiver != null) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mUsbReceiver = new USBDeviceReceiver();
        this.client.getActivity().registerReceiver(mUsbReceiver, filter);
    }

    private void unregisterReceiver() {
        if (mUsbReceiver != null) {
            this.client.getActivity().unregisterReceiver(mUsbReceiver);
            mUsbReceiver = null;
        }

    }

    public class USBDeviceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            HardwareController.this.refreshInputs();
        }
    }
}
