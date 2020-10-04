package com.aof.mcinabox.gamecontroller.input;

import android.view.KeyEvent;
import android.view.View;

import com.aof.mcinabox.definitions.id.AppEvent;

public interface HwInput extends Input, AppEvent {
    boolean onKey(KeyEvent event);
    int getSource();
}
