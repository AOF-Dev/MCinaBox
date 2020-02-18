package com.aof.mcinabox.keyboardUtils;

import android.content.Context;
import android.widget.Button;

public class GameButton extends androidx.appcompat.widget.AppCompatButton {
    public GameButton(Context context){
        super(context);
    }
    private int specialOne;
    private int specialTwo;
    private boolean isKeep;
    private boolean isHide;
    private boolean isMult;
    private int keyboardIndex;

    public int getKeyboardIndex() { return keyboardIndex; }
    public void setKeyboardIndex(int keyboardIndex) { this.keyboardIndex = keyboardIndex; }
    public boolean isHide() { return isHide; }
    public void setHide(boolean hide) { isHide = hide; }
    public boolean isKeep() { return isKeep; }
    public void setKeep(boolean keep) { isKeep = keep; }
    public int getSpecialTwo() { return specialTwo;}
    public void setSpecialTwo(int specialTwo) { this.specialTwo = specialTwo; }
    public int getSpecialOne() { return specialOne; }
    public void setSpecialOne(int specialOne) { this.specialOne = specialOne; }
    public boolean isMult() { return isMult; }
    public void setMult(boolean mult) { isMult = mult; }

}
