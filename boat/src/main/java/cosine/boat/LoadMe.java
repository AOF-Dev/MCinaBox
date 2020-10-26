package cosine.boat;

import com.aof.mcinabox.definitions.models.BoatArgs;

import static com.aof.mcinabox.definitions.manifest.AppManifest.BOAT_CACHE_HOME;

public class LoadMe {

    public static native void chdir(String str);

    public static native int jliLaunch(String[] strArr);

    public static native void redirectStdio(String file);

    public static native void setenv(String name, String value);

    public static native void setupJLI();

    public static native void dlopen(String name);

    static {
        System.loadLibrary("boat");
    }

    public static void exec(BoatArgs args) {
        try {
            setenv("HOME", args.getGamedir());
            setenv("JAVA_HOME", args.getJava_home());
            setenv("LIBGL_MIPMAP", "3");

            for (String str : args.getShared_libraries()) {
                dlopen(str);
            }

            setupJLI();
            redirectStdio(BOAT_CACHE_HOME + "/boat_output.txt");
            chdir(args.getGamedir());
            jliLaunch(args.getArgs());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
