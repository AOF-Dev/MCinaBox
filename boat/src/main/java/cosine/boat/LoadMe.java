package cosine.boat;

import com.aof.mcinabox.definitions.models.BoatArgs;

import static com.aof.mcinabox.definitions.manifest.AppManifest.BOAT_CACHE_HOME;

public class LoadMe {

    public static native int chdir(String str);

    public static native int jliLaunch(String[] strArr);

    public static native void redirectStdio(String file);

    public static native void setenv(String str, String str2);

    public static native void setupJLI();

    public static native int dlopen(String name);

    private final static String TAG = "LoadMe";

    static {
        System.loadLibrary("boat");
    }

    public static int exec(BoatArgs args) {
        try {

            setenv("HOME", args.getGamedir());
            setenv("JAVA_HOME", args.getJava_home());
            setenv("LIBGL_MIPMAP", "3");

            // sharedlibraries
            for (String str : args.getShared_libraries()) {
                dlopen(str);
            }

            setupJLI();
            redirectStdio(BOAT_CACHE_HOME + "/boat_output.txt");
            chdir(args.getGamedir());
            jliLaunch(args.getArgs());

        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
}
