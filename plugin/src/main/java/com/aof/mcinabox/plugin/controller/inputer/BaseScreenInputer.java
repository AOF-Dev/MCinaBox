package com.aof.mcinabox.plugin.controller.inputer;

import android.view.View;

import com.aof.mcinabox.plugin.controller.inputer.Inputer;
import com.aof.mcinabox.plugin.controller.keyevent.Event;

public interface BaseScreenInputer extends Inputer, View.OnTouchListener, Event {
    void setUiMoveable(boolean moveable);
    void setUiVisiability(int visiablity);
}
