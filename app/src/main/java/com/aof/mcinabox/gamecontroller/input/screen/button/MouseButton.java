package com.aof.mcinabox.gamecontroller.input.screen.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.aof.mcinabox.R;

public class MouseButton extends BaseButton {

    public MouseButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MouseButton);
        setMouseName(array.getString(R.styleable.MouseButton_mouse_name));
        array.recycle();
    }

    String MouseName;

    public String getMouseName() {
        return MouseName;
    }

    public void setMouseName(String mouseName) {
        MouseName = mouseName;
    }
}
