package com.steelkiwi.cropiwa;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */

class Utils {

    private Context context;

    public Utils(Context context) {
        this.context = context;
    }

    @ColorInt
    public int color(@ColorRes int colorRes) {
        return ContextCompat.getColor(context, colorRes);
    }

    public static RectF enlargeRectBy(float value, @NonNull RectF outRect) {
        outRect.top -= value;
        outRect.bottom += value;
        outRect.left -= value;
        outRect.right += value;
        return outRect;
    }

    public static int dpToPx(int dp) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return Math.round(dm.density * dp);
    }

    public static RectF moveRect(RectF initial, float deltaX, float deltaY, @NonNull RectF outRect) {
        outRect.set(
                initial.left + deltaX, initial.top + deltaY,
                initial.right + deltaX, initial.bottom + deltaY);
        return outRect;
    }

}
