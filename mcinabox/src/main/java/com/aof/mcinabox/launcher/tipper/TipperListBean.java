package com.aof.mcinabox.launcher.tipper;

import android.content.Context;

public class TipperListBean {
    private String tipper_info;
    private Context context;
    private int tipper_index;

    public int getTipper_index() {
        return tipper_index;
    }

    public void setTipper_index(int tipper_index) {
        this.tipper_index = tipper_index;
    }

    public String getTipper_info() {
        return tipper_info;
    }
    public void setTipper_info(String tipper_info) {
        this.tipper_info = tipper_info;
    }
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
}