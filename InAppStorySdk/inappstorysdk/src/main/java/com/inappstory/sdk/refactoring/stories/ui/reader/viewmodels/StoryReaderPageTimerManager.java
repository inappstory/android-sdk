package com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.refactoring.stories.ui.reader.states.StoryReaderImmutableState;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.utils.ScheduledTPEManager;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StoryReaderPageTimerManager {
    public StoryReaderPageTimerManager(
            IASCore core,
            StoryReaderPageViewModel pageViewModel
    ) {
        this.core = core;
        this.pageViewModel = pageViewModel;
    }

    private final IASCore core;
    private final StoryReaderPageViewModel pageViewModel;
    private long timerStartTimestamp;

    public void storyDTO(StoryDTO storyDTO) {
        this.storyDTO = storyDTO;
    }

    public void readerState(StoryReaderImmutableState readerState) {
        this.readerState = readerState;
    }

    private StoryDTO storyDTO;
    private StoryReaderImmutableState readerState;

    private long timerDuration;

    ScheduledFuture scheduledFuture;

    public long startPauseTime;


    public long pauseTime = 0;

    private final ScheduledTPEManager executorService = new ScheduledTPEManager();

    public void setTimerDuration(long timerDuration) {
        this.timerDuration = timerDuration;
    }

    Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            if (timerDuration > 0 && System.currentTimeMillis() - timerStartTimestamp >= timerDuration) {
                pageViewModel.nextSlideAuto();
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
        core.statistic().storiesV1(
                readerState.sessionParameters().sessionId(),
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
        core.statistic().storiesV1(
                readerState.sessionParameters().sessionId(),
                new GetStatisticV1Callback() {
                    @Override
                    public void get(@NonNull IASStatisticStoriesV1 manager) {
                        manager.closeStatisticEvent(null, true);
                        manager.sendStatistic();
                        manager.increaseEventCount();
                    }
                }
        );
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                IReaderContent story = storyDTO;
                if (story != null) {
                    core.statistic().storiesV2().addFakeEvents(
                            story.id(),
                            pageViewModel.storyReaderPageState().slideIndex(),
                            story.slidesCount(),
                            readerState.feed()
                    );
                }
                startPauseTime = System.currentTimeMillis();
            }
        });
    }

}
