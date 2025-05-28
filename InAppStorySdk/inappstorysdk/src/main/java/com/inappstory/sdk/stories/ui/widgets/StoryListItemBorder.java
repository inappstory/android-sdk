package com.inappstory.sdk.stories.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.inappstory.sdk.stories.utils.Sizes;

public class StoryListItemBorder extends View {
    private Paint paint;
    private int radius = 0;
    float margin = 0f;

    public StoryListItemBorder(Context context) {
        super(context);
    }

    public StoryListItemBorder(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StoryListItemBorder(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void radius(int radius) {
        this.radius = radius;
    }

    public void color(int color) {
        margin = Sizes.dpToPxExt(1, getContext()) / 2f;
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(Sizes.dpToPxExt(1, getContext()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (paint == null) {
            super.onDraw(canvas);
            invalidate();
        } else {
            canvas.drawRoundRect(
                    margin,
                    margin,
                    getWidth() - 2 * margin,
                    getHeight() - 2 * margin,
                    radius,
                    radius,
                    paint
            );
            invalidate();
        }
    }
}
