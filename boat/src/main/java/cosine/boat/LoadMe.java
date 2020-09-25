package cosine.boat;

import com.aof.mcinabox.definitions.models.BoatArgs;
import static com.aof.mcinabox.definitions.manifest.AppManifest.*;

public class LoadMe {

	public static native int chdir(String str);
	public static native int jliLaunch(String[] strArr);
	public static native void redirectStdio(String file);
	public static native void setenv(String str, String str2);
	public static native void setupJLI();
	public static native int dlopen(String name);

	static {
		System.loadLibrary("boat");
	}

	public static int exec(BoatArgs args) {
		try {

			setenv("HOME", args.getGamedir());
			setenv("JAVA_HOME" ,args.getJava_home());

			// sharedlibraries
			for(String str : args.getShared_libraries()){
				dlopen(str);
			}

			setupJLI();
			redirectStdio(BOAT_CACHE_HOME + "/boat_output.txt");
			chdir(args.getGamedir());

			String finalArgs[] = new String[] {"java"};
			for (int i = 0; i < finalArgs.length; i++){

				System.out.println(finalArgs[i]);
			}
			System.out.println("OpenJDK exited with code : " + jliLaunch(args.getArgs()));

		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
		return 0;
	}
}
