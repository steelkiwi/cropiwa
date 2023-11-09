package com.steelkiwi.cropiwa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.steelkiwi.cropiwa.config.ConfigChangeListener;
import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;
import com.steelkiwi.cropiwa.shape.CropIwaShape;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */
@SuppressLint("ViewConstructor")
class CropIwaOverlayView extends View implements ConfigChangeListener, OnImagePositionedListener {

    private Paint overlayPaint;
    private OnNewBoundsListener newBoundsListener;
    private CropIwaShape cropShape;

    private float cropScale;

    private RectF imageBounds;

    protected RectF cropRect;
    protected CropIwaOverlayConfig config;

    protected boolean shouldDrawOverlay;

    public CropIwaOverlayView(Context context, CropIwaOverlayConfig config) {
        super(context);
        initWith(config);
    }

    protected void initWith(CropIwaOverlayConfig c) {
        config = c;
        config.addConfigChangeListener(this);

        imageBounds = new RectF();
        cropScale = config.getCropScale();
        cropShape = c.getCropShape();

        cropRect = new RectF();

        overlayPaint = new Paint();
        overlayPaint.setStyle(Paint.Style.FILL);
        overlayPaint.setColor(c.getOverlayColor());

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public void onImagePositioned(RectF imageRect) {
        imageBounds.set(imageRect);
        setCropRectAccordingToAspectRatio();
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
        if (shouldDrawOverlay) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);
            if (isValidCrop()) {
                cropShape.draw(canvas, cropRect);
            }
        }
    }

    protected void notifyNewBounds() {
        if (newBoundsListener != null) {
            //Do not allow client code to modify our cropRect!
            RectF rect = new RectF(cropRect);
            newBoundsListener.onNewBounds(rect);
        }
    }

    private boolean isValidCrop() {
        return cropRect.width() >= config.getMinWidth()
                && cropRect.height() >= config.getMinHeight();
    }

    public boolean isResizing() {
        return false;
    }

    public boolean isDraggingCropArea() {
        return false;
    }

    public RectF getCropRect() {
        return new RectF(cropRect);
    }

    public void setDrawOverlay(boolean shouldDraw) {
        shouldDrawOverlay = shouldDraw;
        invalidate();
    }

    public boolean isDrawn() {
        return shouldDrawOverlay;
    }

    public void setNewBoundsListener(OnNewBoundsListener newBoundsListener) {
        this.newBoundsListener = newBoundsListener;
    }

    @Override
    public void onConfigChanged() {
        overlayPaint.setColor(config.getOverlayColor());
        cropShape = config.getCropShape();
        cropScale = config.getCropScale();
        cropShape.onConfigChanged();
        setCropRectAccordingToAspectRatio();
        notifyNewBounds();
        invalidate();
    }

    private void setCropRectAccordingToAspectRatio() {
        float viewWidth = getMeasuredWidth(), viewHeight = getMeasuredHeight();
        if (viewWidth == 0 || viewHeight == 0) {
            return;
        }

        AspectRatio aspectRatio = getAspectRatio();
        if (aspectRatio == null) {
            return;
        }

        if (cropRect.width() != 0 && cropRect.height() != 0) {
            float currentRatio = cropRect.width() / cropRect.height();
            if (Math.abs(currentRatio - aspectRatio.getRatio()) < 0.001) {
                return;
            }
        }

        float centerX = viewWidth * 0.5f;
        float centerY = viewHeight * 0.5f;
        float halfWidth, halfHeight;

        boolean calculateFromWidth =
                aspectRatio.getHeight() < aspectRatio.getWidth()
                        || (aspectRatio.isSquare() && viewWidth < viewHeight);

        if (calculateFromWidth) {
            halfWidth = viewWidth * cropScale * 0.5f;
            halfHeight = halfWidth / aspectRatio.getRatio();
        } else {
            halfHeight = viewHeight * cropScale * 0.5f;
            halfWidth = halfHeight * aspectRatio.getRatio();
        }

        cropRect.set(
                centerX - halfWidth, centerY - halfHeight,
                centerX + halfWidth, centerY + halfHeight);
    }

    @Nullable
    private AspectRatio getAspectRatio() {
        AspectRatio aspectRatio = config.getAspectRatio();
        if (aspectRatio == AspectRatio.IMG_SRC) {
            if (imageBounds.width() == 0 || imageBounds.height() == 0) {
                return null;
            }
            aspectRatio = new AspectRatio(
                    Math.round(imageBounds.width()),
                    Math.round(imageBounds.height()));
        }
        return aspectRatio;
    }

}
