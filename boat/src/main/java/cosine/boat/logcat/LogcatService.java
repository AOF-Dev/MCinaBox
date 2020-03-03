package cosine.boat.logcat;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.content.Context;
import java.io.IOException;

/**
 * Service for out-of-process crash handling daemon. Should be run from a separate process.
 */
public class LogcatService extends Service
{

    /**
     * Indicates if a daemon was started.
     */
    private static boolean mDaemonStarted = false;

    /**
     * A name for shared preferences.
     */
    private static final String PREFS_NAME = "LogcatService";

    /**
     * Key for report file in arguments.
     */
    public static final String EXTRA_REPORT_FILE = "report_file";

	private static Process mLogcatProcess;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String reportPath = null;
        if (intent != null) {
            reportPath = intent.getStringExtra(EXTRA_REPORT_FILE);
            // Using the same keys as extras.
            final SharedPreferences.Editor editor = preferences.edit();
            if (reportPath != null) {
                editor.putString(EXTRA_REPORT_FILE, reportPath);
            } else {
                editor.remove(EXTRA_REPORT_FILE);
            }
            editor.apply();
        } else {
            reportPath = preferences.getString(EXTRA_REPORT_FILE, null);
        }
        if (!mDaemonStarted) {
			mDaemonStarted = true;
			final int initResult = startOutOfProcessDaemon(this, reportPath);
			if (initResult != 0) {
				//Log.e(TAG, "Couldn't start NDCrash out-of-process daemon with unwinder: " + unwinder + ", error: " + initResult);
			} else {
				//Log.i(TAG, "Out-of-process unwinding daemon is started with unwinder: " + unwinder + " report path: " +
				//	  (reportPath != null ? reportPath : "null"));
				
			}
        } else {
            //Log.i(TAG, "NDCrash out-of-process daemon is already started.");
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
            final boolean stoppedSuccessfully = stopOutOfProcessDaemon();
            //Log.i(TAG, "Out-of-process daemon " + (stoppedSuccessfully ? "is successfully stopped." : "failed to stop."));
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Service doesn't support to be bound.
        return null;
    }
	
	/**
     * Starts NDCrash out-of-process unwinding daemon. This is necessary for out of process crash
     * handling. This method is run from a service that works in separate process.
     *
     * @param context         Context instance. Used to determine a socket name.
     * @param crashReportPath Path where to save a crash report.
     * @param unwinder        Unwinder to use.
     * @param callback        Callback to execute when a crash has occurred.
     * @return Error status.
     */
    static int startOutOfProcessDaemon(
		/*@NonNull */Context context,
		/*@Nullable */String crashReportPath) {
        if (LogcatUtils.isMainProcess(context)) {
            return 1;
        }
		try
		{
			mLogcatProcess = new ProcessBuilder("logcat", "-v", "long", "-f", crashReportPath).start();
		}
		catch (IOException e)
		{}
        return 0;
    }

    /**
     * Stops NDCrash out-of-process unwinding daemon.
     *
     * @return Flag whether daemon stopping was successful.
     */
    static boolean stopOutOfProcessDaemon() {
        mLogcatProcess.destroy();
        return true;
    }
	
}
