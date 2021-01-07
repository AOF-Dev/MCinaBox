package com.aof.mcinabox.gamecontroller.controller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aof.mcinabox.gamecontroller.client.Client;
import com.aof.mcinabox.gamecontroller.input.Input;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseController implements Controller {
    public ArrayList<Input> inputs;
    public Client client;
    private Context context;
    private final static String TAG = "BaseController";
    private Timer mTimer;
    private final static int DEFAULT_INTERVAL_TIME = 5000;
    private int internalTime;

    public BaseController(Client client, int intervalTime) {
        this.client = client;
        this.context = client.getActivity();
        inputs = new ArrayList<>();
        this.internalTime = intervalTime;
        createAutoSaveTimer();
    }

    public BaseController(Client client){
        this(client, BaseController.DEFAULT_INTERVAL_TIME);
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
        if(mTimer != null)
            mTimer.cancel();
        for (Input i : inputs){
            i.onPaused();
        }
    }

    @Override
    public void onResumed() {
        createAutoSaveTimer();
        for (Input i : inputs){
            i.onResumed();
        }
    }

    private void createAutoSaveTimer(){
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                BaseController.this.saveConfig();
            }
        }, internalTime);
    }

    @Override
    public int[] getLossenPointer(){
        return client.getLoosenPointer();
    }


}


