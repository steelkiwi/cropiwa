package com.steelkiwi.cropiwa.sample;

import android.net.Uri;
import android.support.annotation.DrawableRes;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author yarolegovich
 * 10.02.2017.
 */

public class CropGallery {

    public List<Uri> getCroppedImageUris() {
        return Arrays.asList(
                resToUri(R.drawable.crop1),
                resToUri(R.drawable.crop2),
                resToUri(R.drawable.crop3),
                resToUri(R.drawable.crop4));
    }

    private Uri resToUri(@DrawableRes int res) {
        return Uri.parse(String.format(
                Locale.US, "android.resource://%s/%d",
                App.getInstance().getPackageName(),
                res));
    }
}
