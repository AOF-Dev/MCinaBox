package com.aof.mcinabox.gamecontroller.input.screen;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.aof.mcinabox.definitions.id.AppEvent;
import com.aof.mcinabox.gamecontroller.ckb.achieve.CkbManagerDialog;
import com.aof.mcinabox.gamecontroller.ckb.achieve.CkbManager;
import com.aof.mcinabox.gamecontroller.ckb.support.CallCustomizeKeyboard;
import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.Input;
import com.aof.mcinabox.gamecontroller.input.OnscreenInput;
import java.util.ArrayList;

public class CustomizeKeyboard implements OnscreenInput, AppEvent, Controller, CallCustomizeKeyboard {

    private Context mContext;
    private Controller mController;
    private String TAG = "CustomKeyboard";
    private int type = KEYBOARD_BUTTON;

    private CkbManager mManager;
    private CkbManagerDialog mDialog;

    @Override
    public boolean load(Context context, Controller controller) {
        this.mContext = context;
        this.mController = controller;

        mManager = new CkbManager(mContext, this, this);
        mDialog = new CkbManagerDialog(mContext, mManager);

        return true;
    }

    @Override
    public boolean isEnable() {
        return false;
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
    public boolean containInput(Input input) {
        return false;
    }

    @Override
    public ArrayList<Input> getAllInputs() {
        return null;
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
        //to do nothing.
    }

    @Override
    public int getInputMode() {
        return mController.getInputMode();
    }

    @Override
    public int[] getPointer() {
        return mController.getPointer();
    }

    @Override
    public void runConfigure() {
        mDialog.show();
    }

    @Override
    public void saveConfig() {
        //当调用保存方法时通过管理器自动保存键盘文件
        mManager.autoSaveKeyboard();
    }

    @Override
    public void setEnable(boolean enable) {
        if(enable){
            mManager.showOrHideGameButtons(CkbManager.SHOW_BUTTON);
        }else{
            mManager.showOrHideGameButtons(CkbManager.HIDE_BUTTON);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void setUiMoveable(boolean moveable) {
        // to do nothing.
    }

    @Override
    public void setUiVisibility(int visiablity) {
        // to do nothing
    }

    @Override
    public float[] getPos() {
        return null;
    }

    @Override
    public void setMargins(int left, int top, int right, int bottom) {
        //to do nothing
    }

    @Override
    public int[] getSize() {
        return null;
    }

    @Override
    public boolean unload() {
        //TODO: 接入自带的控制器
        return true;
    }

    @Override
    public void setInputMode(int inputMode) {
        mManager.setInputMode(inputMode);
    }

    @Override
    public View[] getViews() {
        return mManager.getGameButtons();
    }

    @Override
    public int getUiVisiability() {
        return View.VISIBLE;
    }

}


