package com.inappstory.sdk.inappmessage.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BottomSheetLine extends View {
    public void setColor(int color) {
        initPaint(color);
    }

    private Paint paint;

    private void initPaint(int color) {
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    public BottomSheetLine(Context context) {
        super(context);
        initPaint(Color.BLACK);
    }

    public BottomSheetLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint(Color.BLACK);
    }

    public BottomSheetLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(Color.BLACK);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF = new RectF(0, 0, w, h);
        radius = h / 2f;
        paint.setStrokeWidth(h);
    }

    private RectF rectF = new RectF(0, 0, 0, 0);
    private float radius = 0f;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(
                rectF,
                radius,
                radius,
                paint
        );
        invalidate();
    }
}
