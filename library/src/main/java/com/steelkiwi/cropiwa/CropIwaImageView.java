package com.steelkiwi.cropiwa;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.steelkiwi.cropiwa.util.MatrixAnimator;
import com.steelkiwi.cropiwa.util.MatrixUtils;
import com.steelkiwi.cropiwa.util.TensionInterpolator;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */
class CropIwaImageView extends ImageView implements OnNewBoundsListener {

    private static final float MAX_SCALE = 3f;
    private static final float MIN_SCALE = 0.7f;

    private Matrix imageMatrix;
    private GestureProcessor gestureDetector;

    private RectF allowedBounds;
    private RectF imageBounds;
    private RectF realImageBounds;

    private MatrixUtils matrixUtils;

    private boolean isOnScreen;

    public CropIwaImageView(Context context) {
        super(context);
    }

    public CropIwaImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CropIwaImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CropIwaImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
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
            moveToAllowedBounds();
            updateImageBounds();
        }
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

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float newScale = matrixUtils.getScaleX(imageMatrix) * scaleFactor;
            if (isValidScale(newScale)) {
                scaleImage(scaleFactor, detector.getFocusX(), detector.getFocusY());
            }
            return true;
        }

        private boolean isValidScale(float newScale) {
            return newScale >= MIN_SCALE && newScale <= (MIN_SCALE + MAX_SCALE);
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
            scaleDetector.onTouchEvent(event);

            if (!scaleDetector.isInProgress()) {
                translationGestureListener.onTouchEvent(event);
            }
        }
    }

}