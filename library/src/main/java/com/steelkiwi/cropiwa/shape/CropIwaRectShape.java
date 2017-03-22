package com.steelkiwi.cropiwa.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;

/**
 * Created by yarolegovich on 04.02.2017.
 * https://github.com/yarolegovich
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

    @Override
    public CropIwaShapeMask getMask() {
        return new RectShapeMask();
    }

    private static class RectShapeMask implements CropIwaShapeMask {
        @Override
        public Bitmap applyMaskTo(Bitmap croppedRegion) {
            //Nothing to do
            return croppedRegion;
        }
    }
}

