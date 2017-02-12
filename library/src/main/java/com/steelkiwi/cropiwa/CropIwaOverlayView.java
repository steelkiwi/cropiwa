package com.steelkiwi.cropiwa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;
import com.steelkiwi.cropiwa.shape.CropIwaShape;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */
@SuppressLint("ViewConstructor")
class CropIwaOverlayView extends View {

    private Paint overlayPaint;
    protected RectF cropRect;
    protected CropIwaOverlayConfig config;

    public CropIwaOverlayView(Context context, CropIwaOverlayConfig config) {
        super(context);
        initWith(config);
    }

    protected void initWith(CropIwaOverlayConfig c) {
        config = c;
        config.setOverlayView(this);

        cropRect = new RectF();

        overlayPaint = new Paint();
        overlayPaint.setStyle(Paint.Style.FILL);
        overlayPaint.setColor(c.getOverlayColor());

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            float centerX = w * 0.5f, centerY = h * 0.5f;
            //Initial width/height are in percents of view's width and height
            float halfWidth = w * config.getInitialWidth() * 0.01f * 0.5f;
            float halfHeight = h * config.getInitialHeight() * 0.01f * 0.5f;
            cropRect.set(
                    centerX - halfWidth, centerY - halfHeight,
                    centerX + halfWidth, centerY + halfHeight);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //We will get here measured dimensions of an ImageView
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

        CropIwaShape cropShape = config.getCropShape();
        cropShape.draw(canvas, cropRect);
    }

    public boolean isResizing() {
        return false;
    }

    public boolean isDraggingCropArea() {
        return false;
    }


}