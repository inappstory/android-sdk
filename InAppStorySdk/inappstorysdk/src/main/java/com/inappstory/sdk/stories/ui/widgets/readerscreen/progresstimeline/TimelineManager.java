package com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline;

import android.os.Build;
import android.util.Log;

import java.util.List;

public class TimelineManager {
    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
    }

    public void setStoryDurations(List<Integer> durations) {
        if (durations == null) return;
        for (int i = 0; i < timeline.progressBars.size(); i++) {
            timeline.progressBars.get(i).setDuration(durations.get(i) * 1L);
        }
    }

    Timeline timeline;

    public void setSlidesCount(int slidesCount) {
        timeline.setSlidesCount(slidesCount);
    }

    public void syncTime(long timeLeft, long syncTime) {
        if (timeline.curAnimation != null) {
        }
    }

    public void start(int ind) {
        mAnimationRest = -1;
        timeline.setActive(ind);
        Log.d("cur_animation", timeline.curAnimation.toString() + " start");
        timeline.curAnimation.start();
    }

    public void setCurrentSlide(int ind) {
        if (ind < 0) return;
        if (ind > timeline.slidesCount) return;
        for (int i = 0; i < ind; i++) {
            timeline.progressBars.get(i).setMax();
        }
        for (int i = ind + 1; i < timeline.slidesCount; i++) {
            timeline.progressBars.get(i).clear();
        }
        timeline.progressBars.get(ind).setMin();
        //timeline.setActive(ind);
    }

    public void stop() {
        mAnimationRest = -1;
    }


    private long mAnimationRest;

    public void pause() {
        Log.d("cur_animation", timeline.curAnimation.toString() + " pause");
        timeline.curAnimation.pause();
        mAnimationRest = timeline.curAnimation.getDuration() - timeline.curAnimation.getCurrentPlayTime();
    }

    public void resume() {
        Log.d("cur_animation", timeline.curAnimation.toString() + " resume");
        timeline.curAnimation.resume();
    }

    public void next() {
        start(timeline.activeInd + 1);
    }

    public void prev() {
        start(timeline.activeInd - 1);
    }
}
