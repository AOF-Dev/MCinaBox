package ru.ivanarh.jndcrash;

import android.content.Context;
import android.content.Intent;
/*
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
*/

/**
 * Main binding class for NDCrash functionality.
 */
public class NDCrash {

    /**
     * Initializes NDCrash library signal handler using in-process mode.
     *
     * @param crashReportPath Path where a crash report is saved.
     * @param unwinder        Used unwinder. See ndcrash_unwinder type in ndcrash.h.
     * @return Error status.
     */
    public static NDCrashError initializeInProcess(/*@Nullable */String crashReportPath, NDCrashUnwinder unwinder) {
        return NDCrashError.values()[nativeInitializeInProcess(crashReportPath, unwinder.ordinal())];
    }

    /// Native implementation method.
    private static native int nativeInitializeInProcess(/*@Nullable*/ String crashReportPath, int unwinder);

    /**
     * De-initializes NDCrash library signal handler using in-process mode.
     *
     * @return Flag whether de-initialization was successful.
     */
    public static boolean deInitializeInProcess() {
        return nativeDeInitializeInProcess();
    }

    /// Native implementation method.
    private static native boolean nativeDeInitializeInProcess();

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
    public static NDCrashError initializeOutOfProcess(
            /*@NonNull*/ Context context,
            /*@Nullable*/ String crashReportPath,
            /*@NonNull */NDCrashUnwinder unwinder,
            /*@NonNull*/ Class<? extends NDCrashService> serviceClass) {
        if (NDCrashUtils.isCrashServiceProcess(context, serviceClass)) {
            // If it's a background crash service process we don't need to initialize anything,
            // we treat this situation as no error because this method is designed to call from
            // Application.onCreate().
            return NDCrashError.ok;
        }
        // Saving service class, we should be able to stop it on de-initialization.
        mServiceClass = serviceClass;
        // Starting crash reporting service. Only from main process.
        if (NDCrashUtils.isMainProcess(context)) {
            final Intent serviceIntent = new Intent(context, serviceClass);
            serviceIntent.putExtra(NDCrashService.EXTRA_REPORT_FILE, crashReportPath);
            serviceIntent.putExtra(NDCrashService.EXTRA_UNWINDER, unwinder.ordinal());
            try {
                context.startService(serviceIntent);
            } catch (RuntimeException e) {
                return NDCrashError.error_service_start_failed;
            }
        }
        // Initializing signal handler.
        return NDCrashError.values()[nativeInitializeOutOfProcess(getSocketName(context))];
    }

    /// Native implementation method.
    private static native int nativeInitializeOutOfProcess(/*@NonNull */String socketName);

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
        return nativeDeInitializeOutOfProcess();
    }

    /// Native implementation method.
    private static native boolean nativeDeInitializeOutOfProcess();

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
    static NDCrashError startOutOfProcessDaemon(
            /*@NonNull */Context context,
            /*@Nullable */String crashReportPath,
            /*@NonNull*/ NDCrashUnwinder unwinder,
            /*@Nullable */OnCrashCallback callback) {
        if (NDCrashUtils.isMainProcess(context)) {
            return NDCrashError.error_wrong_process;
        }
        mOnCrashCallback = callback;
        final NDCrashError result = NDCrashError.values()[nativeStartOutOfProcessDaemon(getSocketName(context), crashReportPath, unwinder.ordinal())];
        if (result != NDCrashError.ok) {
            mOnCrashCallback = null;
        }
        return result;
    }

    /// Native implementation method.
    private static native int nativeStartOutOfProcessDaemon(
            /*@NonNull*/ String socketName,
            /*@Nullable*/ String crashReportPath,
            int unwinder);

    /**
     * Stops NDCrash out-of-process unwinding daemon.
     *
     * @return Flag whether daemon stopping was successful.
     */
    static boolean stopOutOfProcessDaemon() {
        final boolean result = nativeStopOutOfProcessDaemon();
        mOnCrashCallback = null;
        return result;
    }

    /// Native implementation method.
    private static native boolean nativeStopOutOfProcessDaemon();

    /**
     * Instance of crash callback.
     */
    //@Nullable
    private static volatile OnCrashCallback mOnCrashCallback = null;

    /**
     * Background service class for out-of-process mode.
     */
    //@Nullable
    private static Class<? extends NDCrashService> mServiceClass = null;

    /**
     * Runs on crash callback if it was set. This method is called from native code.
     *
     * @param reportPath Path to file containing crash report.
     */
    private static void runOnCrashCallback(String reportPath) {
        final OnCrashCallback callback = mOnCrashCallback;
        if (callback != null) {
            callback.onCrash(reportPath);
        }
    }

    /**
     * Retrieves a socket name from Context instance. We use a package name with additional suffix
     * to make sure that socket name doesn't intersect with another application.
     *
     * @param context Context to use.
     * @return Socket name.
     */
    private static String getSocketName(/*@NonNull */Context context) {
        return context.getPackageName() + ".ndcrash";
    }

    /**
     * Crash callback that allows to process a report immediately after crash. Works only in out of
     * process mode.
     */
    public interface OnCrashCallback {

        /**
         * Runs when crash is detected. This method is run from background (daemon) thread.
         * This method allows to process a report immediately after crash.
         *
         * @param reportPath Path to file containing crash report.
         */
        void onCrash(String reportPath);

    }

    static {
        System.loadLibrary("jndcrash");
    }
}
