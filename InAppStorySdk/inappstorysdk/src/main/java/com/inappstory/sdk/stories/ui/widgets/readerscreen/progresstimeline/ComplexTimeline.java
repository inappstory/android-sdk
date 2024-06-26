package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ComplexTimeline extends View {
    public ComplexTimeline(Context context) {
        super(context);
    }

    public ComplexTimeline(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ComplexTimeline(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ComplexTimelineParameters parameters = null;
    private ComplexTimelineState state = null;
    private Paint fillPaint = null;
    private Paint backgroundPaint = null;
    private int timelineWidth = getWidth();

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        timelineWidth = getWidth();
    }

    public void setParameters(ComplexTimelineParameters parameters) {
        this.parameters = parameters;
        fillPaint = new Paint();
        fillPaint.setColor(parameters.fillColor);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(parameters.backgroundColor);
    }

    public void setState(ComplexTimelineState state) {
        this.state = state;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (parameters == null || state == null) {
            super.onDraw(canvas);
        } else {
            drawSegments(canvas);
        }
        invalidate();
    }

    private void drawSegments(Canvas canvas) {
        float segmentWidth = (timelineWidth - parameters.gapWidth * (state.slidesCount - 1)) / state.slidesCount;
        for (int i = 0; i < state.slidesCount; i++) {
            drawSegment(canvas, i, segmentWidth);
        }

    }

    private void drawSegment(Canvas canvas, int index, float segmentWidth) {
        float offset = index * (parameters.gapWidth + segmentWidth);
        canvas.drawRoundRect(
                new RectF(
                        offset,
                        0,
                        segmentWidth,
                        parameters.lineHeight
                ),
                parameters.lineRadius,
                parameters.lineRadius,
                backgroundPaint
        );
        if (state.currentIndex > index) {
            canvas.drawRoundRect(
                    new RectF(
                            offset,
                            0,
                            segmentWidth,
                            parameters.lineHeight
                    ),
                    parameters.lineRadius,
                    parameters.lineRadius,
                    fillPaint
            );
        } else if (state.currentIndex == index) {
            canvas.drawRoundRect(
                    new RectF(
                            offset,
                            0,
                            segmentWidth * state.currentProgress,
                            parameters.lineHeight
                    ),
                    parameters.lineRadius,
                    parameters.lineRadius,
                    fillPaint
            );
        }
    }


}
