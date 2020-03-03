package cosine.boat.logcat;

import android.content.Context;
import android.content.Intent;
/*
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
*/

/**
 * Main binding class for NDCrash functionality.
 */
public class Logcat {

    /**
     * Initializes NDCrash library signal handler using out-of-process mode. Should be called from
     * onCreate() method of your subclass of Application.
     *
     * @param context         Context instance. Used to determine a socket name and start a service.
     * @param crashReportPath Path where a crash report is saved.
     * @param unwinder        Used unwinder. See ndcrash_unwinder type in ndcrash.h.
     * @param serviceClass    Class of background service. Used when we need to use a custom subclass
     *                        of NDCrashUnwinder to use as a background service. If you didn't subclass
     *                        NDCrashUnwinder, please pass NDCrashUnwinder.class.
     * @return Error status.
     */
    public static int initializeOutOfProcess(
            /*@NonNull*/ Context context,
            /*@Nullable*/ String crashReportPath,
            /*@NonNull*/ Class<? extends LogcatService> serviceClass) {
        if (LogcatUtils.isLogcatServiceProcess(context, serviceClass)) {
            // If it's a background crash service process we don't need to initialize anything,
            // we treat this situation as no error because this method is designed to call from
            // Application.onCreate().
            return 1;
        }
        // Saving service class, we should be able to stop it on de-initialization.
        mServiceClass = serviceClass;
        // Starting crash reporting service. Only from main process.
        if (LogcatUtils.isMainProcess(context)) {
            final Intent serviceIntent = new Intent(context, serviceClass);
            serviceIntent.putExtra(LogcatService.EXTRA_REPORT_FILE, crashReportPath);
            try {
                context.startService(serviceIntent);
            } catch (RuntimeException e) {
                return 2;
            }
        }
        // Initializing signal handler.
        return 0;
    }


    /**
     * De-initializes NDCrash library signal handler using out-of-process mode.
     *
     * @param context Context instance. Used to stop a service.
     * @return Flag whether de-initialization was successful.
     */
    public static boolean deInitializeOutOfProcess(/*@NonNull*/ Context context) {
        if (mServiceClass != null) {
            context.stopService(new Intent(context, mServiceClass));
            mServiceClass = null;
        }
        return true;
    }

    

    

    /**
     * Background service class for out-of-process mode.
     */
    //@Nullable
    private static Class<? extends LogcatService> mServiceClass = null;



}
