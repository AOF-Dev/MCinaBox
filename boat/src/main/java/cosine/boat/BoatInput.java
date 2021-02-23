package cosine.boat;

public class BoatInput {

    static void setMouseButton(int button, boolean isPressed) {
        setMouseButton(System.nanoTime(), button, isPressed);
    }

    static void setPointer(int x, int y) {
        setPointer(System.nanoTime(), x, y);
    }

    static void setKey(int keyCode, int keyChar, boolean isPressed) {
        setKey(System.nanoTime(), isPressed, keyCode, keyChar);
    }

    static native int[] getPointer();

    private static native void setMouseButton(long time, int button, boolean isPressed);

    private static native void setPointer(long time, int x, int y);

    private static native void setKey(long time, boolean isPressed, int keyCode, int keyChar);

    static {
        System.loadLibrary("boat");
    }
}
