package com.aof.mcinabox.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class PicUtils {

    //高斯模糊
    public static Bitmap blur(Context context, int radius, final Bitmap bitmap){

        RenderScript rs = RenderScript.create(context);
        Bitmap bitmap1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Allocation allocation = Allocation.createFromBitmap(rs, bitmap1);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, allocation.getElement());
        blur.setInput(allocation);
        blur.setRadius(radius);

        blur.forEach(allocation);
        allocation.copyTo(bitmap1);

        rs.destroy();
        return bitmap1;
    }

}
