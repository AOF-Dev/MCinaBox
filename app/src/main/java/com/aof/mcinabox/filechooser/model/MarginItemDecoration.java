package com.aof.mcinabox.filechooser.model;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarginItemDecoration extends RecyclerView.ItemDecoration {
    private static final int px = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8, Resources.getSystem().getDisplayMetrics());
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        if (position == 0) {
            outRect.top = px;
        }
        outRect.right = px;
        outRect.bottom = px;
        outRect.left = px;
    }
}
