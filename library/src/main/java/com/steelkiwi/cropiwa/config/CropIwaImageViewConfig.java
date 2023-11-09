package com.steelkiwi.cropiwa.config;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.FloatRange;

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

    public static final int SCALE_UNSPECIFIED = -1;

    @SuppressWarnings("Range")
    public static CropIwaImageViewConfig createDefault() {
        return new CropIwaImageViewConfig()
                .setMaxScale(DEFAULT_MAX_SCALE)
                .setMinScale(DEFAULT_MIN_SCALE)
                .setImageTranslationEnabled(true)
                .setImageScaleEnabled(true)
                .setScale(SCALE_UNSPECIFIED);
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
            config.setImageInitialPosition(InitialPosition.values()[
                    ta.getInt(R.styleable.CropIwaView_ci_initial_position, 0)]);
        } finally {
            ta.recycle();
        }
        return config;
    }

    private float maxScale;
    private float minScale;
    private boolean isScaleEnabled;
    private boolean isTranslationEnabled;
    private float scale;

    private InitialPosition initialPosition;

    private List<ConfigChangeListener> configChangeListeners;

    public CropIwaImageViewConfig() {
        configChangeListeners = new ArrayList<>();
    }

    public float getMaxScale() {
        return maxScale;
    }

    public float getMinScale() {
        return minScale;
    }

    public boolean isImageScaleEnabled() {
        return isScaleEnabled;
    }

    public boolean isImageTranslationEnabled() {
        return isTranslationEnabled;
    }

    public InitialPosition getImageInitialPosition() {
        return initialPosition;
    }

    public float getScale() {
        return scale;
    }

    public CropIwaImageViewConfig setMinScale(@FloatRange(from = 0.001) float minScale) {
        this.minScale = minScale;
        return this;
    }

    public CropIwaImageViewConfig setMaxScale(@FloatRange(from = 0.001) float maxScale) {

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

    public CropIwaImageViewConfig setImageInitialPosition(InitialPosition initialPosition) {
        this.initialPosition = initialPosition;
        return this;
    }

    public CropIwaImageViewConfig setScale(@FloatRange(from = 0.01, to = 1f) float scale) {
        this.scale = scale;
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

}
