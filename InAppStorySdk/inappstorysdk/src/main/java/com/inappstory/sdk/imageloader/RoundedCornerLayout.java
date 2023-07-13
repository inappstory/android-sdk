package com.inappstory.sdk.imageloader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.inappstory.sdk.stories.utils.Sizes;

public class RoundedCornerLayout extends FrameLayout {
    private final static float CORNER_RADIUS = 40.0f;

    Path path;
    private Paint maskPaint;

    public RoundedCornerLayout(Context context) {
        super(context);
        radius = Sizes.dpToPxExt(16, context);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        radius = Sizes.dpToPxExt(16, context);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        radius = Sizes.dpToPxExt(16, context);
    }

    int radius;

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        float cornerRadius = radius;
        this.path = new Path();
        this.path.addRoundRect(new RectF(0, 0, width, height), cornerRadius, cornerRadius, Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (this.path != null) {
            canvas.clipPath(this.path);
        }
        super.dispatchDraw(canvas);
    }
}