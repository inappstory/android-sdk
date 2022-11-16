package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;

import java.util.ArrayList;
import java.util.List;

public class TimelineManager {
    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    List<Integer> durations;

    int activeInd = 0;

    public void setStoryDurations(List<Integer> durations, boolean createFirst) {
        if (durations == null) return;
        if (this.durations == null) this.durations = new ArrayList<>();
        this.durations.clear();
        this.durations.addAll(durations);
        timeline.setDurations(durations);
        if (createFirst)
            createFirstAnimation();
    }


    Timeline timeline;

    public void setSlidesCount(int slidesCount) {
        timeline.setSlidesCount(slidesCount);
    }

    public void start() {
        mAnimationRest = -1;
        getCurrentBar().start();
    }

    public void createFirstAnimation() {
        if (activeInd == 0) {
            // timeline.progressBars.get(0).setMin();
            // timeline.progressBars.get(0).createAnimation();
            timeline.setActiveProgressBar(0, true);
        }
    }

    public void setCurrentSlide(int ind) {
        if (ind < 0) return;
        if (ind > timeline.slidesCount) return;
        if (timeline.getProgressBars().isEmpty()) return;
        activeInd = ind;
        for (int i = 0; i < timeline.getProgressBars().size(); i++) {
            timeline.setActiveProgressBar(i, i == activeInd);
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < timeline.getProgressBars().size(); i++) {
                    timeline.getProgressBars().get(i).stopInLooper();
                }
                //createCurrentAnimation(ind);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (timeline.getProgressBars().size() < activeInd) return;
                        for (int i = 0; i < activeInd; i++) {
                            timeline.getProgressBars().get(i).setMax();
                        }
                        for (int i = activeInd + 1; i < timeline.getProgressBars().size(); i++) {
                            timeline.getProgressBars().get(i).clearInLooper();
                        }
                        timeline.getProgressBars().get(activeInd).setMin();
                        timeline.getProgressBars().get(activeInd).createAnimation();
                    }
                }, 100);

            }
        });

    }


    public void stop() {
        getCurrentBar().stop();
    }


    private long mAnimationRest;

    public ReaderPageManager pageManager;


    TimelineProgressBar getCurrentBar() {
        if (timeline.getProgressBars().size() <= pageManager.getSlideIndex())
            return new TimelineProgressBar(timeline.getContext());
        return timeline.getProgressBars().get(pageManager.getSlideIndex());
    }

    public void restart() {
        getCurrentBar().createAnimation();
        getCurrentBar().restart();
    }

    public void pause() {
        getCurrentBar().pause();
    }

    public void resume() {
        getCurrentBar().resume();
    }
}
