package com.steelkiwi.cropiwa.sample.config;

import android.content.Context;
import android.support.annotation.StringRes;

import com.steelkiwi.cropiwa.sample.App;
import com.steelkiwi.cropiwa.sample.R;

public class Prefs {

    private static final Prefs prefs = new Prefs(App.getInstance());

    public static Prefs keys() {
        return prefs;
    }

    public final String KEY_CORNER_COLOR;
    public final String KEY_BORDER_COLOR;
    public final String KEY_GRID_COLOR;
    public final String KEY_OVERLAY_COLOR;
    public final String KEY_DYNAMIC_CROP;
    public final String KEY_DRAW_GRID;

    public final String KEY_ENABLE_SCALE;
    public final String KEY_ENABLE_TRANSLATE;
    public final String KEY_SCALE;

    public final String KEY_IMAGE_FORMAT;
    public final String KEY_IMAGE_QUALITY;

    private Prefs(Context c) {
        KEY_CORNER_COLOR = str(c, R.string.key_corner_color);
        KEY_BORDER_COLOR = str(c, R.string.key_border_color);
        KEY_GRID_COLOR = str(c, R.string.key_grid_color);
        KEY_OVERLAY_COLOR = str(c, R.string.key_overlay_color);
        KEY_DYNAMIC_CROP = str(c, R.string.key_dynamic_crop);
        KEY_DRAW_GRID = str(c, R.string.key_draw_grid);

        KEY_ENABLE_SCALE = str(c, R.string.key_enable_scale_gesture);
        KEY_ENABLE_TRANSLATE = str(c, R.string.key_enable_translate_gesture);
        KEY_SCALE = str(c, R.string.key_scale);

        KEY_IMAGE_FORMAT = str(c, R.string.key_save_format);
        KEY_IMAGE_QUALITY = str(c, R.string.key_quality);
    }

    private String str(Context c, @StringRes int resId) {
        return c.getResources().getString(resId);
    }
}
