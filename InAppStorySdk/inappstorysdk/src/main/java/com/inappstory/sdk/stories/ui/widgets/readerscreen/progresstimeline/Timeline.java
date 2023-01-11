package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.List;

public class Timeline extends LinearLayout {
    public Timeline(Context context) {
        super(context);
        init();
    }

    public TimelineManager getManager() {
        return timelineManager;
    }

    TimelineManager timelineManager;

    int slidesCount = -1;

    public Timeline(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Timeline(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private final LayoutParams PROGRESS_BAR_LAYOUT_PARAM = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
    private final LayoutParams SPACE_LAYOUT_PARAM = new LayoutParams(Sizes.dpToPxExt(8), LayoutParams.WRAP_CONTENT);

    private TimelineProgressBar createProgressBar() {
        TimelineProgressBar p = new TimelineProgressBar(getContext());
        p.setLayoutParams(PROGRESS_BAR_LAYOUT_PARAM);
        return p;
    }

    private View createSpace() {
        View v = new View(getContext());
        v.setLayoutParams(SPACE_LAYOUT_PARAM);
        return v;
    }

    private void init() {
        setVisibility(INVISIBLE);
        setOrientation(LinearLayout.HORIZONTAL);
        bindViews();
        timelineManager = new TimelineManager();
        timelineManager.setTimeline(this);
    }

    void setSlidesCount(int slidesCount) {
        if (this.slidesCount != slidesCount) {
            this.slidesCount = slidesCount;
            bindViews();
        }
    }

    public void setCurrentSlide(int ind) {

    }

    public void setDurations(List<Integer> durations) {
        if (durations == null) return;
        synchronized (lock) {
            this.durations = durations;
        }
        checkTimelineVisibility();
        if (progressBars != null && progressBars.size() == durations.size()) {
            for (int i = 0; i < progressBars.size(); i++) {
                setSlideDuration(i);
            }
        }
        Log.e("durations", durations.toString());
    }

    private void checkTimelineVisibility() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    if (durations.isEmpty()) {
                        setVisibility(INVISIBLE);
                    } else if (durations.size() == 1) {
                        if (durations.get(0) == 0)
                            setVisibility(INVISIBLE);
                        else {
                            setVisibility(VISIBLE);
                        }
                    } else {
                        setVisibility(VISIBLE);
                    }
                }
            }
        });
    }

    List<Integer> durations;

    private final Object lock = new Object();

    ValueAnimator curAnimation;

    private void setSlideDuration(int index) {
        synchronized (lock) {
            if (durations != null && progressBars.size() > index && durations.size() > index) {
                progressBars.get(index).setDuration(1L * durations.get(index));
            }
        }
    }

    private List<TimelineProgressBar> progressBars = new ArrayList<>();

    public List<TimelineProgressBar> getProgressBars() {
        if (progressBars == null) progressBars = new ArrayList<>();
        return progressBars;
    }

    public void setActiveProgressBar(int index, boolean active) {
        if (progressBars == null) progressBars = new ArrayList<>();
        if (progressBars.size() > index) {
            progressBars.get(index).isActive = active;
        }
    }

    private void bindViews() {
        progressBars.clear();
        removeAllViews();

        for (int i = 0; i < slidesCount; i++) {
            TimelineProgressBar p = createProgressBar();
            progressBars.add(p);
            setSlideDuration(i);
            addView(p);
            if ((i + 1) < slidesCount) {
                addView(createSpace());
            }
            if (i == 0) p.setMin();
        }
    }
}
