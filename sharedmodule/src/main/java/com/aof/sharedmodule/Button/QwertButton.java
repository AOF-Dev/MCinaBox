package com.aof.sharedmodule.Button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.aof.sharedmodule.R;

public class QwertButton extends androidx.appcompat.widget.AppCompatButton {

    public QwertButton(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.QwertButton);
        setButtonIndex(array.getInt(R.styleable.QwertButton_ButtonIndex,-1));
        setButtonName(array.getString(R.styleable.QwertButton_ButtonName));
    }
    private int ButtonIndex;
    private String ButtonName;

    public int getButtonIndex() { return ButtonIndex; }
    public void setButtonIndex(int buttonIndex) { ButtonIndex = buttonIndex; }
    public String getButtonName() { return ButtonName; }
    public void setButtonName(String buttonName) { ButtonName = buttonName; }
}
