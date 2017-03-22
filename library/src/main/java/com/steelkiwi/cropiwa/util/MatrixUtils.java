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

        RectF current = new RectF(initial);
        transform.mapRect(current);

        if (current.width() < allowedBounds.width()) {
            float scale = allowedBounds.width() / current.width();
            scale(initialBounds, scale, transform, current);
        }

        if (current.height() < allowedBounds.height()) {
            float scale = allowedBounds.height() / current.height();
            scale(initialBounds, scale, transform, current);
        }

        if (current.left > allowedBounds.left) {
            translate(initialBounds, allowedBounds.left - current.left, 0, transform, current);
        }
        if (current.right < allowedBounds.right) {
            translate(initialBounds, allowedBounds.right - current.right, 0, transform, current);
        }

        if (current.top > allowedBounds.top) {
            translate(initialBounds, 0, allowedBounds.top - current.top, transform, current);
        }
        if (current.bottom < allowedBounds.bottom) {
            translate(initialBounds, 0, allowedBounds.bottom - current.bottom, transform, current);
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
