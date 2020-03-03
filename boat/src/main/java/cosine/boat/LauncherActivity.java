package cosine.boat;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import cosine.boat.logcat.Logcat;
import cosine.boat.logcat.LogcatService;
import ru.ivanarh.jndcrash.NDCrashError;
import ru.ivanarh.jndcrash.NDCrash;
import ru.ivanarh.jndcrash.NDCrashService;
import ru.ivanarh.jndcrash.NDCrashUnwinder;
import android.content.Intent;
import android.widget.TextView;
import java.io.*;
import android.util.*;
import java.util.*;
import android.os.Handler;
import android.os.Message;

public class LauncherActivity extends Activity implements View.OnClickListener, View.OnLongClickListener
{

    public Button playButton;
	public EditText configText;
    public Button excuteButton;
	public boolean mode = false;
    public EditText inputText;
	public TextView outputText;
	
	private class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg)
		{
			
			switch (msg.what){
				case -1:
					outputText.append("No such file!\n");
					break;
				case -2:
					outputText.append("Installing...\n");
					break;
				case -3:
					outputText.append("Package has been extracted in " + getDir("runtime", 0).getAbsolutePath() + "\n");
					break;
				case -4:
					outputText.append("Try to set executing permission: true\n");
					break;
				case -5:
					outputText.append("Try to set executing permission: false\n");
					break;
				case -6:
					outputText.append("Setting up property...\n");
					break;
				case -7:
					outputText.append("Finished!\n");
					break;
				default:
					outputText.append(result + "\n");
					outputText.append("\n");
					outputText.append(error + "\n");
					outputText.append("Command process exited with value: " + msg.what + "\n");
			}
			
			
		}
	}
	private MyHandler mHandler;
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
		
		final String logPath = "/mnt/sdcard/boat/log.txt";
		Logcat.initializeOutOfProcess( this, logPath, LogcatService.class);
		
		final String reportPath = "/mnt/sdcard/boat/crash.txt";
		System.out.println("Crash report: " + reportPath);
		final NDCrashError error = NDCrash.initializeOutOfProcess( this, reportPath, NDCrashUnwinder.libcorkscrew, NDCrashService.class);
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
		
		this.configText.setText( getPreferences(MODE_PRIVATE).getString("config", "/sdcard/boat/config.txt"));
		getPreferences(MODE_PRIVATE).edit().putString("config", this.configText.getText().toString()).commit();
		
		if (this.configText.getText().toString() != null && !this.configText.getText().toString().equals("")){
			if (!new File(this.configText.getText().toString()).exists()){
				LauncherConfig.toFile(this.configText.getText().toString(), new LauncherConfig());
				
			}
					
		}
		this.excuteButton = (Button)findViewById(R.id.launcher_excute_button);
		this.excuteButton.setOnClickListener(this);
		this.excuteButton.setOnLongClickListener(this);
		this.inputText = (EditText)findViewById(R.id.launcher_input_text);
		this.outputText = (TextView)findViewById(R.id.launcher_output_text);
		
		
		outputText.append("Runtime directory: " + this.getDir("runtime", 0) + "\n");
		
		
    }
	
	private String result = "";
	private String error = "";
	
	public void excuteCommand(final String args[]){
		
		
		new Thread(){
			@Override
			public void run(){
				try
				{
					Process p = new ProcessBuilder(args).start();
					BufferedReader bri=new BufferedReader(new InputStreamReader(p.getInputStream()));

					result = "";
					String linei;
					while((linei = bri.readLine()) != null){
						result = result + "\n" + linei;			
						
					}
					
					BufferedReader bre=new BufferedReader(new InputStreamReader(p.getErrorStream()));

					error = "";
					String linee;
					while((linee = bre.readLine()) != null){
						error = error + "\n" + linee;			

					}

					p.waitFor();
					int e = p.exitValue();
					
					Message endMsg=new Message();
					endMsg.what = e;
					mHandler.sendMessage(endMsg);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}.start();
		
		
	}
	
	public void install(final String packagePath){
		new Thread(){
			@Override
			public void run(){
				Message endMsg = null;
				File packageFile = new File(packagePath);
				if (!packageFile.exists()){
					endMsg = new Message();
					endMsg.what = -1;
					mHandler.sendMessage(endMsg);
					
				}
				endMsg = new Message();
				endMsg.what = -2;
				mHandler.sendMessage(endMsg);
				
				Utils.extractTarXZ(packagePath, getDir("runtime", 0));
				endMsg = new Message();
				endMsg.what = -3;
				mHandler.sendMessage(endMsg);
				
				if (Utils.setExecutable(getDir("runtime", 0))){
					endMsg = new Message();
					endMsg.what = -4;
					mHandler.sendMessage(endMsg);
				}
				else{
					endMsg = new Message();
					endMsg.what = -5;
					mHandler.sendMessage(endMsg);
				}
				endMsg = new Message();
				endMsg.what = -6;
				mHandler.sendMessage(endMsg);
				
				LauncherConfig config = LauncherConfig.fromFile(configText.getText().toString());
				config.remove("runtimePath");
				config.put("runtimePath", getDir("runtime", 0).getAbsolutePath());
				LauncherConfig.toFile(configText.getText().toString(), config);
				endMsg = new Message();
				endMsg.what = -7;
				mHandler.sendMessage(endMsg);
			}
		}.start();
	}
	
	//OnClickListener
    public void onClick(View v) {
        if (v == this.playButton) {
			
			
            Intent i = new Intent(this, BoatClientActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString("config", configText.getText().toString());
			i.putExtras(bundle);
			this.startActivity(i);
			
			
        }
		else if(v == this.excuteButton){
			if (!mode){
				if (!inputText.getText().equals("")){
					String packagePath = inputText.getText().toString();
					install(packagePath);
				}
			}
			else{
				if (!inputText.getText().equals("")){
					String cmd = inputText.getText().toString();
					excuteCommand(cmd.split(" "));

				}
			}
			
		}
		
    }
	
	//OnLongClickListener
	@Override
	public boolean onLongClick(View p1)
	{
		// TODO: Implement this method
		if (p1 == excuteButton){
			excuteButton.setText(R.string.excute);
			mode = true;
			outputText.append("\n~~~For debuging only~~~\n");
			return true;
		}
		return false;
	}

	@Override
	protected void onPause()
	{
		// TODO: Implement this method
		super.onPause();
		getPreferences(MODE_PRIVATE).edit().putString("config", this.configText.getText().toString()).commit();
	}
    
}
