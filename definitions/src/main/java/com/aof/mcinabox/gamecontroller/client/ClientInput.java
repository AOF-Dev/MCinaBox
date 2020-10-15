package com.aof.mcinabox.gamecontroller.client;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.aof.mcinabox.definitions.id.key.KeyEvent;

public interface ClientInput extends KeyEvent {
    void setKey(int keyCode, boolean pressed);

    void setMouseButton(int mouseCode, boolean pressed);

    void setMousePoniter(int x, int y);

    Activity getActivity();

    void addView(View v);

    void addContentView(View view, ViewGroup.LayoutParams params);

    void typeWords(String str);

    void addControllerView(View v);

    int[] getPointer();
}