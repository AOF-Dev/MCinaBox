package com.aof.mcinabox.definitions.id.key;

public interface KeyEvent {
    //Define Type ID
    int KEYBOARD_BUTTON = 11;
    int MOUSE_BUTTON = 12;
    int MOUSE_POINTER = 13;
    int TYPE_WORDS = 14;

    int TO_LWJGL_KEY = 21;
    int TO_GLFW_KEY = 22;
    int TO_X_KEY = 23;

    String MARK_KEYNAME_SPLIT = "\\|";
    String MARK_KEYNAME_SPLIT_STRING = "|";
}
