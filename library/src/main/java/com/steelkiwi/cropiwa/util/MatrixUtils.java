package com.steelkiwi.cropiwa.util;

import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * Created by yarolegovich on 06.02.2017.
 */

public class MatrixUtils {
    private float[] outValues;

    public MatrixUtils() {
        outValues = new float[9];
    }

    public float getScaleX(Matrix mat) {
        mat.getValues(outValues);
        return outValues[Matrix.MSCALE_X];
    }

    public float getXTranslation(Matrix mat) {
        mat.getValues(outValues);
        return outValues[Matrix.MTRANS_X];
    }

    public float getYTranslation(Matrix mat) {
        mat.getValues(outValues);
        return outValues[Matrix.MTRANS_Y];
    }

    public static Matrix findTransformToAllowedBounds(
            RectF initial, Matrix initialTransform,
            RectF allowedBounds) {
        RectF initialBounds = new RectF();
        initialBounds.set(initial);

        Matrix transform = new Matrix();
        transform.set(initialTransform);

        RectF bounds = new RectF();
        transformInitial(initialBounds, transform, bounds);

        if (allowedBounds.width() > allowedBounds.height()) {
            if (bounds.width() < allowedBounds.width()) {
                float scale = allowedBounds.width() / bounds.width();
                scale(initialBounds, scale, transform, bounds);
            }
        } else {
            if (bounds.height() < allowedBounds.height()) {
                float scale = allowedBounds.height() / bounds.height();
                scale(initialBounds, scale, transform, bounds);
            }
        }

        if (Math.abs(bounds.left - allowedBounds.left) < Math.abs(bounds.right - allowedBounds.right)) {
            if (bounds.left > allowedBounds.left) {
                translate(initialBounds, allowedBounds.left - bounds.left, 0, transform, bounds);
            }
        } else {
            if (bounds.right < allowedBounds.right) {
                translate(initialBounds, allowedBounds.right - bounds.right, 0, transform, bounds);
            }
        }

        if (Math.abs(bounds.top - allowedBounds.top) < Math.abs(bounds.bottom - allowedBounds.bottom)) {
            if (bounds.top > allowedBounds.top) {
                translate(initialBounds, 0, allowedBounds.top - bounds.top, transform, bounds);
            }
        } else {
            if (bounds.bottom < allowedBounds.bottom) {
                translate(initialBounds, 0, allowedBounds.bottom - bounds.bottom, transform, bounds);
            }
        }

        return transform;
    }

    private static void scale(RectF initial, float scale, Matrix transform, RectF outRect) {
        transform.postScale(scale, scale, outRect.centerY(), outRect.centerY());
        transformInitial(initial, transform, outRect);
    }

    private static void translate(RectF initial, float dx, float dy, Matrix transform, RectF outRect) {
        transform.postTranslate(dx, dy);
        transformInitial(initial, transform, outRect);
    }

    private static void transformInitial(RectF initial, Matrix transform, RectF outRect) {
        outRect.set(initial);
        transform.mapRect(outRect);
    }


}
