package com.steelkiwi.cropiwa.customization.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by yarolegovich on 04.02.2017.
 */

public class CropIwaCircleShape implements CropIwaShape {
    @Override
    public void clearCropArea(Canvas canvas, RectF cropBounds, Paint paint) {
        canvas.drawOval(cropBounds, paint);
    }

    @Override
    public void drawBorder(Canvas canvas, RectF cropBounds, Paint paint) {
        canvas.drawRect(cropBounds, paint);
        canvas.drawOval(cropBounds, paint);
    }
}
