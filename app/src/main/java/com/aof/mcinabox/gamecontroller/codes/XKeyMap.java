package com.aof.mcinabox.gamecontroller.codes;

import java.util.HashMap;

import static com.aof.mcinabox.gamecontroller.codes.BoatKeycodes.*;
import static com.aof.mcinabox.gamecontroller.codes.BoatMousecodes.BOAT_MOUSE_BUTTON_left;
import static com.aof.mcinabox.gamecontroller.codes.BoatMousecodes.BOAT_MOUSE_BUTTON_middle;
import static com.aof.mcinabox.gamecontroller.codes.BoatMousecodes.BOAT_MOUSE_BUTTON_right;
import static com.aof.mcinabox.gamecontroller.codes.BoatMousecodes.BOAT_MOUSE_WHEEL_down;
import static com.aof.mcinabox.gamecontroller.codes.BoatMousecodes.BOAT_MOUSE_WHEEL_up;
import static com.aof.mcinabox.gamecontroller.definitions.map.KeyMap.*;
import static com.aof.mcinabox.gamecontroller.definitions.map.MouseMap.MOUSEMAP_BUTTON_LEFT;
import static com.aof.mcinabox.gamecontroller.definitions.map.MouseMap.MOUSEMAP_BUTTON_MIDDLE;
import static com.aof.mcinabox.gamecontroller.definitions.map.MouseMap.MOUSEMAP_BUTTON_RIGHT;
import static com.aof.mcinabox.gamecontroller.definitions.map.MouseMap.MOUSEMAP_WHEEL_DOWN;
import static com.aof.mcinabox.gamecontroller.definitions.map.MouseMap.MOUSEMAP_WHEEL_UP;

public class XKeyMap {

    private final HashMap<String, Integer> xKeyMap;

    public XKeyMap() {
        xKeyMap = new HashMap<>();
        init();
    }

    private void init() {
        xKeyMap.put(KEYMAP_KEY_0, BOAT_KEYBOARD_0);
        xKeyMap.put(KEYMAP_KEY_1, BOAT_KEYBOARD_1);
        xKeyMap.put(KEYMAP_KEY_2, BOAT_KEYBOARD_2);
        xKeyMap.put(KEYMAP_KEY_3, BOAT_KEYBOARD_3);
        xKeyMap.put(KEYMAP_KEY_4, BOAT_KEYBOARD_4);
        xKeyMap.put(KEYMAP_KEY_5, BOAT_KEYBOARD_5);
        xKeyMap.put(KEYMAP_KEY_6, BOAT_KEYBOARD_6);
        xKeyMap.put(KEYMAP_KEY_7, BOAT_KEYBOARD_7);
        xKeyMap.put(KEYMAP_KEY_8, BOAT_KEYBOARD_8);
        xKeyMap.put(KEYMAP_KEY_9, BOAT_KEYBOARD_9);
        xKeyMap.put(KEYMAP_KEY_A, BOAT_KEYBOARD_A);
        xKeyMap.put(KEYMAP_KEY_B, BOAT_KEYBOARD_B);
        xKeyMap.put(KEYMAP_KEY_C, BOAT_KEYBOARD_C);
        xKeyMap.put(KEYMAP_KEY_D, BOAT_KEYBOARD_D);
        xKeyMap.put(KEYMAP_KEY_E, BOAT_KEYBOARD_E);
        xKeyMap.put(KEYMAP_KEY_F, BOAT_KEYBOARD_F);
        xKeyMap.put(KEYMAP_KEY_G, BOAT_KEYBOARD_G);
        xKeyMap.put(KEYMAP_KEY_H, BOAT_KEYBOARD_H);
        xKeyMap.put(KEYMAP_KEY_I, BOAT_KEYBOARD_I);
        xKeyMap.put(KEYMAP_KEY_J, BOAT_KEYBOARD_J);
        xKeyMap.put(KEYMAP_KEY_K, BOAT_KEYBOARD_K);
        xKeyMap.put(KEYMAP_KEY_L, BOAT_KEYBOARD_L);
        xKeyMap.put(KEYMAP_KEY_M, BOAT_KEYBOARD_M);
        xKeyMap.put(KEYMAP_KEY_N, BOAT_KEYBOARD_N);
        xKeyMap.put(KEYMAP_KEY_O, BOAT_KEYBOARD_O);
        xKeyMap.put(KEYMAP_KEY_P, BOAT_KEYBOARD_P);
        xKeyMap.put(KEYMAP_KEY_Q, BOAT_KEYBOARD_Q);
        xKeyMap.put(KEYMAP_KEY_R, BOAT_KEYBOARD_R);
        xKeyMap.put(KEYMAP_KEY_S, BOAT_KEYBOARD_S);
        xKeyMap.put(KEYMAP_KEY_T, BOAT_KEYBOARD_T);
        xKeyMap.put(KEYMAP_KEY_U, BOAT_KEYBOARD_U);
        xKeyMap.put(KEYMAP_KEY_V, BOAT_KEYBOARD_V);
        xKeyMap.put(KEYMAP_KEY_W, BOAT_KEYBOARD_W);
        xKeyMap.put(KEYMAP_KEY_X, BOAT_KEYBOARD_X);
        xKeyMap.put(KEYMAP_KEY_Y, BOAT_KEYBOARD_Y);
        xKeyMap.put(KEYMAP_KEY_Z, BOAT_KEYBOARD_Z);
        xKeyMap.put(KEYMAP_KEY_MINUS, BOAT_KEYBOARD_minus);
        xKeyMap.put(KEYMAP_KEY_EQUALS, BOAT_KEYBOARD_equal);
        xKeyMap.put(KEYMAP_KEY_LBRACKET, BOAT_KEYBOARD_bracketleft);
        xKeyMap.put(KEYMAP_KEY_RBRACKET, BOAT_KEYBOARD_bracketright);
        xKeyMap.put(KEYMAP_KEY_SEMICOLON, BOAT_KEYBOARD_semicolon);
        xKeyMap.put(KEYMAP_KEY_APOSTROPHE, BOAT_KEYBOARD_apostrophe);
        xKeyMap.put(KEYMAP_KEY_GRAVE, BOAT_KEYBOARD_grave);
        xKeyMap.put(KEYMAP_KEY_BACKSLASH, BOAT_KEYBOARD_backslash);
        xKeyMap.put(KEYMAP_KEY_COMMA, BOAT_KEYBOARD_comma);
        xKeyMap.put(KEYMAP_KEY_PERIOD, BOAT_KEYBOARD_period);
        xKeyMap.put(KEYMAP_KEY_SLASH, BOAT_KEYBOARD_slash);
        xKeyMap.put(KEYMAP_KEY_ESC, BOAT_KEYBOARD_Escape);
        xKeyMap.put(KEYMAP_KEY_F1, BOAT_KEYBOARD_F1);
        xKeyMap.put(KEYMAP_KEY_F2, BOAT_KEYBOARD_F2);
        xKeyMap.put(KEYMAP_KEY_F3, BOAT_KEYBOARD_F3);
        xKeyMap.put(KEYMAP_KEY_F4, BOAT_KEYBOARD_F4);
        xKeyMap.put(KEYMAP_KEY_F5, BOAT_KEYBOARD_F5);
        xKeyMap.put(KEYMAP_KEY_F6, BOAT_KEYBOARD_F6);
        xKeyMap.put(KEYMAP_KEY_F7, BOAT_KEYBOARD_F7);
        xKeyMap.put(KEYMAP_KEY_F8, BOAT_KEYBOARD_F8);
        xKeyMap.put(KEYMAP_KEY_F9, BOAT_KEYBOARD_F9);
        xKeyMap.put(KEYMAP_KEY_F10, BOAT_KEYBOARD_F10);
        xKeyMap.put(KEYMAP_KEY_F11, BOAT_KEYBOARD_F11);
        xKeyMap.put(KEYMAP_KEY_F12, BOAT_KEYBOARD_F12);
        xKeyMap.put(KEYMAP_KEY_TAB, BOAT_KEYBOARD_Tab);
        xKeyMap.put(KEYMAP_KEY_BACKSPACE, BOAT_KEYBOARD_BackSpace);
        xKeyMap.put(KEYMAP_KEY_SPACE, BOAT_KEYBOARD_space);
        xKeyMap.put(KEYMAP_KEY_CAPITAL, BOAT_KEYBOARD_Caps_Lock);
        xKeyMap.put(KEYMAP_KEY_ENTER, BOAT_KEYBOARD_KP_Enter);
        xKeyMap.put(KEYMAP_KEY_LSHIFT, BOAT_KEYBOARD_Shift_L);
        xKeyMap.put(KEYMAP_KEY_LCTRL, BOAT_KEYBOARD_Control_L);
        xKeyMap.put(KEYMAP_KEY_LALT, BOAT_KEYBOARD_Alt_L);
        xKeyMap.put(KEYMAP_KEY_RSHIFT, BOAT_KEYBOARD_Shift_R);
        xKeyMap.put(KEYMAP_KEY_RCTRL, BOAT_KEYBOARD_Control_R);
        xKeyMap.put(KEYMAP_KEY_RALT, BOAT_KEYBOARD_Alt_R);
        xKeyMap.put(KEYMAP_KEY_UP, BOAT_KEYBOARD_Up);
        xKeyMap.put(KEYMAP_KEY_DOWN, BOAT_KEYBOARD_Down);
        xKeyMap.put(KEYMAP_KEY_LEFT, BOAT_KEYBOARD_Left);
        xKeyMap.put(KEYMAP_KEY_RIGHT, BOAT_KEYBOARD_Right);
        xKeyMap.put(KEYMAP_KEY_PAGEUP, BOAT_KEYBOARD_Page_Up);
        xKeyMap.put(KEYMAP_KEY_PAGEDOWN, BOAT_KEYBOARD_Page_Down);
        xKeyMap.put(KEYMAP_KEY_HOME, BOAT_KEYBOARD_Home);
        xKeyMap.put(KEYMAP_KEY_END, BOAT_KEYBOARD_End);
        xKeyMap.put(KEYMAP_KEY_INSERT, BOAT_KEYBOARD_Insert);
        xKeyMap.put(KEYMAP_KEY_DELETE, BOAT_KEYBOARD_Delete);
        xKeyMap.put(KEYMAP_KEY_PAUSE, BOAT_KEYBOARD_Pause);
        xKeyMap.put(KEYMAP_KEY_NUMPAD0, BOAT_KEYBOARD_KP_0);
        xKeyMap.put(KEYMAP_KEY_NUMPAD1, BOAT_KEYBOARD_KP_1);
        xKeyMap.put(KEYMAP_KEY_NUMPAD2, BOAT_KEYBOARD_KP_2);
        xKeyMap.put(KEYMAP_KEY_NUMPAD3, BOAT_KEYBOARD_KP_3);
        xKeyMap.put(KEYMAP_KEY_NUMPAD4, BOAT_KEYBOARD_KP_4);
        xKeyMap.put(KEYMAP_KEY_NUMPAD5, BOAT_KEYBOARD_KP_5);
        xKeyMap.put(KEYMAP_KEY_NUMPAD6, BOAT_KEYBOARD_KP_6);
        xKeyMap.put(KEYMAP_KEY_NUMPAD7, BOAT_KEYBOARD_KP_7);
        xKeyMap.put(KEYMAP_KEY_NUMPAD8, BOAT_KEYBOARD_KP_8);
        xKeyMap.put(KEYMAP_KEY_NUMPAD9, BOAT_KEYBOARD_KP_9);
        xKeyMap.put(KEYMAP_KEY_NUMLOCK, BOAT_KEYBOARD_Num_Lock);
        xKeyMap.put(KEYMAP_KEY_SCROLL, BOAT_KEYBOARD_Scroll_Lock);
        xKeyMap.put(KEYMAP_KEY_SUBTRACT, BOAT_KEYBOARD_KP_Subtract);
        xKeyMap.put(KEYMAP_KEY_ADD, BOAT_KEYBOARD_KP_Add);
        xKeyMap.put(KEYMAP_KEY_DECIMAL, BOAT_KEYBOARD_KP_Decimal);
        xKeyMap.put(KEYMAP_KEY_NUMPADENTER, BOAT_KEYBOARD_KP_Enter);
        xKeyMap.put(KEYMAP_KEY_DIVIDE, BOAT_KEYBOARD_KP_Divide);
        xKeyMap.put(KEYMAP_KEY_MULTIPLY, BOAT_KEYBOARD_KP_Multiply);
        xKeyMap.put(KEYMAP_KEY_PRINT, BOAT_KEYBOARD_Print);
        xKeyMap.put(KEYMAP_KEY_LWIN, BOAT_KEYBOARD_Super_L);
        xKeyMap.put(KEYMAP_KEY_RWIN, BOAT_KEYBOARD_Super_R);
        /* missing RightK in BoatKeycodes.java */

        /* Mouse buttons codes */
        xKeyMap.put(MOUSEMAP_BUTTON_LEFT, BOAT_MOUSE_BUTTON_left);
        xKeyMap.put(MOUSEMAP_BUTTON_RIGHT, BOAT_MOUSE_BUTTON_right);
        xKeyMap.put(MOUSEMAP_BUTTON_MIDDLE, BOAT_MOUSE_BUTTON_middle);
        xKeyMap.put(MOUSEMAP_WHEEL_UP, BOAT_MOUSE_WHEEL_up);
        xKeyMap.put(MOUSEMAP_WHEEL_DOWN, BOAT_MOUSE_WHEEL_down);
    }

    public int translate(String s) {
        if (xKeyMap.containsKey(s)) {
            return xKeyMap.get(s);
        } else {
            return -1;
        }
    }
}
