package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.Random;

public class TimelineProgressBar extends View {
    Context context;

    String id;
    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private int timelineHeight = Sizes.dpToPxExt(3, getContext());
    int timelineWidth = getWidth();
    int radius = timelineHeight / 2;

    private Paint getBackgroundPaint() {
        if (backgroundPaint == null) {
            backgroundPaint = new Paint();
            backgroundPaint.setColor(Color.parseColor("#8affffff"));
        }
        return backgroundPaint;
    }

    private Paint getForegroundPaint() {
        if (foregroundPaint == null) {
            foregroundPaint = new Paint();
            foregroundPaint.setColor(Color.parseColor("#ffffff"));
        }
        return foregroundPaint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRoundRect(
                new RectF(
                        0,
                        0,
                        getWidth(),
                        timelineHeight
                ),
                radius,
                radius,
                getBackgroundPaint()
        );
        if (progressForegroundVisibility) {
            canvas.drawRoundRect(
                    new RectF(
                            0,
                            0,
                            currentProgress * getWidth(),
                            timelineHeight
                    ),
                    radius,
                    radius,
                    getForegroundPaint()
            );
        }
        invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        timelineWidth = getWidth();
    }

    public TimelineProgressBar(@NonNull Context context) {
        super(context);
        this.timelineHeight = Sizes.dpToPxExt(3, context);
        this.radius = timelineHeight / 2;
        this.context = context;
        init();
    }

    boolean progressForegroundVisibility = false;

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    Long duration;


    float currentProgress = 0;

    public Long getDuration() {
        if (duration == null || duration == 0) return 1000L;
        return duration;
    }

    public void clear() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (progressForegroundVisibility) {
                    progressForegroundVisibility = false;
                    setCurrentProgress(0);
                }
            }
        });
    }

    public void clearInLooper() {
        if (progressForegroundVisibility) {
            progressForegroundVisibility = false;
            setCurrentProgress(0);
        }
    }

    public void setMin() {
        progressForegroundVisibility = true;
        setCurrentProgress(0);
    }


    public void setMax() {
        progressForegroundVisibility = true;
        setCurrentProgress(1f);
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
                clearAnimation();
                animate().cancel();
            }
        });
    }

    public void stopInLooper() {
        clearAnimation();
        animate().cancel();
    }

    private final int animationStep = 25;
    public void start() {
        if (!isActive) return;
        setPaused(false);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setCurrentProgress(0);
                progressForegroundVisibility = true;
                if (duration == null || duration == 0) return;
                final long startTime = System.currentTimeMillis();
                final Long totalDuration = getDuration();
                final Runnable loopedRunnable = new Runnable() {
                    @Override
                    public void run() {
                        long curTime = System.currentTimeMillis();
                        float val = (curTime - startTime) / (1f * totalDuration);
                        if (isActive) {
                            setCurrentProgress(Math.min(val, 1f));
                            if ((curTime - startTime <= totalDuration) && !isPaused()) {
                                handler.postDelayed(this, animationStep);
                            }
                        }
                    }
                };
                handler.post(loopedRunnable);
            }
        }, 100);
    }


    private void setCurrentProgress(float progress) {
        currentProgress = progress;
    }

    boolean paused = false;
    private Object pauseLock = new Object();

    public void pause() {
        synchronized (pauseLock) {
            setPaused(true);
        }
    }

    private void setPaused(boolean isPaused) {
        synchronized (pauseLock) {
            paused = isPaused;
        }
    }

    private boolean isPaused() {
        synchronized (pauseLock) {
            return paused;
        }
    }

    public void restart() {
        if (!isActive) return;
        setPaused(false);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setCurrentProgress(0);
                if (duration == null || duration == 0) return;
                final long startTime = System.currentTimeMillis();
                final Long totalDuration = getDuration();
                final Runnable loopedRunnable = new Runnable() {
                    @Override
                    public void run() {
                        long curTime = System.currentTimeMillis();
                        float val = (curTime - startTime) / (1f * totalDuration);
                        if (isActive) {
                            setCurrentProgress(Math.min(val, 1f));
                            if (curTime - startTime <= totalDuration && !isPaused()) {
                                handler.postDelayed(this, animationStep);
                            }
                        }
                    }
                };
                handler.post(loopedRunnable);
            }
        }, 100);
    }

    public void resume() {
        if (!isActive) return;
        setPaused(false);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (duration == null || duration == 0) return;
                final long dur = (long) (getDuration() * Math.max(0f, 1f - currentProgress));
                final long startTime = System.currentTimeMillis();
                final Long totalDuration = getDuration();
                final Runnable loopedRunnable = new Runnable() {
                    @Override
                    public void run() {
                        long curTime = System.currentTimeMillis();
                        float val = 1f - ((dur -
                                (curTime - startTime)) / (1f * totalDuration));
                        if (isActive) {
                            setCurrentProgress(Math.min(Math.max(0f, val), 1f));
                            if (curTime - startTime <= dur && !isPaused()) {
                                handler.postDelayed(this, animationStep);
                            }
                        }
                    }
                };
                handler.post(loopedRunnable);
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
                        progressForegroundVisibility = true;
                        setCurrentProgress(progress);
                    }
                });
            }
        }

    }

    private void init() {
        id = new Random().nextDouble() + "";
        getBackgroundPaint();
        getForegroundPaint();
    }

}
