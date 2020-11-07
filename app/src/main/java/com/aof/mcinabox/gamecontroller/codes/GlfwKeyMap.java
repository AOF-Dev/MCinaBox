package com.aof.mcinabox.gamecontroller.codes;

import com.aof.mcinabox.gamecontroller.definitions.map.KeyMap;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class GlfwKeyMap implements KeyMap {

    private final HashMap<String, Integer> glfwKeyMap;

    public GlfwKeyMap() {
        glfwKeyMap = new HashMap<>();
        init();
    }

    private void init() {
        glfwKeyMap.put(KEYMAP_KEY_0, GLFW_KEY_0);
        glfwKeyMap.put(KEYMAP_KEY_1, GLFW_KEY_1);
        glfwKeyMap.put(KEYMAP_KEY_2, GLFW_KEY_2);
        glfwKeyMap.put(KEYMAP_KEY_3, GLFW_KEY_3);
        glfwKeyMap.put(KEYMAP_KEY_4, GLFW_KEY_4);
        glfwKeyMap.put(KEYMAP_KEY_5, GLFW_KEY_5);
        glfwKeyMap.put(KEYMAP_KEY_6, GLFW_KEY_6);
        glfwKeyMap.put(KEYMAP_KEY_7, GLFW_KEY_7);
        glfwKeyMap.put(KEYMAP_KEY_8, GLFW_KEY_8);
        glfwKeyMap.put(KEYMAP_KEY_9, GLFW_KEY_9);
        glfwKeyMap.put(KEYMAP_KEY_A, GLFW_KEY_A);
        glfwKeyMap.put(KEYMAP_KEY_B, GLFW_KEY_B);
        glfwKeyMap.put(KEYMAP_KEY_C, GLFW_KEY_C);
        glfwKeyMap.put(KEYMAP_KEY_D, GLFW_KEY_D);
        glfwKeyMap.put(KEYMAP_KEY_E, GLFW_KEY_E);
        glfwKeyMap.put(KEYMAP_KEY_F, GLFW_KEY_F);
        glfwKeyMap.put(KEYMAP_KEY_G, GLFW_KEY_G);
        glfwKeyMap.put(KEYMAP_KEY_H, GLFW_KEY_H);
        glfwKeyMap.put(KEYMAP_KEY_I, GLFW_KEY_I);
        glfwKeyMap.put(KEYMAP_KEY_J, GLFW_KEY_J);
        glfwKeyMap.put(KEYMAP_KEY_K, GLFW_KEY_K);
        glfwKeyMap.put(KEYMAP_KEY_L, GLFW_KEY_L);
        glfwKeyMap.put(KEYMAP_KEY_M, GLFW_KEY_M);
        glfwKeyMap.put(KEYMAP_KEY_N, GLFW_KEY_N);
        glfwKeyMap.put(KEYMAP_KEY_O, GLFW_KEY_O);
        glfwKeyMap.put(KEYMAP_KEY_P, GLFW_KEY_P);
        glfwKeyMap.put(KEYMAP_KEY_Q, GLFW_KEY_Q);
        glfwKeyMap.put(KEYMAP_KEY_R, GLFW_KEY_R);
        glfwKeyMap.put(KEYMAP_KEY_S, GLFW_KEY_S);
        glfwKeyMap.put(KEYMAP_KEY_T, GLFW_KEY_T);
        glfwKeyMap.put(KEYMAP_KEY_U, GLFW_KEY_U);
        glfwKeyMap.put(KEYMAP_KEY_V, GLFW_KEY_V);
        glfwKeyMap.put(KEYMAP_KEY_W, GLFW_KEY_W);
        glfwKeyMap.put(KEYMAP_KEY_X, GLFW_KEY_X);
        glfwKeyMap.put(KEYMAP_KEY_Y, GLFW_KEY_Y);
        glfwKeyMap.put(KEYMAP_KEY_Z, GLFW_KEY_Z);
        glfwKeyMap.put(KEYMAP_KEY_MINUS, GLFW_KEY_MINUS);
        glfwKeyMap.put(KEYMAP_KEY_EQUALS, GLFW_KEY_EQUAL);
        glfwKeyMap.put(KEYMAP_KEY_LBRACKET, GLFW_KEY_LEFT_BRACKET);
        glfwKeyMap.put(KEYMAP_KEY_RBRACKET, GLFW_KEY_RIGHT_BRACKET);
        glfwKeyMap.put(KEYMAP_KEY_SEMICOLON, GLFW_KEY_SEMICOLON);
        glfwKeyMap.put(KEYMAP_KEY_APOSTROPHE, GLFW_KEY_APOSTROPHE);
        glfwKeyMap.put(KEYMAP_KEY_GRAVE, GLFW_KEY_GRAVE_ACCENT);
        glfwKeyMap.put(KEYMAP_KEY_BACKSLASH, GLFW_KEY_BACKSLASH);
        glfwKeyMap.put(KEYMAP_KEY_COMMA, GLFW_KEY_COMMA);
        glfwKeyMap.put(KEYMAP_KEY_PERIOD, GLFW_KEY_PERIOD);
        glfwKeyMap.put(KEYMAP_KEY_SLASH, GLFW_KEY_SLASH);
        glfwKeyMap.put(KEYMAP_KEY_ESC, GLFW_KEY_ESCAPE);
        glfwKeyMap.put(KEYMAP_KEY_F1, GLFW_KEY_F1);
        glfwKeyMap.put(KEYMAP_KEY_F2, GLFW_KEY_F2);
        glfwKeyMap.put(KEYMAP_KEY_F3, GLFW_KEY_F3);
        glfwKeyMap.put(KEYMAP_KEY_F4, GLFW_KEY_F4);
        glfwKeyMap.put(KEYMAP_KEY_F5, GLFW_KEY_F5);
        glfwKeyMap.put(KEYMAP_KEY_F6, GLFW_KEY_F6);
        glfwKeyMap.put(KEYMAP_KEY_F7, GLFW_KEY_F7);
        glfwKeyMap.put(KEYMAP_KEY_F8, GLFW_KEY_F8);
        glfwKeyMap.put(KEYMAP_KEY_F9, GLFW_KEY_F9);
        glfwKeyMap.put(KEYMAP_KEY_F10, GLFW_KEY_F10);
        glfwKeyMap.put(KEYMAP_KEY_F11, GLFW_KEY_F11);
        glfwKeyMap.put(KEYMAP_KEY_F12, GLFW_KEY_F12);
        glfwKeyMap.put(KEYMAP_KEY_TAB, GLFW_KEY_TAB);
        glfwKeyMap.put(KEYMAP_KEY_BACKSPACE, GLFW_KEY_BACKSPACE);
        glfwKeyMap.put(KEYMAP_KEY_SPACE, GLFW_KEY_SPACE);
        glfwKeyMap.put(KEYMAP_KEY_CAPITAL, GLFW_KEY_CAPS_LOCK);
        glfwKeyMap.put(KEYMAP_KEY_ENTER, GLFW_KEY_ENTER);
        glfwKeyMap.put(KEYMAP_KEY_LSHIFT, GLFW_KEY_LEFT_SHIFT);
        glfwKeyMap.put(KEYMAP_KEY_LCTRL, GLFW_KEY_LEFT_CONTROL);
        glfwKeyMap.put(KEYMAP_KEY_LALT, GLFW_KEY_LEFT_ALT);
        glfwKeyMap.put(KEYMAP_KEY_RSHIFT, GLFW_KEY_RIGHT_SHIFT);
        glfwKeyMap.put(KEYMAP_KEY_RCTRL, GLFW_KEY_RIGHT_CONTROL);
        glfwKeyMap.put(KEYMAP_KEY_RALT, GLFW_KEY_RIGHT_ALT);
        glfwKeyMap.put(KEYMAP_KEY_UP, GLFW_KEY_UP);
        glfwKeyMap.put(KEYMAP_KEY_DOWN, GLFW_KEY_DOWN);
        glfwKeyMap.put(KEYMAP_KEY_LEFT, GLFW_KEY_LEFT);
        glfwKeyMap.put(KEYMAP_KEY_RIGHT, GLFW_KEY_RIGHT);
        glfwKeyMap.put(KEYMAP_KEY_PAGEUP, GLFW_KEY_PAGE_UP);
        glfwKeyMap.put(KEYMAP_KEY_PAGEDOWN, GLFW_KEY_PAGE_DOWN);
        glfwKeyMap.put(KEYMAP_KEY_HOME, GLFW_KEY_HOME);
        glfwKeyMap.put(KEYMAP_KEY_END, GLFW_KEY_END);
        glfwKeyMap.put(KEYMAP_KEY_INSERT, GLFW_KEY_INSERT);
        glfwKeyMap.put(KEYMAP_KEY_DELETE, GLFW_KEY_DELETE);
        glfwKeyMap.put(KEYMAP_KEY_PAUSE, GLFW_KEY_PAUSE);
        glfwKeyMap.put(KEYMAP_KEY_NUMPAD0, GLFW_KEY_KP_0);
        glfwKeyMap.put(KEYMAP_KEY_NUMPAD1, GLFW_KEY_KP_1);
        glfwKeyMap.put(KEYMAP_KEY_NUMPAD2, GLFW_KEY_KP_2);
        glfwKeyMap.put(KEYMAP_KEY_NUMPAD3, GLFW_KEY_KP_3);
        glfwKeyMap.put(KEYMAP_KEY_NUMPAD4, GLFW_KEY_KP_4);
        glfwKeyMap.put(KEYMAP_KEY_NUMPAD5, GLFW_KEY_KP_5);
        glfwKeyMap.put(KEYMAP_KEY_NUMPAD6, GLFW_KEY_KP_6);
        glfwKeyMap.put(KEYMAP_KEY_NUMPAD7, GLFW_KEY_KP_7);
        glfwKeyMap.put(KEYMAP_KEY_NUMPAD8, GLFW_KEY_KP_8);
        glfwKeyMap.put(KEYMAP_KEY_NUMPAD9, GLFW_KEY_KP_9);
        glfwKeyMap.put(KEYMAP_KEY_NUMLOCK, GLFW_KEY_NUM_LOCK);
        glfwKeyMap.put(KEYMAP_KEY_SCROLL, GLFW_KEY_SCROLL_LOCK);
        glfwKeyMap.put(KEYMAP_KEY_SUBTRACT, GLFW_KEY_KP_SUBTRACT);
        glfwKeyMap.put(KEYMAP_KEY_ADD, GLFW_KEY_KP_ADD);
        glfwKeyMap.put(KEYMAP_KEY_DECIMAL, GLFW_KEY_KP_DECIMAL);
        glfwKeyMap.put(KEYMAP_KEY_NUMPADENTER, GLFW_KEY_KP_ENTER);
        glfwKeyMap.put(KEYMAP_KEY_DIVIDE, GLFW_KEY_KP_DIVIDE);
        glfwKeyMap.put(KEYMAP_KEY_MULTIPLY, GLFW_KEY_KP_MULTIPLY);
        glfwKeyMap.put(KEYMAP_KEY_PRINT, GLFW_KEY_PRINT_SCREEN);
        glfwKeyMap.put(KEYMAP_KEY_LWIN, GLFW_KEY_LEFT_SUPER);
        glfwKeyMap.put(KEYMAP_KEY_RWIN, GLFW_KEY_RIGHT_SUPER);
        /* missing RightK in GLFW.java */
    }

    public int translate(String s) {
        if (glfwKeyMap.containsKey(s)) {
            return glfwKeyMap.get(s);
        } else {
            return -1;
        }
    }

}
