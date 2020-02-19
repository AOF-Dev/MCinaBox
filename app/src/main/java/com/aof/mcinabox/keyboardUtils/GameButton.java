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
    private int shape;
    private int MainPos;
    private int SpecialOnePos;
    private int SpecialTwoPos;

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
    public int getShape(){return shape;}
    public void setShape(int shape){this.shape = shape;}
    public int getMainPos(){return MainPos;}
    public void setMainPos(int MainPos){this.MainPos = MainPos;}
    public int getSpecialTwoPos() { return SpecialTwoPos; }
    public void setSpecialTwoPos(int specialTwoPos) { SpecialTwoPos = specialTwoPos; }
    public int getSpecialOnePos() { return SpecialOnePos; }
    public void setSpecialOnePos(int specialOnePos) { SpecialOnePos = specialOnePos; }

}
