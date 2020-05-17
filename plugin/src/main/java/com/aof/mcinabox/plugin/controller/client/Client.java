package com.aof.mcinabox.plugin.controller.client;

import android.app.Activity;

import com.aof.mcinabox.plugin.controller.controller.Controller;
import com.aof.mcinabox.plugin.controller.keyevent.Event;

public interface Client extends Event {
    void setKey(int keyCode,boolean pressed);
    void setMouseButton(int mouseCode,boolean pressed);
    void setMousePoniter(int x,int y);
    Activity getActivity();

    /* Useless Methods.
    int getCotrollerCounts();
    boolean addController(Controller controller);
    boolean removeController(Controller controller);
    boolean removeAllController();
    boolean containController(Controller controller);
    Controller[] getAllControllers();
     */
}
