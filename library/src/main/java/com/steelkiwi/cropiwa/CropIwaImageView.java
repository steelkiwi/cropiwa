package com.steelkiwi.cropiwa;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
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
    private GestureDetector gestureDetector;

    private RectF allowedBounds;
    private RectF imageBounds;
    private RectF realImageBounds;

    private MatrixUtils matrixUtils;

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
        setImageResource(R.drawable.default_image);

        imageBounds = new RectF();
        allowedBounds = new RectF();
        realImageBounds = new RectF();

        matrixUtils = new MatrixUtils();

        imageMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);

        gestureDetector = new GestureDetector();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (hasImageSize() && allowedBounds.width() != 0 && allowedBounds.height() != 0) {
            moveToAllowedBounds();
            updateImageBounds();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (hasImageSize()) {
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
        return getImageWidth() != -1 && getImageHeight() != -1;
    }

    public GestureDetector getImageTransformGestureDetector() {
        return gestureDetector;
    }

    @Override
    public void onNewBounds(RectF bounds) {
        allowedBounds.set(bounds);
        moveToAllowedBounds();
        updateImageBounds();
        invalidate();
    }

    private void moveToAllowedBounds() {
        updateImageBounds();
        imageMatrix.set(MatrixUtils.findTransformToAllowedBounds(
                realImageBounds, imageMatrix,
                allowedBounds));
        setImageMatrix(imageMatrix);
    }

    private void animateToAllowedBounds() {
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

    public void scaleImage(float factor, float pivotX, float pivotY) {
        imageMatrix.postScale(factor, factor, pivotX, pivotY);
        setImageMatrix(imageMatrix);
        updateImageBounds();
    }

    public void translateImage(float deltaX, float deltaY) {
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

    private class TranslationGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

        private float prevX;
        private float prevY;
        private float id;

        private TensionInterpolator interpolator = new TensionInterpolator();

        @Override
        public boolean onDown(MotionEvent e) {
            updateImageBounds();
            interpolator.onDown(e.getX(), e.getY(), imageBounds, allowedBounds);
            saveCoordinates(e);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (e2.getPointerId(0) != id) return false;

            updateImageBounds();

            float currentX = interpolator.interpolateX(e2.getX());
            float currentY = interpolator.interpolateY(e2.getY());

            translateImage(currentX - prevX, currentY - prevY);
            saveCoordinates(e2);

            prevX = currentX;
            prevY = currentY;

            return true;
        }

        private void saveCoordinates(MotionEvent event) {
            prevX = event.getX();
            prevY = event.getY();
            id = event.getPointerId(0);
        }
    }

    public class GestureDetector {

        private android.view.GestureDetector translationDetector;
        private ScaleGestureDetector scaleDetector;

        public GestureDetector() {
            translationDetector = new android.view.GestureDetector(getContext(), new TranslationGestureListener());
            scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());
        }

        public void onTouchEvent(MotionEvent event) {
            scaleDetector.onTouchEvent(event);

            if (!scaleDetector.isInProgress()) {
                translationDetector.onTouchEvent(event);
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    animateToAllowedBounds();
                    break;
            }
        }
    }
}