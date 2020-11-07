package com.aof.mcinabox.gamecontroller.codes;

import com.aof.mcinabox.gamecontroller.definitions.map.KeyMap;

import java.util.HashMap;

import static org.lwjgl.input.Keyboard.*;

public class LwjglKeyMap implements KeyMap {

    private final HashMap<String, Integer> lwjglMap;

    public LwjglKeyMap() {
        lwjglMap = new HashMap<>();
        init();
    }

    private void init() {
        lwjglMap.put(KEYMAP_KEY_0, KEY_0);
        lwjglMap.put(KEYMAP_KEY_1, KEY_1);
        lwjglMap.put(KEYMAP_KEY_2, KEY_2);
        lwjglMap.put(KEYMAP_KEY_3, KEY_3);
        lwjglMap.put(KEYMAP_KEY_4, KEY_4);
        lwjglMap.put(KEYMAP_KEY_5, KEY_5);
        lwjglMap.put(KEYMAP_KEY_6, KEY_6);
        lwjglMap.put(KEYMAP_KEY_7, KEY_7);
        lwjglMap.put(KEYMAP_KEY_8, KEY_8);
        lwjglMap.put(KEYMAP_KEY_9, KEY_9);
        lwjglMap.put(KEYMAP_KEY_A, KEY_A);
        lwjglMap.put(KEYMAP_KEY_B, KEY_B);
        lwjglMap.put(KEYMAP_KEY_C, KEY_C);
        lwjglMap.put(KEYMAP_KEY_D, KEY_D);
        lwjglMap.put(KEYMAP_KEY_E, KEY_E);
        lwjglMap.put(KEYMAP_KEY_F, KEY_F);
        lwjglMap.put(KEYMAP_KEY_G, KEY_G);
        lwjglMap.put(KEYMAP_KEY_H, KEY_H);
        lwjglMap.put(KEYMAP_KEY_I, KEY_I);
        lwjglMap.put(KEYMAP_KEY_J, KEY_J);
        lwjglMap.put(KEYMAP_KEY_K, KEY_K);
        lwjglMap.put(KEYMAP_KEY_L, KEY_L);
        lwjglMap.put(KEYMAP_KEY_M, KEY_M);
        lwjglMap.put(KEYMAP_KEY_N, KEY_N);
        lwjglMap.put(KEYMAP_KEY_O, KEY_O);
        lwjglMap.put(KEYMAP_KEY_P, KEY_P);
        lwjglMap.put(KEYMAP_KEY_Q, KEY_Q);
        lwjglMap.put(KEYMAP_KEY_R, KEY_R);
        lwjglMap.put(KEYMAP_KEY_S, KEY_S);
        lwjglMap.put(KEYMAP_KEY_T, KEY_T);
        lwjglMap.put(KEYMAP_KEY_U, KEY_U);
        lwjglMap.put(KEYMAP_KEY_V, KEY_V);
        lwjglMap.put(KEYMAP_KEY_W, KEY_W);
        lwjglMap.put(KEYMAP_KEY_X, KEY_X);
        lwjglMap.put(KEYMAP_KEY_Y, KEY_Y);
        lwjglMap.put(KEYMAP_KEY_Z, KEY_Z);
        lwjglMap.put(KEYMAP_KEY_MINUS, KEY_MINUS);
        lwjglMap.put(KEYMAP_KEY_EQUALS, KEY_EQUALS);
        lwjglMap.put(KEYMAP_KEY_LBRACKET, KEY_LBRACKET);
        lwjglMap.put(KEYMAP_KEY_RBRACKET, KEY_RBRACKET);
        lwjglMap.put(KEYMAP_KEY_SEMICOLON, KEY_SEMICOLON);
        lwjglMap.put(KEYMAP_KEY_APOSTROPHE, KEY_APOSTROPHE);
        lwjglMap.put(KEYMAP_KEY_GRAVE, KEY_GRAVE);
        lwjglMap.put(KEYMAP_KEY_BACKSLASH, KEY_BACKSLASH);
        lwjglMap.put(KEYMAP_KEY_COMMA, KEY_COMMA);
        lwjglMap.put(KEYMAP_KEY_PERIOD, KEY_PERIOD);
        lwjglMap.put(KEYMAP_KEY_SLASH, KEY_SLASH);
        lwjglMap.put(KEYMAP_KEY_ESC, KEY_ESCAPE);
        lwjglMap.put(KEYMAP_KEY_F1, KEY_F1);
        lwjglMap.put(KEYMAP_KEY_F2, KEY_F2);
        lwjglMap.put(KEYMAP_KEY_F3, KEY_F3);
        lwjglMap.put(KEYMAP_KEY_F4, KEY_F4);
        lwjglMap.put(KEYMAP_KEY_F5, KEY_F5);
        lwjglMap.put(KEYMAP_KEY_F6, KEY_F6);
        lwjglMap.put(KEYMAP_KEY_F7, KEY_F7);
        lwjglMap.put(KEYMAP_KEY_F8, KEY_F8);
        lwjglMap.put(KEYMAP_KEY_F9, KEY_F9);
        lwjglMap.put(KEYMAP_KEY_F10, KEY_F10);
        lwjglMap.put(KEYMAP_KEY_F11, KEY_F11);
        lwjglMap.put(KEYMAP_KEY_F12, KEY_F12);
        lwjglMap.put(KEYMAP_KEY_TAB, KEY_TAB);
        lwjglMap.put(KEYMAP_KEY_BACKSPACE, KEY_BACK);
        lwjglMap.put(KEYMAP_KEY_SPACE, KEY_SPACE);
        lwjglMap.put(KEYMAP_KEY_CAPITAL, KEY_CAPITAL);
        lwjglMap.put(KEYMAP_KEY_ENTER, KEY_RETURN);
        lwjglMap.put(KEYMAP_KEY_LSHIFT, KEY_LSHIFT);
        lwjglMap.put(KEYMAP_KEY_LCTRL, KEY_LCONTROL);
        lwjglMap.put(KEYMAP_KEY_LALT, KEY_LMENU);
        lwjglMap.put(KEYMAP_KEY_RSHIFT, KEY_RSHIFT);
        lwjglMap.put(KEYMAP_KEY_RCTRL, KEY_RCONTROL);
        lwjglMap.put(KEYMAP_KEY_RALT, KEY_RMENU);
        lwjglMap.put(KEYMAP_KEY_UP, KEY_UP);
        lwjglMap.put(KEYMAP_KEY_DOWN, KEY_DOWN);
        lwjglMap.put(KEYMAP_KEY_LEFT, KEY_LEFT);
        lwjglMap.put(KEYMAP_KEY_RIGHT, KEY_RIGHT);
        lwjglMap.put(KEYMAP_KEY_PAGEUP, KEY_PRIOR);
        lwjglMap.put(KEYMAP_KEY_PAGEDOWN, KEY_NEXT);
        lwjglMap.put(KEYMAP_KEY_HOME, KEY_HOME);
        lwjglMap.put(KEYMAP_KEY_END, KEY_END);
        lwjglMap.put(KEYMAP_KEY_INSERT, KEY_INSERT);
        lwjglMap.put(KEYMAP_KEY_DELETE, KEY_DELETE);
        lwjglMap.put(KEYMAP_KEY_PAUSE, KEY_PAUSE);
        lwjglMap.put(KEYMAP_KEY_NUMPAD0, KEY_NUMPAD0);
        lwjglMap.put(KEYMAP_KEY_NUMPAD1, KEY_NUMPAD1);
        lwjglMap.put(KEYMAP_KEY_NUMPAD2, KEY_NUMPAD2);
        lwjglMap.put(KEYMAP_KEY_NUMPAD3, KEY_NUMPAD3);
        lwjglMap.put(KEYMAP_KEY_NUMPAD4, KEY_NUMPAD4);
        lwjglMap.put(KEYMAP_KEY_NUMPAD5, KEY_NUMPAD5);
        lwjglMap.put(KEYMAP_KEY_NUMPAD6, KEY_NUMPAD6);
        lwjglMap.put(KEYMAP_KEY_NUMPAD7, KEY_NUMPAD7);
        lwjglMap.put(KEYMAP_KEY_NUMPAD8, KEY_NUMPAD8);
        lwjglMap.put(KEYMAP_KEY_NUMPAD9, KEY_NUMPAD9);
        lwjglMap.put(KEYMAP_KEY_NUMLOCK, KEY_NUMLOCK);
        lwjglMap.put(KEYMAP_KEY_SCROLL, KEY_SCROLL);
        lwjglMap.put(KEYMAP_KEY_SUBTRACT, KEY_SUBTRACT);
        lwjglMap.put(KEYMAP_KEY_ADD, KEY_ADD);
        lwjglMap.put(KEYMAP_KEY_DECIMAL, KEY_DECIMAL);
        lwjglMap.put(KEYMAP_KEY_NUMPADENTER, KEY_NUMPADENTER);
        lwjglMap.put(KEYMAP_KEY_DIVIDE, KEY_DIVIDE);
        lwjglMap.put(KEYMAP_KEY_MULTIPLY, KEY_MULTIPLY);
        lwjglMap.put(KEYMAP_KEY_PRINT, KEY_SYSRQ);
        lwjglMap.put(KEYMAP_KEY_LWIN, KEY_LMETA);
        lwjglMap.put(KEYMAP_KEY_RWIN, KEY_RMETA);
        /* missing RightK in Keyboard.java */
    }

    public int translate(String s) {
        if (lwjglMap.containsKey(s)) {
            return lwjglMap.get(s);
        } else {
            return -1;
        }
    }


}
