package com.steelkiwi.cropiwa.util;

import android.content.Context;
import android.net.Uri;

import com.steelkiwi.cropiwa.image.CropIwaBitmapManager;

/**
 * @author Yaroslav Polyakov https://github.com/polak01
 * on 25.02.2017.
 */
public class LoadBitmapCommand {

    private Uri uri;
    private int width;
    private int height;
    private CropIwaBitmapManager.BitmapLoadListener loadListener;

    private boolean executed;

    public LoadBitmapCommand(Uri uri, int width, int height, CropIwaBitmapManager.BitmapLoadListener loadListener) {
        this.uri = uri;
        this.width = width;
        this.height = height;
        this.loadListener = loadListener;
        this.executed = false;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * If we call .setImageUri(Uri) on {@link com.steelkiwi.cropiwa.CropIwaView} from onCreate
     * view won't know its width and height, so we need to delay image loading until onSizeChanged.
     */
    public void tryExecute(Context context) {
        if (executed) {
            return;
        }
        if (width == 0 || height == 0) {
            CropIwaLog.d(
                    "LoadBitmapCommand for %s delayed, wrong dimensions {width=%d, height=%d}",
                    uri.toString(),
                    width, height);
            return;
        }
        executed = true;
        CropIwaBitmapManager.get().load(context, uri, width, height, loadListener);
    }
}
