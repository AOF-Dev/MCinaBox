package com.aof.sharedmodule.Button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.aof.sharedmodule.R;

public class MouseButton extends PublicButton {

    public MouseButton(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MouseButton);
        setMouseIndex((byte)array.getInt(R.styleable.MouseButton_mouse_index,-1));
        setMouseName(array.getString(R.styleable.MouseButton_mouse_name));
        array.recycle();
    }
    String MouseName;
    byte MouseIndex;

    public String getMouseName() {
        return MouseName;
    }

    public void setMouseName(String mouseName) {
        MouseName = mouseName;
    }

    public byte getMouseIndex() {
        return MouseIndex;
    }

    public void setMouseIndex(byte mouseIndex) {
        MouseIndex = mouseIndex;
    }
}
