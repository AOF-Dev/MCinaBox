package com.aof.mcinabox.gamecontroller.codes;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.ANDROID_TO_KEYMAP;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYMAP_TO_GLFW;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYMAP_TO_LWJGL;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYMAP_TO_X;

public class Translation {

    private final CoKeyMap lwjglKeyTrans;
    private final CoKeyMap glfwKeyTrans;
    private final CoKeyMap xKeyMap;
    private final CoKeyMap aKeyMap;
    private int mode;

    public Translation(int mode) {
        lwjglKeyTrans = new LwjglKeyMap();
        glfwKeyTrans = new GlfwKeyMap();
        xKeyMap = new XKeyMap();
        aKeyMap = new AndroidKeyMap();
        this.mode = mode;
    }

    public int trans(String s) {
        switch (mode) {
            case KEYMAP_TO_LWJGL:
                return (int) lwjglKeyTrans.translate(s);
            case KEYMAP_TO_GLFW:
                return (int) glfwKeyTrans.translate(s);
            case KEYMAP_TO_X:
                return (int) xKeyMap.translate(s);
            default:
                return -1;
        }
    }

    public String trans(int i) {
        switch (mode) {
            case ANDROID_TO_KEYMAP:
                return (String) aKeyMap.translate(i);
            default:
                return null;
        }
    }

    public Translation setMode(int mode) {
        this.mode = mode;
        return this;
    }
}
