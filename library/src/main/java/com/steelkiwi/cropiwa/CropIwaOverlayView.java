package com.steelkiwi.cropiwa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import static com.steelkiwi.cropiwa.Utils.boundValue;
import static com.steelkiwi.cropiwa.Utils.dpToPx;
import static com.steelkiwi.cropiwa.Utils.enlargeRectBy;
import static com.steelkiwi.cropiwa.Utils.moveRectBounded;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */
@SuppressLint("ViewConstructor")
class CropIwaOverlayView extends View {

    private static final float CLICK_AREA_CORNER_POINT = dpToPx(24);

    private static final int LEFT_TOP = 0;
    private static final int RIGHT_TOP = 1;
    private static final int LEFT_BOTTOM = 2;
    private static final int RIGHT_BOTTOM = 3;

    private Paint clearPaint;
    private Paint generalPaint;

    private float[][] cornerSides;
    private CornerPoint[] cornerPoints;
    private SparseArray<CornerPoint> fingerToCornerMapping;

    private PointF cropDragStartPoint;
    private RectF cropRectBeforeDrag;
    private RectF cropRect;
    private Path cornerPath;

    private CropIwaOverlayConfig config;

    public CropIwaOverlayView(Context context, CropIwaOverlayConfig config) {
        super(context);
        initWith(config);
    }

    private void initWith(CropIwaOverlayConfig c) {
        config = c;
        config.setOverlayView(this);

        fingerToCornerMapping = new SparseArray<>();
        cornerPoints = new CornerPoint[4];
        cropRect = new RectF();
        cornerPath = new Path();

        clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        generalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        float cornerCathetusLength = Math.min(config.getMinWidth(), config.getMinHeight()) * 0.3f;
        cornerSides = generateCornerSides(cornerCathetusLength);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        boolean cornerPointsAreNotInitialized = cornerPoints[0] == null;
        if (cornerPointsAreNotInitialized) {
            float centerX = w * 0.5f, centerY = h * 0.5f;
            //Initial width/height are in percents of view's width and height
            float halfWidth = w * config.getInitialWidth() * 0.01f * 0.5f;
            float halfHeight = h * config.getInitialHeight() * 0.01f * 0.5f;
            cropRect.set(
                    centerX - halfWidth, centerY - halfHeight,
                    centerX + halfWidth, centerY + halfHeight);
            initCornerPoints();
        }
    }

    private void initCornerPoints() {
        PointF leftTop = new PointF(cropRect.left, cropRect.top);
        PointF leftBot = new PointF(cropRect.left, cropRect.bottom);
        PointF rightTop = new PointF(cropRect.right, cropRect.top);
        PointF rightBot = new PointF(cropRect.right, cropRect.bottom);
        cornerPoints[LEFT_TOP] = new CornerPoint(leftTop, rightTop, leftBot);
        cornerPoints[LEFT_BOTTOM] = new CornerPoint(leftBot, rightBot, leftTop);
        cornerPoints[RIGHT_TOP] = new CornerPoint(rightTop, leftTop, rightBot);
        cornerPoints[RIGHT_BOTTOM] = new CornerPoint(rightBot, leftBot, rightTop);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //We will get here measured dimensions of an ImageView
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return onStartGesture(ev);
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
        return true;
    }

    /**
     * @return {@literal true} if we are interested in further processing of the event
     */
    private boolean onStartGesture(MotionEvent ev) {
        //Does user want to resize the crop area?
        if (tryAssociateWithCorner(ev)) {
            return true;
        }
        //Does user want to drag the crop area?
        int index = ev.getActionIndex();
        if (cropRect.contains(ev.getX(index), ev.getY(index))) {
            cropDragStartPoint = new PointF(ev.getX(index), ev.getY(index));
            cropRectBeforeDrag = new RectF(cropRect);
            return true;
        }
        //No, we are not interested in this event
        return false;
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
        configurePaintToDrawOverlay(generalPaint);
        canvas.drawRect(0, 0, getWidth(), getHeight(), generalPaint);

        canvas.drawRect(cropRect, clearPaint);

        configurePaintToDrawBorder(generalPaint);
        canvas.drawRect(cropRect, generalPaint);

        if (config.shouldDrawGrid()) {
            configurePaintToDrawGrid(generalPaint);
            drawGrid(canvas, generalPaint);
        }

        configurePaintToDrawCorners(generalPaint);
        for (int i = 0; i < cornerPoints.length; i++) {
            drawCorner(canvas, cornerPoints[i], cornerSides[i][0], cornerSides[i][1], generalPaint);
        }
    }

    private void drawCorner(Canvas canvas, CornerPoint point, float deltaX, float deltaY, Paint paint) {
        cornerPath.rewind();
        cornerPath.moveTo(point.x(), point.y());
        cornerPath.rLineTo(deltaX, 0);
        cornerPath.moveTo(point.x(), point.y());
        cornerPath.rLineTo(0, deltaY);
        canvas.drawPath(cornerPath, paint);
    }

    private void drawGrid(Canvas canvas, Paint paint) {
        float stepX = cropRect.width() * 0.333f;
        float stepY = cropRect.height() * 0.333f;
        float x = cropRect.left;
        float y = cropRect.top;
        for (int i = 0; i < 3; i++) {
            x += stepX;
            y += stepY;
            canvas.drawLine(x, cropRect.top, x, cropRect.bottom, paint);
            canvas.drawLine(cropRect.left, y, cropRect.right, y, paint);
        }
    }

    private void configurePaintToDrawCorners(Paint paint) {
        paint.setColor(config.getCornerColor());
        paint.setStrokeWidth(config.getCornerStrokeWidth());
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void configurePaintToDrawOverlay(Paint paint) {
        paint.setColor(config.getOverlayColor());
        paint.setStyle(Paint.Style.FILL);
    }

    private void configurePaintToDrawBorder(Paint paint) {
        paint.setColor(config.getBorderColor());
        paint.setStrokeWidth(config.getBorderStrokeWidth());
        paint.setStyle(Paint.Style.STROKE);
    }

    private void configurePaintToDrawGrid(Paint paint) {
        paint.setColor(config.getGridColor());
        paint.setStrokeWidth(config.getGridStrokeWidth());
        paint.setStrokeCap(Paint.Cap.SQUARE);
    }

    private boolean isResizing() {
        return fingerToCornerMapping.size() != 0;
    }

    private boolean isDraggingCropArea() {
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