package com.aof.mcinabox.gamecontroller.codes;

import com.aof.mcinabox.definitions.id.AppEvent;

public class Translation implements AppEvent {

    private LwjglKeyMap lwjglKeyTrans;
    private GlfwKeyMap glfwKeyTrans;
    private XKeyMap xKeyMap;
    private int mode;

    public Translation(int mode){
        lwjglKeyTrans = new LwjglKeyMap();
        glfwKeyTrans = new GlfwKeyMap();
        xKeyMap = new XKeyMap();
        this.mode = mode;
    }

    public int trans(String s){
        switch (mode){
            case TO_LWJGL_KEY:
                return lwjglKeyTrans.translate(s);
            case TO_GLFW_KEY:
                return glfwKeyTrans.translate(s);
            case TO_X_KEY:
                return xKeyMap.translate(s);
            default:
                return -1;
        }
    }
}
