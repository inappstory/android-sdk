package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.inappstory.sdk.stories.utils.Sizes;

public class StoryTimeline extends View {
    public StoryTimeline(Context context) {
        super(context);
        init(context);
    }

    public StoryTimeline(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StoryTimeline(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        float height = 3f;
        float gapWidth = 4f;
        float cornerRadius = 1.5f;
        setParameters(
                new StoryTimelineParameters(
                        Sizes.dpFloatToPxExt(gapWidth, context),
                        Sizes.dpFloatToPxExt(height, context),
                        Sizes.dpFloatToPxExt(cornerRadius, context)
                )
        );
    }

    private StoryTimelineParameters parameters = null;
    private StoryTimelineState state = null;
    private Paint fillPaint = null;
    private Paint backgroundPaint = null;
    private int timelineWidth = getWidth();


    public StoryTimelineManager getTimelineManager() {
        return timelineManager;
    }

    StoryTimelineManager timelineManager = new StoryTimelineManager();

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        timelineManager.setHost(this);
    }


    public void setParameters(StoryTimelineParameters parameters) {
        this.parameters = parameters;
        fillPaint = new Paint();
        fillPaint.setColor(parameters.fillColor);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(parameters.backgroundColor);
    }

    public void setState(StoryTimelineState state) {
        this.state = state;
        if (state.slidesCount == 1 && state.timerDuration == 0) setVisibility(INVISIBLE);
        else setVisibility(VISIBLE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (parameters == null || state == null || getWidth() == 0) {
            super.onDraw(canvas);
        } else {
            drawSegments(canvas);
        }
        invalidate();
    }

    private void drawSegments(Canvas canvas) {
        float segmentWidth = (getWidth() - parameters.gapWidth * (state.slidesCount - 1)) / state.slidesCount;
        for (int i = 0; i < state.slidesCount; i++) {
            drawSegment(canvas, i, segmentWidth);
        }

    }

    private void drawSegment(Canvas canvas, int index, float segmentWidth) {
        float offset = index * (parameters.gapWidth + segmentWidth);
        if (state.currentIndex > index) {
            canvas.drawRoundRect(
                    new RectF(
                            offset,
                            0,
                            offset + segmentWidth,
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
                            offset + segmentWidth,
                            parameters.lineHeight
                    ),
                    parameters.lineRadius,
                    parameters.lineRadius,
                    backgroundPaint
            );
            canvas.drawRoundRect(
                    new RectF(
                            offset,
                            0,
                            offset + segmentWidth * state.currentProgress,
                            parameters.lineHeight
                    ),
                    parameters.lineRadius,
                    parameters.lineRadius,
                    fillPaint
            );
        } else {
            canvas.drawRoundRect(
                    new RectF(
                            offset,
                            0,
                            offset + segmentWidth,
                            parameters.lineHeight
                    ),
                    parameters.lineRadius,
                    parameters.lineRadius,
                    backgroundPaint
            );
        }
    }


}
