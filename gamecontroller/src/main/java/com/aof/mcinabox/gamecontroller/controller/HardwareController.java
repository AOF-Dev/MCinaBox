package com.aof.mcinabox.gamecontroller.controller;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.aof.mcinabox.definitions.id.AppEvent;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.Input;
import com.aof.mcinabox.gamecontroller.input.OtgInput;
import com.aof.mcinabox.gamecontroller.input.otg.Keyboard;
import com.aof.mcinabox.gamecontroller.codes.AndroidKeyMap;
import com.aof.mcinabox.gamecontroller.codes.Translation;

public class HardwareController extends BaseController implements KeyEvent.Callback , AppEvent , View.OnHoverListener {

    private AndroidKeyMap androidKeyMap = new AndroidKeyMap();
    private Translation translation;

    private OtgInput keyboard;

    public HardwareController(Activity activity , int transType){
        super(activity);
        translation = new Translation(transType);

        //初始化Input
        keyboard = new Keyboard();
        //添加Input
        this.addInput(keyboard);
    }

    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event){

        for(Input i : this.inputs){
            if(((OtgInput)i).onKeyDown(KeyCode,event)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int KeyCode, KeyEvent event){

        for(Input i : this.inputs){
            if(((OtgInput)i).onKeyUp(KeyCode,event)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }

    @Override
    public void sendKey(BaseKeyEvent event) {
        client.setKey(translation.trans(event.getKeyName()),event.isPressed());
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        for(Input i : this.inputs){
            if(((OtgInput)i).onHover(v,event)){
                return true;
            }
        }
        return false;
    }
}
