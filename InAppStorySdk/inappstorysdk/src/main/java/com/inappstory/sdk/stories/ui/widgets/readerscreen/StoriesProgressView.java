package com.inappstory.sdk.stories.ui.widgets.readerscreen;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.inappstory.sdk.stories.utils.Sizes;

public class StoriesProgressView extends LinearLayout {

    private final LayoutParams PROGRESS_BAR_LAYOUT_PARAM = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
    private final LayoutParams SPACE_LAYOUT_PARAM = new LayoutParams(Sizes.dpToPxExt(8), LayoutParams.WRAP_CONTENT);

    private final List<PausableProgressBar> progressBars = new ArrayList<>();

    private int storiesCount = -1;
    /**
     * pointer of running animation
     */
    public int current = 0;


    public StoriesListener getStoriesListener() {
        return storiesListener;
    }

    private StoriesListener storiesListener;
    boolean isReverse;
    boolean same;
    boolean isComplete;

    public interface StoriesListener {

        boolean webViewLoaded(int index);
    }

    public StoriesProgressView(Context context) {
        this(context, null);
    }

    public StoriesProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StoriesProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StoriesProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        bindViews();
    }

    public void clearAnimation(int index) {

        progressBars.get(index).finishAnimationProgress();
    }

    public void setMax(int index) {
        progressBars.get(index).setMaxWithoutCallback();
    }

    public void clearAllAnimations() {
        for (PausableProgressBar progressBar : progressBars) {
            progressBar.finishAnimationProgress();
        }
    }

    public void setCurrentCounter(int counter) {
        setCurrentCounter(counter, false);
    }

    public void setCurrentCounter(int counter, boolean stopTimer) {
        if (storiesCount <= 0) {
            current = counter;
            return;
        }
        if (counter < progressBars.size() - 1) {
            isComplete = false;
        }
        for (int i = 0; i < storiesCount; i++) {
            if (i < counter) {
                progressBars.get(i).setMaxWithoutCallback();
            } else if (i > counter) {
                progressBars.get(i).clearProgress();
            }
        }
        current = counter;
        if (!stopTimer) {
            same();
        }
    }

    public void setCurrentCounterAndRestart(int counter) {
        if (storiesCount <= 0) {
            current = counter;
            return;
        }

        for (int i = 0; i < storiesCount; i++) {
            if (i < counter) {
                progressBars.get(i).setMaxWithoutCallback();
            }
            if (i > counter) {
                progressBars.get(i).clearProgress();
            }
        }
        progressBars.get(counter).setMinWithoutCallback();
        current = counter;
        if (counter < progressBars.size() - 1) {
            isComplete = false;
        }
        same();
    }

    private void bindViews() {
        progressBars.clear();
        removeAllViews();

        for (int i = 0; i < storiesCount; i++) {
            final PausableProgressBar p = createProgressBar();
            progressBars.add(p);
            addView(p);
            if ((i + 1) < storiesCount) {
                addView(createSpace());
            }
        }
    }

    private PausableProgressBar createProgressBar() {
        PausableProgressBar p = new PausableProgressBar(getContext());
        p.setLayoutParams(PROGRESS_BAR_LAYOUT_PARAM);
        return p;
    }

    private View createSpace() {
        View v = new View(getContext());
        v.setLayoutParams(SPACE_LAYOUT_PARAM);
        return v;
    }

    /**
     * Set story count and create views
     *
     * @param storiesCount story count
     */
    public void setStoriesCount(int storiesCount) {
        this.storiesCount = storiesCount;
        bindViews();
    }


    /**
     * Set storiesListener
     *
     * @param storiesListener StoriesListener
     */
    public void setStoriesListener(StoriesListener storiesListener) {
        this.storiesListener = storiesListener;
    }


    boolean active;

    public void setActive(boolean active) {
        this.active = active;
        for (PausableProgressBar p : progressBars) {
            p.setActive(active);
        }
    }


    public void forceStartProgress() {
        try {

            progressBars.get(current).startProgress();
        } catch (Exception e) {
        }
    }

    public void same() {
        Log.e("eventsLoaded", "same");
        if (isComplete) {
            return;
        }
        if (progressBars == null || current >= progressBars.size()) return;
        same = true;
        PausableProgressBar p = progressBars.get(current);
        p.setMin();

        progressBars.get(current).startProgress();
    }

    public boolean setMin() {
        PausableProgressBar p = progressBars.get(current);
        p.setMin();
        return true;
    }


    /**
     * Set a story's duration
     *
     * @param duration millisecond
     */
    public void setStoryDuration(long duration) {
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setDuration(duration);
            progressBars.get(i).setCallback(callback(i));
        }
    }

    public void setSlideDuration(int index, long duration) {
        progressBars.get(index).setDuration(duration);
    }

    public void setStoryDurations(List<Integer> durations) {
        if (durations == null) return;
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setDuration(durations.get(i));
            progressBars.get(i).setCallback(callback(i));
        }
        Log.e("eventsLoaded", "StoryCacheLoadedEvent set");
    }

    /**
     * Set stories count and each story duration
     *
     * @param durations milli
     */
    public void setStoriesCountWithDurations(@NonNull long[] durations) {
        storiesCount = durations.length;
        bindViews();
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setDuration(durations[i]);
            progressBars.get(i).setCallback(callback(i));
        }
    }

    private PausableProgressBar.Callback callback(final int index) {
        return new PausableProgressBar.Callback() {
            @Override
            public void onStartProgress() {
            }

            @Override
            public boolean onFinishProgress(boolean isDestroy) {

                return true;
            }

            @Override
            public boolean onFinishProgress() {
                return onFinishProgress(false);
            }
        };
    }

    public boolean started = false;

    public void startProgress(int ind) {
        setCurrentCounter(ind);
    }


    public void setMaxProgressTo(int ind) {
        setCurrentCounter(ind, true);
    }

    /**
     * Start progress animation
     */
    public void startStories() {
        Log.e("eventsLoaded", "startStories");
        try {
            if (progressBars.get(current).startProgress()) {
                started = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Need to call when Activity or Fragment destroy
     */
    public void destroy() {
        isComplete = false;
        started = false;
        current = 0;
        for (PausableProgressBar p : progressBars) {
            p.clear();
        }
    }

    /**
     * Pause story
     */
    public void pause(boolean withBackground) {
        if (progressBars != null && progressBars.size() > current && progressBars.get(current) != null) {
            progressBars.get(current).pauseProgress(withBackground);
        }
    }

    /**
     * Resume story
     */
    public void resume() {
        if (started) {
            //same();
            progressBars.get(current).resumeProgress(false);
        } else {
            startStories();
        }
    }

    public void resumeWithPause() {
        if (started) {
            //same();
            progressBars.get(current).resumeProgress(false);
        } else {
            startStories();
            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    if (progressBars == null || progressBars.size() <= current) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (progressBars == null || progressBars.size() <= current)
                                    return;
                                if (storiesListener != null && !storiesListener.webViewLoaded(current)) {
                                    progressBars.get(current).pauseProgress(false);
                                }
                            }
                        }, 50);
                        return;
                    }
                    if (storiesListener != null && !storiesListener.webViewLoaded(current)) {
                        progressBars.get(current).pauseProgress(false);
                    }
                }
            });

        }
    }

    public void resumeWithoutRestart(boolean withBackground) {
        progressBars.get(current).resumeProgress(withBackground);
    }
}
