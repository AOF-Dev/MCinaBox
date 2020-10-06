package cosine.boat;

import android.view.KeyEvent;

public class BoatInput{

    private final static String TAG = "BoatInput";
	public static final int KeyPress              = 2;
	public static final int KeyRelease            = 3;
	public static final int ButtonPress           = 4;
	public static final int ButtonRelease	      = 5;
	public static final int MotionNotify          = 6;

	public static final int Button1               = 1;
	public static final int Button2               = 2;
	public static final int Button3               = 3;
	public static final int Button4               = 4;
	public static final int Button5               = 5;
	public static final int Button6               = 6;
	public static final int Button7               = 7;

	public static final int CursorEnabled         = 1;
	public static final int CursorDisabled        = 0;

	static {
        System.loadLibrary("boat");
    }

	public static void setMouseButton(int button, boolean press) {
        send(System.nanoTime(), press ? ButtonPress : ButtonRelease, button, 0);
    }
	public static void setPointer(int x, int y) {
        send(System.nanoTime(), MotionNotify, x, y);
    }

	public static void setKey(int keyCode, int keyChar, boolean press){
		send(System.nanoTime(), press ? KeyPress : KeyRelease, keyCode, keyChar);
	}

	public static int[] getPointer(){
		return get();
	}

	public static native int[] get();

	public static native void send(long time, int type, int p1, int p2);

	// To be called by lwjgl/glfw.
	public static BoatActivity mActivity;
	public static void setCursorMode(int mode){
		if(mActivity != null){
			mActivity.setCursorMode(mode);
		}
	}

    //实现按键事件分发
    public static void dispatchKeyEvent(long downTime, long eventTime, int action,
                                        int code, int repeat, int metaState,
                                        int deviceId, int scancode, int flags, int source) {
        KeyEvent event = new KeyEvent(downTime, eventTime, action, code, repeat, metaState, deviceId, scancode, flags, source);
        mActivity.dispatchKeyEvent(event);
    }

}
