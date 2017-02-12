package com.steelkiwi.cropiwa.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;

/**
 * @author yarolegovich https://github.com/yarolegovich
 * 06.02.2017.
 */
public abstract class CropIwaShape {

    private Path cornerPath;

    protected Paint clearPaint;
    protected Paint cornerPaint;
    protected Paint paint;

    protected CropIwaOverlayConfig overlayConfig;

    public CropIwaShape(CropIwaOverlayConfig config) {
        overlayConfig = config;

        clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.SQUARE);

        cornerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cornerPaint.setStrokeWidth(overlayConfig.getCornerStrokeWidth());
        cornerPaint.setColor(overlayConfig.getCornerColor());
        cornerPaint.setStrokeCap(Paint.Cap.ROUND);

        cornerPath = new Path();
    }

    public final void draw(Canvas canvas, RectF cropBounds) {
        clearArea(canvas, cropBounds, clearPaint);

        paint.setStrokeWidth(overlayConfig.getBorderStrokeWidth());
        paint.setColor(overlayConfig.getBorderColor());
        drawBorders(canvas, cropBounds, paint);

        if (overlayConfig.shouldDrawGrid()) {
            paint.setStrokeWidth(overlayConfig.getGridStrokeWidth());
            paint.setColor(overlayConfig.getGridColor());
            drawGrid(canvas, cropBounds, paint);
        }
    }

    public void drawCorner(Canvas canvas, float x, float y, float deltaX, float deltaY) {
        cornerPath.rewind();
        cornerPath.moveTo(x, y);
        cornerPath.rLineTo(deltaX, 0);
        cornerPath.moveTo(x, y);
        cornerPath.rLineTo(0, deltaY);
        canvas.drawPath(cornerPath, cornerPaint);
    }

    protected abstract void clearArea(Canvas canvas, RectF cropBounds, Paint clearPaint);

    protected abstract void drawBorders(Canvas canvas, RectF cropBounds, Paint paint);

    protected void drawGrid(Canvas canvas, RectF cropBounds, Paint paint) {
        float stepX = cropBounds.width() * 0.333f;
        float stepY = cropBounds.height() * 0.333f;
        float x = cropBounds.left;
        float y = cropBounds.top;
        for (int i = 0; i < 3; i++) {
            x += stepX;
            y += stepY;
            canvas.drawLine(x, cropBounds.top, x, cropBounds.bottom, paint);
            canvas.drawLine(cropBounds.left, y, cropBounds.right, y, paint);
        }
    }
}
