package cosine.boat;

import android.system.Os;
import android.util.Log;

import java.util.Map;

public class LoadMe {

    public static native int chdir(String str);

    public static native int jliLaunch(String[] strArr);

    public static native void redirectStdio(String file);

    public static native void setenv(String name, String value);

    public static native void setupJLI();

    public static native int dlopen(String name);

    public static native void patchLinker();

    public static void exec(BoatArgs args) {
        patchLinker();
        try {
            /* set JRE Environment */
            setenv("HOME", args.getGameDir());
            setenv("JAVA_HOME", args.getJavaHome());
            setenv("TMPDIR", args.getTmpDir());
            setenv("LD_LIBRARY_PATH", args.getJavaHome() + "/lib/" + args.getPlatform() + "/" + args.getJvmMode() + ":" +
                    args.getJavaHome() + "/lib/" + args.getPlatform() + "/jli:" +
                    args.getJavaHome() + "/lib/" + args.getPlatform() + ":" +
                    "/system/lib64:" +
                    "/vendor/lib64:" +
                    "/vendor/lib64/hw");
            setenv("PATH", args.getJavaHome() + "/bin:" + Os.getenv("PATH"));
            //Fix colors since GL4ES v1.1.5
            //with new runtime pack which includes gl4es v1.1.5
            setenv("LIBGL_NORMALIZE", "1");
            //Disable MIPMAP
            setenv("LIBGL_MIPMAP", "3");
            //DISABLE VBO since GL4ES v1.1.4
            setenv("LIBGL_USEVBO", "0");

            if(args.getSystemEnv() != null){
                for (Map.Entry<String, String> entry : args.getSystemEnv().entrySet()) {
                    setenv(entry.getKey(), entry.getValue());
                }
            }

            /* open libraries*/
            for (String str : args.getSharedLibraries()) {
                dlopen(str);
            }

            setupJLI();
            redirectStdio(args.getStdioFile());
            chdir(args.getGameDir());
            jliLaunch(args.getArgs());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        System.loadLibrary("boat");
    }
}
