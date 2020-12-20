package cosine.boat;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import cosine.boat.databinding.ActivityBoatBinding;

public class BoatActivity extends AppCompatActivity implements View.OnSystemUiVisibilityChangeListener, TextureView.SurfaceTextureListener {
    private static final String TAG = "BoatActivity";
    private static final int SYSTEM_UI_HIDE_DELAY_MS = 3000;

    public static final String EXTRA_BOAT_ARGS = "BoatArgs";

    public static IBoat boatInterface;

    public ActivityBoatBinding binding;
    private BoatArgs boatArgs;
    private Timer timer;
    private TimerTask systemUiTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nOnCreate();

        if (!nIsLoaded()) {
            Log.e(TAG, "onCreate: Native code did not load successfully.");
            Toast.makeText(this, "An error occurred while initialising native code!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check that the interface is not null, as it's required for the app to work
        if (boatInterface == null) {
            Log.e(TAG, "onCreate: boatInterface is null.");
            Toast.makeText(this, "Boat interface is not set!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get and check for the required Boat arguments
        boatArgs = (BoatArgs) getIntent().getSerializableExtra(EXTRA_BOAT_ARGS);
        if (boatArgs == null) {
            Log.e(TAG, "onCreate: boatArgs is null.");
            Toast.makeText(this, "Boat arguments are missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inflate and bind the activity view
        binding = ActivityBoatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the SurfaceHolder callback to this class
        // to get the ANativeWindow instance
        binding.surfaceView.setSurfaceTextureListener(this);

        timer = new Timer();

        // Call the interface onCreate method once we've finished setting up the view,
        // to avoid any errors of un-initialized objects
        boatInterface.onActivityCreate(this);
    }

    private native void nOnCreate();

    @Override
    protected void onDestroy() {
        nOnDestroy();
        super.onDestroy();
    }

    private native void nOnDestroy();

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(this);
            hideSystemUI(decorView);
        } else {
            View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(null);
            if (systemUiTimerTask != null) systemUiTimerTask.cancel();
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            if (systemUiTimerTask != null) systemUiTimerTask.cancel();
            systemUiTimerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> hideSystemUI(getWindow().getDecorView()));
                }
            };
            timer.schedule(systemUiTimerTask, SYSTEM_UI_HIDE_DELAY_MS);
        }
    }

    private void hideSystemUI(View decorView) {
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private native void nSurfaceCreated(Surface surface);

    private native boolean nIsLoaded();

    private native void nSurfaceDestroyed(Surface surface);

    void setGrabCursor(boolean isGrabbed) {
        runOnUiThread(() -> boatInterface.setGrabCursor(isGrabbed));
    }

    // Override addContentView method to dynamically add views
    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        if (params instanceof RelativeLayout.LayoutParams) {
            binding.baseLayout.addView(view, params);
        } else {
            RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(params.width, params.height);
            binding.baseLayout.addView(view, newParams);
        }
    }

    // Override the dispatchKeyEvent method to redirect events to Boat
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (boatInterface.dispatchKeyEvent(event)) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    // Override the dispatchGenericMotionEvent method to redirect events to Boat
    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (boatInterface.dispatchGenericMotionEvent(event)) {
            return true;
        }

        return super.dispatchGenericMotionEvent(event);
    }

    public int[] getPointer() {
        return BoatInput.getPointer();
    }

    public void setKey(int keyCode, int keyChar, boolean isPressed) {
        BoatInput.setKey(keyCode, keyChar, isPressed);
    }

    public void setMouseButton(int button, boolean isPressed) {
        BoatInput.setMouseButton(button, isPressed);
    }

    public void setPointer(int x, int y) {
        BoatInput.setPointer(x, y);
    }

    static {
        System.loadLibrary("boat");
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "surfaceCreated: called.");
        nSurfaceCreated(new Surface(surface));
        new Thread() {
            @Override
            public void run() {
                LoadMe.exec(boatArgs);
            }
        }.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "surface changed: width = " + width + ", height = " + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        Log.d(TAG, "surfaceDestroyed: called.");
        nSurfaceDestroyed(new Surface(surface));
        boatInterface.onStop();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }

    public interface IBoat {

        void onActivityCreate(BoatActivity boatActivity);

        void setGrabCursor(boolean isGrabbed);

        void onStop();

        boolean dispatchKeyEvent(KeyEvent event);

        boolean dispatchGenericMotionEvent(MotionEvent event);
    }
}
