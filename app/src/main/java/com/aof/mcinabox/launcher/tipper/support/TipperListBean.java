package com.aof.mcinabox.launcher.tipper.support;

import android.content.Context;

public class TipperListBean {
    private String tipper_info;
    private Context context;
    private int tipper_level;
    private TipperRunable tipper_runable;
    private int tipper_id;

    public String getTipper_info() {
        return tipper_info;
    }

    public TipperListBean setTipper_info(String tipper_info) {
        this.tipper_info = tipper_info;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public TipperListBean setContext(Context context) {
        this.context = context;
        return this;
    }

    public int getTipper_level(){
        return this.tipper_level;
    }

    public TipperListBean setTipper_level(int level){
        this.tipper_level = level;
        return this;
    }

    public TipperRunable getTipper_runable(){
        return  this.tipper_runable;
    }

    public TipperListBean setTipper_runable(TipperRunable runable){
        this.tipper_runable = runable;
        return this;
    }

    public int getTipper_id(){
        return this.tipper_id;
    }

    public TipperListBean setTipper_id(int id){
        this.tipper_id = id;
        return this;
    }
}