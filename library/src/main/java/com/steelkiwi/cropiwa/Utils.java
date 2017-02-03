package com.steelkiwi.cropiwa;

import android.graphics.RectF;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */

public abstract class Utils {

    public static void enlargeRectBy(float value, RectF outRect) {
        outRect.top -= value;
        outRect.bottom += value;
        outRect.left -= value;
        outRect.right += value;
    }
}
