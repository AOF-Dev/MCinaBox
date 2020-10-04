package com.aof.mcinabox.gamecontroller.codes;

import com.aof.mcinabox.definitions.id.AppEvent;

public class Translation implements AppEvent {

    private LwjglKeyMap lwjglKeyTrans;
    private GlfwKeyMap glfwKeyTrans;
    private XKeyMap xKeyMap;
    private AndroidKeyMap aKeyMap;
    private int mode;

    public Translation(int mode){
        lwjglKeyTrans = new LwjglKeyMap();
        glfwKeyTrans = new GlfwKeyMap();
        xKeyMap = new XKeyMap();
        aKeyMap = new AndroidKeyMap();
        this.mode = mode;
    }

    public int trans(String s){
        switch (mode){
            case KEYMAP_TO_LWJGL:
                return lwjglKeyTrans.translate(s);
            case KEYMAP_TO_GLFW:
                return glfwKeyTrans.translate(s);
            case KEYMAP_TO_X:
                return xKeyMap.translate(s);
            default:
                return -1;
        }
    }

    public String trans(int i){
        switch (mode){
            case ANDROID_TO_KEYMAP:
                return aKeyMap.translate(i);
            default:
                return null;
        }
    }

    public Translation setMode(int mode){
        this.mode = mode;
        return this;
    }
}
