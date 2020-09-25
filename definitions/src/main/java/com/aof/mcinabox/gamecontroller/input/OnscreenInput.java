package com.aof.mcinabox.gamecontroller.input;

import android.view.View;

import com.aof.mcinabox.definitions.id.AppEvent;

public interface OnscreenInput extends Input, View.OnTouchListener, AppEvent{
    void setUiMoveable(boolean moveable);
    void setUiVisibility(int visiablity);
    float[] getPos(); // View.getX() , View.getY()
    void setMargins(int left, int top ,int right , int bottom);
    int[] getSize(); // View.getWidth() , View.getHeight()
    View[] getViews();
}
