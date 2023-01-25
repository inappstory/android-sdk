package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryLinkObject;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outerevents.CallToAction;
import com.inappstory.sdk.stories.outerevents.ClickOnButton;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.reader.ReaderManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanelManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.TimelineManager;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.List;

public class ReaderPageManager {
    TimelineManager timelineManager;
    ButtonsPanelManager buttonsPanelManager;
    StoriesViewManager webViewManager;
    TimerManager timerManager;
    ReaderPageFragment host;

    private boolean checkIfManagersIsNull() {
        return webViewManager == null || timerManager == null
                || timelineManager == null || buttonsPanelManager == null;
    }

    public void removeStoryFromFavorite() {
        if (checkIfManagersIsNull()) return;
        buttonsPanelManager.removeStoryFromFavorite();
    }

    public void screenshotShare() {
        if (checkIfManagersIsNull()) return;
        webViewManager.screenshotShare();
    }

    public void swipeUp() {
        if (checkIfManagersIsNull()) return;
        webViewManager.swipeUp();
    }

    public void gameComplete(String data) {
        if (checkIfManagersIsNull()) return;
        webViewManager.gameComplete(data);
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public int getStoryId() {
        return storyId;
    }

    private int storyId;

    public int getSlideIndex() {
        return slideIndex;
    }

    public void showSingleStory(int storyId, int slideIndex) {
        parentManager.showSingleStory(storyId, slideIndex);
    }

    public void setSlideIndex(int slideIndex) {

        if (checkIfManagersIsNull()) return;
        this.slideIndex = slideIndex;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        timelineManager.setCurrentSlide(slideIndex);
        timerManager.stopTimer();
        if (story != null) {
            if (story.durations == null || story.durations.size() <= slideIndex) return;
            timerManager.setCurrentDuration(story.durations.get(slideIndex));
        }
    }

    int slideIndex;

    public void shareComplete(String id, boolean isSuccess) {
        if (checkIfManagersIsNull()) return;
        webViewManager.shareComplete(id, isSuccess);
    }

    public void storyClick(String payload, int coordinate, boolean isForbidden) {
        parentManager.storyClick();
        if (payload == null || payload.isEmpty()) {
            int sz = (!Sizes.isTablet() ? Sizes.getScreenSize().x : Sizes.dpToPxExt(400));
            if (coordinate >= 0.3 * sz && !isForbidden) {
                nextSlide();
            } else if (coordinate < 0.3 * sz) {
                prevSlide();
            }
        } else {
            tapOnLink(payload);
        }
    }

    public void reloadStory() {
        InAppStoryService.getInstance().getDownloadManager().reloadStory(storyId);
    }

    public void widgetEvent(String widgetName, String widgetData) {
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(
                storyId
        );
        if (story == null) return;
        if (CallbackManager.getInstance().getStoryWidgetCallback() != null) {
            CallbackManager.getInstance().getStoryWidgetCallback().widgetEvent(
                    widgetName,
                    JsonParser.toMap(widgetData),
                    story.id,
                    story.title,
                    getFeedSlug(),
                    story.getSlidesCount(),
                    story.lastIndex,
                    story.tags);
        }
    }

    private void tapOnLink(String link) {
        StoryLinkObject object = JsonParser.fromJson(link, StoryLinkObject.class);
        if (object != null) {

            int cta = CallToAction.BUTTON;
            ClickAction action = ClickAction.BUTTON;
            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(
                    storyId
            );
            switch (object.getLink().getType()) {
                case "url":
                    if (story != null) {
                        CsEventBus.getDefault().post(new ClickOnButton(story.id, story.title,
                                story.tags, story.getSlidesCount(), story.lastIndex,
                                object.getLink().getTarget()));
                    }
                    if (object.getType() != null && !object.getType().isEmpty()) {
                        switch (object.getType()) {
                            case "swipeUpLink":
                                cta = CallToAction.SWIPE;
                                action = ClickAction.SWIPE;
                                break;
                            default:
                                break;
                        }
                    }
                    if (story != null) {
                        CsEventBus.getDefault().post(new CallToAction(story.id, story.title,
                                story.tags, story.getSlidesCount(), story.lastIndex,
                                object.getLink().getTarget(), cta));
                        if (CallbackManager.getInstance().getCallToActionCallback() != null) {
                            CallbackManager.getInstance().getCallToActionCallback().callToAction(
                                    story.id, story.title,
                                    story.tags, story.getSlidesCount(), story.lastIndex,
                                    object.getLink().getTarget(), action);
                        }
                    }
                    OldStatisticManager.getInstance().addLinkOpenStatistic();
                    if (CallbackManager.getInstance().getUrlClickCallback() != null) {
                        CallbackManager.getInstance().getUrlClickCallback().onUrlClick(
                                object.getLink().getTarget()
                        );
                    } else {
                        parentManager.defaultTapOnLink(object.getLink().getTarget());
                    }
                    break;
                case "json":
                    if (object.getType() != null && !object.getType().isEmpty()) {
                        switch (object.getType()) {
                            case "swipeUpItems":
                                if (story != null)
                                    showGoods(object.getLink().getTarget(), object.getElementId(), story.id, story.lastIndex);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    if (CallbackManager.getInstance().getAppClickCallback() != null) {
                        CallbackManager.getInstance().getAppClickCallback().onAppClick(
                                object.getLink().getType(),
                                object.getLink().getTarget()
                        );
                    }
                    break;
            }
        }
    }

    void storyLoaded(int id, int index) {
        webViewManager.storyLoaded(id, index);
    }

    public void startStoryTimers() {
        if (checkIfManagersIsNull()) return;
        isPaused = false;
        timelineManager.setCurrentSlide(slideIndex);
        timelineManager.start();

        timerManager.setCurrentDuration(durations.get(slideIndex));
        timerManager.startCurrentTimer();
    }

    public void storyOpen(int storyId) {
        if (checkIfManagersIsNull()) return;
        isPaused = false;
        if (storyId != this.storyId) {
            webViewManager.stopStory();
            timerManager.stopTimer();
            timelineManager.stop();
        } else {
            webViewManager.playStory();
            webViewManager.resumeStory();
        }
    }


    public void stopStory(int currentId) {
        if (checkIfManagersIsNull()) return;
        if (currentId == storyId) return;
        webViewManager.stopStory();
        timelineManager.stop();
        timerManager.stopTimer();
        isPaused = false;
        //stop timers and timelines
    }

    public void pauseSlide(boolean withBackground) {
        if (checkIfManagersIsNull()) return;
        if (!withBackground && isPaused) return;
        isPaused = true;
        timelineManager.pause();
        if (withBackground) {
            timerManager.pauseTimer();
        } else {
            timerManager.pauseLocalTimer();
        }
        webViewManager.pauseStory();
    }

    boolean isPaused;

    public void resumeSlide(boolean withBackground) {
        if (checkIfManagersIsNull()) return;
        if (!isPaused) return;
        isPaused = false;
        timelineManager.resume();
        if (withBackground) {
            timerManager.resumeTimer();
        } else {
            timerManager.resumeLocalTimer();
        }
        webViewManager.resumeStory();
    }

    public void restartSlide() {
        if (checkIfManagersIsNull()) return;
        //webViewManager.restartStory();
        if (durations.size() <= slideIndex) return;
        timelineManager.setStoryDurations(durations, false);
        timelineManager.restart();
        timerManager.restartTimer(durations.get(slideIndex));

    }

    List<Integer> durations = new ArrayList<>();

    public void setStoryInfo(Story story) {
        if (checkIfManagersIsNull()) return;
        //webViewManager.setIndex(story.lastIndex);
        timelineManager.setSlidesCount(story.getSlidesCount());
        this.durations = new ArrayList<>();
        if (story.durations != null)
            this.durations.addAll(story.durations);
        timelineManager.setStoryDurations(this.durations, true);

        webViewManager.loadStory(story.id, story.lastIndex);

    }

    public void loadStoryAndSlide(int storyId, int slideIndex) {
        if (checkIfManagersIsNull()) return;
        webViewManager.loadStory(storyId, slideIndex);
    }

    public void openSlideByIndex(int index) {
        Story story = InAppStoryService.getInstance().getDownloadManager()
                .getStoryById(storyId);
        if (index < 0) index = 0;
        if (story == null) return;
        if (story.getSlidesCount() <= index) index = 0;
        story.setLastIndex(index);
        if (slideIndex != index) {
            slideIndex = index;
            changeCurrentSlide();
        }
    }

    public String getFeedId() {
        if (parentManager != null) return parentManager.getFeedId();
        return null;
    }

    public String getFeedSlug() {
        if (parentManager != null) return parentManager.getFeedSlug();
        return null;
    }

    public void restartCurrentWithDuration(long duration) {
        if (checkIfManagersIsNull()) return;
        if (durations.size() <= slideIndex) return;
        durations.set(slideIndex, (int) duration);
        if (parentManager != null && parentManager.getCurrentStoryId() == storyId) {
            restartSlide();
        } else {
            timelineManager.setStoryDurations(durations, false);
        }
    }


    public void resetCurrentDuration() {
        if (checkIfManagersIsNull()) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);

        if (story == null) return;
        this.durations.clear();
        this.durations.addAll(story.durations);
        //  this.durations.set(slideIndex, story.durations.get(slideIndex));
        timelineManager.setStoryDurations(durations, false);
    }

    public void showGoods(final String skus, final String widgetId, final int storyId, final int slideIndex) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                parentManager.showGoods(skus, widgetId, new ShowGoodsCallback() {
                    @Override
                    public void onPause() {
                        if (checkIfManagersIsNull()) return;
                        parentManager.pause();
                    }

                    @Override
                    public void onResume(String widgetId) {
                        if (checkIfManagersIsNull()) return;
                        parentManager.resume();
                        webViewManager.goodsWidgetComplete(widgetId);
                    }

                    @Override
                    public void onEmptyResume(String widgetId) {
                        if (checkIfManagersIsNull()) return;
                        webViewManager.goodsWidgetComplete(widgetId);
                    }
                }, storyId, slideIndex);
            }
        });
    }

    public void nextStory() {
        if (checkIfManagersIsNull()) return;
        timerManager.setTimerDuration(0);
        parentManager.nextStory();
    }

    public void prevStory() {
        if (checkIfManagersIsNull()) return;
        timerManager.setTimerDuration(0);
        parentManager.prevStory();
    }

    public void nextSlide() {
        if (checkIfManagersIsNull()) return;
        if (InAppStoryService.isNull()) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);

        if (story == null) return;
        timerManager.setTimerDuration(0);

        if (slideIndex < story.getSlidesCount() - 1) {
            if (webViewManager == null) return;
            webViewManager.stopStory();
            slideIndex++;
            story.setLastIndex(slideIndex);
            changeCurrentSlide();
        } else {
            parentManager.nextStory();
        }
    }

    public void changeCurrentSlide() {
        if (checkIfManagersIsNull()) return;
        if (durations == null) return;
        List<Integer> localDurations = new ArrayList<>(durations);
        if (localDurations.size() < slideIndex) return;
        ProfilingManager.getInstance().addTask("slide_show",
                storyId + "_" + slideIndex);
        isPaused = false;
        timelineManager.setCurrentSlide(slideIndex);
        timerManager.stopTimer();
        timerManager.setCurrentDuration(localDurations.get(slideIndex));
        StatisticManager.getInstance().sendCurrentState();
        InAppStoryService.getInstance().getDownloadManager().changePriorityForSingle(storyId);
        InAppStoryService.getInstance().sendPageOpenStatistic(storyId, slideIndex,
                parentManager != null ? parentManager.getFeedId() : null);
        loadStoryAndSlide(storyId, slideIndex);
    }

    public void setParentManager(ReaderManager parentManager) {
        this.parentManager = parentManager;
    }

    ReaderManager parentManager;

    public void prevSlide() {
        if (checkIfManagersIsNull()) return;
        if (InAppStoryService.isNull()) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);

        if (story == null) return;
        timerManager.setTimerDuration(0);
        if (slideIndex > 0) {
            if (webViewManager == null) return;
            webViewManager.stopStory();
            slideIndex--;
            story.setLastIndex(slideIndex);
            changeCurrentSlide();
        } else {
            parentManager.prevStory();
        }
    }

    public void closeReader() {

    }

    public void changeSoundStatus() {
        if (InAppStoryService.isNull()) return;
        InAppStoryService.getInstance().changeSoundStatus();
        if (parentManager != null) {
            parentManager.updateSoundStatus();
        }
    }

    public void updateSoundStatus() {
        if (checkIfManagersIsNull()) return;
        buttonsPanelManager.refreshSoundStatus();
        webViewManager.changeSoundStatus();
    }

    public void slideLoadedInCache(int index) {
        slideLoadedInCache(index, false);
    }

    public void slideLoadedInCache(int index, boolean alreadyLoaded) {
        if (slideIndex == index) {
            if (checkIfManagersIsNull()) return;
            webViewManager.storyLoaded(storyId, index, alreadyLoaded);
            host.storyLoadedSuccess();
        }
    }


    void storyInfoLoaded() {
        this.timelineManager.setStoryDurations(InAppStoryService.getInstance().getDownloadManager()
                .getStoryById(storyId).durations, false);
    }

    public void setTimelineManager(TimelineManager timelineManager, int storyId) {
        timelineManager.pageManager = this;
        this.timelineManager = timelineManager;
    }

    public void setButtonsPanelManager(ButtonsPanelManager buttonsPanelManager, int storyId) {
        buttonsPanelManager.setParentManager(this);
        this.buttonsPanelManager = buttonsPanelManager;
        this.buttonsPanelManager.setStoryId(storyId);
    }

    public void setWebViewManager(StoriesViewManager webViewManager, int storyId) {
        webViewManager.setPageManager(this);
        this.webViewManager = webViewManager;
        this.webViewManager.setStoryId(storyId);
    }

    public void setTimerManager(TimerManager timerManager) {
        timerManager.setPageManager(this);
        this.timerManager = timerManager;
    }

    public void storyLoadStart() {
        if (host != null)
            host.storyLoadStart();
    }

    public void storyLoadError() {
        if (host != null)
            host.storyLoadError();
    }

    public void storyLoadedInCache() {
        if (InAppStoryService.isNull()) return;
        if (checkIfManagersIsNull()) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        if (story == null) return;
        if (story.durations != null && !story.durations.isEmpty()) {
            if (this.durations == null)
                this.durations = new ArrayList<>();
            this.durations.clear();
            this.durations.addAll(story.durations);
            story.setSlidesCount(story.durations.size());

            timerManager.setCurrentDuration(this.durations.get(slideIndex));
            //timelineManager.setStoryDurations(story.durations);
        }


        setStoryInfo(story);
        /*if (story.durations != null && !story.durations.isEmpty()) {
            timelineManager.createFirstAnimation();
        }*/
    }

}
