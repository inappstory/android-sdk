package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.R;

public class TimelineProgressBar extends FrameLayout {
    Context context;

    public TimelineProgressBar(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    View progressBackground;
    View progressForeground;

    public void setDuration(Long duration) {
        this.duration = duration;

        animation = ValueAnimator.ofFloat(1f / getDuration(), 1f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(getDuration());
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setProgress((float) animation.getAnimatedValue());
            }
        });
    }

    Long duration;

    public ValueAnimator getTimelineAnimation() {
        return animation;
    }

    ValueAnimator animation;

    public Long getDuration() {
        if (duration == null || duration == 0) return 1000L;
        return duration;
    }

    public void clear() {
        if (animation != null)

        if (progressForeground.getVisibility() == VISIBLE)
            progressForeground.setVisibility(INVISIBLE);
    }

    public void setMin() {
        if (progressForeground.getVisibility() == INVISIBLE)
            progressForeground.setVisibility(VISIBLE);
        progressForeground.setScaleX(1f / getDuration());
    }


    public void setMax() {
        if (progressForeground.getVisibility() == INVISIBLE)
            progressForeground.setVisibility(VISIBLE);
        progressForeground.setScaleX(1);
    }

    public TimelineProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }


    public TimelineProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public void setProgress(float progress) {
        if (duration == null || duration == 0) {
            clear();
        } else {
            if (progress <= 0) {
                clear();
            } else {
                progressForeground.setScaleX(progress);
            }
        }

    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.cs_progress_bar, this);
        progressForeground = findViewById(R.id.progress_foreground);
        progressBackground = findViewById(R.id.progress_background);
        progressForeground.setPivotX(-progressForeground.getWidth() / 2);
        animation = ValueAnimator.ofFloat(1f / getDuration(), 1f);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setProgress((float) animation.getAnimatedValue());
            }
        });
    }

}
