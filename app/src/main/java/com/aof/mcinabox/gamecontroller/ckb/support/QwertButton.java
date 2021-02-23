package com.aof.mcinabox.gamecontroller.ckb.support;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.aof.mcinabox.R;

public class QwertButton extends androidx.appcompat.widget.AppCompatButton {

    public QwertButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BaseButton);
        setButtonName(array.getString(R.styleable.BaseButton_button_name));
        array.recycle();
    }

    private String button_name;

    public String getButtonName() {
        return button_name;
    }

    public void setButtonName(String buttonName) {
        button_name = buttonName;
    }
}
