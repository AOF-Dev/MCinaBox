package cosine.boat;

import java.io.IOException;
import java.util.*;
import java.io.*;

public class LoadMe {
    
    public static native int chdir(String str);
    public static native void jliLaunch(String[] strArr);
    public static native void redirectStdio(String file);
    public static native void setenv(String str, String str2);
	public static native void setupJLI();
	public static native int dlopen(String name);
	
    static {
        System.loadLibrary("boat");
    }
	
    public static int exec(LauncherConfig config, BoatClientActivity activity) {
        try {
			
			MinecraftVersion mcVersion = MinecraftVersion.fromDirectory(new File(config.get("currentVersion")));
			
			String runtimePath = config.get("runtimePath");
			String libraryPath = runtimePath + "/j2re-image/lib/aarch32/jli:" + runtimePath + "/j2re-image/lib/aarch32:" + runtimePath;
			String home = config.get("home");
			String classPath = runtimePath + "/lwjgl-jemalloc.jar:" + runtimePath + "/lwjgl-tinyfd.jar:" + runtimePath + "/lwjgl-opengl.jar:" + runtimePath + "/lwjgl-openal.jar:" + runtimePath + "/lwjgl-glfw.jar:" + runtimePath + "/lwjgl-stb.jar:" + runtimePath + "/lwjgl.jar:" +  mcVersion.getClassPath(config);
			
			setenv("HOME", home);
			setenv("JAVA_HOME" ,runtimePath + "/j2re-image");
			//setenv("BOAT_INPUT_PORT", Integer.toString(activity.mInputEventSender.port));
            
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libfreetype.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/jli/libjli.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/client/libjvm.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libverify.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libjava.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libnet.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libnio.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libawt.so");
			dlopen(runtimePath + "/j2re-image/lib/aarch32/libawt_headless.so");
			
			dlopen("libserver.so");	
			
			dlopen(runtimePath + "/libopenal.so.1");
			dlopen(runtimePath + "/libGL.so.1");
			dlopen("/sdcard/libglfw.so");
			
			//dlopen(runtimePath + "/libjemalloc.so.2");
			//System.load("/data/data/jackpal.androidterm/0/libjemalloc.so.2");
			dlopen(runtimePath + "/liblwjgl_stb.so");
			dlopen(runtimePath + "/liblwjgl_tinyfd.so");
			dlopen(runtimePath + "/liblwjgl_opengl.so");
			dlopen(runtimePath + "/liblwjgl.so");
					
			setupJLI();	
			
            redirectStdio(home + "/boat_output.txt");
            chdir(home);
			
			Vector<String> args = new Vector<String>();
			
			
			
			args.add(runtimePath +  "/j2re-image/bin/java");
			args.add("-cp");
			args.add(classPath);
			args.add("-Djava.library.path=" + libraryPath);
			
			args.add("-Dorg.lwjgl.util.Debug=true");
			args.add("-Dorg.lwjgl.util.DebugLoader=true");
			String extraJavaFlags[] = config.get("extraJavaFlags").split(" ");
			for (String flag : extraJavaFlags){
				args.add(flag);
			}
			
			args.add(mcVersion.mainClass);
			
			String minecraftArgs[] = mcVersion.getMinecraftArguments(config);	
			for (String flag : minecraftArgs){
				args.add(flag);
			}
			String extraMinecraftArgs[] = config.get("extraMinecraftFlags").split(" ");
			for (String flag : extraMinecraftArgs){
				args.add(flag);
			}
			
			String finalArgs[] = new String[args.size()];
			for (int i = 0; i < args.size(); i++){
				finalArgs[i] = args.get(i);
				System.out.println(finalArgs[i]);
			}
            jliLaunch(finalArgs);
			
			
        } catch (Exception e) {
            e.printStackTrace();
			return 1;
        }
		return 0;
    }
}





