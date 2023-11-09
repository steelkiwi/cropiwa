package com.steelkiwi.cropiwa.sample.config;

import android.graphics.Bitmap;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.steelkiwi.cropiwa.AspectRatio;
import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.ConfigChangeListener;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.steelkiwi.cropiwa.sample.R;
import com.steelkiwi.cropiwa.sample.adapter.AspectRatioPreviewAdapter;
import com.steelkiwi.cropiwa.sample.data.CropGallery;
import com.steelkiwi.cropiwa.shape.CropIwaOvalShape;
import com.steelkiwi.cropiwa.shape.CropIwaRectShape;
import com.steelkiwi.cropiwa.shape.CropIwaShape;
import com.yarolegovich.mp.MaterialPreferenceScreen;
import com.yarolegovich.mp.MaterialSeekBarPreference;
import com.yarolegovich.mp.io.StorageModule;
import com.yarolegovich.mp.util.Utils;

import java.util.Set;

public class CropViewConfigurator implements StorageModule, ConfigChangeListener,
        AspectRatioPreviewAdapter.OnNewSelectedListener {

    private CropIwaView cropIwaView;
    private CropIwaSaveConfig.Builder saveConfig;

    private RecyclerView fixedRatioList;

    private MaterialSeekBarPreference seekBarPreference;

    public CropViewConfigurator(CropIwaView cropIwaView, MaterialPreferenceScreen screen) {
        this.cropIwaView = cropIwaView;
        this.saveConfig = new CropIwaSaveConfig.Builder(CropGallery.createNewEmptyFile());
        this.seekBarPreference = (MaterialSeekBarPreference) screen.findViewById(R.id.scale_seek_bar);

        AspectRatioPreviewAdapter ratioPreviewAdapter = new AspectRatioPreviewAdapter();
        ratioPreviewAdapter.setListener(this);
        fixedRatioList = (RecyclerView) screen.findViewById(R.id.fixed_ratio_list);
        fixedRatioList.setLayoutManager(new LinearLayoutManager(
                cropIwaView.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        fixedRatioList.setAdapter(ratioPreviewAdapter);

        cropIwaView.configureImage().addConfigChangeListener(this);
    }


    @Override
    public void saveBoolean(String key, boolean value) {
        if (Prefs.keys().KEY_DRAW_GRID.equals(key)) {
            cropIwaView.configureOverlay().setShouldDrawGrid(value).apply();
        } else if (Prefs.keys().KEY_ENABLE_TRANSLATE.equals(key)) {
            cropIwaView.configureImage().setImageTranslationEnabled(value).apply();
        } else if (Prefs.keys().KEY_ENABLE_SCALE.equals(key)) {
            cropIwaView.configureImage().setImageScaleEnabled(value).apply();
        } else if (Prefs.keys().KEY_DYNAMIC_CROP.equals(key)) {
            cropIwaView.configureOverlay().setDynamicCrop(value).apply();
            fixedRatioList.setVisibility(value ? View.GONE : View.VISIBLE);
        } else if (Prefs.keys().KEY_DASHED_GRID.equals(key)) {
            int dashLength = Utils.dpToPixels(cropIwaView.getContext(), 2);
            int spaceLength = Utils.dpToPixels(cropIwaView.getContext(), 4);
            float[] intervals = {dashLength, spaceLength};
            PathEffect effect = value ? new DashPathEffect(intervals, 0) : null;
            getGridPaint().setPathEffect(effect);
            cropIwaView.invalidate();
        }
    }

    @Override
    public void saveString(String key, String value) {
        if (Prefs.keys().KEY_IMAGE_FORMAT.equals(key)) {
            saveConfig.setCompressFormat(stringToCompressFormat(value));
        } else if (Prefs.keys().KEY_CROP_SHAPE.equals(key)) {
            cropIwaView.configureOverlay().setCropShape(stringToCropShape(value)).apply();
        }
    }

    @Override
    public void saveInt(String key, int value) {
        if (Prefs.keys().KEY_GRID_COLOR.equals(key)) {
            cropIwaView.configureOverlay().setGridColor(value).apply();
        } else if (Prefs.keys().KEY_OVERLAY_COLOR.equals(key)) {
            cropIwaView.configureOverlay().setOverlayColor(value).apply();
        } else if (Prefs.keys().KEY_CORNER_COLOR.equals(key)) {
            cropIwaView.configureOverlay().setCornerColor(value).apply();
        } else if (Prefs.keys().KEY_BORDER_COLOR.equals(key)) {
            cropIwaView.configureOverlay().setBorderColor(value).apply();
        } else if (Prefs.keys().KEY_IMAGE_QUALITY.equals(key)) {
            saveConfig.setQuality(value);
        } else if (Prefs.keys().KEY_SCALE.equals(key)) {
            float newScale = value / 100f;
            if (Math.abs(newScale - cropIwaView.configureImage().getScale()) > 0.01) {
                cropIwaView.configureImage().setScale(value / 100f).apply();
            }
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defaultVal) {
        if (Prefs.keys().KEY_DRAW_GRID.equals(key)) {
            return cropIwaView.configureOverlay().shouldDrawGrid();
        } else if (Prefs.keys().KEY_ENABLE_TRANSLATE.equals(key)) {
            return cropIwaView.configureImage().isImageTranslationEnabled();
        } else if (Prefs.keys().KEY_ENABLE_SCALE.equals(key)) {
            return cropIwaView.configureImage().isImageScaleEnabled();
        } else if (Prefs.keys().KEY_DYNAMIC_CROP.equals(key)) {
            return cropIwaView.configureOverlay().isDynamicCrop();
        } else if (Prefs.keys().KEY_DASHED_GRID.equals(key)) {
            return getGridPaint().getPathEffect() != null;
        }
        return false;
    }

    @Override
    public String getString(String key, String defaultVal) {
        if (Prefs.keys().KEY_IMAGE_FORMAT.equals(key)) {
            return compressFormatToString(saveConfig.build().getCompressFormat());
        } else if (Prefs.keys().KEY_CROP_SHAPE.equals(key)) {
            return cropShapeToString(cropIwaView.configureOverlay().getCropShape());
        }
        return "";
    }

    @Override
    public int getInt(String key, int defaultVal) {
        if (Prefs.keys().KEY_GRID_COLOR.equals(key)) {
            return cropIwaView.configureOverlay().getGridColor();
        } else if (Prefs.keys().KEY_OVERLAY_COLOR.equals(key)) {
            return cropIwaView.configureOverlay().getOverlayColor();
        } else if (Prefs.keys().KEY_CORNER_COLOR.equals(key)) {
            return cropIwaView.configureOverlay().getCornerColor();
        } else if (Prefs.keys().KEY_BORDER_COLOR.equals(key)) {
            return cropIwaView.configureOverlay().getBorderColor();
        } else if (Prefs.keys().KEY_SCALE.equals(key)) {
            return (int) (cropIwaView.configureImage().getScale() * 100);
        } else if (Prefs.keys().KEY_IMAGE_QUALITY.equals(key)) {
            return saveConfig.build().getQuality();
        }
        return 0;
    }

    @Override
    public void onNewAspectRatioSelected(AspectRatio ratio) {
        cropIwaView.configureOverlay().setAspectRatio(ratio).apply();
    }

    public CropIwaSaveConfig getSelectedSaveConfig() {
        return saveConfig.build();
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defaultVal) {
        return null;
    }

    @Override
    public void saveStringSet(String key, Set<String> value) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedState) {

    }

    @Override
    public void onConfigChanged() {
        int scale = (int) Math.max(1, cropIwaView.configureImage().getScale() * 100);
        seekBarPreference.setValue(scale);
    }

    private Paint getGridPaint() {
        return cropIwaView.configureOverlay().getCropShape().getGridPaint();
    }

    private static Bitmap.CompressFormat stringToCompressFormat(String str) {
        return Bitmap.CompressFormat.valueOf(str.toUpperCase());
    }

    private static String compressFormatToString(Bitmap.CompressFormat format) {
        return format.name();
    }

    private CropIwaShape stringToCropShape(String str) {
        if ("rectangle".equals(str.toLowerCase())) {
            return new CropIwaRectShape(cropIwaView.configureOverlay());
        } else if ("oval".equals(str.toLowerCase())) {
            return new CropIwaOvalShape(cropIwaView.configureOverlay());
        }
        throw new IllegalArgumentException("Unknown shape");
    }

    private static String cropShapeToString(CropIwaShape shape) {
        if (shape instanceof CropIwaRectShape) {
            return "Rectangle";
        } else if (shape instanceof CropIwaOvalShape) {
            return "Oval";
        }
        throw new IllegalArgumentException("Instance of unknown class");
    }
}
