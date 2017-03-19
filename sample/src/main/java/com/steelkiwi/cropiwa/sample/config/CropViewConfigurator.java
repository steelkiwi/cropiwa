package com.steelkiwi.cropiwa.sample.config;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.ConfigChangeListener;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.steelkiwi.cropiwa.sample.R;
import com.steelkiwi.cropiwa.sample.data.CropGallery;
import com.yarolegovich.mp.MaterialPreferenceScreen;
import com.yarolegovich.mp.MaterialSeekBarPreference;
import com.yarolegovich.mp.io.StorageModule;

import java.util.Set;

public class CropViewConfigurator implements StorageModule, ConfigChangeListener {

    private CropIwaView cropIwaView;
    private CropIwaSaveConfig.Builder saveConfig;

    private MaterialSeekBarPreference seekBarPreference;

    public CropViewConfigurator(CropIwaView cropIwaView, MaterialPreferenceScreen screen) {
        this.cropIwaView = cropIwaView;
        this.saveConfig = new CropIwaSaveConfig.Builder(CropGallery.createNewEmptyFile());
        this.seekBarPreference = (MaterialSeekBarPreference) screen.findViewById(R.id.scale_seek_bar);

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
        }
    }

    @Override
    public void saveString(String key, String value) {
        if (Prefs.keys().KEY_IMAGE_FORMAT.equals(key)) {
            saveConfig.setCompressFormat(stringToCompressFormat(value));
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
    public void saveStringSet(String key, Set<String> value) {

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
        }
        return false;
    }

    @Override
    public String getString(String key, String defaultVal) {
        if (Prefs.keys().KEY_IMAGE_FORMAT.equals(key)) {
            return compressFormatToString(saveConfig.build().getCompressFormat());
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
    public Set<String> getStringSet(String key, Set<String> defaultVal) {
        return null;
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

    private static Bitmap.CompressFormat stringToCompressFormat(String str) {
        if ("jpeg".equals(str.toLowerCase())) {
            return Bitmap.CompressFormat.JPEG;
        } else if ("png".equals(str.toLowerCase())) {
            return Bitmap.CompressFormat.PNG;
        } else if ("webp".equals(str.toLowerCase())) {
            return Bitmap.CompressFormat.WEBP;
        }
        throw new IllegalArgumentException("Unknown compress format");
    }

    private static String compressFormatToString(Bitmap.CompressFormat format) {
        return format.name();
    }
}
