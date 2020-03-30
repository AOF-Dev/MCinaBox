package cosine.boat.version3;

import com.aof.sharedmodule.Model.ArgsModel;

import static com.aof.sharedmodule.Data.DataPathManifest.*;

public class LoadMe {
    
    public static native int chdir(String str);
    public static native void jliLaunch(String[] strArr);
    public static native void redirectStdio(String file);
    public static native void setenv(String str, String str2);
	public static native void setupJLI();
	public static native int dlopen(String name);
	
    static {
        System.loadLibrary("boat3");
    }
	
    public static int exec(ArgsModel args, BoatClientActivity activity) {
        try {

			String runtimePath = RUNTIME_HOME;
			String home = args.getHome();

			setenv("HOME", home);
			setenv("JAVA_HOME" ,runtimePath + "/j2re-image");

			dlopen(runtimePath + "/j2re-image/lib/aarch32/libfreetype.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/jli/libjli.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/client/libjvm.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libverify.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libjava.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libnet.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libnio.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libawt.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libawt_headless.so");
			dlopen("libserve3.so");
			dlopen(runtimePath + "/libopenal.so.1");
			dlopen(runtimePath + "/libGL.so.1");
			dlopen(runtimePath + "/lwjgl3/libglfw.so");
			dlopen(runtimePath + "/lwjgl3/liblwjgl_stb.so");
			dlopen(runtimePath + "/lwjgl3/liblwjgl_tinyfd.so");
			dlopen(runtimePath + "/lwjgl3/liblwjgl_opengl.so");
			dlopen(runtimePath + "/lwjgl3/liblwjgl.so");

			setupJLI();
            redirectStdio(home + "/boat_output.txt");
            chdir(home);
            jliLaunch(args.getArgs());

        } catch (Exception e) {
            e.printStackTrace();
			return 1;
        }
		return 0;
    }
}





