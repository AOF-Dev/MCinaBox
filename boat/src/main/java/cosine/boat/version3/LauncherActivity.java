package cosine.boat.version3;

import android.app.Activity;
import android.os.Bundle;
import com.aof.sharedmodule.Model.ArgsModel;

import cosine.boat.R;
import cosine.boat.logcat.Logcat;
import cosine.boat.logcat.LogcatService;
import ru.ivanarh.jndcrash.NDCrashError;
import ru.ivanarh.jndcrash.NDCrash;
import ru.ivanarh.jndcrash.NDCrashService;
import ru.ivanarh.jndcrash.NDCrashUnwinder;
import android.content.Intent;
import static com.aof.sharedmodule.Data.DataPathManifest.*;

public class LauncherActivity extends Activity{
    public ArgsModel argsModel;

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        //从序列化中取出参数对象
        argsModel = (ArgsModel) getIntent().getSerializableExtra("LauncherConfig");

        //初始化日志
        final String logPath = BOAT_HOME + "/log.txt";
        Logcat.initializeOutOfProcess(this, logPath, LogcatService.class);

        final String reportPath = BOAT_HOME + "/crash.txt";
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

        setContentView(R.layout.launcher_layout);

        //界面跳转至Client
        Intent intent = new Intent(this, BoatClientActivity.class);
        intent.putExtra("LauncherConfig", argsModel);
        this.startActivity(intent);
        this.finish();

    }

}
