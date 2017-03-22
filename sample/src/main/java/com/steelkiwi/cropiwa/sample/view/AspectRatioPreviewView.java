package com.steelkiwi.cropiwa.sample.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.steelkiwi.cropiwa.AspectRatio;
import com.steelkiwi.cropiwa.sample.R;
import com.steelkiwi.cropiwa.util.ResUtil;
import com.yarolegovich.mp.util.Utils;

import java.util.Locale;

public class AspectRatioPreviewView extends View {

    private static final int SIZE_TEXT = 12;

    private final int colorRectNotSelected;
    private final int colorRectSelected;
    private final int colorText;

    private AspectRatio ratio;
    private boolean isSelected;

    private Paint rectPaint;
    private Paint textPaint;

    private float centerX;

    private RectF previewRect;
    private Rect textOutBounds;

    public AspectRatioPreviewView(Context context) {
        super(context);
    }

    public AspectRatioPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AspectRatioPreviewView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        textOutBounds = new Rect();

        ResUtil r = new ResUtil(getContext());
        colorRectSelected = r.color(R.color.colorAccent);
        colorRectNotSelected = r.color(R.color.aspectRatioNotSelected);
        colorText = r.color(R.color.aspectRatioText);

        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(colorRectNotSelected);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(Utils.dpToPixels(getContext(), 2));

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, SIZE_TEXT, dm));
        textPaint.setColor(colorText);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w * 0.5f;
        if (ratio != null) {
            configurePreviewRect();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (previewRect != null) {
            canvas.drawRect(previewRect, rectPaint);
            String text = getAspectRatioString();
            textPaint.getTextBounds(text, 0, text.length(), textOutBounds);
            canvas.drawText(
                    text, centerX - textOutBounds.width() * 0.5f,
                    getBottom() - textOutBounds.height() * 0.5f,
                    textPaint);
        }
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        rectPaint.setColor(selected ? colorRectSelected : colorRectNotSelected);
        invalidate();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setAspectRatio(AspectRatio ratio) {
        this.ratio = ratio;
        if (getWidth() != 0 && getHeight() != 0) {
            configurePreviewRect();
        }
        invalidate();
    }

    public AspectRatio getRatio() {
        return ratio;
    }

    private void configurePreviewRect() {
        String str = getAspectRatioString();
        textPaint.getTextBounds(str, 0, str.length(), textOutBounds);
        RectF freeSpace = new RectF(0, 0, getWidth(), getHeight() - textOutBounds.height() * 1.2f);

        boolean calculateFromWidth =
                ratio.getHeight() < ratio.getWidth()
                        || (ratio.isSquare() && freeSpace.width() < freeSpace.height());

        float halfWidth, halfHeight;
        if (calculateFromWidth) {
            halfWidth = freeSpace.width() * 0.8f * 0.5f;
            halfHeight = halfWidth / ratio.getRatio();
        } else {
            halfHeight = freeSpace.height() * 0.8f * 0.5f;
            halfWidth = halfHeight * ratio.getRatio();
        }

        previewRect = new RectF(
                freeSpace.centerX() - halfWidth, freeSpace.centerY() - halfHeight,
                freeSpace.centerX() + halfWidth, freeSpace.centerY() + halfHeight);
    }

    private String getAspectRatioString() {
        if (ratio == null) {
            return "";
        } else {
            return String.format(Locale.US, "%d:%d", ratio.getWidth(), ratio.getHeight());
        }
    }
}
