package com.steelkiwi.cropiwa.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;
import com.steelkiwi.cropiwa.shape.CropIwaShape;

/**
 * Created by yarolegovich on 04.02.2017.
 */

public class CropIwaRectShape extends CropIwaShape {

    public CropIwaRectShape(CropIwaOverlayConfig config) {
        super(config);
    }

    @Override
    protected void clearArea(Canvas canvas, RectF cropBounds, Paint clearPaint) {
        canvas.drawRect(cropBounds, clearPaint);
    }

    @Override
    protected void drawBorders(Canvas canvas, RectF cropBounds, Paint paint) {
        canvas.drawRect(cropBounds, paint);
    }

}

