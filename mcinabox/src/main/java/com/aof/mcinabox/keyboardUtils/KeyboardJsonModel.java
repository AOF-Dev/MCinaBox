package com.aof.mcinabox.keyboardUtils;

public class KeyboardJsonModel {
    private String KeyName;
    private int KeySize;
    private int KeyAlpha;
    private float KeyLX;
    private float KeyLY;
    private String KeyMain;
    private String SpecialOne;
    private String SpecialTwo;
    private boolean isAutoKeep;
    private boolean isHide;
    private boolean isMult;
    private String shape;
    private int MainPos;
    private int SpecialOnePos;
    private int SpecialTwoPos;
    private String colorhex;

    public KeyboardJsonModel(){
        super();
    }

    public KeyboardJsonModel(String keyName, int keySize, int keyAlpha, float keyLX, float keyLY, String keyMain, String specialOne, String specialTwo, boolean isAutoKeep, boolean isHide, boolean isMult, String shape, int mainPos, int specialOnePos, int specialTwoPos,String colorhex) {
        super();
        KeyName = keyName;
        KeySize = keySize;
        KeyAlpha = keyAlpha;
        KeyLX = keyLX;
        KeyLY = keyLY;
        KeyMain = keyMain;
        SpecialOne = specialOne;
        SpecialTwo = specialTwo;
        this.isAutoKeep = isAutoKeep;
        this.isHide = isHide;
        this.isMult = isMult;
        this.shape = shape;
        MainPos = mainPos;
        SpecialOnePos = specialOnePos;
        SpecialTwoPos = specialTwoPos;
        this.colorhex = colorhex;
    }

    public String getKeyName() {
        return KeyName;
    }

    public void setKeyName(String keyName) {
        KeyName = keyName;
    }

    public int getKeySize() {
        return KeySize;
    }

    public void setKeySize(int keySize) {
        KeySize = keySize;
    }

    public int getKeyAlpha() {
        return KeyAlpha;
    }

    public void setKeyAlpha(int keyAlpha) {
        KeyAlpha = keyAlpha;
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

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
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
