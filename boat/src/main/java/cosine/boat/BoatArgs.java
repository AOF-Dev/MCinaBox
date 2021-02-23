package cosine.boat;

import java.io.Serializable;
import java.util.Map;

public class BoatArgs implements Serializable {

    private String[] args;
    private String javaHome;
    private String[] sharedLibraries;
    private String gameDir;
    private String tmpdir;
    private String stdioFile;
    private String platform;
    private String jvmMode;
    private boolean debug;
    private Map<String, String> systemEnv;

    public BoatArgs(String[] args, String javaHome, String[] sharedLibraries, String gameDir, String tmpdir, String stdioFile, boolean debug, String platform, String jvmMode, Map<String, String> env) {
        this.args = args;
        this.javaHome = javaHome;
        this.sharedLibraries = sharedLibraries;
        this.gameDir = gameDir;
        this.stdioFile = stdioFile;
        this.debug = debug;
        this.tmpdir = tmpdir;
        this.platform = platform;
        this.jvmMode = jvmMode;
        this.systemEnv = env;
    }

    public BoatArgs() {
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

    public String getTmpDir() {
        return tmpdir;
    }

    public String getPlatform() {
        return platform;
    }

    public String getJvmMode() {
        return jvmMode;
    }

    public Map<String, String> getSystemEnv() {
        return systemEnv;
    }

    public static class Builder {
        private final BoatArgs mArgs;

        public Builder() {
            mArgs = new BoatArgs();
        }

        public Builder setDebug(boolean debug) {
            mArgs.debug = debug;
            return this;
        }

        public Builder setJavaHome(String path) {
            mArgs.javaHome = path;
            return this;
        }

        public Builder setGameDir(String dir) {
            mArgs.gameDir = dir;
            return this;
        }

        public Builder setArgs(String[] args) {
            mArgs.args = args;
            return this;
        }

        public Builder setSharedLibraries(String[] libraries) {
            mArgs.sharedLibraries = libraries;
            return this;
        }

        public Builder setStdioFile(String stdioFile) {
            mArgs.stdioFile = stdioFile;
            return this;
        }

        public Builder setTmpDir(String dir) {
            mArgs.tmpdir = dir;
            return this;
        }

        public Builder setPlatform(String platform) {
            mArgs.platform = platform;
            return this;
        }

        public Builder setJvmMode(String mode) {
            mArgs.jvmMode = mode;
            return this;
        }

        public Builder setSystemEnv(Map<String, String> env) {
            mArgs.systemEnv = env;
            return this;
        }

        public BoatArgs build() {
            return mArgs;
        }
    }
}
