package com.aof.mcinabox.gamecontroller.ckb.support;

public class KeyboardRecorder {

    public final static int VERSION_UNKNOWN = 0;
    public final static int VERSION_0_1_3 = 1;
    public final static int VERSION_0_1_4_P = 2;
    public final static int VERSION_THIS = VERSION_0_1_4_P;

    private int screenWidth;
    private int screenHeight;
    private int versionCode;
    private GameButtonRecorder[] games;

    public void setScreenArgs(int sw, int sh) {
        this.screenWidth = sw;
        this.screenHeight = sh;
    }

    public void setRecorderDatas(GameButtonRecorder[] data) {
        this.games = data;
    }

    public GameButtonRecorder[] getRecorderDatas() {
        return games;
    }

    public int[] getScreenData() {
        return new int[]{screenWidth, screenHeight};
    }

    public KeyboardRecorder setVersionCode(int version){
        this.versionCode = version;
        return this;
    }

    public int getVersionCode(){
        return this.versionCode;
    }
}
