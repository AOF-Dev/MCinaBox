package com.aof.mcinabox.gamecontroller.input.screen;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.aof.mcinabox.gamecontroller.ckb.achieve.CkbManager;
import com.aof.mcinabox.gamecontroller.ckb.achieve.CkbManagerDialog;
import com.aof.mcinabox.gamecontroller.ckb.support.CallCustomizeKeyboard;
import com.aof.mcinabox.gamecontroller.client.Client;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.Input;
import com.aof.mcinabox.gamecontroller.input.OnscreenInput;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;

public class CustomizeKeyboard implements OnscreenInput, Controller, CallCustomizeKeyboard {

    private Context mContext;
    private Controller mController;
    private final String TAG = "CustomKeyboard";
    private final int type = KEYBOARD_BUTTON;

    public CkbManager mManager;
    public CkbManagerDialog mDialog;

    @Override
    public boolean load(Context context, Controller controller) {
        this.mContext = context;
        this.mController = controller;

        mManager = new CkbManager(mContext, this, this);
        mDialog = new CkbManagerDialog(mContext, mManager);

        return true;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            mManager.showOrHideGameButtons(CkbManager.SHOW_BUTTON);
        } else {
            mManager.showOrHideGameButtons(CkbManager.HIDE_BUTTON);
        }
    }

    @Override
    public void sendKey(BaseKeyEvent event) {
        mController.sendKey(event);
    }

    @Override
    public int getInputCounts() {
        return mController.getInputCounts();
    }

    @Override
    public boolean addInput(Input input) {
        return true;
    }

    @Override
    public boolean removeInput(Input input) {
        return true;
    }

    @Override
    public boolean removeAllInputs() {
        return true;
    }

    @Override
    public boolean containsInput(Input input) {
        return false;
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        mController.addContentView(view, params);
    }

    @Override
    public void addView(View v) {
        mController.addView(v);
    }

    @Override
    public void typeWords(String str) {
        mController.typeWords(str);
    }

    @Override
    public void onStop() {

    }

    @Override
    public boolean isGrabbed() {
        return mController.isGrabbed();
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        mManager.setInputMode(isGrabbed);
    }

    @Override
    public int[] getGrabbedPointer() {
        return mController.getGrabbedPointer();
    }

    @Override
    public int[] getLossenPointer() {
        return mController.getLossenPointer();
    }

    @Override
    public void runConfigure() {
        mDialog.show();
    }

    @Override
    public void saveConfig() {
        mManager.autoSaveKeyboard();
    }

    @Override
    public Client getClient() {
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void setUiMoveable(boolean moveable) {

    }

    @Override
    public void setUiVisibility(int visiablity) {

    }

    @Override
    public float[] getPos() {
        return null;
    }

    @Override
    public void setMargins(int left, int top, int right, int bottom) {

    }

    @Override
    public int[] getSize() {
        return null;
    }

    @Override
    public boolean unload() {
        return true;
    }

    @Override
    public View[] getViews() {
        return mManager.getGameButtons();
    }

    @Override
    public int getUiVisiability() {
        return View.VISIBLE;
    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onResumed() {

    }

    @Override
    public Config getConfig() {
        return mController.getConfig();
    }

    @Override
    public Controller getController() {
        return this.mController;
    }
}


