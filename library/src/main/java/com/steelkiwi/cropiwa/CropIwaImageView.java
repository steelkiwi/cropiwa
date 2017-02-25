package com.steelkiwi.cropiwa;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.steelkiwi.cropiwa.config.ConfigChangeListener;
import com.steelkiwi.cropiwa.config.CropIwaImageViewConfig;
import com.steelkiwi.cropiwa.util.CropIwaLog;
import com.steelkiwi.cropiwa.util.CropIwaUtils;
import com.steelkiwi.cropiwa.util.MatrixAnimator;
import com.steelkiwi.cropiwa.util.MatrixUtils;
import com.steelkiwi.cropiwa.util.TensionInterpolator;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */
class CropIwaImageView extends ImageView implements OnNewBoundsListener, ConfigChangeListener {

    private float maxScale;
    private float minScale;

    private Matrix imageMatrix;
    private GestureProcessor gestureDetector;

    private RectF allowedBounds;
    private RectF imageBounds;
    private RectF realImageBounds;

    private MatrixUtils matrixUtils;

    private boolean isOnScreen;

    private OnImagePositionedListener imagePositionedListener;

    private CropIwaImageViewConfig.ScaleChangeListener scaleChangeListener;
    private CropIwaImageViewConfig config;

    public CropIwaImageView(Context context, CropIwaImageViewConfig config) {
        super(context);
        initWith(config);
    }

    private void initWith(CropIwaImageViewConfig c) {
        config = c;
        config.addConfigChangeListener(this);

        scaleChangeListener = c.getScaleChangeListener();
        maxScale = c.getMaxScale();
        minScale = c.getDefaultMinScale();

        imageBounds = new RectF();
        allowedBounds = new RectF();
        realImageBounds = new RectF();

        matrixUtils = new MatrixUtils();

        imageMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);

        gestureDetector = new GestureProcessor();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (hasImageSize()) {
            minScale = calculateMinScale();
            placeImageToInitialPosition();
        }
    }

    private void placeImageToInitialPosition() {
        updateImageBounds();
        switch (config.getImageInitialPosition()) {
            case CENTER_CROP:
                resizeImageToFillTheView();
                break;
            case CENTER_INSIDE:
                resizeImageToBeInsideTheView();
                break;
        }
        moveImageToTheCenter();

        if (imagePositionedListener != null) {
            RectF imageRect = new RectF(imageBounds);
            CropIwaUtils.constrainRectTo(0, 0, getWidth(), getHeight(), imageRect);
            imagePositionedListener.onImagePositioned(imageRect);
        }
    }

    private void resizeImageToFillTheView() {
        float scale;
        if (getWidth() < getHeight()) {
            scale = ((float) getHeight()) / getImageHeight();
        } else {
            scale = ((float) getWidth()) / getImageWidth();
        }
        scaleImage(scale);
    }

    private void resizeImageToBeInsideTheView() {
        float scale;
        if (getImageWidth() < getImageHeight()) {
            scale = ((float) getHeight()) / getImageHeight();
        } else {
            scale = ((float) getWidth()) / getImageWidth();
        }
        scaleImage(scale);
    }

    private void moveImageToTheCenter() {
        updateImageBounds();
        float deltaX = (getWidth() / 2f) - imageBounds.centerX();
        float deltaY = (getHeight() / 2f) - imageBounds.centerY();
        translateImage(deltaX, deltaY);
    }

    private float calculateMinScale() {
        float viewWidth = getWidth(), viewHeight = getHeight();
        if (getRealImageWidth() <= viewWidth && getRealImageHeight() <= viewHeight) {
            return config.getDefaultMinScale();
        }
        float scaleFactor = viewWidth < viewHeight ?
                viewWidth / getRealImageWidth() :
                viewHeight / getRealImageHeight();
        return scaleFactor * 0.8f;
    }

    private int getRealImageWidth() {
        Drawable image = getDrawable();
        return image != null ? image.getIntrinsicWidth() : -1;
    }

    private int getRealImageHeight() {
        Drawable image = getDrawable();
        return image != null ? image.getIntrinsicHeight() : -1;
    }

    public int getImageWidth() {
        return (int) imageBounds.width();
    }

    public int getImageHeight() {
        return (int) imageBounds.height();
    }

    public boolean hasImageSize() {
        return getRealImageWidth() != -1 && getRealImageHeight() != -1;
    }

    public GestureProcessor getImageTransformGestureDetector() {
        return gestureDetector;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        isOnScreen = true;
    }

    @Override
    public void onNewBounds(RectF bounds) {
        allowedBounds.set(bounds);
        if (hasImageSize()) {
            if (isOnScreen) {
                animateToAllowedBounds();
            } else {
                moveToAllowedBounds();
            }
            updateImageBounds();
            invalidate();
        }
    }

    private void moveToAllowedBounds() {
        updateImageBounds();
        imageMatrix.set(MatrixUtils.findTransformToAllowedBounds(
                realImageBounds, imageMatrix,
                allowedBounds));
        setImageMatrix(imageMatrix);
    }

    private void animateToAllowedBounds() {
        updateImageBounds();
        Matrix endMatrix = MatrixUtils.findTransformToAllowedBounds(
                realImageBounds, imageMatrix,
                allowedBounds);
        MatrixAnimator animator = new MatrixAnimator();
        animator.animate(imageMatrix, endMatrix, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imageMatrix.set((Matrix) animation.getAnimatedValue());
                setImageMatrix(imageMatrix);
                updateImageBounds();
                invalidate();
            }
        });
    }

    public void setScale(@FloatRange(from = 0.01f, to = 1f) float percent) {
        float desiredScale = minScale + (maxScale - minScale) * percent;
        float currentScale = matrixUtils.getScaleX(imageMatrix);
        float factor = desiredScale / currentScale;
        scaleImage(factor);
    }

    private void scaleImage(float factor) {
        updateImageBounds();
        scaleImage(factor, imageBounds.centerX(), imageBounds.centerY());
    }

    private void scaleImage(float factor, float pivotX, float pivotY) {
        imageMatrix.postScale(factor, factor, pivotX, pivotY);
        setImageMatrix(imageMatrix);
        updateImageBounds();
    }

    private void translateImage(float deltaX, float deltaY) {
        imageMatrix.postTranslate(deltaX, deltaY);
        setImageMatrix(imageMatrix);
        if (deltaX > 0.01f || deltaY > 0.01f) {
            updateImageBounds();
        }
    }

    private void updateImageBounds() {
        realImageBounds.set(0, 0, getRealImageWidth(), getRealImageHeight());
        imageBounds.set(realImageBounds);
        imageMatrix.mapRect(imageBounds);
    }

    @Override
    public void onConfigChanged() {
        maxScale = config.getMaxScale();
        scaleChangeListener = config.getScaleChangeListener();
    }

    public void setImagePositionedListener(OnImagePositionedListener imagePositionedListener) {
        this.imagePositionedListener = imagePositionedListener;
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float newScale = matrixUtils.getScaleX(imageMatrix) * scaleFactor;
            if (isValidScale(newScale)) {
                scaleImage(scaleFactor, detector.getFocusX(), detector.getFocusY());
                if (scaleChangeListener != null) {
                    scaleChangeListener.onScaleChanged(newScale / (maxScale - minScale));
                }
            }
            return true;
        }

        private boolean isValidScale(float newScale) {
            return newScale >= minScale && newScale <= (minScale + maxScale);
        }
    }

    private class TranslationGestureListener {

        private float prevX;
        private float prevY;
        private int id;

        private TensionInterpolator interpolator = new TensionInterpolator();

        public void onDown(MotionEvent e) {
            updateImageBounds();
            interpolator.onDown(e.getX(), e.getY(), imageBounds, allowedBounds);
            saveCoordinates(e.getX(), e.getY(), e.getPointerId(0));
        }

        public void onTouchEvent(MotionEvent e) {
            if (e.getAction() != MotionEvent.ACTION_MOVE) {
                return;
            }
            if (e.getPointerId(0) != id) {
                return;
            }

            updateImageBounds();

            float currentX = interpolator.interpolateX(e.getX());
            float currentY = interpolator.interpolateY(e.getY());

            translateImage(currentX - prevX, currentY - prevY);
            saveCoordinates(currentX, currentY);
        }

        private void saveCoordinates(float x, float y) {
            saveCoordinates(x, y, id);
        }

        private void saveCoordinates(float x, float y, int id) {
            this.prevX = x;
            this.prevY = y;
            this.id = id;
        }
    }

    public class GestureProcessor {

        private ScaleGestureDetector scaleDetector;
        private TranslationGestureListener translationGestureListener;

        public GestureProcessor() {
            scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());
            translationGestureListener = new TranslationGestureListener();
        }

        public void onDown(MotionEvent event) {
            translationGestureListener.onDown(event);
        }

        public void onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    animateToAllowedBounds();
                    return;
            }
            if (config.isImageScaleEnabled()) {
                scaleDetector.onTouchEvent(event);
            }

            if (config.isImageTranslationEnabled() && !scaleDetector.isInProgress()) {
                translationGestureListener.onTouchEvent(event);
            }
        }
    }

}