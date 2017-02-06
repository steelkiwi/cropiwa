package com.steelkiwi.cropiwa.customization;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.steelkiwi.cropiwa.R;
import com.steelkiwi.cropiwa.customization.shape.CropIwaCircleShape;
import com.steelkiwi.cropiwa.customization.shape.CropIwaRectShape;
import com.steelkiwi.cropiwa.customization.shape.CropIwaShape;
import com.steelkiwi.cropiwa.util.ResUtil;

/**
 * @author yarolegovich https://github.com/yarolegovich
 * 04.02.2017.
 */
public class CropIwaOverlayConfig {

    //Default size of crop area in percents
    private static final int DEFAULT_INITIAL_HEIGHT = 50;
    private static final int DEFAULT_INITIAL_WIDTH = 50;

    public static CropIwaOverlayConfig createDefault(Context context) {
        ResUtil r = new ResUtil(context);
        return new CropIwaOverlayConfig()
                .setBorderColor(r.color(R.color.cropiwa_default_border_color))
                .setCornerColor(r.color(R.color.cropiwa_default_corner_color))
                .setGridColor(r.color(R.color.cropiwa_default_grid_color))
                .setOverlayColor(r.color(R.color.cropiwa_default_overlay_color))
                .setBorderStrokeWidth(r.dimen(R.dimen.cropiwa_default_border_stroke_width))
                .setCornerStrokeWidth(r.dimen(R.dimen.cropiwa_default_corner_stroke_width))
                .setGridStrokeWidth(r.dimen(R.dimen.cropiwa_default_grid_stroke_width))
                .setInitialWidth(DEFAULT_INITIAL_WIDTH)
                .setInitialHeight(DEFAULT_INITIAL_HEIGHT)
                .setMinWidth(r.dimen(R.dimen.cropiwa_default_min_width))
                .setMinHeight(r.dimen(R.dimen.cropiwa_default_min_height))
                .setShouldDrawGrid(true)
                .setCropShape(new CropIwaRectShape());
    }

    private int overlayColor;

    private int borderColor;
    private int cornerColor;
    private int gridColor;
    private int borderStrokeWidth;

    private int cornerStrokeWidth;
    private int gridStrokeWidth;
    private int minHeight;

    private int minWidth;
    private int initialHeight;
    private int initialWidth;

    private boolean shouldDrawGrid;
    private CropIwaShape cropShape;

    private View overlayView;

    public int getOverlayColor() {
        return overlayColor;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public int getCornerColor() {
        return cornerColor;
    }

    public int getBorderStrokeWidth() {
        return borderStrokeWidth;
    }

    public int getCornerStrokeWidth() {
        return cornerStrokeWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public int getInitialHeight() {
        return initialHeight;
    }

    public int getInitialWidth() {
        return initialWidth;
    }

    public int getGridColor() {
        return gridColor;
    }

    public int getGridStrokeWidth() {
        return gridStrokeWidth;
    }

    public boolean shouldDrawGrid() {
        return shouldDrawGrid;
    }

    public CropIwaShape getCropShape() {
        return cropShape;
    }

    public CropIwaOverlayConfig setOverlayColor(int overlayColor) {
        this.overlayColor = overlayColor;
        return this;
    }

    public CropIwaOverlayConfig setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public CropIwaOverlayConfig setCornerColor(int cornerColor) {
        this.cornerColor = cornerColor;
        return this;
    }

    public CropIwaOverlayConfig setGridColor(int gridColor) {
        this.gridColor = gridColor;
        return this;
    }

    public CropIwaOverlayConfig setBorderStrokeWidth(int borderStrokeWidth) {
        this.borderStrokeWidth = borderStrokeWidth;
        return this;
    }

    public CropIwaOverlayConfig setCornerStrokeWidth(int cornerStrokeWidth) {
        this.cornerStrokeWidth = cornerStrokeWidth;
        return this;
    }

    public CropIwaOverlayConfig setGridStrokeWidth(int gridStrokeWidth) {
        this.gridStrokeWidth = gridStrokeWidth;
        return this;
    }

    public CropIwaOverlayConfig setMinHeight(int minHeight) {
        this.minHeight = minHeight;
        return this;
    }

    public CropIwaOverlayConfig setMinWidth(int minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public CropIwaOverlayConfig setInitialHeight(int initialHeight) {
        this.initialHeight = initialHeight;
        return this;
    }

    public CropIwaOverlayConfig setInitialWidth(int initialWidth) {
        this.initialWidth = initialWidth;
        return this;
    }

    public CropIwaOverlayConfig setShouldDrawGrid(boolean shouldDrawGrid) {
        this.shouldDrawGrid = shouldDrawGrid;
        return this;
    }

    public CropIwaOverlayConfig setCropShape(@NonNull CropIwaShape cropShape) {
        this.cropShape = cropShape;
        return this;
    }

    public void setOverlayView(View v) {
        overlayView = v;
    }

    public void apply() {
        if (overlayView != null) {
            overlayView.invalidate();
        }
    }
}
