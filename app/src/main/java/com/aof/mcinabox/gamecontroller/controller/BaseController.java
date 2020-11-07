package com.aof.mcinabox.gamecontroller.controller;

import android.view.View;
import android.view.ViewGroup;

import com.aof.mcinabox.gamecontroller.input.Input;

import java.util.ArrayList;

import cosine.boat.BoatActivity;

public abstract class BaseController implements Controller {
    public ArrayList<Input> inputs;
    public BoatActivity boatActivity;
    private boolean isGrabbed = false;
    private final static String TAG = "BaseController";

    public BaseController(BoatActivity boatActivity) {
        this.boatActivity = boatActivity;
        inputs = new ArrayList<>();
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
            if (input.load(boatActivity, this)) {
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
        this.isGrabbed = isGrabbed;
        for (Input i : inputs) {
            i.setGrabCursor(isGrabbed);
        }
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        boatActivity.addContentView(view, params);
    }

    @Override
    public void addView(View view) {
        boatActivity.addContentView(view, view.getLayoutParams());
    }

    @Override
    public void typeWords(String str) {
        for (char c : str.toCharArray()) {
            boatActivity.setKey(0, c, true);
            boatActivity.setKey(0, c, false);
        }
    }

    @Override
    public void onStop() {
        this.saveConfig();
    }

    @Override
    public boolean getGrabbed() {
        return this.isGrabbed;
    }

    @Override
    public int[] getPointer() {
        return boatActivity.getPointer();
    }

    @Override
    public void saveConfig() {
        for (Input i : inputs) {
            i.saveConfig();
        }
    }
}


