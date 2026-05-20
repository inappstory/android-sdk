package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.refactoring.core.utils.observers.Observer;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderPageTimelineState;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryTimelineParameters;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoryReaderPageTimeline extends View
        implements Observer<StoryReaderPageTimelineState> {

    private StoryTimelineParameters parameters = null;
    private StoryReaderPageTimelineState state = null;
    private final Paint fillPaint = new Paint();
    private final Paint backgroundPaint = new Paint();

    private final int defaultFillColor = ColorUtils.parseColorRGBA("#ffffffff");
    private final int defaultBackgroundColor = ColorUtils.parseColorRGBA("#ffffff8a");

    public StoryReaderPageTimeline(Context context) {
        super(context);
        init(context);
    }

    public StoryReaderPageTimeline(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StoryReaderPageTimeline(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setVisibility(INVISIBLE);
        float height = 3f;
        float gapWidth = 4f;
        float cornerRadius = 1.5f;
        this.parameters = new StoryTimelineParameters(
                Sizes.dpFloatToPxExt(gapWidth, context),
                Sizes.dpFloatToPxExt(height, context),
                Sizes.dpFloatToPxExt(cornerRadius, context)
        );
        fillPaint.setColor(defaultFillColor);
        backgroundPaint.setColor(defaultBackgroundColor);
    }

    @Override
    public void onUpdate(StoryReaderPageTimelineState newValue) {
        this.state = newValue;
        final int localVisibility = !(state.slidesCount() == 0 ||
                (state.slidesCount() == 1 && state.timerDuration() == 0) ||
                state.isHidden()
        ) ? VISIBLE : INVISIBLE;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            setVisibility(localVisibility);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    setVisibility(localVisibility);
                }
            });
        }
        int localFgColor = ColorUtils.parseColorRGBA(state.foregroundColor());
        int localBgColor = ColorUtils.parseColorRGBA(state.backgroundColor());
        backgroundPaint.setColor(localBgColor);
        fillPaint.setColor(localFgColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        StoryReaderPageTimelineState currentState = state;
        if (parameters == null || currentState == null || getWidth() == 0) {
            super.onDraw(canvas);
        } else {
            drawSegments(canvas, currentState);
        }
        invalidate();
    }

    private void drawSegments(Canvas canvas,
                              StoryReaderPageTimelineState state
    ) {
        float segmentWidth = (getWidth() - parameters.gapWidth() * (state.slidesCount() - 1)) / state.slidesCount();
        for (int i = 0; i < state.slidesCount(); i++) {
            drawSegment(canvas, i, segmentWidth, state);
        }
    }

    private void drawSegment(
            Canvas canvas,
            int index,
            float segmentWidth,
            StoryReaderPageTimelineState state
    ) {
        float offset = index * (parameters.gapWidth() + segmentWidth);
        if (state.currentIndex() > index) {
            canvas.drawRoundRect(
                    new RectF(
                            offset,
                            0,
                            offset + segmentWidth,
                            parameters.lineHeight()
                    ),
                    parameters.lineRadius(),
                    parameters.lineRadius(),
                    fillPaint
            );
        } else if (state.currentIndex() == index) {
            canvas.drawRoundRect(
                    new RectF(
                            offset,
                            0,
                            offset + segmentWidth,
                            parameters.lineHeight()
                    ),
                    parameters.lineRadius(),
                    parameters.lineRadius(),
                    backgroundPaint
            );
            canvas.drawRoundRect(
                    new RectF(
                            offset,
                            0,
                            offset + segmentWidth * state.currentProgress(),
                            parameters.lineHeight()
                    ),
                    parameters.lineRadius(),
                    parameters.lineRadius(),
                    fillPaint
            );
        } else {
            canvas.drawRoundRect(
                    new RectF(
                            offset,
                            0,
                            offset + segmentWidth,
                            parameters.lineHeight()
                    ),
                    parameters.lineRadius(),
                    parameters.lineRadius(),
                    backgroundPaint
            );
        }
    }

}
