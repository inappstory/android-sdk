package io.casestory.sdk.stories.ui.widgets.readerscreen;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
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

import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.stories.events.NextStoryReaderEvent;
import io.casestory.sdk.stories.events.PrevStoryReaderEvent;
import io.casestory.sdk.stories.utils.Sizes;

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
        void onNext();

        void onNextNarrative();

        void onPrev();

        void onPrevNarrative();

        void onComplete();

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

    public void setCurrentCounter(int counter) {
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

    /**
     * Skip current story
     */
    public boolean skip() {
        PausableProgressBar p = progressBars.get(current);
        p.setMax();
        if (isComplete) {
            EventBus.getDefault().post(new NextStoryReaderEvent());
            return false;
        }
        return true;
    }

    /**
     * Reverse current story
     */
    public boolean reverse() {
        // if (isComplete) return;
        if (current == 0) {
            EventBus.getDefault().post(new PrevStoryReaderEvent());
            return false;
        }
        isComplete = false;
        isReverse = true;
        PausableProgressBar p = progressBars.get(current);
        p.setMin();
        return true;
    }

    public boolean isActive() {
        return active;
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
        if (isComplete) {
            return;
        }
        same = true;
        PausableProgressBar p = progressBars.get(current);
        p.setMin();
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

    public void setSlideDuration(int index,long duration) {
        progressBars.get(index).setDuration(duration);
    }

    public void setStoryDurations(List<Integer> durations) {
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setDuration(durations.get(i));
            progressBars.get(i).setCallback(callback(i));
        }
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
                current = index;
            }

            @Override
            public boolean onFinishProgress(boolean isDestroy) {
                if (isDestroy) return false;
                if (same) {
                    same = false;
                    PausableProgressBar p = progressBars.get(current);
                    p.setMinWithoutCallback();
                    if (storiesListener.webViewLoaded(current))
                        progressBars.get(current).startProgress();
                    return false;
                }

                if (isReverse) {
                    isReverse = false;
                    if (storiesListener != null) storiesListener.onPrev();
                    if (0 <= (current - 1)) {
                        PausableProgressBar p = progressBars.get(current - 1);
                        p.setMinWithoutCallback();
                        if (storiesListener.webViewLoaded(current > 0 ? --current : current))
                            progressBars.get(current).startProgress();
                    } else {
                        if (storiesListener.webViewLoaded(current))
                            progressBars.get(current).startProgress();
                    }
                    Log.d("onFinishProgress", Integer.toString(current));
                    return false;
                }
                final int next = current + 1;
                if (next <= (progressBars.size() - 1)) {
                    if (storiesListener != null)
                        storiesListener.onNext();
                    if (progressBars.get(next).duration <= 1) {
                        current = next;
                    }
                    if (storiesListener != null)
                        progressBars.get(next).startProgress();
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (!storiesListener.webViewLoaded(next)) {
                                progressBars.get(next).pauseProgress(false);
                                Log.d("progressPaused", "paused");
                            }
                        }
                    });

                    Log.d("onFinishProgress", Integer.toString(next));
                    return false;
                } else {
                    isComplete = true;
                    if (storiesListener != null) storiesListener.onComplete();
                    return true;
                }
            }

            @Override
            public boolean onFinishProgress() {
                return onFinishProgress(false);
            }
        };
    }

    public boolean started = false;

    public void startProgress() {
        Log.d("Events", "startProgress");
        if (progressBars.get(current).animationIsEmpty() && storiesListener.webViewLoaded(current)) {
            Log.d("Events", "startProgress2");
            progressBars.get(current).startProgress();
        }
    }

    /**
     * Start progress animation
     */
    public void startStories() {
        Log.d("Events", "startStories");
        try {
            if (progressBars.get(current).startProgress()) {
                Log.d("Events", "startStories2");
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
        progressBars.get(current).pauseProgress(withBackground);
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
        if (started) {
            progressBars.get(current).resumeProgress(withBackground);
        } else {
            startStories();
        }
    }
}
