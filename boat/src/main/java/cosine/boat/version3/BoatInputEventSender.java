package cosine.boat.version3;
import android.app.Activity;

public class BoatInputEventSender{
	
	public static int KeyPress              = 2;
	public static int KeyRelease            = 3;
	public static int ButtonPress           = 4;
	public static int ButtonRelease	        = 5;
	public static int MotionNotify          = 6;

	public static int Button1               = 1;
	public static int Button2               = 2;
	public static int Button3               = 3;
	public static int Button4               = 4;
	public static int Button5               = 5;
	public static int Button6               = 6;
	public static int Button7               = 7;
	
	
	public static void setMouseButton(int button, boolean press) {
        
        send(System.nanoTime(), press ? ButtonPress : ButtonRelease, button, 0);
    }
	public static void setPointer(int x, int y) {
        send(System.nanoTime(), MotionNotify, x, y);
    }
    
	public static void setKey(int keyCode, boolean press , int keyChar){
		
		send(System.nanoTime(), press ? KeyPress : KeyRelease, keyCode, keyChar);
	}
	
	
	public static native void send(long time, int type, int p1, int p2);
		
	public static void setCursorMode(int mode){
		Activity activity = BoatApplication.getCurrentActivity();
		if (activity instanceof BoatClientActivity){
			BoatClientActivity client = (BoatClientActivity)activity;
			client.setCursorMode(mode);
		}
	}
	static {
		System.loadLibrary("client3");
	}
}
