package com.inappstory.sdk.stories.managers;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimerManager {
    private Handler timerHandler = new Handler();
    private long timerStart;

    public void setTimerDuration(long timerDuration) {
        this.timerDuration = timerDuration;
    }

    private long timerDuration;
    private long totalTimerDuration;
    private long pauseShift;

    public void setPageManager(ReaderPageManager pageManager) {
        this.pageManager = pageManager;
    }

    ReaderPageManager pageManager;

    Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            if (timerDuration > 0 && System.currentTimeMillis() - timerStart >= timerDuration) {
                pauseShift = 0;
                if (pageManager != null)
                    pageManager.nextSlide(ShowStory.ACTION_AUTO);
                cancelTask();
            }
        }
    };


    private void cancelTask() {
        if (scheduledFuture != null)
            scheduledFuture.cancel(false);
        scheduledFuture = null;
        executorService.shutdown();
    }

    ScheduledFuture scheduledFuture;



    public long startPauseTime;


    public long pauseTime = 0;


    public void stopTimer() {
        cancelTask();
    }


    public void resumeTimer(int timer) {
        StatisticManager.getInstance().cleanFakeEvents();
        startTimer(timer, false);
        if (OldStatisticManager.getInstance().currentEvent == null) return;
        OldStatisticManager.getInstance().currentEvent.eventType = 1;
        OldStatisticManager.getInstance().currentEvent.timer = System.currentTimeMillis();
        pauseTime += System.currentTimeMillis() - startPauseTime;
        StatisticManager.getInstance().currentState.storyPause = pauseTime;
        startPauseTime = 0;
    }


    public void startTimer(long timerDuration, boolean clearDuration) {
        if (timerDuration == 0) {
            try {
                cancelTask();
                this.timerDuration = timerDuration;
                if (clearDuration)
                    this.currentDuration = 0;
            } catch (Exception e) {

            }
            return;
        }
        if (timerDuration < 0) {
            return;
        }
        if (clearDuration)
            this.currentDuration = timerDuration;
        pauseShift = 0;
        timerStart = System.currentTimeMillis();
        this.timerDuration = timerDuration;
        if (executorService.isShutdown()) {
            executorService = new ScheduledThreadPoolExecutor(1);
        }
        scheduledFuture = executorService.scheduleAtFixedRate(
                timerTask,
                1L,
                50L,
                TimeUnit.MILLISECONDS
        );
    }

    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    public void restartTimer(long duration) {
        startTimer(duration, true);
    }

    public void setCurrentDuration(Integer currentDuration) {
        if (currentDuration != null)
            this.currentDuration = currentDuration;
    }

    long currentDuration;

    public void startCurrentTimer() {
        if (currentDuration != 0)
            startTimer(currentDuration, false);
    }

    public void resumeLocalTimer() {
        startTimer(timerDuration - pauseShift, false);
    }

    public void pauseLocalTimer() {
        cancelTask();
        pauseShift = (System.currentTimeMillis() - timerStart);
    }

    public void resumeTimer() {
        StatisticManager.getInstance().cleanFakeEvents();
        // resumeLocalTimer();
        if (OldStatisticManager.getInstance().currentEvent == null) return;
        OldStatisticManager.getInstance().currentEvent.eventType = 1;
        OldStatisticManager.getInstance().currentEvent.timer = System.currentTimeMillis();
        pauseTime += System.currentTimeMillis() - startPauseTime;
        if (StatisticManager.getInstance() != null && StatisticManager.getInstance().currentState != null)
            StatisticManager.getInstance().currentState.storyPause = pauseTime;
        startPauseTime = 0;
    }

    public void moveTimerToPosition(double position) {
        if (currentDuration >= 0 && currentDuration - position > 0 && position >= 0) {
            timerDuration = (long) (currentDuration - position);
            timerStart = System.currentTimeMillis();
        }
    }

    public void pauseTimer() {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) {
                Story.StoryType type = (pageManager != null) ? pageManager.getStoryType() : Story.StoryType.COMMON;
                Story story = service.getDownloadManager()
                        .getStoryById(service.getCurrentId(), type);
                if (story != null) {
                    StatisticManager.getInstance().addFakeEvents(story.id, story.lastIndex, story.getSlidesCount(),
                            pageManager != null ? pageManager.getFeedId() : null);
                }
                // pauseLocalTimer();
                startPauseTime = System.currentTimeMillis();
                OldStatisticManager.getInstance().closeStatisticEvent(null, true);
                OldStatisticManager.getInstance().sendStatistic();
                OldStatisticManager.getInstance().eventCount++;
            }
        });
    }

}
