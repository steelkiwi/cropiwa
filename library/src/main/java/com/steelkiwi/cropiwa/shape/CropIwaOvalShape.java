package com.steelkiwi.cropiwa.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;

import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;

/**
 * Created by yarolegovich on 04.02.2017.
 */

public class CropIwaOvalShape extends CropIwaShape {

    private Xfermode gridXFermode;

    public CropIwaOvalShape(CropIwaOverlayConfig config) {
        super(config);
        gridXFermode = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);
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
        paint.setXfermode(gridXFermode);
        super.drawGrid(canvas, cropBounds, paint);
        paint.setXfermode(null);
    }
}