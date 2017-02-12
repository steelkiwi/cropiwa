package com.steelkiwi.cropiwa.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;

import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;
import com.steelkiwi.cropiwa.util.CropIwaLog;

/**
 * Created by yarolegovich on 04.02.2017.
 */

public class CropIwaOvalShape extends CropIwaShape {

    public CropIwaOvalShape(CropIwaOverlayConfig config) {
        super(config);
    }

    @Override
    protected void clearArea(Canvas canvas, RectF cropBounds, Paint clearPaint) {
        canvas.drawOval(cropBounds, clearPaint);
    }

    @Override
    protected void drawBorders(Canvas canvas, RectF cropBounds, Paint paint) {
        canvas.drawOval(cropBounds, paint);
        if (overlayConfig.isDynamicCrop()) {
            canvas.drawRect(cropBounds, paint);
        }
    }

    @Override
    protected void drawGrid(Canvas canvas, RectF cropBounds, Paint paint) {
        CropIwaLog.d("grid is not supported for CropIwaOvalShape yet...");
        //Temporarily not supported
    }
}
