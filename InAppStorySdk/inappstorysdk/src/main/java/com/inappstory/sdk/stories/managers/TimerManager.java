package com.inappstory.sdk.stories.managers;

import android.os.Handler;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.models.js.StoryIdSlideIndex;
import com.inappstory.sdk.core.repository.stories.IStoriesRepository;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.models.api.Story;
import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.core.repository.statistic.StatisticV2Manager;
import com.inappstory.sdk.stories.ui.IASUICore;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.stories.uidomain.reader.page.IStoriesReaderPageViewModel;

public class TimerManager {
    private Handler timerHandler = new Handler();
    private long timerStart;

    public void setTimerDuration(long timerDuration) {
        this.timerDuration = timerDuration;
    }

    private long timerDuration;
    private long totalTimerDuration;
    private long pauseShift;

    public TimerManager(IStoriesReaderPageViewModel viewModel) {
        this.viewModel = viewModel;
    }

    private final IStoriesReaderPageViewModel viewModel;

    Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            if (timerDuration > 0 && System.currentTimeMillis() - timerStart >= timerDuration) {
                timerHandler.removeCallbacks(timerTask);
                pauseShift = 0;
                if (viewModel != null)
                    viewModel.openNextSlideByTimer();
                return;
            }
            timerHandler.postDelayed(timerTask, 50);
        }
    };

    public void resumeLocalTimer() {
        startTimer(timerDuration - pauseShift, false);
    }

    public long startPauseTime;


    public long pauseTime = 0;

    public void resumeTimer() {
        StatisticV2Manager.getInstance().cleanFakeEvents();
        resumeLocalTimer();
        if (storyIdSlideIndex != null) {
            IASCore.getInstance().statisticV1Repository.addStatisticEvent(storyIdSlideIndex);
            storyIdSlideIndex = null;
        }
        pauseTime += System.currentTimeMillis() - startPauseTime;
        if (StatisticV2Manager.getInstance() != null && StatisticV2Manager.getInstance().currentState != null)
            StatisticV2Manager.getInstance().currentState.storyPause = pauseTime;
        startPauseTime = 0;
    }

    public void stopTimer() {
        try {
            timerHandler.removeCallbacks(timerTask);
        } catch (Exception e) {

        }
    }

    private StoryIdSlideIndex storyIdSlideIndex;


    public void startTimer(long timerDuration, boolean clearDuration) {
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
    }

    public void setCurrentDuration(Integer currentDuration) {
        if (currentDuration != null)
            this.currentDuration = currentDuration;
    }

    int currentDuration;

    public void startCurrentTimer() {
        if (currentDuration != 0)
            startTimer(currentDuration, false);
    }

    public void pauseLocalTimer() {
        try {
            timerHandler.removeCallbacks(timerTask);
        } catch (Exception e) {

        }
        pauseShift = (System.currentTimeMillis() - timerStart);
    }

    public void pauseTimer() {
        StoriesReaderLaunchData launchData = IASUICore.getInstance().getStoriesReaderVM().getState().launchData();
        Story.StoryType type = (launchData != null) ? launchData.getType() : Story.StoryType.COMMON;
        IStoriesRepository storiesRepository = IASCore.getInstance().getStoriesRepository(type);
        IPreviewStoryDTO story = storiesRepository.getCurrentStory();
        if (story != null) {
            int lastIndex = storiesRepository.getStoryLastIndex(story.getId());
            storyIdSlideIndex = new StoryIdSlideIndex(story.getId(), lastIndex);
            StatisticV2Manager.getInstance().addFakeEvents(story.getId(), lastIndex, story.getSlidesCount(),
                    launchData != null ? launchData.getFeed() : null);
        }

        pauseLocalTimer();
        startPauseTime = System.currentTimeMillis();
        IASCore.getInstance().statisticV1Repository.forceSend();
    }

}
