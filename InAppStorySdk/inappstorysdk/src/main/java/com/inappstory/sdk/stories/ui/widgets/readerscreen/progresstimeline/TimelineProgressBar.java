package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;

import java.util.Random;

public class TimelineProgressBar extends FrameLayout {
    Context context;

    String id;

    public TimelineProgressBar(@NonNull Context context) {
        super(context);
        this.context = context;
        init();
    }

    View progressBackground;
    View progressForeground;

    public void setDuration(Long duration) {
        this.duration = duration;

    }

    Long duration;


    public Long getDuration() {
        if (duration == null || duration == 0) return 1000L;
        return duration;
    }

    public void clear() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (progressForeground.getVisibility() == VISIBLE) {
                    progressForeground.setVisibility(INVISIBLE);
                    progressForeground.setScaleX(0.0001f);
                }
            }
        });
    }

    public void clearInLooper() {
        if (progressForeground.getVisibility() == VISIBLE) {
            progressForeground.setVisibility(INVISIBLE);
            progressForeground.setScaleX(0.0001f);
        }
    }

    public void setMin() {
        if (progressForeground.getVisibility() == INVISIBLE)
            progressForeground.setVisibility(VISIBLE);
        progressForeground.setScaleX(0.0001f);
        //progressForeground.setVisibility(INVISIBLE);
    }


    public void setMax() {
        if (progressForeground.getVisibility() == INVISIBLE)
            progressForeground.setVisibility(VISIBLE);
        progressForeground.setScaleX(1f);

        //progressForeground.setVisibility(INVISIBLE);
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

    //ValueAnimator currentAnimation;

    boolean isActive = false;

    public void stop() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressForeground.clearAnimation();
                progressForeground.animate().cancel();
            }
        });
    }

    public void stopInLooper() {
        progressForeground.clearAnimation();
        progressForeground.animate().cancel();
    }

    public void start() {
        if (!isActive) return;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                progressForeground.clearAnimation();
                progressForeground.animate().cancel();
                progressForeground.setScaleX(0.0001f);
                if (duration == null || duration == 0) return;
                progressForeground.animate().setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float val = (1f * animation.getCurrentPlayTime()) / animation.getDuration();
                        if (isActive)
                            progressForeground.setScaleX(val);
                    }
                }).setDuration(getDuration()).start();
            }
        }, 100);
    }


    public void pause() {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressForeground.clearAnimation();
                progressForeground.animate().cancel();
                // if (currentAnimation == null) return;
                // currentAnimation.pause();
            }
        });
    }

    public void restart() {
        if (!isActive) return;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                progressForeground.clearAnimation();
                progressForeground.animate().cancel();
                progressForeground.setScaleX(0.0001f);
                if (duration == null || duration == 0) return;
                progressForeground.animate().setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float val = (1f * animation.getCurrentPlayTime()) / animation.getDuration();
                        if (isActive)
                            progressForeground.setScaleX(Math.min(val, 1f));
                        //scaleX(1f).
                    }
                }).setDuration(getDuration()).start();
            }
        }, 100);
    }

    public void resume() {
        if (!isActive) return;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (duration == null || duration == 0) return;
                long dur = (long) (getDuration() * Math.max(0f, 1f - progressForeground.getScaleX()));
                InAppStoryManager.showDLog("jsDuration", getDuration() + " " + (1f - progressForeground.getScaleX()));
                progressForeground.animate().setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {

                        float val = 1f - ((1f * animation.getDuration() - animation.getCurrentPlayTime()) / (getDuration()));
                        if (isActive)
                            progressForeground.setScaleX(Math.min(Math.max(0f, val), 1f));
                        //scaleX(1f).
                    }
                }).setDuration(dur).start();

            }
        });
    }

    public void createAnimation() {

    }

    public void setProgress(final float progress) {
        if (duration == null || duration == 0) {
            clear();
        } else {
            if (progress <= 0) {
                clear();
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (progressForeground.getVisibility() == INVISIBLE) {
                            progressForeground.setVisibility(VISIBLE);
                        }
                        progressForeground.setScaleX(progress);
                    }
                });
            }
        }

    }

    private void init() {
        id = new Random().nextDouble() + "";
        LayoutInflater.from(context).inflate(R.layout.cs_progress_bar, this);
        progressForeground = findViewById(R.id.progress_foreground);
        progressBackground = findViewById(R.id.progress_background);
        progressForeground.setPivotX(-progressForeground.getWidth() / 2);
    }

}
