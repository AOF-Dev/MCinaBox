package com.aof.mcinabox.gamecontroller.input;

import android.content.Context;

import com.aof.mcinabox.gamecontroller.controller.Controller;

public interface Input {
    boolean load(Context context, Controller controller);

    boolean unload();

    void setGrabCursor(boolean isGrabbed); // 赋值 MARK_INPUT_MODE

    void runConfigure();

    void saveConfig();

    boolean isEnable();

    void setEnable(boolean enable);
}
