package com.inappstory.sdk.stories.managers;

import android.os.Handler;
import android.util.Log;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.StoryDownloader;
import com.inappstory.sdk.stories.events.NextStoryPageEvent;

public class TimerManager {
   /* public TimerManager() {
        CsEventBus.getDefault().register(this);
    }

    Handler timerHandler = new Handler();
    public long timerStart;
    public long timerDuration;
    public long totalTimerDuration;
    public long pauseShift;

    Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - timerStart >= timerDuration) {
                timerHandler.removeCallbacks(timerTask);
                pauseShift = 0;
                CsEventBus.getDefault().post(
                        new NextStoryPageEvent(InAppStoryService.getInstance().getCurrentId()));
                return;
                //if (currentIndex == )
            }
            timerHandler.postDelayed(timerTask, 50);
        }
    };

    public void resumeTimer() {
        Log.e("startTimer", "resumeTimer");
        StatisticManager.getInstance().cleanFakeEvents();
        resumeLocalTimer();
        if (currentEvent == null) return;
        pauseTime += System.currentTimeMillis() - startPauseTime;
        currentEvent.eventType = 1;
        currentEvent.timer = System.currentTimeMillis();
        currentState.storyPause = pauseTime;
        startPauseTime = 0;
    }

    public void pauseLocalTimer() {
        try {
            timerHandler.removeCallbacks(timerTask);
        } catch (Exception e) {

        }
        Log.e("dragDrop", System.currentTimeMillis() + " " + timerStart);
        pauseShift = (System.currentTimeMillis() - timerStart);
    }

    public void pauseTimer() {
        Story story = StoryDownloader.getInstance().getStoryById(InAppStoryService.getInstance().getCurrentId());

        StatisticManager.getInstance().addFakeEvents(story.id, story.lastIndex, story.slidesCount, System.currentTimeMillis() - currentState.startTime);
        pauseLocalTimer();
        startPauseTime = System.currentTimeMillis();
        closeStatisticEvent(null, true);
        sendStatistic();
        eventCount++;
    }


    public long startPauseTime;


    public long pauseTime = 0;

    public void resumeLocalTimer() {
        Log.e("dragDrop", System.currentTimeMillis() + " " + timerDuration + " " + pauseShift);
        startTimer(timerDuration - pauseShift, false);
    }

    public void startTimer(long timerDuration, boolean clearDuration) {
        Log.e("startTimer", timerDuration + "");
        if (timerDuration == 0) {
            try {
                timerHandler.removeCallbacks(timerTask);
                this.timerDuration = timerDuration;
                if (clearDuration)
                    this.totalTimerDuration = timerDuration;
            } catch (Exception e) {

            }
            return;
        }
        if (timerDuration < 0) {
            return;
        }
        pauseShift = 0;
        timerStart = System.currentTimeMillis();
        this.timerDuration = timerDuration;
        try {
            timerHandler.removeCallbacks(timerTask);
        } catch (Exception e) {

        }
        timerHandler.post(timerTask);
    }

    public void restartTimer(long duration) {
        startTimer(duration, true);
    }*/
}
