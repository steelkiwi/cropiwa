package com.steelkiwi.cropiwa.sample.config;

import android.content.Context;
import android.content.res.Resources;

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
    public final String KEY_DASHED_GRID;
    public final String KEY_CROP_SHAPE;

    public final String KEY_ENABLE_SCALE;
    public final String KEY_ENABLE_TRANSLATE;
    public final String KEY_SCALE;

    public final String KEY_IMAGE_FORMAT;
    public final String KEY_IMAGE_QUALITY;

    private Prefs(Context c) {
        Resources r = c.getResources();

        KEY_CORNER_COLOR = r.getString(R.string.key_corner_color);
        KEY_BORDER_COLOR = r.getString(R.string.key_border_color);
        KEY_GRID_COLOR = r.getString(R.string.key_grid_color);
        KEY_OVERLAY_COLOR = r.getString(R.string.key_overlay_color);
        KEY_DYNAMIC_CROP = r.getString(R.string.key_dynamic_crop);
        KEY_DRAW_GRID = r.getString(R.string.key_draw_grid);
        KEY_DASHED_GRID = r.getString(R.string.key_dashed_grid);
        KEY_CROP_SHAPE = r.getString(R.string.key_crop_shape);

        KEY_ENABLE_SCALE = r.getString(R.string.key_enable_scale_gesture);
        KEY_ENABLE_TRANSLATE = r.getString(R.string.key_enable_translate_gesture);
        KEY_SCALE = r.getString(R.string.key_scale);

        KEY_IMAGE_FORMAT = r.getString(R.string.key_save_format);
        KEY_IMAGE_QUALITY = r.getString(R.string.key_quality);
    }

}
