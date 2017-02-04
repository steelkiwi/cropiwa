package com.steelkiwi.cropiwa;

import android.content.res.Resources;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */

abstract class Utils {

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
