package com.steelkiwi.cropiwa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.steelkiwi.cropiwa.config.ConfigChangeListener;
import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;
import com.steelkiwi.cropiwa.shape.CropIwaShape;
import com.steelkiwi.cropiwa.util.CropIwaLog;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */
@SuppressLint("ViewConstructor")
class CropIwaOverlayView extends View implements ConfigChangeListener, OnImagePositionedListener {

    private Paint overlayPaint;
    private OnNewBoundsListener newBoundsListener;
    private CropIwaShape cropShape;

    protected RectF cropRect;
    protected CropIwaOverlayConfig config;

    public CropIwaOverlayView(Context context, CropIwaOverlayConfig config) {
        super(context);
        initWith(config);
    }

    protected void initWith(CropIwaOverlayConfig c) {
        config = c;
        config.addConfigChangeListener(this);

        cropShape = c.getCropShape();

        cropRect = new RectF();

        overlayPaint = new Paint();
        overlayPaint.setStyle(Paint.Style.FILL);
        overlayPaint.setColor(c.getOverlayColor());

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public void onImagePositioned(RectF imageRect) {
        float halfWidth, halfHeight;
        AspectRatio aspectRatio = config.getAspectRatio();

        boolean calculateFromWidth = aspectRatio.getHeight() < aspectRatio.getWidth()
                || (aspectRatio.isSquare() && imageRect.width() < imageRect.height());

        if (calculateFromWidth) {
            halfWidth = imageRect.width() * 0.8f * 0.5f;
            halfHeight = halfWidth / config.getAspectRatio().getRatio();
        } else {
            halfHeight = imageRect.height() * 0.8f * 0.5f;
            halfWidth = halfHeight * config.getAspectRatio().getRatio();
        }

        cropRect.set(
                imageRect.centerX() - halfWidth, imageRect.centerY() - halfHeight,
                imageRect.centerX() + halfWidth, imageRect.centerY() + halfHeight);

        notifyNewBounds();
        invalidate();
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
        cropShape.draw(canvas, cropRect);
    }

    protected void notifyNewBounds() {
        if (newBoundsListener != null) {
            //Do not allow client code to modify our cropRect!
            RectF rect = new RectF(cropRect);
            newBoundsListener.onNewBounds(rect);
        }
    }

    public boolean isResizing() {
        return false;
    }

    public boolean isDraggingCropArea() {
        return false;
    }

    public void setNewBoundsListener(OnNewBoundsListener newBoundsListener) {
        this.newBoundsListener = newBoundsListener;
    }

    @Override
    public void onConfigChanged() {
        overlayPaint.setColor(config.getOverlayColor());
        cropShape = config.getCropShape();
    }

}