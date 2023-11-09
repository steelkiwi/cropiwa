package com.steelkiwi.cropiwa.sample.data;

import android.net.Uri;

import androidx.annotation.DrawableRes;

import com.steelkiwi.cropiwa.sample.App;
import com.steelkiwi.cropiwa.sample.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * @author yarolegovich
 * 10.02.2017.
 */

public class CropGallery {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void removeFromGallery(Uri uri) {
        if (uri.getScheme().startsWith("file")) {
            File f = new File(uri.getPath());
            if (f.exists()) {
                f.delete();
            }
        }
    }

    public static List<Uri> getCroppedImageUris() {
        List<Uri> images = new ArrayList<>();
        File saved = App.getInstance().getFilesDir();
        File[] files = saved.listFiles();
        if (files != null) {
            Arrays.sort(files, new DateDescendingOrder());
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

    private static Uri resToUri(@DrawableRes int res) {
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

    private static class DateDescendingOrder implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            long timestamp1, timestamp2;
            try {
                timestamp1 = tryExtractTimeStamp(o1.getName());
            } catch (Exception e) {
                return -1;
            }
            try {
                timestamp2 = tryExtractTimeStamp(o2.getName());
            } catch (Exception e) {
                return 1;
            }
            return timestamp1 > timestamp2 ? -1 : timestamp1 == timestamp2 ? 0 : 1;
        }

        private long tryExtractTimeStamp(String name) {
            return Long.parseLong(name.substring(0, name.indexOf(".")));
        }
    }
}

