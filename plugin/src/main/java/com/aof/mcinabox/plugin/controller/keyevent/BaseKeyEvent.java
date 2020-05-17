package com.aof.mcinabox.plugin.controller.keyevent;

public class BaseKeyEvent implements Event {
    private String tag;
    private String keyName;
    private int keyCode;
    private boolean pressed;
    private int type;
    private int[] mPointer;

    public BaseKeyEvent(String tag, String keyName, int keyCode, boolean pressed, int type, int[] mPointer){
        this.tag = tag;
        this.keyName = keyName;
        this.keyCode = keyCode;
        this.pressed = pressed;
        this.type = type;
        this.mPointer = mPointer;
    }

    public String getTag() {
        return tag;
    }

    public String getKeyName() {
        return keyName;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public boolean isPressed() {
        return pressed;
    }

    public int getType() {
        return type;
    }

    public int[] getPointer() {
        return mPointer;
    }
}
