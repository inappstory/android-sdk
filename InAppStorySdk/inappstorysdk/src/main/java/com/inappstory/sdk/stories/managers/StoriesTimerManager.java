package com.inappstory.sdk.stories.managers;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.utils.ScheduledTPEManager;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StoriesTimerManager {
    public StoriesTimerManager(IASCore core) {
        this.core = core;
    }

    private final IASCore core;
    private long timerStartTimestamp;

    private long timerDuration;


    public long startPauseTime;


    public long pauseTime = 0;

    ReaderPageManager pageManager;

    private ScheduledFuture scheduledFuture;
    private final ScheduledTPEManager executorService = new ScheduledTPEManager();

    public void setPageManager(ReaderPageManager pageManager) {
        this.pageManager = pageManager;
    }


    public void setTimerDuration(long timerDuration) {
        this.timerDuration = timerDuration;
    }

    Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            if (timerDuration > 0 && System.currentTimeMillis() - timerStartTimestamp >= timerDuration) {
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
        timerStartTimestamp = System.currentTimeMillis();
        this.timerDuration = timerDuration;
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

        core.statistic().storiesV2().cleanFakeEvents();
        if (pageManager == null) return;
        core.statistic().storiesV1(
                pageManager.getParentManager().getSessionId(),
                new GetStatisticV1Callback() {
                    @Override
                    public void get(@NonNull IASStatisticStoriesV1 manager) {
                        manager.refreshCurrentState();
                    }
                }
        );
        pauseTime += System.currentTimeMillis() - startPauseTime;

        core.statistic().storiesV2().cleanFakeEvents();
        core.statistic().storiesV2().changeV2StatePauseTime(pauseTime);
        startPauseTime = 0;
    }

    public void moveTimerToPosition(double position) {
        if (currentDuration >= 0 && currentDuration - position > 0 && position >= 0) {
            timerDuration = (long) (currentDuration - position);
            timerStartTimestamp = System.currentTimeMillis();
        }
    }

    public void pauseTimerAndRefreshStat() {
        if (pageManager == null) return;
        core.statistic().storiesV1(
                pageManager.getParentManager().getSessionId(),
                new GetStatisticV1Callback() {
                    @Override
                    public void get(@NonNull IASStatisticStoriesV1 manager) {
                        manager.closeStatisticEvent(null, true);
                        manager.sendStatistic();
                        manager.increaseEventCount();
                    }
                }
        );
        if (pageManager == null) return;
        ContentType type = pageManager.getViewContentType();
        int storyId = core
                .screensManager()
                .getStoryScreenHolder()
                .currentOpenedStoryId();
        IReaderContent story = core
                .contentHolder()
                .readerContent()
                .getByIdAndType(storyId, type);
        if (story != null) {
            core.statistic().storiesV2().addFakeEvents(
                    story.id(),
                    pageManager.getParentManager().getByIdAndIndex(story.id()).index(),
                    story.slidesCount(),
                    pageManager != null ? pageManager.getFeedId() : null
            );
        }
        startPauseTime = System.currentTimeMillis();
    }

}
