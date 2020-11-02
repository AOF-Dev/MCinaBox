package cosine.boat;

import java.io.Serializable;

public class BoatArgs implements Serializable {

    private String[] args;
    private String javaHome;
    private String[] sharedLibraries;
    private String gameDir;
    private boolean debug;

    public boolean getDebug(){
        return debug;
    }

    public BoatArgs setDebug(boolean b){
        this.debug = b;
        return this;
    }

    public String getJavaHome(){
        return javaHome;
    }

    public BoatArgs setJavaHome(String path) {
        this.javaHome = path;
        return this;
    }

    public String getGameDir(){
        return gameDir;
    }

    public BoatArgs setGameDir(String dir){
        this.gameDir = dir;
        return this;
    }

    public String[] getArgs() {
        return args;
    }

    public BoatArgs setArgs(String[] args) {
        this.args = args;
        return this;
    }

    public BoatArgs setSharedLibraries(String[] libraries){
        this.sharedLibraries = libraries;
        return this;
    }

    public String[] getSharedLibraries(){
        return sharedLibraries;
    }
}
