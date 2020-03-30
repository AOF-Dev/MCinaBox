package cosine.boat.logcat;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Process;
//import android.support.annotation.NonNull;

/**
 * Contains some utility code.
 */
public class LogcatUtils {

    /**
     * Checks if a current process is a main process of application.
     *
     * @param context Current context.
     * @return Flag whether it's a main process.
     */
    public static boolean isMainProcess(/*@NonNull*/ Context context) {
        final int pid = Process.myPid();
        final String packageName = context.getPackageName();
        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (final ActivityManager.RunningAppProcessInfo info : manager.getRunningAppProcesses()) {
                if (info.pid == pid) {
                    return packageName.equals(info.processName);
                }
            }
        }
        return true;
    }

    /**
     * Checks if a current process is a background crash service process.
     *
     * @param context      Current context.
     * @param serviceClass Class of background crash reporting service.
     * @return Flag whether a current process is a background crash service process.
     */
    public static boolean isLogcatServiceProcess(/*@NonNull */Context context,/* @NonNull */Class<? extends LogcatService> serviceClass) {
        final ServiceInfo serviceInfo;
        try {
            serviceInfo = context.getPackageManager().getServiceInfo(new ComponentName(context, serviceClass), 0);
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
        final int pid = Process.myPid();
        final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (final ActivityManager.RunningAppProcessInfo info : manager.getRunningAppProcesses()) {
                if (info.pid == pid) {
                    return serviceInfo.processName.equals(info.processName);
                }
            }
        }
        return false;
    }
}
