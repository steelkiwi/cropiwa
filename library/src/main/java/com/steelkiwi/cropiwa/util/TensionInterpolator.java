package com.steelkiwi.cropiwa.util;

import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by yarolegovich https://github.com/yarolegovich
 * on 06.02.2017.
 */
public class TensionInterpolator {


    private static final float TENSION_FACTOR = 10f;

    private float tensionZone;
    private float tensionZonePull;

    private TensionBorder yTensionBounds;
    private TensionBorder xTensionBounds;

    private Interpolator interpolator = new DecelerateInterpolator(2);

    private float downX, downY;

    public void onDown(float x, float y, RectF draggedObj, RectF tensionStartBorder) {
        downX = x;
        downY = y;

        tensionZone = Math.min(draggedObj.width(), draggedObj.height()) * 0.2f;
        tensionZonePull = tensionZone * TENSION_FACTOR;

        xTensionBounds = new TensionBorder(
                draggedObj.right - tensionStartBorder.right,
                tensionStartBorder.left - draggedObj.left);
        yTensionBounds = new TensionBorder(
                draggedObj.bottom - tensionStartBorder.bottom,
                tensionStartBorder.top - draggedObj.top);
    }

    public float interpolateX(float x) {
        return downX + interpolateDistance(x - downX, xTensionBounds);
    }

    public float interpolateY(float y) {
        return downY + interpolateDistance(y - downY, yTensionBounds);
    }

    private float interpolateDistance(float delta, TensionBorder tensionBorder) {
        float distance = Math.abs(delta);
        float direction = delta >= 0 ? 1 : -1;

        float tensionStart = direction == 1 ?
                tensionBorder.getPositiveTensionStart() :
                tensionBorder.getNegativeTensionStart();

        if (distance < tensionStart) {
            return delta;
        }

        float tensionDiff = distance - tensionStart;
        float tensionEnd = tensionStart + tensionZone;

        if (distance >= (tensionZonePull + tensionStart)) {
            return tensionEnd * direction;
        }

        float realProgress = tensionDiff / tensionZonePull;
        float progress = interpolator.getInterpolation(realProgress);

        return (tensionStart + progress * tensionZone) * direction;
    }

    private static class TensionBorder {

        private float negativeTensionStart;
        private float positiveTensionStart;

        private TensionBorder(float negativeTensionStart, float positiveTensionStart) {
            this.negativeTensionStart = Math.max(negativeTensionStart, 0);
            this.positiveTensionStart = Math.max(positiveTensionStart, 0);
        }

        public float getNegativeTensionStart() {
            return negativeTensionStart;
        }

        public float getPositiveTensionStart() {
            return positiveTensionStart;
        }

        @Override
        public String toString() {
            return "TensionBorder{" +
                    "negativeTensionStart=" + negativeTensionStart +
                    ", positiveTensionStart=" + positiveTensionStart +
                    '}';
        }
    }

}