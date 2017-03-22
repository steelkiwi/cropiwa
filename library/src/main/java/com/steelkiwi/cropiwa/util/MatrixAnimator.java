package com.steelkiwi.cropiwa.util;

import android.animation.FloatEvaluator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.lang.ref.WeakReference;

/**
 * Created by yarolegovich on 06.02.2017.
 */
public class MatrixAnimator {

    public void animate(Matrix initial, Matrix target, ValueAnimator.AnimatorUpdateListener listener) {
        ValueAnimator animator = ValueAnimator.ofObject(new MatrixEvaluator(), initial, target);
        animator.addUpdateListener(new SafeListener(listener));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(200);
        animator.start();
    }

    private static class MatrixEvaluator implements TypeEvaluator<Matrix> {

        private Matrix current;
        private Matrix lastStart, lastEnd;
        private FloatEvaluator floatEvaluator;

        private float initialTranslationX;
        private float initialTranslationY;
        private float initialScale;

        private float endTranslationX;
        private float endTranslationY;
        private float endScale;

        public MatrixEvaluator() {
            current = new Matrix();
            floatEvaluator = new FloatEvaluator();
        }

        @Override
        public Matrix evaluate(float fraction, Matrix startValue, Matrix endValue) {
            if (shouldReinitialize(startValue, endValue)) {
                collectValues(startValue, endValue);
            }
            float translationX = floatEvaluator.evaluate(fraction, initialTranslationX, endTranslationX);
            float translationY = floatEvaluator.evaluate(fraction, initialTranslationY, endTranslationY);
            float scale = floatEvaluator.evaluate(fraction, initialScale, endScale);
            current.reset();
            current.postScale(scale, scale);
            current.postTranslate(translationX, translationY);
            return current;
        }

        private boolean shouldReinitialize(Matrix start, Matrix end) {
            return lastStart != start || lastEnd != end;
        }

        private void collectValues(Matrix start, Matrix end) {
            MatrixUtils matrixUtils = new MatrixUtils();
            initialTranslationX = matrixUtils.getXTranslation(start);
            initialTranslationY = matrixUtils.getYTranslation(start);
            initialScale = matrixUtils.getScaleX(start);
            endTranslationX = matrixUtils.getXTranslation(end);
            endTranslationY = matrixUtils.getYTranslation(end);
            endScale = matrixUtils.getScaleX(end);
            lastStart = start;
            lastEnd = end;
        }
    }
    private static class SafeListener implements ValueAnimator.AnimatorUpdateListener {

        private WeakReference<ValueAnimator.AnimatorUpdateListener> wrapped;

        private SafeListener(ValueAnimator.AnimatorUpdateListener wrapped) {
            this.wrapped = new WeakReference<>(wrapped);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            ValueAnimator.AnimatorUpdateListener listener = wrapped.get();
            if (listener != null) {
                listener.onAnimationUpdate(animation);
            }
        }
    }
}
