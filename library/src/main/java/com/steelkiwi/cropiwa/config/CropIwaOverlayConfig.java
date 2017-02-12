package com.steelkiwi.cropiwa.config;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.steelkiwi.cropiwa.AspectRatio;
import com.steelkiwi.cropiwa.R;
import com.steelkiwi.cropiwa.shape.CropIwaOvalShape;
import com.steelkiwi.cropiwa.shape.CropIwaRectShape;
import com.steelkiwi.cropiwa.shape.CropIwaShape;
import com.steelkiwi.cropiwa.util.ResUtil;

/**
 * @author yarolegovich https://github.com/yarolegovich
 * 04.02.2017.
 */
public class CropIwaOverlayConfig {

    public static CropIwaOverlayConfig createDefault(Context context) {
        ResUtil r = new ResUtil(context);
        CropIwaOverlayConfig config = new CropIwaOverlayConfig()
                .setBorderColor(r.color(R.color.cropiwa_default_border_color))
                .setCornerColor(r.color(R.color.cropiwa_default_corner_color))
                .setGridColor(r.color(R.color.cropiwa_default_grid_color))
                .setOverlayColor(r.color(R.color.cropiwa_default_overlay_color))
                .setBorderStrokeWidth(r.dimen(R.dimen.cropiwa_default_border_stroke_width))
                .setCornerStrokeWidth(r.dimen(R.dimen.cropiwa_default_corner_stroke_width))
                .setGridStrokeWidth(r.dimen(R.dimen.cropiwa_default_grid_stroke_width))
                .setMinWidth(r.dimen(R.dimen.cropiwa_default_min_width))
                .setMinHeight(r.dimen(R.dimen.cropiwa_default_min_height))
                .setAspectRatio(new AspectRatio(1, 1))
                .setShouldDrawGrid(true)
                .setDynamicCrop(false);
        CropIwaShape shape = new CropIwaRectShape(config);
        config.setCropShape(shape);
        return config;
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

    private AspectRatio aspectRatio;

    private boolean isDynamicCrop;
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

    public boolean isDynamicCrop() {
        return isDynamicCrop;
    }

    public AspectRatio getAspectRatio() {
        return aspectRatio;
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

    public CropIwaOverlayConfig setAspectRatio(AspectRatio ratio) {
        this.aspectRatio = ratio;
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

    public CropIwaOverlayConfig setDynamicCrop(boolean enabled) {
        this.isDynamicCrop = enabled;
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
