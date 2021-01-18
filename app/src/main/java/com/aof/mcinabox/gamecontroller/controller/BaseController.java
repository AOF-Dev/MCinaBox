package com.aof.mcinabox.gamecontroller.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aof.mcinabox.gamecontroller.client.Client;
import com.aof.mcinabox.gamecontroller.input.Input;
import com.aof.mcinabox.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseController implements Controller {
    private final static String TAG = "BaseController";
    public ArrayList<Input> inputs;
    public Client client;
    public Context context;
    private Timer mTimer;
    private final static int DEFAULT_INTERVAL_TIME = 5000;
    private int internalTime;
    private Config mConfig;
    private boolean isTimerEnable;

    public BaseController(Client client, int intervalTime, boolean enableTimer) {
        this.client = client;
        this.context = client.getActivity();
        inputs = new ArrayList<>();
        this.internalTime = intervalTime;
        this.mConfig = new Config(DisplayUtils.getDisplayWindowSize(context)[0], DisplayUtils.getDisplayWindowSize(context)[1]);
        this.isTimerEnable = enableTimer;
        if(enableTimer){
            createAutoSaveTimer();
        }
    }

    public BaseController(Client client,boolean enableTimer){
        this(client, BaseController.DEFAULT_INTERVAL_TIME, enableTimer);
    }


    @Override
    public boolean containsInput(Input input) {
        for (Input i : inputs) {
            if (i == input) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean addInput(Input input) {
        if (containsInput(input) || input == null) {
            return false;
        } else {
            if (input.load(context, this)) {
                inputs.add(input);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean removeInput(Input input) {
        if (!containsInput(input) || input == null || !input.unload()) {
            return false;
        } else {
            ArrayList<Input> tmp = new ArrayList<>();
            for (Input i : inputs) {
                if (input != i) {
                    tmp.add(i);
                }
            }
            inputs = tmp;
            return true;
        }
    }

    @Override
    public int getInputCounts() {
        return inputs.size();
    }

    @Override
    public boolean removeAllInputs() {
        boolean success = true;
        for (Input i : inputs) {
            if (!removeInput(i)) {
                success = false;
            }
        }
        return success;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        for (Input i : inputs) {
            i.setGrabCursor(isGrabbed);
        }
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        client.addContentView(view, params);
    }

    @Override
    public void addView(View view) {
        client.addContentView(view, view.getLayoutParams());
    }

    @Override
    public void typeWords(String str) {
        client.typeWords(str);
    }

    @Override
    public void onStop() {
        this.saveConfig();
    }

    @Override
    public boolean isGrabbed() {
        return client.isGrabbed();
    }

    @Override
    public int[] getGrabbedPointer() {
        return client.getGrabbedPointer();
    }

    @Override
    public void saveConfig() {
        for (Input i : inputs) {
            i.saveConfig();
        }
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public void onPaused() {
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
        for (Input i : inputs){
            i.onPaused();
        }
    }

    @Override
    public void onResumed() {
        if(isTimerEnable){
            createAutoSaveTimer();
        }
        for (Input i : inputs){
            i.onResumed();
        }
    }

    private void createAutoSaveTimer(){
        if(mTimer != null) return;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                BaseController.this.saveConfig();
            }
        }, internalTime, internalTime);
    }

    @Override
    public int[] getLossenPointer(){
        return client.getLoosenPointer();
    }

    @Override
    public Config getConfig() {
        return this.mConfig;
    }

    public boolean isTimerEnabled(){
        return this.isTimerEnable;
    }
}


