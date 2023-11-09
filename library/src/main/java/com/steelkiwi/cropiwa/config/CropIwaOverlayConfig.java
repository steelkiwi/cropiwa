package com.steelkiwi.cropiwa.config;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import com.steelkiwi.cropiwa.AspectRatio;
import com.steelkiwi.cropiwa.R;
import com.steelkiwi.cropiwa.shape.CropIwaOvalShape;
import com.steelkiwi.cropiwa.shape.CropIwaRectShape;
import com.steelkiwi.cropiwa.shape.CropIwaShape;
import com.steelkiwi.cropiwa.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yarolegovich https://github.com/yarolegovich
 *         04.02.2017.
 */
public class CropIwaOverlayConfig {

    private static final float DEFAULT_CROP_SCALE = 0.8f;

    public static CropIwaOverlayConfig createDefault(Context context) {
        ResUtil r = new ResUtil(context);
        CropIwaOverlayConfig config = new CropIwaOverlayConfig()
                .setBorderColor(r.color(R.color.cropiwa_default_border_color))
                .setCornerColor(r.color(R.color.cropiwa_default_corner_color))
                .setGridColor(r.color(R.color.cropiwa_default_grid_color))
                .setOverlayColor(r.color(R.color.cropiwa_default_overlay_color))
                .setBorderStrokeWidth(r.dimen(R.dimen.cropiwa_default_border_stroke_width))
                .setCornerStrokeWidth(r.dimen(R.dimen.cropiwa_default_corner_stroke_width))
                .setCropScale(DEFAULT_CROP_SCALE)
                .setGridStrokeWidth(r.dimen(R.dimen.cropiwa_default_grid_stroke_width))
                .setMinWidth(r.dimen(R.dimen.cropiwa_default_min_width))
                .setMinHeight(r.dimen(R.dimen.cropiwa_default_min_height))
                .setAspectRatio(new AspectRatio(2, 1))
                .setShouldDrawGrid(true)
                .setDynamicCrop(true);
        CropIwaShape shape = new CropIwaRectShape(config);
        config.setCropShape(shape);
        return config;
    }

    public static CropIwaOverlayConfig createFromAttributes(Context context, AttributeSet attrs) {
        CropIwaOverlayConfig c = CropIwaOverlayConfig.createDefault(context);
        if (attrs == null) {
            return c;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CropIwaView);
        try {
            c.setMinWidth(ta.getDimensionPixelSize(
                    R.styleable.CropIwaView_ci_min_crop_width,
                    c.getMinWidth()));
            c.setMinHeight(ta.getDimensionPixelSize(
                    R.styleable.CropIwaView_ci_min_crop_height,
                    c.getMinHeight()));
            c.setAspectRatio(new AspectRatio(
                    ta.getInteger(R.styleable.CropIwaView_ci_aspect_ratio_w, 1),
                    ta.getInteger(R.styleable.CropIwaView_ci_aspect_ratio_h, 1)));
            c.setCropScale(ta.getFloat(
                    R.styleable.CropIwaView_ci_crop_scale,
                    c.getCropScale()));
            c.setBorderColor(ta.getColor(
                    R.styleable.CropIwaView_ci_border_color,
                    c.getBorderColor()));
            c.setBorderStrokeWidth(ta.getDimensionPixelSize(
                    R.styleable.CropIwaView_ci_border_width,
                    c.getBorderStrokeWidth()));
            c.setCornerColor(ta.getColor(
                    R.styleable.CropIwaView_ci_corner_color,
                    c.getCornerColor()));
            c.setCornerStrokeWidth(ta.getDimensionPixelSize(
                    R.styleable.CropIwaView_ci_corner_width,
                    c.getCornerStrokeWidth()));
            c.setGridColor(ta.getColor(
                    R.styleable.CropIwaView_ci_grid_color,
                    c.getGridColor()));
            c.setGridStrokeWidth(ta.getDimensionPixelSize(
                    R.styleable.CropIwaView_ci_grid_width,
                    c.getGridStrokeWidth()));
            c.setShouldDrawGrid(ta.getBoolean(
                    R.styleable.CropIwaView_ci_draw_grid,
                    c.shouldDrawGrid()));
            c.setOverlayColor(ta.getColor(
                    R.styleable.CropIwaView_ci_overlay_color,
                    c.getOverlayColor()));
            c.setCropShape(ta.getInt(R.styleable.CropIwaView_ci_crop_shape, 0) == 0 ?
                    new CropIwaRectShape(c) :
                    new CropIwaOvalShape(c));
            c.setDynamicCrop(ta.getBoolean(
                    R.styleable.CropIwaView_ci_dynamic_aspect_ratio,
                    c.isDynamicCrop()));
        } finally {
            ta.recycle();
        }
        return c;
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

    private float cropScale;

    private boolean isDynamicCrop;
    private boolean shouldDrawGrid;
    private CropIwaShape cropShape;

    private List<ConfigChangeListener> listeners;
    private List<ConfigChangeListener> iterationList;

    public CropIwaOverlayConfig() {
        listeners = new ArrayList<>();
        iterationList = new ArrayList<>();
    }

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

    public float getCropScale() {
        return cropScale;
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

    public CropIwaOverlayConfig setCropScale(@FloatRange(from = 0.01, to = 1f) float cropScale) {
        this.cropScale = cropScale;
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
        if (this.cropShape != null) {
            removeConfigChangeListener(this.cropShape);
        }
        this.cropShape = cropShape;
        return this;
    }

    public CropIwaOverlayConfig setDynamicCrop(boolean enabled) {
        this.isDynamicCrop = enabled;
        return this;
    }

    public void addConfigChangeListener(ConfigChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeConfigChangeListener(ConfigChangeListener listener) {
        listeners.remove(listener);
    }

    public void apply() {
        iterationList.addAll(listeners);
        for (ConfigChangeListener listener : iterationList) {
            listener.onConfigChanged();
        }
        iterationList.clear();
    }
}
