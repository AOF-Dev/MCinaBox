package com.aof.mcinabox.gamecontroller.event;

import cosine.boat.definitions.id.AppEvent;

public class BaseKeyEvent implements AppEvent {
    private String tag;
    private String keyName;
    private boolean pressed;
    private int type;
    private int[] mPointer;
    private String chars;

    public BaseKeyEvent(String tag, String keyName, boolean pressed, int type, int[] mPointer){
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

    public String getChars(){
        return chars;
    }

    public BaseKeyEvent setChars(String str){
        this.chars = str;
        return this;
    }
}
