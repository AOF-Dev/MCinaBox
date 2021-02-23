package com.aof.mcinabox.gamecontroller.event;

import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public String toString(){
        return String.format("BaseKeyEvent { tag = \"%s\", keyName = \"%s\", pressed = %s, type = %s, pointer = %s }", this.tag, this.keyName, this.pressed, this.type, "[0]: " + this.mPointer[0] + "[1]: " + this.mPointer[1]);
    }
}
