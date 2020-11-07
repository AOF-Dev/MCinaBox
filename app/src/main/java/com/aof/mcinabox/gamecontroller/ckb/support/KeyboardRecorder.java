package com.aof.mcinabox.gamecontroller.ckb.support;

public class KeyboardRecorder {
    private int screenWidth;
    private int screenHeight;
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
}
