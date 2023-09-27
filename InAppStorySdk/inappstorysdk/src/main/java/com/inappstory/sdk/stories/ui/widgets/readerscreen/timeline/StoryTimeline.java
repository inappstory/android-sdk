package com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StoryTimeline extends View {

    public StoryTimeline(Context context) {
        super(context);
        setAppearance(new StoryTimelineAppearance().convertDpToPx(context));
    }

    public StoryTimeline(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAppearance(new StoryTimelineAppearance().convertDpToPx(context));
    }

    public StoryTimeline(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAppearance(new StoryTimelineAppearance().convertDpToPx(context));
    }
    StoryTimelineManager timelineManager = new StoryTimelineManager();

    private Paint fillPaint = new Paint();
    private Paint backgroundPaint = new Paint();

    public void setAppearance(StoryTimelineAppearance appearance) {
        this.appearance = appearance;
        fillPaint.setColor(appearance.fillColor);
        backgroundPaint.setColor(appearance.backgroundColor);
    }

    StoryTimelineAppearance appearance = new StoryTimelineAppearance();



    public StoryTimelineManager getTimelineManager() {
        return timelineManager;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawTimeline(
                timelineManager.timelineState,
                getMeasuredWidth(),
                appearance.height,
                appearance.cornerRadius,
                appearance.gapWidth,
                canvas
        );
        invalidate();
    }

    private void drawTimeline(
            StoryTimelineState state,
            float width,
            float height,
            float cornerRadius,
            float marginWidth,
            Canvas canvas
    ) {
        for (int i = 0; i < state.getSlidesCount(); i++) {
            Pair<List<RectF>, List<Integer>> drawingComponents = drawSegment(
                    i,
                    state,
                    width,
                    height,
                    marginWidth
            );
            List<RectF> segments = drawingComponents.first;

            List<Integer> paintColors = drawingComponents.second;
            for (int j = 0; j < segments.size(); j++) {
                canvas.drawRoundRect(
                        segments.get(j),
                        cornerRadius,
                        cornerRadius,
                        paintColors.get(j) == 0 ? fillPaint : backgroundPaint
                );
            }
        }
    }

    private Pair<List<RectF>, List<Integer>> drawSegment(
            int index,
            StoryTimelineState state,
            float width,
            float height,
            float marginWidth
    ) {
        ArrayList<RectF> rectangles = new ArrayList<>();
        ArrayList<Integer> paints = new ArrayList<>();
        float segmentWidth =
                (width - (state.getSlidesCount() - 1) * marginWidth) / state.getSlidesCount();
        float startBound = index * segmentWidth + ((index) * marginWidth);
        float progressBound = startBound + segmentWidth * state.getCurrentSlideProgress();
        float endBound = startBound + segmentWidth;
        StoryTimelineSegmentState segmentState = StoryTimelineSegmentState.EMPTY;
        if (state.getCurrentSlideIndex() == index)
            segmentState = state.getCurrentSlideState();
        else if (state.getCurrentSlideIndex() > index)
            segmentState = StoryTimelineSegmentState.FILLED;
        int fillPaint = (segmentState == StoryTimelineSegmentState.FILLED) ?
                0 :
                1;
        rectangles.add(new RectF(startBound, 0f, endBound, height));
        paints.add(fillPaint);
        if (segmentState == StoryTimelineSegmentState.ANIMATED) {
            rectangles.add(new RectF(startBound, 0f, progressBound, height));
            paints.add(0);
        }
        return new Pair<List<RectF>, List<Integer>>(rectangles, paints);
    }
}
