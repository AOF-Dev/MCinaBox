package cosine.boat;

import java.io.Serializable;

public class BoatArgs implements Serializable {

    private final String[] args;
    private final String javaHome;
    private final String[] sharedLibraries;
    private final String gameDir;
    private final boolean debug;

    private BoatArgs(String[] args, String javaHome, String[] sharedLibraries, String gameDir, boolean debug) {
        this.args = args;
        this.javaHome = javaHome;
        this.sharedLibraries = sharedLibraries;
        this.gameDir = gameDir;
        this.debug = debug;
    }

    public boolean getDebug() {
        return debug;
    }

    public String getJavaHome() {
        return javaHome;
    }

    public String getGameDir() {
        return gameDir;
    }

    public String[] getArgs() {
        return args;
    }

    public String[] getSharedLibraries() {
        return sharedLibraries;
    }

    public static class Builder {
        private String[] args;
        private String javaHome;
        private String[] sharedLibraries;
        private String gameDir;
        private boolean debug;

        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder setJavaHome(String path) {
            this.javaHome = path;
            return this;
        }

        public Builder setGameDir(String dir) {
            this.gameDir = dir;
            return this;
        }

        public Builder setArgs(String[] args) {
            this.args = args;
            return this;
        }

        public Builder setSharedLibraries(String[] libraries) {
            this.sharedLibraries = libraries;
            return this;
        }

        public BoatArgs build() {
            return new BoatArgs(args, javaHome, sharedLibraries, gameDir, debug);
        }
    }
}
