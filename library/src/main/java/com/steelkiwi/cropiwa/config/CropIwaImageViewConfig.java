package com.steelkiwi.cropiwa.config;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.steelkiwi.cropiwa.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yarolegovich https://github.com/yarolegovich
 * 04.02.2017.
 */
public class CropIwaImageViewConfig {

    private static final float DEFAULT_MIN_SCALE = 0.7f;
    private static final float DEFAULT_MAX_SCALE = 3f;

    public static CropIwaImageViewConfig createDefault() {
        return new CropIwaImageViewConfig()
                .setMaxScale(DEFAULT_MAX_SCALE)
                .setImageTranslationEnabled(true)
                .setImageScaleEnabled(true);
    }

    public static CropIwaImageViewConfig createFromAttributes(Context c, AttributeSet attrs) {
        CropIwaImageViewConfig config = createDefault();
        if (attrs == null) {
            return config;
        }
        TypedArray ta = c.obtainStyledAttributes(attrs, R.styleable.CropIwaView);
        try {
            config.setMaxScale(ta.getFloat(
                    R.styleable.CropIwaView_ci_max_scale,
                    config.getMaxScale()));
            config.setImageTranslationEnabled(ta.getBoolean(
                    R.styleable.CropIwaView_ci_translation_enabled,
                    config.isImageTranslationEnabled()));
            config.setImageScaleEnabled(ta.getBoolean(
                    R.styleable.CropIwaView_ci_scale_enabled,
                    config.isImageScaleEnabled()));
        } finally {
            ta.recycle();
        }
        return config;
    }

    private float maxScale;
    private boolean isScaleEnabled;
    private boolean isTranslationEnabled;
    private ScaleChangeListener scaleChangeListener;

    private List<ConfigChangeListener> configChangeListeners;

    public CropIwaImageViewConfig() {
        configChangeListeners = new ArrayList<>();
    }

    public float getMaxScale() {
        return maxScale;
    }

    public float getDefaultMinScale() {
        return DEFAULT_MIN_SCALE;
    }

    public boolean isImageScaleEnabled() {
        return isScaleEnabled;
    }

    public boolean isImageTranslationEnabled() {
        return isTranslationEnabled;
    }

    public ScaleChangeListener getScaleChangeListener() {
        return scaleChangeListener;
    }

    public CropIwaImageViewConfig setMaxScale(float maxScale) {
        this.maxScale = maxScale;
        return this;
    }

    public CropIwaImageViewConfig setImageScaleEnabled(boolean scaleEnabled) {
        this.isScaleEnabled = scaleEnabled;
        return this;
    }

    public CropIwaImageViewConfig setImageTranslationEnabled(boolean imageTranslationEnabled) {
        this.isTranslationEnabled = imageTranslationEnabled;
        return this;
    }

    public CropIwaImageViewConfig setScaleChangeListener(ScaleChangeListener scaleChangeListener) {
        this.scaleChangeListener = scaleChangeListener;
        return this;
    }

    public void addConfigChangeListener(ConfigChangeListener configChangeListener) {
        if (configChangeListener != null) {
            configChangeListeners.add(configChangeListener);
        }
    }

    public void removeConfigChangeListener(ConfigChangeListener configChangeListener) {
        configChangeListeners.remove(configChangeListener);
    }

    public void apply() {
        for (ConfigChangeListener listener : configChangeListeners) {
            listener.onConfigChanged();
        }
    }

    public interface ScaleChangeListener {
        void onScaleChanged(float scale);
    }

}
