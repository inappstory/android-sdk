package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.os.Handler;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.outerevents.ShowSlide;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanelManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.TimelineManager;

public class ReaderPageManager {
    TimelineManager timelineManager;
    ButtonsPanelManager buttonsPanelManager;
    StoriesViewManager webViewManager;

    void gameComplete(String data) {
        webViewManager.gameComplete(data);
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    void shareComplete(String id, boolean isSuccess) {
        webViewManager.shareComplete(id, isSuccess);
    }

    int storyId;

    void storyLoaded(int id, int index) {
        webViewManager.storyLoaded(id, index);
    }

    void syncTime(long timeLeft, long eventTimer) {
        timelineManager.syncTime(timeLeft, eventTimer);
    }

    void storyOpen(int storyId) {
        if (storyId != this.storyId) {
            webViewManager.stopVideo();
        } else {

            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
            if (story != null) {
                CsEventBus.getDefault().post(new ShowSlide(story.id, story.title,
                        story.tags, story.slidesCount, story.lastIndex));
            }
            webViewManager.playVideo();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    webViewManager.resumeVideo();
                }
            }, 200);
        }
    }

    void pauseSlide() {
        timelineManager.pause();
        webViewManager.pauseStory();
    }

    void resumeSlide() {
        timelineManager.resume();
        webViewManager.resumeStory();
    }

    void restartSlide() {
        webViewManager.restartVideo();
    }

    void setStoryInfo(Story story, boolean full) {
        //webViewManager.setIndex(story.lastIndex);
        timelineManager.setSlidesCount(story.slidesCount);
        if (full)
            timelineManager.setStoryDurations(story.durations);
        webViewManager.loadStory(story.id, story.lastIndex);

    }

    void loadStoryAndSlide(int storyId, int slideIndex) {
        webViewManager.loadStory(storyId, slideIndex);
    }

    void openSlideByIndex(int index) {

    }

    void nextSlide() {
        webViewManager.stopVideo();
    }

    void prevSlide() {
        webViewManager.stopVideo();
    }

    void closeReader() {

    }

    void changeSoundStatus() {

    }

    void slideLoaded() {

    }

    void storyInfoLoaded() {
        this.timelineManager.setSlidesCount(InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId).slidesCount);
        this.timelineManager.setStoryDurations(InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId).durations);
    }

    public void setTimelineManager(TimelineManager timelineManager, int storyId) {
        this.timelineManager = timelineManager;
    }

    public void setButtonsPanelManager(ButtonsPanelManager buttonsPanelManager, int storyId) {
        this.buttonsPanelManager = buttonsPanelManager;
        this.buttonsPanelManager.setStoryId(storyId);
    }

    public void setWebViewManager(StoriesViewManager webViewManager, int storyId) {
        this.webViewManager = webViewManager;
        this.webViewManager.setStoryId(storyId);
    }

}
