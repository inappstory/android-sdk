package com.inappstory.sdk.stories.managers;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetOldStatisticManagerCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TimerManager {
    private long timerStart;

    private long timerDuration;

    ScheduledFuture scheduledFuture;

    public long startPauseTime;


    public long pauseTime = 0;

    ReaderPageManager pageManager;

    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    public void setPageManager(ReaderPageManager pageManager) {
        this.pageManager = pageManager;
    }



    public void setTimerDuration(long timerDuration) {
        this.timerDuration = timerDuration;
    }

    Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            if (timerDuration > 0 && System.currentTimeMillis() - timerStart >= timerDuration) {
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



    public void stopTimer() {
        cancelTask();
    }

    public void startTimer(long timerDuration, long totalTimerDuration) {
        if (totalTimerDuration == 0) {
            try {
                cancelTask();
                this.timerDuration = totalTimerDuration;
            } catch (Exception e) {

            }
            return;
        }
        if (totalTimerDuration <= 0) {
            return;
        }
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


    long currentDuration;

    public void startSlideTimer(long newDuration, long currentTime) {
        startTimer(newDuration - currentTime, newDuration);
    }

    public void pauseSlideTimer() {
        cancelTask();
    }

    public void resumeTimerAndRefreshStat() {
        StatisticManager.getInstance().cleanFakeEvents();
        if (pageManager == null) return;
        OldStatisticManager.useInstance(
                pageManager.getParentManager().getSessionId(),
                new GetOldStatisticManagerCallback() {
                    @Override
                    public void get(@NonNull OldStatisticManager manager) {
                        if (manager.currentEvent == null) return;
                        manager.currentEvent.eventType = 1;
                        manager.currentEvent.timer = System.currentTimeMillis();
                    }
                }
        );
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

    public void pauseTimerAndRefreshStat() {
        if (pageManager == null) return;
        OldStatisticManager.useInstance(
                pageManager.getParentManager().getSessionId(),
                new GetOldStatisticManagerCallback() {
                    @Override
                    public void get(@NonNull OldStatisticManager manager) {
                        manager.closeStatisticEvent(null, true);
                        manager.sendStatistic();
                        manager.increaseEventCount();
                    }
                }
        );
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                Story.StoryType type = (pageManager != null) ? pageManager.getStoryType() : Story.StoryType.COMMON;
                Story story = service.getStoryDownloadManager()
                        .getStoryById(service.getCurrentId(), type);
                if (story != null) {
                    StatisticManager.getInstance().addFakeEvents(story.id, story.lastIndex, story.getSlidesCount(),
                            pageManager != null ? pageManager.getFeedId() : null);
                }
                startPauseTime = System.currentTimeMillis();
            }
        });
    }

}
