package com.steelkiwi.cropiwa;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * @author Yaroslav Polyakov https://github.com/polyak01
 * 03.02.2017.
 */
public class CropIwaView extends FrameLayout {

    private CropIwaImageView imageView;
    private CropIwaOverlayView overlayView;

    public CropIwaView(Context context) {
        super(context);
    }

    public CropIwaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CropIwaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CropIwaView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        imageView = new CropIwaImageView(getContext());
        addView(imageView);
        overlayView = new CropIwaOverlayView(getContext());
        addView(overlayView);
    }
}
