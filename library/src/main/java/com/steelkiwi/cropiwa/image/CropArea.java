package com.steelkiwi.cropiwa.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.IOException;

/**
 * @author yarolegovich
 * 25.02.2017.
 */
public class CropArea {

    public static CropArea create(RectF coordinateSystem, RectF imageRect, RectF cropRect) {
        return new CropArea(
                moveRectToCoordinateSystem(coordinateSystem, imageRect),
                moveRectToCoordinateSystem(coordinateSystem, cropRect));
    }

    private static Rect moveRectToCoordinateSystem(RectF system, RectF rect) {
        float originX = system.left, originY = system.top;
        return new Rect(
                Math.round(rect.left - originX), Math.round(rect.top - originY),
                Math.round(rect.right - originX), Math.round(rect.bottom - originY));
    }

    private final Rect imageRect;
    private final Rect cropRect;

    public CropArea(Rect imageRect, Rect cropRect) {
        this.imageRect = imageRect;
        this.cropRect = cropRect;
    }

    public Bitmap applyCropTo(final Context context, final Uri srcUri, final int width, final int height) throws IOException {
        CropIwaBitmapManager bitmapManager = CropIwaBitmapManager.get();
        Uri localSrcUri = bitmapManager.toLocalUri(context, srcUri);
        BitmapFactory.Options options = bitmapManager.getBitmapFactoryOptions(context, localSrcUri, width, height);

        return bitmapManager.loadToMemoryWithCrop(context, srcUri, options, getCropRect(options));
    }

    @NonNull
    private Rect getCropRect(final BitmapFactory.Options options) {
        int left = findRealCoordinate(options.outWidth, cropRect.left, imageRect.width());
        int top = findRealCoordinate(options.outHeight, cropRect.top, imageRect.height());

        int width = findRealCoordinate(options.outWidth, cropRect.width(), imageRect.width());
        int right = left + width;

        int height = findRealCoordinate(options.outHeight, cropRect.height(), imageRect.height());
        int bottom = top + height;

        return new Rect(left,top,right,bottom);
    }

    private int findRealCoordinate(int imageRealSize, int cropCoordinate, float cropImageSize) {
        return Math.round((imageRealSize * cropCoordinate) / cropImageSize);
    }

}
