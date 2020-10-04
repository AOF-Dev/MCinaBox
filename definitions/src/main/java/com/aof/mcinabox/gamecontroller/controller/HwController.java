package com.aof.mcinabox.gamecontroller.controller;

import android.view.KeyEvent;

public interface HwController extends Controller {
    void dispatchKeyEvent(KeyEvent event);
}
