package com.aof.mcinabox.gamecontroller.input;

import android.content.Context;

import cosine.boat.definitions.id.key.KeyEvent;
import com.aof.mcinabox.gamecontroller.controller.Controller;

public interface Input extends KeyEvent {
    boolean load(Context context, Controller controller);
    boolean unload();
    void setInputMode(int inputMode); // 赋值 MARK_INPUT_MODE
    void runConfigure();
    void saveConfig();
    void setEnable(boolean enable);
    boolean isEnable();
}
