package com.steelkiwi.cropiwa;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */
class CropIwaImageView extends ImageView {

    private static final float MAX_SCALE = 3f;
    private static final float MIN_SCALE = 1f;

    private Matrix imageMatrix;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector translationDetector;

    private float defaultXTranslation;
    private float defaultYTranslation;

    private float[] matrixValuesOut;

    private float defaultScale;
    private int imageHeight;
    private int imageWidth;

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

        matrixValuesOut = new float[9];

        imageMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);

        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());
        translationDetector = new GestureDetector(getContext(), new TranslationGestureListener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (hasImageSize()) {
            boolean shouldTranslate = w != getImageWidth() || h != getImageHeight();
            if (shouldTranslate) {
                defaultXTranslation = (w - getImageWidth()) * 0.5f;
                defaultYTranslation = (h - getImageHeight()) * 0.5f;
                imageMatrix.postTranslate(defaultXTranslation, defaultYTranslation);
                setImageMatrix(imageMatrix);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (hasImageSize()) {
            if (getMeasuredWidth() < getMeasuredHeight()) {
                imageWidth = getMeasuredWidth();
                imageHeight = (getRealImageHeight() * imageWidth) / getRealImageWidth();
            } else {
                imageHeight = getMeasuredHeight();
                imageWidth = (getRealImageWidth() * imageHeight) / getRealImageHeight();
            }
            defaultScale = imageWidth / getRealImageWidth();
            imageMatrix.postScale(defaultScale, defaultScale);
            setImageMatrix(imageMatrix);
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
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public boolean hasImageSize() {
        return getImageWidth() != -1 && getImageHeight() != -1;
    }

    public ScaleGestureDetector getScaleDetector() {
        return scaleGestureDetector;
    }

    public GestureDetector getTranslationDetector() {
        return translationDetector;
    }

    private float getCurrentScale() {
        imageMatrix.getValues(matrixValuesOut);
        return (float) Math.hypot(
                matrixValuesOut[Matrix.MSCALE_X],
                matrixValuesOut[Matrix.MSCALE_Y]);
    }

    private float getCurrentXTranslation() {
        imageMatrix.getValues(matrixValuesOut);
        return matrixValuesOut[Matrix.MTRANS_X];
    }

    private float getCurrentYTranslation() {
        imageMatrix.getValues(matrixValuesOut);
        return matrixValuesOut[Matrix.MTRANS_Y];
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            if (isScaleAllowed(scaleFactor)) {
                imageMatrix.postScale(scaleFactor, scaleFactor,
                        detector.getFocusX(),
                        detector.getFocusY());
                setImageMatrix(imageMatrix);
                invalidate();
            }
            return true;
        }

        private boolean isScaleAllowed(float factor) {
            float newScale = getCurrentScale() * factor;
            return newScale >= (defaultScale) && newScale <= (defaultScale + MAX_SCALE);
        }
    }

    private class TranslationGestureListener extends GestureDetector.SimpleOnGestureListener {

        private float prevX;
        private float prevY;

        @Override
        public boolean onDown(MotionEvent e) {
            saveCoordinates(e);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float deltaX = getDeltaWithBoundCheck(
                    e2.getX() - prevX, getCurrentXTranslation(),
                    getWidth() - getRealImageWidth());
            float deltaY = getDeltaWithBoundCheck(
                    e2.getY() - prevY, getCurrentYTranslation(),
                    getHeight() - getRealImageHeight());
            imageMatrix.postTranslate(deltaX, deltaY);
            setImageMatrix(imageMatrix);
            saveCoordinates(e2);
            return true;
        }

        private void saveCoordinates(MotionEvent event) {
            prevX = event.getX();
            prevY = event.getY();
        }

        private float getDeltaWithBoundCheck(float delta, float translation, float bound) {
//            float scaledBound = bound * getCurrentScale();
//            float newValue = translation + delta;
//            if (newValue < 0) {
//                return -translation;
//            } else if (newValue > scaledBound) {
//                return scaledBound - translation;
//            } else {
            return delta;
        }
    }

}
