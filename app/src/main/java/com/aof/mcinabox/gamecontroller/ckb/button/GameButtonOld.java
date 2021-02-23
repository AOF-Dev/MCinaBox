package com.aof.mcinabox.gamecontroller.ckb.button;

public class GameButtonOld {
    private String KeyName;
    private int KeySizeH;
    private int KeySizeW;
    private float KeyLX;
    private float KeyLY;
    private String KeyMain;
    private String SpecialOne;
    private String SpecialTwo;
    private boolean isAutoKeep;
    private boolean isHide;
    private boolean isMult;
    private int MainPos;
    private int SpecialOnePos;
    private int SpecialTwoPos;
    private String colorhex;
    private String TextColorHex;
    private int cornerRadius;

    public GameButtonOld(){
        super();
    }

    public GameButtonOld(String keyName, int keySizeW, int keySizeH, float keyLX, float keyLY, String keyMain, String specialOne, String specialTwo, boolean isAutoKeep, boolean isHide, boolean isMult, int mainPos, int specialOnePos, int specialTwoPos, String colorhex, int radius) {
        super();
        KeyName = keyName;
        KeySizeH = keySizeH;
        KeySizeW = keySizeW;
        KeyLX = keyLX;
        KeyLY = keyLY;
        KeyMain = keyMain;
        SpecialOne = specialOne;
        SpecialTwo = specialTwo;
        this.isAutoKeep = isAutoKeep;
        this.isHide = isHide;
        this.isMult = isMult;
        MainPos = mainPos;
        SpecialOnePos = specialOnePos;
        SpecialTwoPos = specialTwoPos;
        this.colorhex = colorhex;
        cornerRadius = radius;
    }

    public int getKeySizeH() { return KeySizeH; }

    public void setKeySizeH(int keySizeH) { KeySizeH = keySizeH; }

    public String getTextColorHex() { return TextColorHex; }

    public void setTextColorHex(String textColorHex) { TextColorHex = textColorHex; }

    public int getCornerRadius() { return cornerRadius; }

    public void setCornerRadius(int cornerRadius) { this.cornerRadius = cornerRadius; }

    public String getKeyName() {
        return KeyName;
    }

    public void setKeyName(String keyName) {
        KeyName = keyName;
    }

    public int getKeySizeW() {
        return KeySizeW;
    }

    public void setKeySizeW(int keySizeW) {
        KeySizeW = keySizeW;
    }

    public float getKeyLX() {
        return KeyLX;
    }

    public void setKeyLX(float keyLX) {
        KeyLX = keyLX;
    }

    public float getKeyLY() {
        return KeyLY;
    }

    public void setKeyLY(float keyLY) {
        KeyLY = keyLY;
    }

    public String getKeyMain() {
        return KeyMain;
    }

    public void setKeyMain(String keyMain) {
        KeyMain = keyMain;
    }

    public String getSpecialOne() {
        return SpecialOne;
    }

    public void setSpecialOne(String specialOne) {
        SpecialOne = specialOne;
    }

    public String getSpecialTwo() {
        return SpecialTwo;
    }

    public void setSpecialTwo(String specialTwo) {
        SpecialTwo = specialTwo;
    }

    public boolean isAutoKeep() {
        return isAutoKeep;
    }

    public void setAutoKeep(boolean autoKeep) {
        isAutoKeep = autoKeep;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public boolean isMult() {
        return isMult;
    }

    public void setMult(boolean mult) {
        isMult = mult;
    }

    public int getMainPos() {
        return MainPos;
    }

    public void setMainPos(int mainPos) {
        MainPos = mainPos;
    }

    public int getSpecialOnePos() {
        return SpecialOnePos;
    }

    public void setSpecialOnePos(int specialOnePos) {
        SpecialOnePos = specialOnePos;
    }

    public int getSpecialTwoPos() {
        return SpecialTwoPos;
    }

    public void setSpecialTwoPos(int specialTwoPos) {
        SpecialTwoPos = specialTwoPos;
    }

    public String getColorhex() { return colorhex; }

    public void setColorhex(String colorhex) { this.colorhex = colorhex; }
}
