package com.steelkiwi.cropiwa;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.steelkiwi.cropiwa.image.BitmapLoader;
import com.steelkiwi.cropiwa.image.LoadBitmapTask;
import com.steelkiwi.cropiwa.util.CropIwaLog;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by yarolegovich on 02.02.2017.
 */
public class CropIwaView extends FrameLayout {

    private static final int UNSPECIFIED = -1;

    /**
     * TODO:
     * 1. Downscale image, if it is larger than view
     * 2. Add ability to configure using xml
     * 3. Add API:
     * -Scale image and listen for scale change
     * -Rotate image
     * -Enable/disable gestures
     * 4. Clean everything, add important logs, double check
     * 5. Add ability to crop...
     * The last one is pretty important!
     */

    private CropIwaImageView imageView;
    private CropIwaOverlayView overlayView;

    private CropIwaOverlayConfig overlayConfig;

    private CropIwaImageView.GestureProcessor gestureDetector;

    private Uri imageUri;
    private LoadBitmapTask loadBitmapTask;

    public CropIwaView(Context context) {
        super(context);
        init(null);
    }

    public CropIwaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CropIwaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CropIwaView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        imageView = new CropIwaImageView(getContext());
        imageView.setBackgroundColor(Color.BLACK);
        gestureDetector = imageView.getImageTransformGestureDetector();
        addView(imageView);

        overlayConfig = CropIwaOverlayConfig.createDefault(getContext());
        overlayView = overlayConfig.isDynamicCrop() ?
                new CropIwaDynamicOverlayView(getContext(), overlayConfig) :
                new CropIwaOverlayView(getContext(), overlayConfig);
        overlayView.setNewBoundsListener(imageView);
        LayoutParams params = generateDefaultLayoutParams();
        params.gravity = Gravity.CENTER;
        overlayView.setLayoutParams(params);
        addView(overlayView);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (loadBitmapTask != null) {
            loadBitmapTask.setDimensions(w, h);
            loadBitmapTask.executeIfAllowed(getContext());
        }
    }

    @Override
    @SuppressWarnings("RedundantIfStatement")
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //I think this "redundant" if statements improve code readability
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            gestureDetector.onDown(ev);
            return false;
        }
        if (overlayView.isResizing() || overlayView.isDraggingCropArea()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        imageView.measure(widthMeasureSpec, heightMeasureSpec);
        overlayView.measure(
                imageView.getMeasuredWidthAndState(),
                imageView.getMeasuredHeightAndState());
        setMeasuredDimension(
                imageView.getMeasuredWidthAndState(),
                imageView.getMeasuredHeightAndState());
    }

    public CropIwaOverlayConfig configureOverlay() {
        return overlayConfig;
    }

    public void setImageUri(Uri uri) {
        setImageUri(uri, null);
    }

    public void setImageUri(Uri uri, BitmapLoadErrorListener listener) {
        imageUri = uri;
        loadBitmapTask = new LoadBitmapTask(
                uri, getWidth(), getHeight(),
                new BitmapLoadListener(listener));
        loadBitmapTask.executeIfAllowed(getContext());
    }

    public void setImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public void crop(CropIwaSaveConfig saveConfig) {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (imageUri != null) {
            BitmapLoader loader = BitmapLoader.get();
            loader.unregisterListenerFor(imageUri);
            loader.removeIfCached(imageUri);
        }
    }

    private class BitmapLoadListener implements BitmapLoader.BitmapLoadListener {

        private BitmapLoadErrorListener listener;

        private BitmapLoadListener(BitmapLoadErrorListener listener) {
            this.listener = listener;
        }

        @Override
        public void onBitmapLoaded(Uri imageUri, Bitmap bitmap) {
            setImage(bitmap);
        }

        @Override
        public void onLoadFailed(Throwable e) {
            CropIwaLog.e("CropIwa Image loading from " + imageUri + " failed", e);
            if (listener != null) {
                listener.onError(e);
            }
        }
    }

    public interface BitmapLoadErrorListener {
        void onError(Throwable e);
    }
}