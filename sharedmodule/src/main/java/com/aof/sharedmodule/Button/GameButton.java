package com.aof.sharedmodule.Button;
import android.content.Context;

//为避免循环依赖,且顾及到前后端适配的可扩展性
//需要copy一份adapt包保持对象的一致性

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
    private int MainPos;
    private int SpecialOnePos;
    private int SpecialTwoPos;
    private int cornerRadius;
    private String colorHex;
    private int MainIndex;
    private int SpecialOneIndex;
    private int SpecialTwoIndex;
    private String TextColorHex;
    private int KeyLX_dp;
    private int KeyLY_dp;
    private int KeySizeW;
    private int KeySizeH;

    public int getMainIndex() { return MainIndex; }
    public void setMainIndex(int mainIndex) { MainIndex = mainIndex; }
    public int getSpecialOneIndex() { return SpecialOneIndex; }
    public void setSpecialOneIndex(int specialOneIndex) { SpecialOneIndex = specialOneIndex;}
    public int getSpecialTwoIndex() { return SpecialTwoIndex; }
    public void setSpecialTwoIndex(int specialTwoIndex) { SpecialTwoIndex = specialTwoIndex; }
    public String getTextColorHex() { return TextColorHex; }
    public void setTextColorHex(String textColorHex) { TextColorHex = textColorHex; }
    public int getKeySizeH() { return KeySizeH; }
    public void setKeySizeH(int keySizeH) { KeySizeH = keySizeH; }

    public int getKeySizeW() {
        return KeySizeW;
    }
    public void setKeySizeW(int keySizeW) {
        KeySizeW = keySizeW;
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
    public int getMainPos(){return MainPos;}
    public void setMainPos(int MainPos){this.MainPos = MainPos;}
    public int getSpecialTwoPos() { return SpecialTwoPos; }
    public void setSpecialTwoPos(int specialTwoPos) { SpecialTwoPos = specialTwoPos; }
    public int getSpecialOnePos() { return SpecialOnePos; }
    public void setSpecialOnePos(int specialOnePos) { SpecialOnePos = specialOnePos; }

}