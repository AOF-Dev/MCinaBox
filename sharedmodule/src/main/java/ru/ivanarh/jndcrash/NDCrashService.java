package ru.ivanarh.jndcrash;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
//import android.support.annotation.CallSuper;
import android.util.Log;

/**
 * Service for out-of-process crash handling daemon. Should be run from a separate process.
 */
public class NDCrashService extends Service implements NDCrash.OnCrashCallback
{
    /**
     * Log tag.
     */
    private static final String TAG = "JNDCRASH";

    /**
     * Indicates if a daemon was started.
     */
    private static boolean mDaemonStarted = false;

    /**
     * A name for shared preferences.
     */
    private static final String PREFS_NAME = "NDCrashService";

    /**
     * Key for report file in arguments.
     */
    public static final String EXTRA_REPORT_FILE = "report_file";

    /**
     * Key for unwinder in arguments. Ordinal value is saved as integer.
     */
    public static final String EXTRA_UNWINDER = "unwinder";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        NDCrashUnwinder unwinder = null;
        String reportPath = null;
        if (intent != null) {
            unwinder = NDCrashUnwinder.values()[intent.getIntExtra(EXTRA_UNWINDER, NDCrashUnwinder.libunwind.ordinal())];
            reportPath = intent.getStringExtra(EXTRA_REPORT_FILE);
            // Using the same keys as extras.
            final SharedPreferences.Editor editor = preferences.edit();
            if (unwinder != null) {
                editor.putInt(EXTRA_UNWINDER, unwinder.ordinal());
            } else {
                editor.remove(EXTRA_UNWINDER);
            }
            if (reportPath != null) {
                editor.putString(EXTRA_REPORT_FILE, reportPath);
            } else {
                editor.remove(EXTRA_REPORT_FILE);
            }
            editor.apply();
        } else {
            unwinder = NDCrashUnwinder.values()[preferences.getInt(EXTRA_UNWINDER, NDCrashUnwinder.libunwind.ordinal())];
            reportPath = preferences.getString(EXTRA_REPORT_FILE, null);
        }
        if (!mDaemonStarted) {
            if (unwinder != null) {
                mDaemonStarted = true;
                final NDCrashError initResult = NDCrash.startOutOfProcessDaemon(this, reportPath, unwinder, this);
                if (initResult != NDCrashError.ok) {
                    Log.e(TAG, "Couldn't start NDCrash out-of-process daemon with unwinder: " + unwinder + ", error: " + initResult);
                } else {
                    Log.i(TAG, "Out-of-process unwinding daemon is started with unwinder: " + unwinder + " report path: " +
                            (reportPath != null ? reportPath : "null"));
                    onDaemonStart(unwinder, reportPath, initResult);
                }
            } else {
                Log.e(TAG, "Couldn't start NDCrash out-of-process daemon: unwinder is unknown.");
            }
        } else {
            Log.i(TAG, "NDCrash out-of-process daemon is already started.");
        }
        // START_REDELIVER_INTENT may seem better but found by experimental way that when we return
        // this value a service is restarted significantly slower (with a longer delay) after its
        // process is killed. So a workaround is used: Saving initialization parameters to shared
        // preferences and reading them when intent is null.
        return Service.START_STICKY;
    }

    @Override //@CallSuper
    public void onDestroy() {
		
        if (mDaemonStarted) {
            mDaemonStarted = false;
            final boolean stoppedSuccessfully = NDCrash.stopOutOfProcessDaemon();
            Log.i(TAG, "Out-of-process daemon " + (stoppedSuccessfully ? "is successfully stopped." : "failed to stop."));
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service doesn't support to be bound.
        return null;
    }

    @Override
    public void onCrash(String reportPath) {
    }

    /**
     * Called on daemon start attempt, both on success and failed.
     *
     * @param unwinder Unwinder that is used.
     * @param reportPath Path to crash report file.
     * @param result Start result.
     */
    protected void onDaemonStart(NDCrashUnwinder unwinder, String reportPath, NDCrashError result) {
    }
}
