package com.aof.mcinabox.plugin.controller.controller;

import com.aof.mcinabox.plugin.controller.inputer.Inputer;
import com.aof.mcinabox.plugin.controller.keyevent.BaseKeyEvent;

import java.util.ArrayList;

public interface Controller{

    void sendKey(BaseKeyEvent event);

    int getInputerCounts();
    boolean addInputer(Inputer inputer);
    boolean removeInputer(Inputer inputer);
    boolean removeAllInputers();
    boolean containInputer(Inputer inputer);
    ArrayList<Inputer> getAllInputers();
}
