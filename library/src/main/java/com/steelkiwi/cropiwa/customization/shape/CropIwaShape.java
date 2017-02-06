package com.steelkiwi.cropiwa.customization.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * @author yarolegovich https://github.com/yarolegovich
 * 06.02.2017.
 */
public interface CropIwaShape {
    void clearCropArea(Canvas canvas, RectF cropBounds, Paint paint);

    void drawBorder(Canvas canvas, RectF cropBounds, Paint paint);
}
