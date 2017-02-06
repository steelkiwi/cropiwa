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

    public static float boundValue(float value, float lowBound, float highBound) {
        return Math.max(Math.min(value, highBound), lowBound);
    }

    public static RectF moveRectBounded(
            @NonNull RectF initial, float deltaX, float deltaY,
            int horizontalBound, int verticalBound,
            @NonNull RectF outRect) {
        float newLeft = boundValue(initial.left + deltaX, 0, horizontalBound - initial.width());
        float newRight = newLeft + initial.width();
        float newTop = boundValue(initial.top + deltaY, 0, verticalBound - initial.height());
        float newBottom = newTop + initial.height();
        outRect.set(newLeft, newTop, newRight, newBottom);
        return outRect;
    }

    public static int dpToPx(int dp) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        return Math.round(dm.density * dp);
    }

}