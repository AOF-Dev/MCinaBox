package cosine.boat;

import android.app.Application;
import android.app.Activity;
import android.os.Bundle;

public class BoatApplication extends Application implements Application.ActivityLifecycleCallbacks
{
	public static Activity mCurrentActivity;
	public static Activity getCurrentActivity(){
		return BoatApplication.mCurrentActivity;
	}

	@Override
	public void onActivityCreated(Activity p1, Bundle p2)
	{
		// TODO: Implement this method
		
	}

	@Override
	public void onActivityStarted(Activity p1)
	{
		// TODO: Implement this method
		BoatApplication.mCurrentActivity = p1;
		System.out.println(BoatApplication.mCurrentActivity);
	}

	@Override
	public void onActivityResumed(Activity p1)
	{
		// TODO: Implement this method

	}

	@Override
	public void onActivityPaused(Activity p1)
	{
		// TODO: Implement this method

	}

	@Override
	public void onActivityStopped(Activity p1)
	{
		// TODO: Implement this method
	}

	@Override
	public void onActivitySaveInstanceState(Activity p1, Bundle p2)
	{
		// TODO: Implement this method
	}

	@Override
	public void onActivityDestroyed(Activity p1)
	{
		// TODO: Implement this method
	}


	@Override
	public void onCreate()
	{
		// TODO: Implement this method
		super.onCreate();
		this.registerActivityLifecycleCallbacks(this);
	}

}
