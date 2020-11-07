package cosine.boat;

import java.io.Serializable;

public class BoatArgs implements Serializable {

    private final String[] args;
    private final String javaHome;
    private final String[] sharedLibraries;
    private final String gameDir;
    private final String stdioFile;
    private final boolean debug;

    private BoatArgs(String[] args, String javaHome, String[] sharedLibraries, String gameDir, String stdioFile, boolean debug) {
        this.args = args;
        this.javaHome = javaHome;
        this.sharedLibraries = sharedLibraries;
        this.gameDir = gameDir;
        this.stdioFile = stdioFile;
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

    public String getStdioFile() {
        return stdioFile;
    }

    public static class Builder {
        private String[] args;
        private String javaHome;
        private String[] sharedLibraries;
        private String gameDir;
        private String stdioFile;
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

        public Builder setStdioFile(String stdioFile) {
            this.stdioFile = stdioFile;
            return this;
        }

        public BoatArgs build() {
            return new BoatArgs(args, javaHome, sharedLibraries, gameDir, stdioFile, debug);
        }
    }
}
