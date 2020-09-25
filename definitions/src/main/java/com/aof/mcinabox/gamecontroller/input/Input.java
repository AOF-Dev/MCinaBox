package com.aof.mcinabox.gamecontroller.input;

import android.app.Activity;
import android.content.Context;

import com.aof.mcinabox.gamecontroller.controller.BaseController;
import com.aof.mcinabox.definitions.id.key.KeyEvent;
import com.aof.mcinabox.gamecontroller.controller.Controller;

public interface Input extends KeyEvent {
    boolean load(Context context, Controller controller);
    boolean unload();
    void setInputMode(int inputMode); // 赋值 MARK_INPUT_MODE
    void runConfigure();
    void saveConfig();
    void setEnable(boolean enable);
}
