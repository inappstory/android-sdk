package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;

import static com.inappstory.sdk.stories.api.models.StoryTimelineSettings.DEFAULT_BG_COLOR;
import static com.inappstory.sdk.stories.api.models.StoryTimelineSettings.DEFAULT_FG_COLOR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import com.inappstory.sdk.stories.ui.widgets.elasticview.ColorUtils;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
        setVisibility(INVISIBLE);
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        timelineManager.setHost(null);
    }

    public void setParameters(StoryTimelineParameters parameters) {
        this.parameters = parameters;
        fillPaint = new Paint();
        fillPaint.setColor(parameters.fillColor);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(parameters.backgroundColor);
    }

    @MainThread
    public void setState(StoryTimelineState state) {
        this.state = state;
        int localFgColor = ColorUtils.parseColorRGBA(state.getForegroundColor());
        int localBgColor = ColorUtils.parseColorRGBA(state.getBackgroundColor());
        final int localVisibility = !(
                (state.slidesCount == 1 && state.timerDuration == 0)
                        || state.isHidden
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

        if (fgColor.get() != localFgColor) {
            fgColor.set(localFgColor);
            fgColorChanged.set(true);
        }
        if (bgColor.get() != localBgColor) {
            bgColor.set(localBgColor);
            bgColorChanged.set(true);
        }
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

    private final AtomicInteger oldVisibility = new AtomicInteger(VISIBLE);
    private final AtomicInteger bgColor = new AtomicInteger(Color.parseColor(DEFAULT_BG_COLOR));
    private final AtomicInteger fgColor = new AtomicInteger(Color.parseColor(DEFAULT_FG_COLOR));
    private final AtomicBoolean visibilityChanged = new AtomicBoolean(false);
    private final AtomicBoolean bgColorChanged = new AtomicBoolean(false);
    private final AtomicBoolean fgColorChanged = new AtomicBoolean(false);

    private void drawSegments(Canvas canvas) {
        // setVisibility(oldVisibility.get());
        if (bgColorChanged.compareAndSet(true, false)) {
            backgroundPaint.setColor(bgColor.get());
        }
        if (fgColorChanged.compareAndSet(true, false)) {
            fillPaint.setColor(fgColor.get());
        }
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
