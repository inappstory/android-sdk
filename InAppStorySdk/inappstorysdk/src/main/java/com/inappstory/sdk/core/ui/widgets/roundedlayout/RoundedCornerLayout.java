package com.inappstory.sdk.core.ui.widgets.roundedlayout;

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
        setRadius(Sizes.dpToPxExt(16, context));
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRadius(Sizes.dpToPxExt(16, context));
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setRadius(Sizes.dpToPxExt(16, context));
    }

    int radiusTopLeft;
    int radiusBottomLeft;
    int radiusTopRight;
    int radiusBottomRight;

    public void setRadius(int radius) {
        this.radiusTopLeft = radius;
        this.radiusTopRight = radius;
        this.radiusBottomLeft = radius;
        this.radiusBottomRight = radius;
    }

    public void setRadius(
            int radiusTopLeft,
            int radiusTopRight,
            int radiusBottomLeft,
            int radiusBottomRight
    ) {
        this.radiusTopLeft = radiusTopLeft;
        this.radiusTopRight = radiusTopRight;
        this.radiusBottomLeft = radiusBottomLeft;
        this.radiusBottomRight = radiusBottomRight;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        this.path = new Path();
        float[] corners = new float[]{
                radiusTopLeft, radiusTopLeft,
                radiusTopRight, radiusTopRight,
                radiusBottomRight, radiusBottomRight,
                radiusBottomLeft, radiusBottomLeft
        };

        this.path.addRoundRect(
                new RectF(0, 0, width, height),
                corners,
                Path.Direction.CW
        );
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (this.path != null) {
            canvas.clipPath(this.path);
        }
        super.dispatchDraw(canvas);
    }
}