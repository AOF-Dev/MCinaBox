package com.aof.mcinabox.gamecontroller.ckb.support;

public class CkbThemeRecorder {
    public final static int COLOR_INDEX_LENGTH = 3;

    private final int[] themeColors = new int[COLOR_INDEX_LENGTH];
    private int cornerRadiusPt;
    private int designIndex;
    private int textColor;

    public CkbThemeRecorder setColors(int index, int color) {
        if (index >= 0 && index < COLOR_INDEX_LENGTH) {
            this.themeColors[index] = color;
        }
        return this;
    }

    public CkbThemeRecorder setCornerRadiusPt(int radius) {
        this.cornerRadiusPt = radius;
        return this;
    }

    public int getCornerRadius() {
        return (int) (cornerRadiusPt * 0.01f * 180);
    }

    public int getCornerRadiusPt() {
        return this.cornerRadiusPt;
    }

    public void setDesignIndex(int index) {
        this.designIndex = index;
    }

    public void setTextColor(int color) {
        this.textColor = color;
    }

    public int getColor(int index) {
        if (index >= 0 && index < COLOR_INDEX_LENGTH) {
            return themeColors[index];
        } else {
            return 0;
        }
    }

    public int[] getColors() {
        return themeColors;
    }

    public int getDesignIndex() {
        return designIndex;
    }

    public int getTextColor() {
        return textColor;
    }
}
