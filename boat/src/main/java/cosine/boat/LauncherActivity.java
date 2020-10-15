package cosine.boat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.aof.mcinabox.definitions.models.BoatArgs;

import cosine.boat.logcat.Logcat;
import cosine.boat.logcat.LogcatService;
import ru.ivanarh.jndcrash.NDCrash;
import ru.ivanarh.jndcrash.NDCrashError;
import ru.ivanarh.jndcrash.NDCrashService;
import ru.ivanarh.jndcrash.NDCrashUnwinder;

import static com.aof.mcinabox.definitions.manifest.AppManifest.BOAT_CACHE_HOME;

public class LauncherActivity extends Activity {
    public BoatArgs boatArgs;

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        //从序列化中取出参数对象
        boatArgs = (BoatArgs) getIntent().getSerializableExtra("LauncherConfig");

        if (boatArgs.getDebug()) {
            final String logPath = BOAT_CACHE_HOME + "/log.txt";
            Logcat.initializeOutOfProcess(this, logPath, LogcatService.class);

            final String reportPath = BOAT_CACHE_HOME + "/crash.txt";
            System.out.println("Crash report: " + reportPath);
            final NDCrashError error = NDCrash.initializeOutOfProcess(this, reportPath, NDCrashUnwinder.libcorkscrew, NDCrashService.class);
            if (error == NDCrashError.ok) {
                System.out.println("NDCrash: OK");
                // Initialization is successful.
            } else {
                System.out.println("NDCrash: Error");
                System.out.println(error.name());
                // Initialization failed, check error value.
            }
        }

        //界面跳转至Client
        Intent intent = new Intent(this, BoatActivity.class);
        intent.putExtra("LauncherConfig", boatArgs);
        this.startActivity(intent);
        this.finish();

    }

}
