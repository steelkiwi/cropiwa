package com.steelkiwi.cropiwa;

import android.annotation.SuppressLint;
import android.content.Context;

import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;

/**
 * @author yarolegovich
 *  on 05.02.2017.
 */
@SuppressLint("ViewConstructor")
public class CropIwaFixedOverlayView  extends CropIwaOverlayView {


    public CropIwaFixedOverlayView(Context context, CropIwaOverlayConfig config) {
        super(context, config);
    }
}
