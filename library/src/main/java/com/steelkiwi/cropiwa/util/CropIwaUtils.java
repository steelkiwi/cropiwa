package com.steelkiwi.cropiwa.util;

import android.content.res.Resources;
import android.graphics.RectF;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.File;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */
public abstract class CropIwaUtils {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void delete(@Nullable File file) {
        if (file != null) {
            file.delete();
        }
    }

    public static boolean isAnyNull(Iterable<?> iterable) {
        for (Object o : iterable) {
            if (o == null) {
                return true;
            }
        }
        return false;
    }

    public static void constrainRectTo(int minLeft, int minTop, int maxRight, int maxBottom, RectF rect) {
        rect.set(
                Math.max(rect.left, minLeft), Math.max(rect.top, minTop),
                Math.min(rect.right, maxRight), Math.min(rect.bottom, maxBottom));
    }

    public static void closeSilently(@Nullable Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (Exception e) { /* NOP */ }
    }

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