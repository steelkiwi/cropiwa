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

import com.steelkiwi.cropiwa.config.ConfigChangeListener;
import com.steelkiwi.cropiwa.config.CropIwaImageViewConfig;
import com.steelkiwi.cropiwa.config.CropIwaOverlayConfig;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.steelkiwi.cropiwa.image.CropArea;
import com.steelkiwi.cropiwa.image.CropIwaBitmapManager;
import com.steelkiwi.cropiwa.image.CropIwaResultReceiver;
import com.steelkiwi.cropiwa.image.LoadBitmapCommand;
import com.steelkiwi.cropiwa.util.CropIwaLog;

/**
 * Created by yarolegovich on 02.02.2017.
 */
public class CropIwaView extends FrameLayout {

    private CropIwaImageView imageView;
    private CropIwaOverlayView overlayView;

    private CropIwaOverlayConfig overlayConfig;
    private CropIwaImageViewConfig imageConfig;

    private CropIwaImageView.GestureProcessor gestureDetector;

    private Uri imageUri;
    private LoadBitmapCommand loadBitmapCommand;

    private ErrorListener errorListener;
    private CropSaveCompleteListener cropSaveCompleteListener;

    private CropIwaResultReceiver cropIwaResultReceiver;

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
        imageConfig = CropIwaImageViewConfig.createFromAttributes(getContext(), attrs);
        imageView = new CropIwaImageView(getContext(), imageConfig);
        imageView.setBackgroundColor(Color.BLACK);
        gestureDetector = imageView.getImageTransformGestureDetector();
        addView(imageView);

        overlayConfig = CropIwaOverlayConfig.createFromAttributes(getContext(), attrs);
        overlayView = overlayConfig.isDynamicCrop() ?
                new CropIwaDynamicOverlayView(getContext(), overlayConfig) :
                new CropIwaOverlayView(getContext(), overlayConfig);
        overlayConfig.addConfigChangeListener(new ConfigChangeListener() {
            @Override
            public void onConfigChanged() {
                boolean cropModeChanged = overlayConfig.isDynamicCrop() !=
                        (overlayView instanceof CropIwaDynamicOverlayView);
                if (cropModeChanged) {
                    CropIwaLog.d("changing crop mode!");
                    overlayConfig.removeConfigChangeListener(overlayView);
                    removeView(overlayView);
                    overlayView = configureOverlay().isDynamicCrop() ?
                            new CropIwaDynamicOverlayView(getContext(), overlayConfig) :
                            new CropIwaOverlayView(getContext(), overlayConfig);
                    overlayView.setNewBoundsListener(imageView);
                    imageView.setImagePositionedListener(overlayView);
                    addView(overlayView);
                    invalidate();
                }
            }
        });
        overlayView.setNewBoundsListener(imageView);
        addView(overlayView);

        imageView.setImagePositionedListener(overlayView);

        cropIwaResultReceiver = new CropIwaResultReceiver();
        cropIwaResultReceiver.register(getContext());
        cropIwaResultReceiver.setListener(new CropResultRouter());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (loadBitmapCommand != null) {
            loadBitmapCommand.setDimensions(w, h);
            loadBitmapCommand.tryExecute(getContext());
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
        imageView.notifyImagePositioned();
        setMeasuredDimension(
                imageView.getMeasuredWidthAndState(),
                imageView.getMeasuredHeightAndState());
    }



    public CropIwaOverlayConfig configureOverlay() {
        return overlayConfig;
    }

    public CropIwaImageViewConfig configureImage() {
        return imageConfig;
    }

    public void setImageUri(Uri uri) {
        imageUri = uri;
        loadBitmapCommand = new LoadBitmapCommand(
                uri, getWidth(), getHeight(),
                new BitmapLoadListener());
        loadBitmapCommand.tryExecute(getContext());
    }

    public void setImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public void crop(CropIwaSaveConfig saveConfig) {
        CropArea cropArea = CropArea.create(
                imageView.getImageRect(),
                imageView.getImageRect(),
                overlayView.getCropRect());
        CropIwaBitmapManager.get().crop(
                getContext(), cropArea, imageUri,
                saveConfig);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (imageUri != null) {
            CropIwaBitmapManager loader = CropIwaBitmapManager.get();
            loader.unregisterLoadListenerFor(imageUri);
            loader.scheduleRemoveIfCached(imageUri);
        }
        if (cropIwaResultReceiver != null) {
            cropIwaResultReceiver.unregister(getContext());
        }
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setCropSaveCompleteListener(CropSaveCompleteListener cropSaveCompleteListener) {
        this.cropSaveCompleteListener = cropSaveCompleteListener;
    }

    private class BitmapLoadListener implements CropIwaBitmapManager.BitmapLoadListener {

        @Override
        public void onBitmapLoaded(Uri imageUri, Bitmap bitmap) {
            setImage(bitmap);
        }

        @Override
        public void onLoadFailed(Throwable e) {
            CropIwaLog.e("CropIwa Image loading from [" + imageUri + "] failed", e);
            if (errorListener != null) {
                errorListener.onError(e);
            }
        }
    }

    private class CropResultRouter implements CropIwaResultReceiver.Listener {

        @Override
        public void onCropSuccess(Uri croppedUri) {
            if (cropSaveCompleteListener != null) {
                cropSaveCompleteListener.onCroppedRegionSaved(croppedUri);
            }
        }

        @Override
        public void onCropFailed(Throwable e) {
            if (errorListener != null) {
                errorListener.onError(e);
            }
        }
    }

    public interface CropSaveCompleteListener {
        void onCroppedRegionSaved(Uri bitmapUri);
    }

    public interface ErrorListener {
        void onError(Throwable e);
    }
}
