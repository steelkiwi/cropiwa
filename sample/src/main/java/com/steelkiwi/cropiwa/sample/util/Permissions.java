package com.steelkiwi.cropiwa.sample.util;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

/**
 * Created by yarolegovich https://github.com/yarolegovich
 * on 22.03.2017.
 */

public class Permissions {

    public static boolean isGranted(Activity activity, String permission) {
        return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

}
