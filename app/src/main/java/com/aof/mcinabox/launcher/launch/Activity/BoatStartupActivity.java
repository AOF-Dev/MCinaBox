package com.aof.mcinabox.launcher.launch.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aof.mcinabox.R;
import com.aof.mcinabox.gamecontroller.client.Client;
import com.aof.mcinabox.gamecontroller.controller.HardwareController;
import com.aof.mcinabox.gamecontroller.controller.VirtualController;
import com.aof.mcinabox.utils.DisplayUtils;

import java.util.Timer;
import java.util.TimerTask;

import cosine.boat.BoatActivity;

public class BoatStartupActivity extends BoatActivity implements Client {

    private int[] grabbedPointer = new int[]{0, 0};
    private boolean grabbed = false;
    private ImageView cursorIcon;
    private final static int CURSOR_SIZE = 16; //dp
    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        cursorIcon = new ImageView(this);
        cursorIcon.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(this, CURSOR_SIZE), DisplayUtils.getPxFromDp(this, CURSOR_SIZE)));
        cursorIcon.setImageResource(R.drawable.cursor);
        this.addView(cursorIcon);
    }

    @Override
    public void setKey(int keyCode, boolean pressed) {
        this.setKey(keyCode,0,pressed);
    }

    @Override
    public void setPointerInc(int xInc, int yInc) {
        if(!grabbed){
            int x, y;
            x = grabbedPointer[0] + xInc;
            y = grabbedPointer[1] + yInc;
            if(x >= 0 && x <= screenWidth)
                grabbedPointer[0] += xInc;
            if(y >= 0 && y <= screenHeight)
                grabbedPointer[1] += yInc;
            setPointer(grabbedPointer[0], grabbedPointer[1]);
            this.cursorIcon.setX(grabbedPointer[0]);
            this.cursorIcon.setY(grabbedPointer[1]);
        }else{
            setPointer(getPointer()[0] + xInc, getPointer()[1] + yInc);
        }
    }

    @Override
    public void setPointer(int x, int y){
        super.setPointer(x, y);
        if(!grabbed){
            this.cursorIcon.setX(x);
            this.cursorIcon.setY(y);
            grabbedPointer[0] = x;
            grabbedPointer[1] = y;
        }
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
        if(str == null) return;
        for(int i = 0; i < str.length(); i++){
            setKey(0, str.charAt(i),true);
            setKey(0, str.charAt(i),false);
        }
    }

    @Override
    public int[] getGrabbedPointer() {
        return this.grabbedPointer;
    }

    @Override
    public int[] getLoosenPointer() {
        return this.getPointer();
    }

    @Override
    public ViewGroup getViewsParent() {
        return (binding != null)?binding.getRoot():null;
    }

    @Override
    public View getSurfaceLayerView() {
        return (binding != null)?binding.getRoot().findViewById(R.id.surface_view):null;
    }

    @Override
    public boolean isGrabbed() {
        return this.grabbed;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed){
        super.setGrabCursor(isGrabbed);
        this.grabbed = isGrabbed;
        if(!isGrabbed){
            setPointer(grabbedPointer[0], grabbedPointer[1]);
            cursorIcon.post(new Runnable() {
                @Override
                public void run() {
                    cursorIcon.setVisibility(View.VISIBLE);
                }
            });
        }else if(cursorIcon.getVisibility() == View.VISIBLE) {
            cursorIcon.post(new Runnable() {
                @Override
                public void run() {
                    cursorIcon.setVisibility(View.INVISIBLE);
                }
            });
        }
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
            }

            @Override
            public void setGrabCursor(boolean isGrabbed) {
                virtualController.setGrabCursor(isGrabbed);
                hardwareController.setGrabCursor(isGrabbed);
            }

            @Override
            public void onStop() {
                virtualController.onStop();
                hardwareController.onStop();
            }

            @Override
            public void onResume() {
                virtualController.onResumed();
                hardwareController.onResumed();
            }

            @Override
            public void onPause() {
                virtualController.onPaused();
                hardwareController.onPaused();
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                return hardwareController.dispatchKeyEvent(event);
            }

            @Override
            public boolean dispatchGenericMotionEvent(MotionEvent event) {
                return hardwareController.dispatchMotionKeyEvent(event);
            }
        };
    }



}
