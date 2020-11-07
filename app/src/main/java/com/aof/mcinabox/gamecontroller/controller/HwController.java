package com.aof.mcinabox.gamecontroller.controller;

import android.view.KeyEvent;
import android.view.MotionEvent;

public interface HwController extends Controller {
    void dispatchKeyEvent(KeyEvent event);

    void dispatchMotionKeyEvent(MotionEvent event);
}
