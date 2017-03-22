package com.steelkiwi.cropiwa.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.ConfigChangeListener;
import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;

/**
 * @author yarolegovich https://github.com/yarolegovich
 * 06.02.2017.
 */
public abstract class CropIwaShape implements ConfigChangeListener {

    private Paint clearPaint;
    private Paint cornerPaint;
    private Paint gridPaint;
    private Paint borderPaint;

    protected CropIwaOverlayConfig overlayConfig;

    public CropIwaShape(CropIwaView cropIwaView) {
        this(cropIwaView.configureOverlay());
    }

    public CropIwaShape(CropIwaOverlayConfig config) {
        overlayConfig = config;

        clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeCap(Paint.Cap.SQUARE);

        borderPaint = new Paint(gridPaint);

        cornerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cornerPaint.setStyle(Paint.Style.STROKE);
        cornerPaint.setStrokeCap(Paint.Cap.ROUND);

        updatePaintObjectsFromConfig();
    }

    public final void draw(Canvas canvas, RectF cropBounds) {
        clearArea(canvas, cropBounds, clearPaint);
        if (overlayConfig.shouldDrawGrid()) {
            drawGrid(canvas, cropBounds, gridPaint);
        }
        drawBorders(canvas, cropBounds, borderPaint);
    }

    public void drawCorner(Canvas canvas, float x, float y, float deltaX, float deltaY) {
        canvas.drawLine(x, y, x + deltaX, y, cornerPaint);
        canvas.drawLine(x, y, x, y + deltaY, cornerPaint);
    }
    public Paint getCornerPaint() {
        return cornerPaint;
    }

    public Paint getGridPaint() {
        return gridPaint;
    }

    public Paint getBorderPaint() {
        return borderPaint;
    }

    public abstract CropIwaShapeMask getMask();

    protected abstract void clearArea(Canvas canvas, RectF cropBounds, Paint clearPaint);

    protected abstract void drawBorders(Canvas canvas, RectF cropBounds, Paint paint);

    protected void drawGrid(Canvas canvas, RectF cropBounds, Paint paint) {
        float stepX = cropBounds.width() * 0.333f;
        float stepY = cropBounds.height() * 0.333f;
        float x = cropBounds.left;
        float y = cropBounds.top;
        for (int i = 0; i < 2; i++) {
            x += stepX;
            y += stepY;
            canvas.drawLine(x, cropBounds.top, x, cropBounds.bottom, paint);
            canvas.drawLine(cropBounds.left, y, cropBounds.right, y, paint);
        }
    }

    @Override
    public void onConfigChanged() {
        updatePaintObjectsFromConfig();
    }

    private void updatePaintObjectsFromConfig() {
        cornerPaint.setStrokeWidth(overlayConfig.getCornerStrokeWidth());
        cornerPaint.setColor(overlayConfig.getCornerColor());
        gridPaint.setColor(overlayConfig.getGridColor());
        gridPaint.setStrokeWidth(overlayConfig.getGridStrokeWidth());
        borderPaint.setColor(overlayConfig.getBorderColor());
        borderPaint.setStrokeWidth(overlayConfig.getBorderStrokeWidth());
    }
}