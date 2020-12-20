package com.aof.mcinabox.launcher.launch.Activity;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.aof.mcinabox.R;
import com.aof.mcinabox.gamecontroller.client.Client;
import com.aof.mcinabox.gamecontroller.controller.HardwareController;
import com.aof.mcinabox.gamecontroller.controller.VirtualController;

import java.util.Timer;
import java.util.TimerTask;

import cosine.boat.BoatActivity;

public class BoatStartupActivity extends BoatActivity implements Client {

    @Override
    public void setKey(int keyCode, boolean pressed) {
        this.setKey(keyCode,0,pressed);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void addView(View v) {
        this.addContentView(v, v.getLayoutParams());
    }

    @Override
    public void typeWords(String str) {
        for(int i = 0; i < str.length(); i++){
            setKey(0, str.charAt(i),true);
            setKey(0, str.charAt(i),false);
        }
    }

    @Override
    public ViewGroup getViewsParent() {
        return (binding != null)?binding.getRoot():null;
    }

    @Override
    public View getSurfaceLayerView() {
        return (binding != null)?binding.getRoot().findViewById(R.id.surface_view):null;
    }

    public static void attachControllerInterface() {
        BoatStartupActivity.boatInterface = new BoatStartupActivity.IBoat() {
            private VirtualController virtualController;
            private HardwareController hardwareController;
            private Timer timer;

            @Override
            public void onActivityCreate(BoatActivity boatActivity) {
                virtualController = new VirtualController((Client) boatActivity, KEYMAP_TO_X);
                hardwareController = new HardwareController((Client) boatActivity, KEYMAP_TO_X);

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        virtualController.saveConfig();
                        hardwareController.saveConfig();
                    }
                }, 5000, 5000);
            }

            @Override
            public void setGrabCursor(boolean isGrabbed) {
                virtualController.setGrabCursor(isGrabbed);
                hardwareController.setGrabCursor(isGrabbed);
            }

            @Override
            public void onStop() {
                timer.cancel();
                virtualController.onStop();
                hardwareController.onStop();
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                hardwareController.dispatchKeyEvent(event);
                return true;
            }

            @Override
            public boolean dispatchGenericMotionEvent(MotionEvent event) {
                hardwareController.dispatchMotionKeyEvent(event);
                return true;
            }
        };
    }
}
