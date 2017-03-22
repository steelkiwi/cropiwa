package com.steelkiwi.cropiwa.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.steelkiwi.cropiwa.shape.CropIwaShapeMask;
import com.steelkiwi.cropiwa.util.CropIwaUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Yaroslav Polyakov on 22.03.2017.
 * https://github.com/polyak01
 */

class CropImageTask extends AsyncTask<Void, Void, Throwable> {

    private Context context;
    private CropArea cropArea;
    private CropIwaShapeMask mask;
    private Uri srcUri;
    private CropIwaSaveConfig saveConfig;

    public CropImageTask(
            Context context, CropArea cropArea, CropIwaShapeMask mask,
            Uri srcUri, CropIwaSaveConfig saveConfig) {
        this.context = context;
        this.cropArea = cropArea;
        this.mask = mask;
        this.srcUri = srcUri;
        this.saveConfig = saveConfig;
    }

    @Override
    protected Throwable doInBackground(Void... params) {
        try {
            Bitmap bitmap = CropIwaBitmapManager.get().loadToMemory(
                    context, srcUri, saveConfig.getWidth(),
                    saveConfig.getHeight());

            if (bitmap == null) {
                return new NullPointerException("Failed to load bitmap");
            }

            Bitmap cropped = cropArea.applyCropTo(bitmap);

            cropped = mask.applyMaskTo(cropped);

            Uri dst = saveConfig.getDstUri();
            OutputStream os = context.getContentResolver().openOutputStream(dst);
            cropped.compress(saveConfig.getCompressFormat(), saveConfig.getQuality(), os);
            CropIwaUtils.closeSilently(os);

            bitmap.recycle();
            cropped.recycle();
        } catch (IOException e) {
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Throwable throwable) {
        if (throwable == null) {
            CropIwaResultReceiver.onCropCompleted(context, saveConfig.getDstUri());
        } else {
            CropIwaResultReceiver.onCropFailed(context, throwable);
        }
    }
}