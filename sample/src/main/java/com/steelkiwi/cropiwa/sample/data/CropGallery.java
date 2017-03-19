package com.steelkiwi.cropiwa.sample.data;

import android.net.Uri;
import android.support.annotation.DrawableRes;

import com.steelkiwi.cropiwa.sample.App;
import com.steelkiwi.cropiwa.sample.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author yarolegovich
 * 10.02.2017.
 */

public class CropGallery {

    public List<Uri> getCroppedImageUris() {
        List<Uri> images = new ArrayList<>();
        File saved = App.getInstance().getFilesDir();
        File[] files = saved.listFiles();
        if (files != null) {
            for (File f : files) {
                images.add(Uri.fromFile(f));
            }
        }
        images.addAll(Arrays.asList(
                resToUri(R.drawable.crop1),
                resToUri(R.drawable.crop2),
                resToUri(R.drawable.crop3),
                resToUri(R.drawable.crop4)));
        return images;
    }

    private Uri resToUri(@DrawableRes int res) {
        return Uri.parse(String.format(
                Locale.US, "android.resource://%s/%d",
                App.getInstance().getPackageName(),
                res));
    }

    public static Uri createNewEmptyFile() {
        return Uri.fromFile(new File(
                App.getInstance().getFilesDir(),
                System.currentTimeMillis() + ".png"));
    }
}
