package com.steelkiwi.cropiwa.config;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.IntRange;

import java.io.File;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 25.02.2017.
 */

public class CropIwaSaveConfig {

    public static final int SIZE_UNSPECIFIED = -1;

    private Bitmap.Config bitmapConfig;
    private Bitmap.CompressFormat compressFormat;
    private int quality;
    private int width, height;

    private File dstPath;

    private OnCropErrorListener errorListener;
    private OnCropSaveCompleteListener cropSaveCompleteListener;
    private OnCropSaveToFileCompleteListener cropSaveToFileCompleteListener;

    public CropIwaSaveConfig() {
        this.bitmapConfig = Bitmap.Config.ARGB_8888;
        this.compressFormat = Bitmap.CompressFormat.PNG;
        this.width = SIZE_UNSPECIFIED;
        this.height = SIZE_UNSPECIFIED;
        this.quality = 90;
    }

    public Bitmap.Config getBitmapConfig() {
        return bitmapConfig;
    }

    public Bitmap.CompressFormat getCompressFormat() {
        return compressFormat;
    }

    public int getQuality() {
        return quality;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean shouldSaveToFile() {
        return dstPath != null;
    }

    public File getPath() {
        return dstPath;
    }

    public OnCropSaveToFileCompleteListener getSaveToFileCompleteListener() {
        return cropSaveToFileCompleteListener;
    }

    public OnCropSaveCompleteListener getSaveCompleteListener() {
        return cropSaveCompleteListener;
    }

    public OnCropErrorListener getErrorListener() {
        return errorListener;
    }

    public static class Builder {

        private CropIwaSaveConfig saveConfig;

        public Builder() {
            saveConfig = new CropIwaSaveConfig();
        }

        public Builder withBitmapConfig(Bitmap.Config bitmapConfig) {
            saveConfig.bitmapConfig = bitmapConfig;
            return this;
        }

        public Builder withSize(int width, int height) {
            saveConfig.width = width;
            saveConfig.height = height;
            return this;
        }

        public Builder withCompressFormat(Bitmap.CompressFormat compressFormat) {
            saveConfig.compressFormat = compressFormat;
            return this;
        }

        public Builder withQuality(@IntRange(from = 0, to = 100) int quality) {
            saveConfig.quality = quality;
            return this;
        }

        public Builder onError(OnCropErrorListener errorListener) {
            saveConfig.errorListener = errorListener;
            return this;
        }

        public Builder onCropComplete(OnCropSaveCompleteListener listener) {
            saveConfig.cropSaveCompleteListener = listener;
            return this;
        }

        public Builder onCropSaveComplete(OnCropSaveToFileCompleteListener listener) {
            saveConfig.cropSaveToFileCompleteListener = listener;
            return this;
        }

        public Builder saveToFile(File path) {
            saveConfig.dstPath = path;
            return this;
        }

        public CropIwaSaveConfig build() {
            return saveConfig;
        }
    }


    public interface OnCropErrorListener {
        void onError(Throwable e);
    }

    public interface OnCropSaveCompleteListener {
        void onCropComplete(Bitmap bitmap);
    }

    public interface OnCropSaveToFileCompleteListener {
        void onCropComplete(Uri uri);
    }

}
