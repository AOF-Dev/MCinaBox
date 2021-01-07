package com.aof.mcinabox.gamecontroller.controller;

import android.view.View;
import android.view.ViewGroup;

import com.aof.mcinabox.gamecontroller.client.Client;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.Input;

public interface Controller {

    void sendKey(BaseKeyEvent event);

    int getInputCounts();

    boolean addInput(Input input);

    boolean removeInput(Input input);

    boolean removeAllInputs();

    boolean containsInput(Input input);

    void setGrabCursor(boolean mode);

    void addContentView(View view, ViewGroup.LayoutParams params);

    void addView(View v);

    void typeWords(String str);

    void onStop();

    boolean isGrabbed();

    int[] getGrabbedPointer();

    int[] getLossenPointer();

    void saveConfig();

    Client getClient();

    void onPaused();

    void onResumed();
}

