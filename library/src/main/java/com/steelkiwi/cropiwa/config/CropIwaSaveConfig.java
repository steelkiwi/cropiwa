package com.steelkiwi.cropiwa.config;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.IntRange;

import com.steelkiwi.cropiwa.image.CropIwaBitmapManager;
/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 *         25.02.2017.
 */
public class CropIwaSaveConfig {

    private Bitmap.CompressFormat compressFormat;
    private int quality;
    private int width, height;
    private int widthPx, heightPx;
    private Uri dstUri;

    public CropIwaSaveConfig(Uri dstPath) {
        this.dstUri = dstPath;
        this.compressFormat = Bitmap.CompressFormat.PNG;
        this.width = CropIwaBitmapManager.SIZE_UNSPECIFIED;
        this.height = CropIwaBitmapManager.SIZE_UNSPECIFIED;
        this.widthPx = CropIwaBitmapManager.SIZE_UNSPECIFIED;
        this.heightPx = CropIwaBitmapManager.SIZE_UNSPECIFIED;
        this.quality = 90;
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

    public Uri getDstUri() {
        return dstUri;
    }

    public int getWidthPx(){
        return widthPx;
    }

    public int getHeightPx(){
        return heightPx;
    }

    public static class Builder {

        private CropIwaSaveConfig saveConfig;

        public Builder(Uri dstPath) {
            saveConfig = new CropIwaSaveConfig(dstPath);
        }

        public Builder setSize(int width, int height) {
            saveConfig.width = width;
            saveConfig.height = height;
            return this;
        }

        public Builder setCompressFormat(Bitmap.CompressFormat compressFormat) {
            saveConfig.compressFormat = compressFormat;
            return this;
        }

        public Builder setQuality(@IntRange(from = 0, to = 100) int quality) {
            saveConfig.quality = quality;
            return this;
        }

        public Builder setSizeInPx(int width, int height){
            saveConfig.widthPx = width;
            saveConfig.heightPx = height;
            return this;
        }

        public Builder saveToFile(Uri uri) {
            saveConfig.dstUri = uri;
            return this;
        }

        public CropIwaSaveConfig build() {
            return saveConfig;
        }
    }


}
