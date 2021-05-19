package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.os.Handler;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanelManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.TimelineManager;

public class ReaderPageManager {
    TimelineManager timelineManager;
    ButtonsPanelManager buttonsPanelManager;
    StoriesViewManager webViewManager;

    public void setStoryId(int storyId) {
        this.storyId = storyId;
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
