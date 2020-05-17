package com.aof.mcinabox.plugin.controller.inputer;

import android.app.Activity;

import com.aof.mcinabox.plugin.controller.controller.Controller;
import com.aof.mcinabox.plugin.controller.keyevent.Event;

public interface Inputer extends Event {
    boolean load(Activity context, Controller controller);
    boolean unload();
}
