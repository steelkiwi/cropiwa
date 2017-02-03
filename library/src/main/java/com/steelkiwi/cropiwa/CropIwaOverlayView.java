package com.steelkiwi.cropiwa;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */
class CropIwaOverlayView extends View {

    private static final float CLICK_AREA_CORNER_POINT = 0f;
    private static final int MIN_HEIGHT_CROP_AREA = 0;
    private static final int MIN_WIDTH_CROP_AREA = 0;

    public CropIwaOverlayView(Context context) {
        super(context);
    }

    public CropIwaOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CropIwaOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CropIwaOverlayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private static class CornerPoint {

        private RectF clickableArea;

        private PointF thisPoint;
        private PointF horizontalNeighbourPoint;
        private PointF verticalNeighbourPoint;

        public CornerPoint(
                PointF thisPoint, PointF horizontalNeighbourPoint,
                PointF verticalNeighbourPoint) {
            this.thisPoint = thisPoint;
            this.horizontalNeighbourPoint = horizontalNeighbourPoint;
            this.verticalNeighbourPoint = verticalNeighbourPoint;
            this.clickableArea = new RectF();
        }

        public void moveTo(float x, float y) {
            processDrag(x, y);
        }

        public void processDrag(float x, float y) {
            float newX = computeCoordinate(
                    thisPoint.x, x, horizontalNeighbourPoint.x,
                    MIN_WIDTH_CROP_AREA);
            thisPoint.x = newX;
            verticalNeighbourPoint.x = newX;

            float newY = computeCoordinate(
                    thisPoint.y, y, verticalNeighbourPoint.y,
                    MIN_HEIGHT_CROP_AREA);
            thisPoint.y = newY;
            horizontalNeighbourPoint.y = newY;
        }

        private float computeCoordinate(float old, float candidate, float opposite, int min) {
            float minAllowedPosition;
            boolean isCandidateAllowed = Math.abs(candidate - opposite) > min;
            boolean isDraggingFromLeftOrTop = opposite > old;
            if (isDraggingFromLeftOrTop) {
                minAllowedPosition = opposite - min;
                isCandidateAllowed &= candidate < opposite;
            } else {
                minAllowedPosition = opposite + min;
                isCandidateAllowed &= candidate > opposite;
            }
            return isCandidateAllowed ? candidate : minAllowedPosition;
        }

        public boolean isClicked(float x, float y) {
            clickableArea.set(thisPoint.x, thisPoint.y, thisPoint.x, thisPoint.y);
            Utils.enlargeRectBy(CLICK_AREA_CORNER_POINT, clickableArea);
            return clickableArea.contains(x, y);
        }
    }
}
