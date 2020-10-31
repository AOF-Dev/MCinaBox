package cosine.boat;

public class BoatInput {
    private final static String TAG = "BoatInput";

    public static final int KEY_PRESS = 2;
    public static final int KEY_RELEASE = 3;
    public static final int BUTTON_PRESS = 4;
    public static final int BUTTON_RELEASE = 5;
    public static final int MOTION_NOTIFY = 6;

    public static final int BUTTON_1 = 1;
    public static final int BUTTON_2 = 2;
    public static final int BUTTON_3 = 3;
    public static final int BUTTON_4 = 4;
    public static final int BUTTON_5 = 5;
    public static final int BUTTON_6 = 6;
    public static final int BUTTON_7 = 7;

    public static final int CURSOR_ENABLED = 1;
    public static final int CURSOR_DISABLED = 0;

    public static void setMouseButton(int button, boolean press) {
        send(System.nanoTime(), press ? BUTTON_PRESS : BUTTON_RELEASE, button, 0);
    }

    public static void setPointer(int x, int y) {
        send(System.nanoTime(), MOTION_NOTIFY, x, y);
    }

    public static void setKey(int keyCode, int keyChar, boolean press) {
        send(System.nanoTime(), press ? KEY_PRESS : KEY_RELEASE, keyCode, keyChar);
    }

    public static int[] getPointer() {
        return get();
    }

    public static native int[] get();

    public static native void send(long time, int type, int p1, int p2);

    static {
        System.loadLibrary("boat");
    }
}
