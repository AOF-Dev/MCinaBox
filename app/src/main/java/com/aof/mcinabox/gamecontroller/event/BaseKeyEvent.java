package com.aof.mcinabox.gamecontroller.event;

public class BaseKeyEvent {
    private final String tag;
    private final String keyName;
    private final boolean pressed;
    private final int type;
    private final int[] mPointer;
    private String chars;

    public BaseKeyEvent(String tag, String keyName, boolean pressed, int type, int[] mPointer) {
        this.tag = tag;
        this.keyName = keyName;
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

    public boolean isPressed() {
        return pressed;
    }

    public int getType() {
        return type;
    }

    public int[] getPointer() {
        return mPointer;
    }

    public String getChars() {
        return chars;
    }

    public BaseKeyEvent setChars(String str) {
        this.chars = str;
        return this;
    }
}
