package com.aof.mcinabox.keyboardUtils;

import android.content.Context;

public class GameButton extends androidx.appcompat.widget.AppCompatButton {
    public GameButton(Context context){
        super(context);
    }
    private String specialOne;
    private String specialTwo;
    private boolean isKeep;
    private boolean isHide;
    private boolean isMult;
    private String KeyMain;
    private String shape;
    private int MainPos;
    private int SpecialOnePos;
    private int SpecialTwoPos;
    private int cornerRadius;
    private String colorHex;
    private int KeyLX_dp;
    private int KeyLY_dp;
    private int KeySize;


    public int getKeySize() {
        return KeySize;
    }

    public void setKeySize(int keySize) {
        KeySize = keySize;
    }

    public int getKeyLX_dp() { return KeyLX_dp; }
    public void setKeyLX_dp(int keyLX_dp) { KeyLX_dp = keyLX_dp; }
    public int getKeyLY_dp() { return KeyLY_dp; }
    public void setKeyLY_dp(int keyLY_dp) { KeyLY_dp = keyLY_dp; }
    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
    public int getCornerRadius() { return cornerRadius; }
    public void setCornerRadius(int cornerRadius) { this.cornerRadius = cornerRadius; }
    public String getKeyMain() { return KeyMain; }
    public void setKeyMain(String keyMain) { this.KeyMain = keyMain; }
    public boolean isHide() { return isHide; }
    public void setHide(boolean hide) { isHide = hide; }
    public boolean isKeep() { return isKeep; }
    public void setKeep(boolean keep) { isKeep = keep; }
    public String getSpecialTwo() { return specialTwo;}
    public void setSpecialTwo(String specialTwo) { this.specialTwo = specialTwo; }
    public String getSpecialOne() { return specialOne; }
    public void setSpecialOne(String specialOne) { this.specialOne = specialOne; }
    public boolean isMult() { return isMult; }
    public void setMult(boolean mult) { isMult = mult; }
    public String getShape(){return shape;}
    public void setShape(String shape){this.shape = shape;}
    public int getMainPos(){return MainPos;}
    public void setMainPos(int MainPos){this.MainPos = MainPos;}
    public int getSpecialTwoPos() { return SpecialTwoPos; }
    public void setSpecialTwoPos(int specialTwoPos) { SpecialTwoPos = specialTwoPos; }
    public int getSpecialOnePos() { return SpecialOnePos; }
    public void setSpecialOnePos(int specialOnePos) { SpecialOnePos = specialOnePos; }

}
