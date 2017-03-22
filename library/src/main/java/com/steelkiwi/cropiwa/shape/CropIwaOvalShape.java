package com.steelkiwi.cropiwa.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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

    private Path clipPath;

    public CropIwaOvalShape(CropIwaOverlayConfig config) {
        super(config);
        clipPath = new Path();
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
        clipPath.rewind();
        clipPath.addOval(cropBounds, Path.Direction.CW);

        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipPath(clipPath);
        super.drawGrid(canvas, cropBounds, paint);
        canvas.restore();
    }

    @Override
    public CropIwaShapeMask getMask() {
        return new OvalShapeMask();
    }

    private static class OvalShapeMask implements CropIwaShapeMask {
        @Override
        public void applyMaskTo(Bitmap croppedRegion) {
            Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

            RectF ovalRect = new RectF(0, 0, croppedRegion.getWidth(), croppedRegion.getHeight());
            Path maskShape = new Path();
            maskShape.addRect(ovalRect, Path.Direction.CW);
            maskShape.addOval(ovalRect, Path.Direction.CCW);

            Canvas canvas = new Canvas(croppedRegion);
            canvas.drawPath(maskShape, maskPaint);
        }
    }
}

