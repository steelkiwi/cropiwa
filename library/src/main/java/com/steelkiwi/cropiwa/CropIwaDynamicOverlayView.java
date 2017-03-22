package com.steelkiwi.cropiwa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;
import com.steelkiwi.cropiwa.shape.CropIwaShape;
import com.steelkiwi.cropiwa.util.CropIwaUtils;

import java.util.Arrays;

import static com.steelkiwi.cropiwa.util.CropIwaUtils.boundValue;
import static com.steelkiwi.cropiwa.util.CropIwaUtils.dpToPx;
import static com.steelkiwi.cropiwa.util.CropIwaUtils.enlargeRectBy;
import static com.steelkiwi.cropiwa.util.CropIwaUtils.moveRectBounded;

/**
 * @author yarolegovich
 * on 05.02.2017.
 */
@SuppressLint("ViewConstructor")
class CropIwaDynamicOverlayView extends CropIwaOverlayView {

    private static final float CLICK_AREA_CORNER_POINT = dpToPx(24);

    private static final int LEFT_TOP = 0;
    private static final int RIGHT_TOP = 1;
    private static final int LEFT_BOTTOM = 2;
    private static final int RIGHT_BOTTOM = 3;

    private float[][] cornerSides;
    private CornerPoint[] cornerPoints;
    private SparseArray<CornerPoint> fingerToCornerMapping;

    private PointF cropDragStartPoint;
    private RectF cropRectBeforeDrag;

    public CropIwaDynamicOverlayView(Context context, CropIwaOverlayConfig config) {
        super(context, config);
    }

    @Override
    protected void initWith(CropIwaOverlayConfig config) {
        super.initWith(config);

        fingerToCornerMapping = new SparseArray<>();
        cornerPoints = new CornerPoint[4];

        float cornerCathetusLength = Math.min(config.getMinWidth(), config.getMinHeight()) * 0.3f;
        cornerSides = generateCornerSides(cornerCathetusLength);
    }

    @Override
    public void onImagePositioned(RectF imageRect) {
        super.onImagePositioned(imageRect);
        initCornerPoints();
        invalidate();
    }

    private void initCornerPoints() {
        if (cropRect.width() > 0 && cropRect.height() > 0) {
            if (CropIwaUtils.isAnyNull(Arrays.asList(cornerPoints))) {
                PointF leftTop = new PointF(cropRect.left, cropRect.top);
                PointF leftBot = new PointF(cropRect.left, cropRect.bottom);
                PointF rightTop = new PointF(cropRect.right, cropRect.top);
                PointF rightBot = new PointF(cropRect.right, cropRect.bottom);
                cornerPoints[LEFT_TOP] = new CornerPoint(leftTop, rightTop, leftBot);
                cornerPoints[LEFT_BOTTOM] = new CornerPoint(leftBot, rightBot, leftTop);
                cornerPoints[RIGHT_TOP] = new CornerPoint(rightTop, leftTop, rightBot);
                cornerPoints[RIGHT_BOTTOM] = new CornerPoint(rightBot, leftBot, rightTop);
            } else {
                updateCornerPointsCoordinates();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!shouldDrawOverlay) {
            return false;
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onStartGesture(ev);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onPointerDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                onPointerMove(ev);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onPointerUp(ev);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                onEndGesture();
                break;
            default:
                return false;
        }
        invalidate();
//        invalidate(
//                (int) cropRect.left, (int) cropRect.top,
//                (int) cropRect.right, (int) cropRect.bottom);
        return true;
    }

    private void onStartGesture(MotionEvent ev) {
        //Does user want to resize the crop area?
        if (tryAssociateWithCorner(ev)) {
            return;
        }
        //Does user want to drag the crop area?
        int index = ev.getActionIndex();
        if (cropRect.contains(ev.getX(index), ev.getY(index))) {
            cropDragStartPoint = new PointF(ev.getX(index), ev.getY(index));
            cropRectBeforeDrag = new RectF(cropRect);
        }
    }

    private void onPointerDown(MotionEvent ev) {
        if (isResizing()) {
            tryAssociateWithCorner(ev);
        }
    }

    private void onPointerUp(MotionEvent ev) {
        int id = ev.getPointerId(ev.getActionIndex());
        fingerToCornerMapping.remove(id);
    }

    private void onPointerMove(MotionEvent ev) {
        if (isResizing()) {
            for (int i = 0; i < ev.getPointerCount(); i++) {
                int id = ev.getPointerId(i);
                CornerPoint point = fingerToCornerMapping.get(id);
                if (point != null) {
                    point.processDrag(
                            boundValue(ev.getX(i), 0, getWidth()),
                            boundValue(ev.getY(i), 0, getHeight()));
                }
            }
            updateCropAreaCoordinates();
        } else if (isDraggingCropArea()) {
            float deltaX = ev.getX() - cropDragStartPoint.x;
            float deltaY = ev.getY() - cropDragStartPoint.y;
            cropRect = moveRectBounded(
                    cropRectBeforeDrag, deltaX, deltaY,
                    getWidth(), getHeight(),
                    cropRect);
            updateCornerPointsCoordinates();
        }
    }

    private void onEndGesture() {
        if (cropRectBeforeDrag != null && !cropRectBeforeDrag.equals(cropRect)) {
            notifyNewBounds();
        }
        if (fingerToCornerMapping.size() > 0) {
            notifyNewBounds();
        }
        fingerToCornerMapping.clear();
        cropDragStartPoint = null;
        cropRectBeforeDrag = null;
    }

    private void updateCornerPointsCoordinates() {
        cornerPoints[LEFT_TOP].processDrag(cropRect.left, cropRect.top);
        cornerPoints[RIGHT_BOTTOM].processDrag(cropRect.right, cropRect.bottom);
    }

    private void updateCropAreaCoordinates() {
        cropRect.set(
                cornerPoints[LEFT_TOP].x(), cornerPoints[LEFT_TOP].y(),
                cornerPoints[RIGHT_BOTTOM].x(), cornerPoints[RIGHT_BOTTOM].y());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (shouldDrawOverlay) {
            super.onDraw(canvas);
            if (areCornersInitialized()) {
                CropIwaShape shape = config.getCropShape();
                for (int i = 0; i < cornerPoints.length; i++) {
                    shape.drawCorner(
                            canvas, cornerPoints[i].x(), cornerPoints[i].y(),
                            cornerSides[i][0], cornerSides[i][1]);
                }
            }
        }
    }

    @Override
    public boolean isResizing() {
        return fingerToCornerMapping.size() != 0;
    }

    @Override
    public boolean isDraggingCropArea() {
        return cropDragStartPoint != null;
    }

    /**
     * @return {@literal true} if ev.x && ev.y are in area of some corner point
     */
    private boolean tryAssociateWithCorner(MotionEvent ev) {
        int index = ev.getActionIndex();
        return tryAssociateWithCorner(
                ev.getPointerId(index),
                ev.getX(index), ev.getY(index));
    }

    private boolean tryAssociateWithCorner(int id, float x, float y) {
        for (CornerPoint cornerPoint : cornerPoints) {
            if (cornerPoint.isClicked(x, y)) {
                fingerToCornerMapping.put(id, cornerPoint);
                return true;
            }
        }
        return false;
    }

    private boolean areCornersInitialized() {
        return cornerPoints[0] != null && cornerPoints[0].isValid();
    }

    @Override
    public void onConfigChanged() {
        super.onConfigChanged();
        initCornerPoints();
    }

    private class CornerPoint {

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

        public void processDrag(float x, float y) {
            float newX = computeCoordinate(
                    thisPoint.x, x, horizontalNeighbourPoint.x,
                    config.getMinWidth());
            thisPoint.x = newX;
            verticalNeighbourPoint.x = newX;

            float newY = computeCoordinate(
                    thisPoint.y, y, verticalNeighbourPoint.y,
                    config.getMinHeight());
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
            enlargeRectBy(CLICK_AREA_CORNER_POINT, clickableArea);
            return clickableArea.contains(x, y);
        }

        public float x() {
            return thisPoint.x;
        }

        public float y() {
            return thisPoint.y;
        }

        @Override
        public String toString() {
            return thisPoint.toString();
        }

        public boolean isValid() {
            return Math.abs(thisPoint.x - horizontalNeighbourPoint.x) >= config.getMinWidth();
        }
    }

    private float[][] generateCornerSides(float length) {
        float[][] result = new float[4][2];
        result[LEFT_TOP] = new float[]{length, length};
        result[LEFT_BOTTOM] = new float[]{length, -length};
        result[RIGHT_TOP] = new float[]{-length, length};
        result[RIGHT_BOTTOM] = new float[]{-length, -length};
        return result;
    }
}