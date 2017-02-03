package com.steelkiwi.cropiwa;

import android.graphics.RectF;
import android.support.annotation.NonNull;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */

public abstract class Utils {

    public static RectF enlargeRectBy(float value, @NonNull RectF outRect) {
        outRect.top -= value;
        outRect.bottom += value;
        outRect.left -= value;
        outRect.right += value;
        return outRect;
    }

    public static RectF moveRect(RectF initial, float deltaX, float deltaY, @NonNull RectF outRect) {


        return outRect;
    }
}
