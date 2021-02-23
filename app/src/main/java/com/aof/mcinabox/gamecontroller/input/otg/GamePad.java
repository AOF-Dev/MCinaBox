package com.aof.mcinabox.gamecontroller.input.otg;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.aof.mcinabox.gamecontroller.controller.Controller;
import com.aof.mcinabox.gamecontroller.definitions.map.KeyMap;
import com.aof.mcinabox.gamecontroller.definitions.map.MouseMap;
import com.aof.mcinabox.gamecontroller.event.BaseKeyEvent;
import com.aof.mcinabox.gamecontroller.input.HwInput;

import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_POINTER_INC;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.KEYBOARD_BUTTON;
import static com.aof.mcinabox.gamecontroller.definitions.id.key.KeyEvent.MOUSE_BUTTON;

public class GamePad implements HwInput {

    private final static String TAG = "GamePad";

    private final static int POINTER_SEND_LAG = 5;
    private final static int EVENT_DEAL_LAG = 5;

    private Controller mController;
    private Context mContext;
    private boolean isEnabled;

    private final static int type_1 = KEYBOARD_BUTTON;
    private final static int type_2 = MOUSE_BUTTON;
    private final static int type_3 = MOUSE_POINTER_INC;

    private boolean G_B_PRESS;
    @Override
    public boolean onKey(KeyEvent event) {
        if (mController.isGrabbed()){
            switch(event.getKeyCode()){
                case KeyEvent.KEYCODE_BUTTON_A:
                    //跳跃
                    sendEvent(KeyMap.KEYMAP_KEY_SPACE, event, type_1);
                    break;
                case KeyEvent.KEYCODE_BUTTON_B:
                    //潜行(保持)
                    if(event.getAction() == KeyEvent.ACTION_UP){
                        if(G_B_PRESS){
                            G_B_PRESS = false;
                            sendEvent(KeyMap.KEYMAP_KEY_LSHIFT, false, type_1);
                        }else{
                            G_B_PRESS = true;
                            sendEvent(KeyMap.KEYMAP_KEY_LSHIFT, true, type_1);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_BUTTON_X:
                    //背包
                    sendEvent(KeyMap.KEYMAP_KEY_E, event, type_1);
                    break;
                case KeyEvent.KEYCODE_BUTTON_Y:
                    //丢弃
                    sendEvent(KeyMap.KEYMAP_KEY_Q, event, type_1);
                    break;
                case KeyEvent.KEYCODE_BUTTON_L1:
                    //滚轮上
                    sendEvent(MouseMap.MOUSEMAP_WHEEL_UP, event, type_2);
                    break;
                case KeyEvent.KEYCODE_BUTTON_R1:
                    //滚轮下
                    sendEvent(MouseMap.MOUSEMAP_WHEEL_DOWN, event, type_2);
                    break;
                case KeyEvent.KEYCODE_BUTTON_THUMBR:
                    //潜行
                    sendEvent(KeyMap.KEYMAP_KEY_LSHIFT, event, type_1);
                    break;
            }
        } else{

            switch(event.getKeyCode()){
                case KeyEvent.KEYCODE_BUTTON_A:
                    //跳跃
                    sendEvent(KeyMap.KEYMAP_KEY_SPACE, event, type_1);
                    break;
                case KeyEvent.KEYCODE_BUTTON_B:
                    //潜行
                    sendEvent(KeyMap.KEYMAP_KEY_LSHIFT, event, type_1);
                    break;
                case KeyEvent.KEYCODE_BUTTON_X:
                    //背包
                    sendEvent(KeyMap.KEYMAP_KEY_E, event, type_1);
                    break;
                case KeyEvent.KEYCODE_BUTTON_Y:
                    //丢弃
                    sendEvent(KeyMap.KEYMAP_KEY_Q, event, type_1);
                    break;
                case KeyEvent.KEYCODE_BUTTON_L1:
                    //滚轮上
                    sendEvent(MouseMap.MOUSEMAP_WHEEL_UP, event, type_2);
                    break;
                case KeyEvent.KEYCODE_BUTTON_R1:
                    //滚轮下
                    sendEvent(MouseMap.MOUSEMAP_WHEEL_DOWN, event, type_2);
                    break;
                case KeyEvent.KEYCODE_BUTTON_THUMBR:
                    break;
            }
        }
        return true;
    }

    private void sendEvent(String keyName, KeyEvent keyEvent, int keyType){
        boolean pressed;
        switch(keyEvent.getAction()){
            case KeyEvent.ACTION_DOWN:
                pressed = true;
                break;
            case KeyEvent.ACTION_UP:
                pressed = false;
                break;
            default:
                return;
        }
        sendEvent(keyName, pressed, keyType);
    }

    private void sendEvent(String keyName, boolean pressed, int keyType){
        mController.sendKey(new BaseKeyEvent(TAG, keyName, pressed, keyType, null));
    }


    private boolean G_LT_PRESS;
    private boolean G_RT_PRESS;
    private final static float G_LT_THR_VALUE = 1f;
    private final static float G_RT_THR_VALUE = 1f;

    private boolean G_L_B_X_POSITIVE_PRESS;
    private boolean G_L_B_X_NEGATIVE_PRESS;
    private boolean G_L_B_Y_POSITIVE_PRESS;
    private boolean G_L_B_Y_NEGATIVE_PRESS;
    private final static float G_L_B_THR_POSITIVE = 0.15f;
    private final static float G_L_B_THR_NEGATIVE = -0.15f;
    private final static String G_L_B_X_POSITIVE_KEY = KeyMap.KEYMAP_KEY_D;
    private final static String G_L_B_X_NEGATIVE_KEY = KeyMap.KEYMAP_KEY_A;
    private final static String G_L_B_Y_POSITIVE_KEY = KeyMap.KEYMAP_KEY_S;
    private final static String G_L_B_Y_NEGATIVE_KEY = KeyMap.KEYMAP_KEY_W;

    private boolean G_HAT_X_POSITIVE_PRESS;
    private boolean G_HAT_X_NEGATIVE_PRESS;
    private boolean G_HAT_Y_POSITIVE_PRESS;
    private boolean G_HAT_Y_NEGATIVE_PRESS;
    private final static float G_HAT_THR_POSITIVE = 1f;
    private final static float G_HAT_THR_NEGATIVE = -1f;
    private final static String G_HAT_X_POSITIVE_KEY = KeyMap.KEYMAP_KEY_F3;
    private final static String G_HAT_X_NEGATIVE_KEY = KeyMap.KEYMAP_KEY_F2;
    private final static String G_HAT_Y_POSITIVE_KEY = KeyMap.KEYMAP_KEY_ESC;
    private final static String G_HAT_Y_NEGATIVE_KEY = KeyMap.KEYMAP_KEY_F5;

    private final static float G_R_B_THR_POSITIVE = 0.02f;
    private final static float G_R_B_THR_NEGATIVE = -0.02f;
    private final static float G_R_B_NUM_TIMES = 10f;
    private GamePadThread mGamePadThread;


    @Override
    public boolean onMotionKey(MotionEvent event) {

        //由于安卓对于轴输入做了一个整合处理，所以一个MotionEvent事件实际上包含了这一时刻的某一个设备的全部轴数据
        //因此我们不能像处理KeyEvent那样根据键值来分类处理
        //应该顺序处理每一个轴的数据
        //由于响应时间的要求，我们先处理耗时短的轴的数据

        if (mController.isGrabbed()){

            //LT: AXIS_LTRIGGER
            //RT: AXIS_RTRIGGER
            if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) >= G_LT_THR_VALUE && !G_LT_PRESS){
                G_LT_PRESS = true;
                sendEvent(MouseMap.MOUSEMAP_BUTTON_RIGHT, true, type_2);
            } else if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) < G_LT_THR_VALUE && G_LT_PRESS){
                G_LT_PRESS = false;
                sendEvent(MouseMap.MOUSEMAP_BUTTON_RIGHT, false, type_2);
            }

            if (event.getAxisValue(MotionEvent.AXIS_RTRIGGER) >= G_RT_THR_VALUE && !G_RT_PRESS){
                G_RT_PRESS = true;
                sendEvent(MouseMap.MOUSEMAP_BUTTON_LEFT, true, type_2);
            } else if (event.getAxisValue(MotionEvent.AXIS_RTRIGGER) < G_RT_THR_VALUE && G_RT_PRESS){
                G_RT_PRESS = false;
                sendEvent(MouseMap.MOUSEMAP_BUTTON_LEFT, false, type_2);
            }

            //左摇杆X: AXIS_X
            //左摇杆Y: AXIS_Y
            if (event.getAxisValue(MotionEvent.AXIS_X) >= G_L_B_THR_POSITIVE){
                if (!G_L_B_X_POSITIVE_PRESS){
                    G_L_B_X_POSITIVE_PRESS = true;
                    sendEvent(G_L_B_X_POSITIVE_KEY, true, type_1);
                }
                if (G_L_B_X_NEGATIVE_PRESS){
                    G_L_B_X_NEGATIVE_PRESS = false;
                    sendEvent(G_L_B_X_NEGATIVE_KEY, false, type_1);
                }
            } else if (event.getAxisValue(MotionEvent.AXIS_X) <= G_L_B_THR_NEGATIVE){
                if(!G_L_B_X_NEGATIVE_PRESS){
                    G_L_B_X_NEGATIVE_PRESS = true;
                    sendEvent(G_L_B_X_NEGATIVE_KEY, true, type_1);
                }
                if(G_L_B_X_POSITIVE_PRESS){
                    G_L_B_X_POSITIVE_PRESS = false;
                    sendEvent(G_L_B_X_POSITIVE_KEY, false, type_1);
                }
            } else {
                if(G_L_B_X_POSITIVE_PRESS){
                    G_L_B_X_POSITIVE_PRESS = false;
                    sendEvent(G_L_B_X_POSITIVE_KEY, false, type_1);
                }
                if(G_L_B_X_NEGATIVE_PRESS){
                    G_L_B_X_NEGATIVE_PRESS = false;
                    sendEvent(G_L_B_X_NEGATIVE_KEY, false, type_1);
                }
            }

            if (event.getAxisValue(MotionEvent.AXIS_Y) >= G_L_B_THR_POSITIVE){
                if (!G_L_B_Y_POSITIVE_PRESS){
                    G_L_B_Y_POSITIVE_PRESS = true;
                    sendEvent(G_L_B_Y_POSITIVE_KEY, true, type_1);
                }
                if (G_L_B_Y_NEGATIVE_PRESS){
                    G_L_B_Y_NEGATIVE_PRESS = false;
                    sendEvent(G_L_B_Y_NEGATIVE_KEY, false, type_1);
                }
            } else if (event.getAxisValue(MotionEvent.AXIS_Y) <= G_L_B_THR_NEGATIVE){
                if(!G_L_B_Y_NEGATIVE_PRESS){
                    G_L_B_Y_NEGATIVE_PRESS = true;
                    sendEvent(G_L_B_Y_NEGATIVE_KEY, true, type_1);
                }
                if(G_L_B_Y_POSITIVE_PRESS){
                    G_L_B_Y_POSITIVE_PRESS = false;
                    sendEvent(G_L_B_Y_POSITIVE_KEY, false, type_1);
                }
            } else {
                if(G_L_B_Y_POSITIVE_PRESS){
                    G_L_B_Y_POSITIVE_PRESS = false;
                    sendEvent(G_L_B_Y_POSITIVE_KEY, false, type_1);
                }
                if(G_L_B_Y_NEGATIVE_PRESS){
                    G_L_B_Y_NEGATIVE_PRESS = false;
                    sendEvent(G_L_B_Y_NEGATIVE_KEY, false, type_1);
                }
            }

            //苦力帽X: AXIS_HAT_X
            //苦力帽Y: AXIS_HAT_Y
            if (event.getAxisValue(MotionEvent.AXIS_HAT_X) >= G_HAT_THR_POSITIVE){
                if (!G_HAT_X_POSITIVE_PRESS){
                    G_HAT_X_POSITIVE_PRESS = true;
                    sendEvent(G_HAT_X_POSITIVE_KEY, true, type_1);
                }
                if (G_HAT_X_NEGATIVE_PRESS){
                    G_HAT_X_NEGATIVE_PRESS = false;
                    sendEvent(G_HAT_X_NEGATIVE_KEY, false, type_1);
                }
            } else if (event.getAxisValue(MotionEvent.AXIS_HAT_X) <= G_HAT_THR_NEGATIVE){
                if(!G_HAT_X_NEGATIVE_PRESS){
                    G_HAT_X_NEGATIVE_PRESS = true;
                    sendEvent(G_HAT_X_NEGATIVE_KEY, true, type_1);
                }
                if(G_HAT_X_POSITIVE_PRESS){
                    G_HAT_X_POSITIVE_PRESS = false;
                    sendEvent(G_HAT_X_POSITIVE_KEY, false, type_1);
                }
            } else {
                if(G_HAT_X_POSITIVE_PRESS){
                    G_HAT_X_POSITIVE_PRESS = false;
                    sendEvent(G_HAT_X_POSITIVE_KEY, false, type_1);
                }
                if(G_HAT_X_NEGATIVE_PRESS){
                    G_HAT_X_NEGATIVE_PRESS = false;
                    sendEvent(G_HAT_X_NEGATIVE_KEY, false, type_1);
                }
            }

            if (event.getAxisValue(MotionEvent.AXIS_HAT_Y) >= G_HAT_THR_POSITIVE){
                if (!G_HAT_Y_POSITIVE_PRESS){
                    G_HAT_Y_POSITIVE_PRESS = true;
                    sendEvent(G_HAT_Y_POSITIVE_KEY, true, type_1);
                }
                if (G_HAT_Y_NEGATIVE_PRESS){
                    G_HAT_Y_NEGATIVE_PRESS = false;
                    sendEvent(G_HAT_Y_NEGATIVE_KEY, false, type_1);
                }
            } else if (event.getAxisValue(MotionEvent.AXIS_HAT_Y) <= G_HAT_THR_NEGATIVE){
                if(!G_HAT_Y_NEGATIVE_PRESS){
                    G_HAT_Y_NEGATIVE_PRESS = true;
                    sendEvent(G_HAT_Y_NEGATIVE_KEY, true, type_1);
                }
                if(G_HAT_Y_POSITIVE_PRESS){
                    G_HAT_Y_POSITIVE_PRESS = false;
                    sendEvent(G_HAT_Y_POSITIVE_KEY, false, type_1);
                }
            } else {
                if(G_HAT_Y_POSITIVE_PRESS){
                    G_HAT_Y_POSITIVE_PRESS = false;
                    sendEvent(G_HAT_Y_POSITIVE_KEY, false, type_1);
                }
                if(G_HAT_Y_NEGATIVE_PRESS){
                    G_HAT_Y_NEGATIVE_PRESS = false;
                    sendEvent(G_HAT_Y_NEGATIVE_KEY, false, type_1);
                }
            }


            //右摇杆Z:  AXIS_Z
            //右摇杆RZ: AXIS_RZ
            if (mGamePadThread != null){
                int xInc = 0, yInc = 0;
                if(event.getAxisValue(MotionEvent.AXIS_Z) >= G_R_B_THR_POSITIVE){
                    xInc = (int)((event.getAxisValue(MotionEvent.AXIS_Z) - G_R_B_THR_POSITIVE) * G_R_B_NUM_TIMES);
                } else if (event.getAxisValue(MotionEvent.AXIS_Z) <= G_R_B_THR_NEGATIVE){
                    xInc = (int)((event.getAxisValue(MotionEvent.AXIS_Z) - G_R_B_THR_NEGATIVE) * G_R_B_NUM_TIMES);
                }

                if(event.getAxisValue(MotionEvent.AXIS_RZ) >= G_R_B_THR_POSITIVE){
                    yInc = (int)((event.getAxisValue(MotionEvent.AXIS_RZ) - G_R_B_THR_POSITIVE)* G_R_B_NUM_TIMES);
                } else if (event.getAxisValue(MotionEvent.AXIS_RZ) <= G_R_B_THR_NEGATIVE){
                    yInc = (int)((event.getAxisValue(MotionEvent.AXIS_RZ) - G_R_B_THR_NEGATIVE) * G_R_B_NUM_TIMES);
                }

                if( event.getAxisValue(MotionEvent.AXIS_Z) > G_R_B_THR_NEGATIVE && event.getAxisValue(MotionEvent.AXIS_Z) < G_R_B_THR_POSITIVE && event.getAxisValue(MotionEvent.AXIS_RZ) > G_R_B_THR_NEGATIVE && event.getAxisValue(MotionEvent.AXIS_RZ) < G_R_B_THR_POSITIVE)
                    mGamePadThread.setPaused(true);
                else{
                    mGamePadThread.getRunnable().setIncs(xInc, yInc);
                    mGamePadThread.setPaused(false);
                }
            }

        } else {

            //LT: AXIS_LTRIGGER
            //RT: AXIS_RTRIGGER
            if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) >= G_LT_THR_VALUE && !G_LT_PRESS){
                G_LT_PRESS = true;
                sendEvent(MouseMap.MOUSEMAP_BUTTON_RIGHT, true, type_2);
            } else if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) < G_LT_THR_VALUE && G_LT_PRESS){
                G_LT_PRESS = false;
                sendEvent(MouseMap.MOUSEMAP_BUTTON_RIGHT, false, type_2);
            }

            if (event.getAxisValue(MotionEvent.AXIS_RTRIGGER) >= G_RT_THR_VALUE && !G_RT_PRESS){
                G_RT_PRESS = true;
                sendEvent(MouseMap.MOUSEMAP_BUTTON_LEFT, true, type_2);
            } else if (event.getAxisValue(MotionEvent.AXIS_RTRIGGER) < G_RT_THR_VALUE && G_RT_PRESS){
                G_RT_PRESS = false;
                sendEvent(MouseMap.MOUSEMAP_BUTTON_LEFT, false, type_2);
            }

            //苦力帽X: AXIS_HAT_X
            //苦力帽Y: AXIS_HAT_Y
            if (event.getAxisValue(MotionEvent.AXIS_HAT_X) >= G_HAT_THR_POSITIVE){
                if (!G_HAT_X_POSITIVE_PRESS){
                    G_HAT_X_POSITIVE_PRESS = true;
                    sendEvent(G_HAT_X_POSITIVE_KEY, true, type_1);
                }
                if (G_HAT_X_NEGATIVE_PRESS){
                    G_HAT_X_NEGATIVE_PRESS = false;
                    sendEvent(G_HAT_X_NEGATIVE_KEY, false, type_1);
                }
            } else if (event.getAxisValue(MotionEvent.AXIS_HAT_X) <= G_HAT_THR_NEGATIVE){
                if(!G_HAT_X_NEGATIVE_PRESS){
                    G_HAT_X_NEGATIVE_PRESS = true;
                    sendEvent(G_HAT_X_NEGATIVE_KEY, true, type_1);
                }
                if(G_HAT_X_POSITIVE_PRESS){
                    G_HAT_X_POSITIVE_PRESS = false;
                    sendEvent(G_HAT_X_POSITIVE_KEY, false, type_1);
                }
            } else {
                if(G_HAT_X_POSITIVE_PRESS){
                    G_HAT_X_POSITIVE_PRESS = false;
                    sendEvent(G_HAT_X_POSITIVE_KEY, false, type_1);
                }
                if(G_HAT_X_NEGATIVE_PRESS){
                    G_HAT_X_NEGATIVE_PRESS = false;
                    sendEvent(G_HAT_X_NEGATIVE_KEY, false, type_1);
                }
            }

            if (event.getAxisValue(MotionEvent.AXIS_HAT_Y) >= G_HAT_THR_POSITIVE){
                if (!G_HAT_Y_POSITIVE_PRESS){
                    G_HAT_Y_POSITIVE_PRESS = true;
                    sendEvent(G_HAT_Y_POSITIVE_KEY, true, type_1);
                }
                if (G_HAT_Y_NEGATIVE_PRESS){
                    G_HAT_Y_NEGATIVE_PRESS = false;
                    sendEvent(G_HAT_Y_NEGATIVE_KEY, false, type_1);
                }
            } else if (event.getAxisValue(MotionEvent.AXIS_HAT_Y) <= G_HAT_THR_NEGATIVE){
                if(!G_HAT_Y_NEGATIVE_PRESS){
                    G_HAT_Y_NEGATIVE_PRESS = true;
                    sendEvent(G_HAT_Y_NEGATIVE_KEY, true, type_1);
                }
                if(G_HAT_Y_POSITIVE_PRESS){
                    G_HAT_Y_POSITIVE_PRESS = false;
                    sendEvent(G_HAT_Y_POSITIVE_KEY, false, type_1);
                }
            } else {
                if(G_HAT_Y_POSITIVE_PRESS){
                    G_HAT_Y_POSITIVE_PRESS = false;
                    sendEvent(G_HAT_Y_POSITIVE_KEY, false, type_1);
                }
                if(G_HAT_Y_NEGATIVE_PRESS){
                    G_HAT_Y_NEGATIVE_PRESS = false;
                    sendEvent(G_HAT_Y_NEGATIVE_KEY, false, type_1);
                }
            }

            //右摇杆Z:  AXIS_Z
            //右摇杆RZ: AXIS_RZ
            if (mGamePadThread != null){
                int xInc = 0, yInc = 0;
                if(event.getAxisValue(MotionEvent.AXIS_Z) >= G_R_B_THR_POSITIVE){
                    xInc = (int)((event.getAxisValue(MotionEvent.AXIS_Z) - G_R_B_THR_POSITIVE) * G_R_B_NUM_TIMES);
                } else if (event.getAxisValue(MotionEvent.AXIS_Z) <= G_R_B_THR_NEGATIVE){
                    xInc = (int)((event.getAxisValue(MotionEvent.AXIS_Z) - G_R_B_THR_NEGATIVE) * G_R_B_NUM_TIMES);
                }

                if(event.getAxisValue(MotionEvent.AXIS_RZ) >= G_R_B_THR_POSITIVE){
                    yInc = (int)((event.getAxisValue(MotionEvent.AXIS_RZ) - G_R_B_THR_POSITIVE)* G_R_B_NUM_TIMES);
                } else if (event.getAxisValue(MotionEvent.AXIS_RZ) <= G_R_B_THR_NEGATIVE){
                    yInc = (int)((event.getAxisValue(MotionEvent.AXIS_RZ) - G_R_B_THR_NEGATIVE) * G_R_B_NUM_TIMES);
                }

                if( event.getAxisValue(MotionEvent.AXIS_Z) > G_R_B_THR_NEGATIVE && event.getAxisValue(MotionEvent.AXIS_Z) < G_R_B_THR_POSITIVE && event.getAxisValue(MotionEvent.AXIS_RZ) > G_R_B_THR_NEGATIVE && event.getAxisValue(MotionEvent.AXIS_RZ) < G_R_B_THR_POSITIVE)
                    mGamePadThread.setPaused(true);
                else{
                    mGamePadThread.getRunnable().setIncs(xInc, yInc);
                    mGamePadThread.setPaused(false);
                }
            }

        }

        return true;
    }

    @Override
    public int getSource() {
        return 0;
    }

    @Override
    public boolean load(Context context, Controller controller) {
        this.mContext = context;
        this.mController = controller;
        //启动右摇杆的输入线程
        this.mGamePadThread = new GamePadThread();
        this.mGamePadThread.start();

        return true;
    }

    @Override
    public boolean unload() {
        //销毁输入进程
        if(mGamePadThread != null && !mGamePadThread.isInterrupted())
            mGamePadThread.interrupt();
        return true;
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
    }

    @Override
    public void runConfigure() {

    }

    @Override
    public void saveConfig() {

    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Override
    public void onPaused() {
        //销毁输入进程
        if(mGamePadThread != null && !mGamePadThread.isInterrupted())
            mGamePadThread.interrupt();
    }

    @Override
    public void onResumed() {
        //重建输入进程
        if(mGamePadThread == null || mGamePadThread.isInterrupted()){
            mGamePadThread = new GamePadThread();
            mGamePadThread.start();
        }
    }

    @Override
    public Controller getController() {
        return this.mController;
    }

    private class GamePadThread extends Thread{

        private class GamePadRunnable implements Runnable {
            private int xInc;
            private int yInc;
            private long lastPointerTime;
            private final GamePadThread mThread;

            public GamePadRunnable(GamePadThread thread){
                this.mThread = thread;
            }

            @Override
            public void run() {
                while (true){
                    if(mThread.isPaused()){
                        continue;
                    }
                    if(Thread.currentThread().isInterrupted()){
                        return;
                    }
                    if(System.currentTimeMillis() - this.lastPointerTime >= POINTER_SEND_LAG){
                        //Log.e(TAG, "Thread: do." + " xInc: " + xInc + " yInc: " + yInc);
                        this.lastPointerTime = System.currentTimeMillis();
                        mController.sendKey(new BaseKeyEvent(TAG, null, false, type_3, new int[]{xInc, yInc}));
                        GamePad.this.getController().getClient().setPointerInc(this.xInc, this.yInc);
                    }
                }
            }
            public void setIncs(int xInc, int yInc){
                //Log.e(TAG, "Thread: setIncs." + " xInc: " + xInc + " yInc: " + yInc);
                this.xInc = xInc;
                this.yInc = yInc;
                this.lastPointerTime = System.currentTimeMillis();
            }
        }

        @Override
        public void run() {
            super.run();
            mRunnable = new GamePadRunnable(this);
            mRunnable.run();
        }

        public void setPaused(boolean b){
            //Log.e(TAG, "Thread: setPaused." + isPaused);
            this.isPaused = b;
        }

        public boolean isPaused(){
            return this.isPaused;
        }

        private GamePadRunnable mRunnable;
        private boolean isPaused = true;

        public GamePadRunnable getRunnable(){
            return this.mRunnable;
        }
    }
}
