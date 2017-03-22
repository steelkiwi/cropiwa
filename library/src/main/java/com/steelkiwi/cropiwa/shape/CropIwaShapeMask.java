package com.steelkiwi.cropiwa.shape;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by yarolegovich on 22.03.2017
 * https://github.com/yarolegovich
 */
public interface CropIwaShapeMask extends Serializable {
    Bitmap applyMaskTo(Bitmap croppedRegion);
}
