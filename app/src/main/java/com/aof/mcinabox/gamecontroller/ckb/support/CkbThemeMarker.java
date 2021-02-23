package com.aof.mcinabox.gamecontroller.ckb.support;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

public class CkbThemeMarker {

    private final static String TAG = "CkbThemeMaker";
    public final static int DESIGN_SIGNLE_FILL = 1; //单层纯色背景
    public final static int DESIGN_SIGNLE_RING = 2; //单边框空心背景
    public final static int DESIGN_DOUBLE_RING = 3; //双边框空心背景
    public final static int DESIGN_BLACK_SHADOW = 4; //阴影背景

    public final static String[] DESIGNS = new String[]{"1", "2", "3", "4"};

    public static LayerDrawable getDesign(final CkbThemeRecorder recorder) {

        switch (recorder.getDesignIndex()) {
            case DESIGN_SIGNLE_FILL:
                return getDesign_signle_fill(recorder);
            case DESIGN_SIGNLE_RING:
                return getDesign_signle_ring(recorder);
            case DESIGN_DOUBLE_RING:
                return getDesign_double_ring(recorder);
            case DESIGN_BLACK_SHADOW:
                return getDesign_black_shadow(recorder);
            default:
                return null;
        }
    }

    private static LayerDrawable getDesign_signle_fill(CkbThemeRecorder recorder) {
        int radiusSize = recorder.getCornerRadius();
        int mainColor = recorder.getColor(0);

        float[] outerR = new float[]{radiusSize, radiusSize, radiusSize, radiusSize, radiusSize, radiusSize, radiusSize, radiusSize};
        RoundRectShape rectShape = new RoundRectShape(outerR, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable();
        shapeDrawable.setShape(rectShape);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setColor(mainColor);

        Drawable[] layers = new Drawable[]{shapeDrawable};

        return new LayerDrawable(layers);
    }

    private static LayerDrawable getDesign_signle_ring(CkbThemeRecorder recorder) {
        int strokeWidth = 5;
        int drawableSize = 50;
        int radius = recorder.getCornerRadius();
        int mainColor = recorder.getColor(0);

        GradientDrawable drawable1 = new GradientDrawable();
        drawable1.setShape(GradientDrawable.RECTANGLE);
        drawable1.setStroke(strokeWidth, mainColor);
        drawable1.setCornerRadius(radius);
        drawable1.setSize(drawableSize, drawableSize);

        Drawable[] layers = new Drawable[]{drawable1};

        return new LayerDrawable(layers);

    }

    private static LayerDrawable getDesign_double_ring(CkbThemeRecorder recorder) {
        int strokeWidth_1 = 5;
        int strokeWidth_2 = 5;
        int mainColor = recorder.getColor(0);
        int drawableSize = 50;
        int radius = recorder.getCornerRadius();

        GradientDrawable drawable1 = new GradientDrawable();
        drawable1.setShape(GradientDrawable.RECTANGLE);
        drawable1.setStroke(strokeWidth_1, mainColor);
        drawable1.setCornerRadius(radius);
        drawable1.setSize(drawableSize, drawableSize);

        GradientDrawable drawable2 = new GradientDrawable();
        drawable2.setShape(GradientDrawable.RECTANGLE);
        drawable2.setStroke(strokeWidth_2, mainColor);
        drawable2.setCornerRadius(radius);
        drawable2.setSize(drawableSize, drawableSize);

        Drawable[] layers = new Drawable[]{drawable1, drawable2};
        LayerDrawable mainDrawable = new LayerDrawable(layers);
        mainDrawable.setLayerInset(1, strokeWidth_1 * 2, strokeWidth_1 * 2, strokeWidth_1 * 2, strokeWidth_1 * 2);

        return mainDrawable;
    }

    private static LayerDrawable getDesign_black_shadow(CkbThemeRecorder recorder) {
        int storkeWidth = 5;
        int mainColor = Color.WHITE;
        int drawableSize = 50;
        int radius = recorder.getCornerRadius();

        GradientDrawable drawable1 = new GradientDrawable();
        drawable1.setShape(GradientDrawable.RECTANGLE);
        drawable1.setStroke(storkeWidth, mainColor);
        drawable1.setCornerRadius(radius);
        drawable1.setSize(drawableSize, drawableSize);

        GradientDrawable drawable2 = new GradientDrawable();
        drawable2.setShape(GradientDrawable.RECTANGLE);
        drawable2.setColor(Color.BLACK);
        drawable2.setCornerRadius(radius);
        drawable2.setSize(drawableSize, drawableSize);

        Drawable[] layers = new Drawable[]{drawable2, drawable1};
        LayerDrawable mainDrawable = new LayerDrawable(layers);
        mainDrawable.setLayerInset(1, 1, 1, 1, 1);

        return mainDrawable;
    }
}
