package cosine.boat;

import static cosine.boat.definitions.manifest.AppManifest.BOAT_CACHE_HOME;

public class LoadMe {

    public static native void chdir(String str);

    public static native int jliLaunch(String[] strArr);

    public static native void redirectStdio(String file);

    public static native void setenv(String name, String value);

    public static native void setupJLI();

    public static native void dlopen(String name);

    public static void exec(BoatArgs args) {
        try {
            setenv("HOME", args.getGameDir());
            setenv("JAVA_HOME", args.getJavaHome());
            setenv("LIBGL_MIPMAP", "3");

            for (String str : args.getSharedLibraries()) {
                dlopen(str);
            }

            setupJLI();
            redirectStdio(BOAT_CACHE_HOME + "/boat_output.txt");
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
