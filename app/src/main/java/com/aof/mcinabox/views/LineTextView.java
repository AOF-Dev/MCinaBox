package com.aof.mcinabox.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;

public class LineTextView extends androidx.appcompat.widget.AppCompatTextView {
    private final Paint line;

    public LineTextView(Context context) {
        super(context);
        setFocusable(true);
        line = new Paint();
        line.setColor(Color.RED);
        line.setStrokeWidth(2);
        setPadding(95, 0, 0, 0);
        setGravity(Gravity.TOP);
    }

    @Override
    protected void onDraw(final Canvas canvas) {

        if (getText().toString().length() != 0) {
            float y;
            Paint p = new Paint();
            p.setColor(Color.WHITE);
            p.setTextSize(getTextSize());
            for (int l = 0; l < getLineCount(); l++) {
                y = ((l + 1) * getLineHeight()) - getLineHeight() / 4;
                canvas.drawText(String.valueOf(l + 1), 0, y, p);
                canvas.save();
            }
        }
        int k = getLineHeight();
        int i = getLineCount();
        canvas.drawLine(90, 0, 90, getHeight() + (i * k), line);
        int y = (getLayout().getLineForOffset(getSelectionStart()) + 1) * k;
        canvas.save();
        canvas.restore();
        super.onDraw(canvas);
    }
}
