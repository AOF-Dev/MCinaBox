package cosine.boat2;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import cosine.boat2.logcat.Logcat;
import cosine.boat2.logcat.LogcatService;
import ru.ivanarh.jndcrash.NDCrashError;
import ru.ivanarh.jndcrash.NDCrash;
import ru.ivanarh.jndcrash.NDCrashService;
import ru.ivanarh.jndcrash.NDCrashUnwinder;
import android.content.Intent;
import android.widget.TextView;
import java.io.*;
import android.util.*;
import android.os.Handler;
import android.os.Message;


public class LauncherActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {


    public Button playButton;
    public EditText configText;
    public Button excuteButton;
    public boolean mode = false;
    public EditText inputText;
    public TextView outputText;

    private MyHandler mHandler;
    private File busybox;
    private String result = "";
    private String error = "";

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                default:
                    outputText.append("\n" + result + "\n");
                    outputText.append("\n");
                    outputText.append("\n" + error + "\n");

                    break;
            }
        }
    }

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        final String logPath = "/mnt/sdcard/boat/log.txt";
        Logcat.initializeOutOfProcess(this, logPath, LogcatService.class);

        final String reportPath = "/mnt/sdcard/boat/crash.txt";
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
        this.mHandler = new MyHandler();
        this.playButton = (Button) findViewById(R.id.launcher_play_button);
        this.playButton.setOnClickListener(this);
        this.configText = (EditText) findViewById(R.id.launcher_config_text);

        this.configText.setText(getPreferences(MODE_PRIVATE).getString("config", "/sdcard/boat/config.txt"));
        getPreferences(MODE_PRIVATE).edit().putString("config", this.configText.getText().toString()).commit();

        if (this.configText.getText().toString() != null && !this.configText.getText().toString().equals("")) {
            if (!new File(this.configText.getText().toString()).exists()) {
                LauncherConfig.toFile(this.configText.getText().toString(), new LauncherConfig());
            }
        }

        this.excuteButton = (Button) findViewById(R.id.launcher_excute_button);
        this.excuteButton.setOnClickListener(this);
        this.excuteButton.setOnLongClickListener(this);
        this.inputText = (EditText) findViewById(R.id.launcher_input_text);
        this.outputText = (TextView) findViewById(R.id.launcher_output_text);
        this.busybox = new File(this.getDir("runtime", 0), "busybox");

        if (!busybox.exists()) {
            Utils.extractAsset(this.getAssets(), "busybox", busybox.getAbsolutePath());
            Log.i("Launcher", "Busybox has been extracted in " + busybox.getAbsolutePath());
            busybox.setExecutable(true);
        } else {
            Log.i("Launcher", "Busybox has been installed in " + busybox.getAbsolutePath());
        }

        outputText.append("Runtime directory: " + this.getDir("runtime", 0) + "\n");
        outputText.append("Busybox: " + busybox + "\n");

    }

    public void excuteCommand(final String args[]) {


        new Thread() {
            @Override
            public void run() {
                try {
                    Process p = new ProcessBuilder(args).start();
                    BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    result = "";
                    String linei;
                    while ((linei = bri.readLine()) != null) {
                        result = result + "\n" + linei;

                    }

                    BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    error = "";
                    String linee;
                    while ((linee = bre.readLine()) != null) {
                        error = error + "\n" + linee;

                    }

                    p.waitFor();
                    int e = p.exitValue();

                    Message endMsg = new Message();
                    endMsg.what = e;
                    mHandler.sendMessage(endMsg);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    //OnClickListener
    public void onClick(View v) {
        if (v == this.playButton) {


            Intent i = new Intent(this, BoatClientActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("config", configText.getText().toString());
            i.putExtras(bundle);
            this.startActivity(i);


        } else if (v == this.excuteButton) {
            if (!mode) {
                if (!inputText.getText().equals("")) {
                    String packagePath = inputText.getText().toString();

                    excuteCommand(new String[]{busybox.getAbsolutePath(), "tar", "-xJvf", packagePath, "-C", getDir("runtime", 0).getAbsolutePath()});
                    excuteCommand(new String[]{busybox.getAbsolutePath(), "chmod", "-R", "0777", getDir("runtime", 0).getAbsolutePath()});

                    LauncherConfig config = LauncherConfig.fromFile(this.configText.getText().toString());
                    config.remove("runtimePath");
                    config.put("runtimePath", getDir("runtime", 0).getAbsolutePath());
                    LauncherConfig.toFile(this.configText.getText().toString(), config);

                }
            } else {
                if (!inputText.getText().equals("")) {
                    String cmd = inputText.getText().toString();
                    if (cmd.equals("busybox")) {
                        excuteCommand(new String[]{busybox.getAbsolutePath()});
                    } else {
                        cmd = busybox + " " + cmd;
                        excuteCommand(cmd.split(" "));
                    }

                }
            }

        }

    }


    //OnLongClickListener
    @Override
    public boolean onLongClick(View p1) {
        // TODO: Implement this method
        if (p1 == excuteButton) {
            excuteButton.setText(R.string.excute);
            mode = true;
            outputText.append("\n~~~For debuging only~~~\n");
            return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        // TODO: Implement this method
        super.onPause();
        getPreferences(MODE_PRIVATE).edit().putString("config", this.configText.getText().toString()).commit();
    }
}
