package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryLinkObject;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.UgcStoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetOldStatisticManagerCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.reader.ReaderManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanelManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.StoryTimelineManager;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReaderPageManager {


    StoryTimelineManager timelineManager;
    ButtonsPanelManager buttonsPanelManager;
    StoriesViewManager webViewManager;
    TimerManager timerManager;
    ReaderPageFragment host;


    public void unlockShareButton() {
        buttonsPanelManager.unlockShareButton();
    }

    public void removeStoryFromFavorite() {
        if (checkIfManagersIsNull()) return;
        buttonsPanelManager.removeStoryFromFavorite();
    }

    public void showLoader(int index) {

        host.showLoaderContainer();
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

    public Story.StoryType getStoryType() {
        return parentManager != null ? parentManager.storyType : Story.StoryType.COMMON;
    }


    public StoryData getStoryData(Story story) {
        return StoryData.getStoryData(story, getFeedId(), getSourceType(), getStoryType());
    }

    public SlideData getSlideData(Story story) {
        return new SlideData(
                getStoryData(story),
                story.lastIndex,
                story.getSlideEventPayload(story.lastIndex)
        );
    }

    private int storyId;

    public int getSlideIndex() {
        return slideIndex;
    }

    public void showSingleStory(int storyId, int slideIndex) {
        parentManager.showSingleStory(storyId, slideIndex);
    }

    public void setSlideIndex(int slideIndex) {
        if (this.slideIndex == slideIndex) return;
        if (checkIfManagersIsNull()) return;
        this.slideIndex = slideIndex;
        Story story = InAppStoryService.getInstance().getDownloadManager()
                .getStoryById(storyId, getStoryType());
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
        if (checkIfManagersIsNull()) return;
        parentManager.storyClick();
        if (payload == null || payload.isEmpty()) {
            int sz = (!Sizes.isTablet() ? Sizes.getScreenSize().x : Sizes.dpToPxExt(400, host.getContext()));
            if (coordinate >= 0.3 * sz && !isForbidden) {
                nextSlide(ShowStory.ACTION_TAP);
            } else if (coordinate < 0.3 * sz) {
                prevSlide(ShowStory.ACTION_TAP);
            }
        } else {
            tapOnLink(payload);
        }
    }

    public void reloadStory() {
        InAppStoryService.getInstance().getDownloadManager().reloadStory(storyId, getStoryType());
    }

    public void widgetEvent(String widgetName, String widgetData) {
        Story story = InAppStoryService.getInstance()
                .getDownloadManager().getStoryById(storyId, getStoryType());
        if (story == null) return;
        Map<String, String> widgetEventMap = JsonParser.toMap(widgetData);
        if (widgetEventMap != null)
            widgetEventMap.put("feed_id", getFeedId());
        if (CallbackManager.getInstance().getStoryWidgetCallback() != null) {
            CallbackManager.getInstance().getStoryWidgetCallback().widgetEvent(
                    getSlideData(story),
                    StringsUtils.getNonNull(widgetName),
                    widgetEventMap
            );
        }
    }

    private void tapOnLink(String link) {
        StoryLinkObject object = JsonParser.fromJson(link, StoryLinkObject.class);
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (object != null) {

            ClickAction action = ClickAction.BUTTON;
            Story story = service.getDownloadManager().getStoryById(
                    storyId, getStoryType()
            );
            switch (object.getLink().getType()) {
                case "url":
                    if (object.getType() != null && !object.getType().isEmpty()) {
                        if ("swipeUpLink".equals(object.getType())) {
                            action = ClickAction.SWIPE;
                        }
                    }
                    if (getStoryType() == Story.StoryType.COMMON)
                        OldStatisticManager.useInstance(
                                parentManager.getSessionId(),
                                new GetOldStatisticManagerCallback() {
                                    @Override
                                    public void get(@NonNull OldStatisticManager manager) {
                                        manager.addLinkOpenStatistic();
                                    }
                                }
                        );
                    if (CallbackManager.getInstance().getCallToActionCallback() != null) {
                        if (story != null) {
                            CallbackManager.getInstance().getCallToActionCallback().callToAction(
                                    host != null ? host.getContext() : null,
                                    getSlideData(story),
                                    object.getLink().getTarget(),
                                    action
                            );
                        }
                    } else if (CallbackManager.getInstance().getUrlClickCallback() != null) {
                        CallbackManager.getInstance().getUrlClickCallback().onUrlClick(
                                object.getLink().getTarget()
                        );
                    } else {
                        parentManager.defaultTapOnLink(object.getLink().getTarget());
                    }
                    break;
                case "json":
                    if (object.getType() != null && !object.getType().isEmpty()) {
                        if ("swipeUpItems".equals(object.getType())) {
                            if (story != null)
                                showGoods(object.getLink().getTarget(), object.getElementId(),
                                        getSlideData(story)
                                );
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

    public void startStoryTimers() {
        if (checkIfManagersIsNull()) return;
        isPaused = false;
        timelineManager.startSegment(slideIndex);
        timelineManager.active(true);

        timerManager.setCurrentDuration(durations.get(slideIndex));
        timerManager.startCurrentTimer();
    }

    public void storyOpen(int storyId) {
        if (checkIfManagersIsNull()) return;
        isPaused = false;
        if (storyId != this.storyId) {
            webViewManager.stopStory();
            timerManager.stopTimer();
            timelineManager.active(false);
        } else {
            timelineManager.active(true);
            webViewManager.playStory();
            webViewManager.resumeStory();
        }
    }


    private boolean checkIfManagersIsNull() {
        return webViewManager == null || timerManager == null
                || timelineManager == null || buttonsPanelManager == null;
    }

    public void stopStory(int currentId) {
        if (currentId == storyId) return;
        if (checkIfManagersIsNull()) return;
        webViewManager.stopStory();
        timelineManager.active(false);
        timerManager.stopTimer();
        isPaused = false;
        //stop timers and timelines
    }

    public void pauseSlide(boolean withBackground) {
        if (checkIfManagersIsNull()) return;
        if (!withBackground && isPaused) return;
        isPaused = true;
        timelineManager.pause();
        timerManager.pauseLocalTimer();
        if (withBackground) {
            timerManager.pauseTimer();
        }
        webViewManager.pauseStory();
    }

    boolean isPaused;

    public void resumeSlide(boolean withBackground) {
        if (checkIfManagersIsNull()) return;
        if (!isPaused) return;
        isPaused = false;
        timelineManager.resume();
        timerManager.resumeLocalTimer();
        if (withBackground) {
            timerManager.resumeTimer();
        }
        webViewManager.resumeStory();
    }

    public void restartSlide() {
        if (checkIfManagersIsNull()) return;
        if (durations.size() <= slideIndex) return;
        timelineManager.setDurations(durations, false);
        timelineManager.startSegment(slideIndex);
        timerManager.restartTimer(durations.get(slideIndex));
    }

    public void moveTimerToPosition(double position) {
        timerManager.moveTimerToPosition(position);
        timelineManager.setCurrentPosition(position);
    }

    List<Integer> durations = new ArrayList<>();

    public void setStoryInfo(Story story) {
        if (checkIfManagersIsNull()) return;
        timelineManager.setSlidesCount(story.getSlidesCount());
        this.durations = new ArrayList<>();
        if (story.durations == null) {
            for (int i = 0; i < story.slidesCount; i++) {
                this.durations.add(0);
            }
        } else {
            this.durations.addAll(story.durations);
        }
        timelineManager.setDurations(this.durations, true);
        webViewManager.loadStory(story, story.lastIndex);

    }

    public void loadStoryAndSlide(Story story, int slideIndex) {
        if (checkIfManagersIsNull()) return;
        webViewManager.loadStory(story, slideIndex);
    }

    public void openSlideByIndex(int index) {
        Story story = InAppStoryService.getInstance().getDownloadManager()
                .getStoryById(storyId, getStoryType());
        if (index < 0) index = 0;
        if (story == null) return;
        if (story.getSlidesCount() <= index) index = 0;
        story.lastIndex = index;
        if (slideIndex != index) {
            slideIndex = index;
            changeCurrentSlide(index);
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
            timelineManager.setDurations(durations, false);
        }
    }

    public void restartCurrentWithoutDuration() {
        if (checkIfManagersIsNull()) return;
        if (parentManager != null && parentManager.getCurrentStoryId() == storyId) {
            restartSlide();
        }
    }


    public void resetCurrentDuration() {
        if (checkIfManagersIsNull()) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, getStoryType());

        if (story == null) return;
        this.durations.clear();
        this.durations.addAll(story.durations);
        //  this.durations.set(slideIndex, story.durations.get(slideIndex));
        timelineManager.setDurations(durations, false);
    }

    public void showGoods(final String skus, final String widgetId, final SlideData slideData) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                parentManager.showGoods(skus, widgetId, new ShowGoodsCallback() {
                    @Override
                    public void goodsIsOpened() {
                        if (checkIfManagersIsNull()) return;
                        parentManager.pauseCurrent(true);
                        parentManager.unsubscribeClicks();
                    }

                    @Override
                    public void goodsIsClosed(String widgetId) {
                        if (checkIfManagersIsNull()) return;
                        parentManager.resumeCurrent(true);
                        parentManager.subscribeClicks();
                        webViewManager.goodsWidgetComplete(widgetId);
                    }

                    @Override
                    public void goodsIsCanceled(String widgetId) {
                        if (checkIfManagersIsNull()) return;
                        webViewManager.goodsWidgetComplete(widgetId);
                    }
                }, slideData);
            }
        });
    }

    public void nextStory(int action) {
        if (checkIfManagersIsNull()) return;
        timerManager.setTimerDuration(0);
        parentManager.nextStory(action);
    }

    public void prevStory(int action) {
        if (checkIfManagersIsNull()) return;
        timerManager.setTimerDuration(0);
        parentManager.prevStory(action);
    }

    public void nextSlide(int action) {
        if (checkIfManagersIsNull()) return;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        Story story = service.getDownloadManager().getStoryById(storyId, getStoryType());
        if (story == null) return;
        timerManager.setTimerDuration(0);
        int lastIndex = slideIndex;
        if (lastIndex < story.getSlidesCount() - 1) {
            if (webViewManager == null) return;
            webViewManager.stopStory();
            lastIndex++;
            story.lastIndex = lastIndex;
            slideIndex = lastIndex;
            changeCurrentSlide(lastIndex);
        } else {
            parentManager.nextStory(action);
        }
    }

    public void changeCurrentSlide(int slideIndex) {
        if (checkIfManagersIsNull()) return;
        if (durations == null) return;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        List<Integer> localDurations = new ArrayList<>(durations);
        if (localDurations.size() <= slideIndex) return;
        host.showLoader();
        currentSlideIsLoaded = false;
        ProfilingManager.getInstance().addTask("slide_show",
                storyId + "_" + slideIndex);
        isPaused = false;
        timelineManager.setSegment(slideIndex);
        timerManager.stopTimer();
        timerManager.setCurrentDuration(localDurations.get(slideIndex));
        StatisticManager.getInstance().sendCurrentState();
        service.getDownloadManager().changePriorityForSingle(storyId,
                parentManager.storyType);
        if (getStoryType() == Story.StoryType.COMMON)
            service.sendPageOpenStatistic(storyId, slideIndex,
                    parentManager != null ? parentManager.getFeedId() : null);
        loadStoryAndSlide(host.story, slideIndex);
    }

    public void setParentManager(ReaderManager parentManager) {
        this.parentManager = parentManager;
    }

    public SourceType getSourceType() {
        if (parentManager != null)
            return parentManager.source;
        return SourceType.LIST;
    }

    public ReaderManager getParentManager() {
        return parentManager;
    }

    ReaderManager parentManager;

    public void showShareView(InnerShareData shareData) {
        if (parentManager != null) {

            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, getStoryType());
            if (story != null)
                parentManager.showShareView(shareData, storyId, slideIndex);
        }
    }

    public void sendShowStoryEvents(int storyId) {
        if (parentManager != null) {
            parentManager.sendShowStoryEvents(storyId);
        }
    }

    public void prevSlide(int action) {
        if (checkIfManagersIsNull()) return;

        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        Story story = service.getDownloadManager().getStoryById(storyId, getStoryType());

        if (story == null) return;
        timerManager.setTimerDuration(0);
        int lastIndex = slideIndex;
        if (lastIndex > 0) {
            if (webViewManager == null) return;
            webViewManager.stopStory();
            lastIndex--;
            story.lastIndex = lastIndex;
            slideIndex = lastIndex;
            changeCurrentSlide(lastIndex);
        } else {
            parentManager.prevStory(action);
        }
    }

    public void closeReader() {

    }

    public void changeSoundStatus() {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        service.changeSoundStatus();
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
            //host.storyLoadedSuccess();
        }
    }

    boolean currentSlideIsLoaded = false;


    void storyInfoLoaded() {
        this.timelineManager.setDurations(InAppStoryService.getInstance().getDownloadManager()
                .getStoryById(storyId, getStoryType()).durations, false);
    }

    public void setTimelineManager(StoryTimelineManager timelineManager, int storyId) {
        this.timelineManager = timelineManager;
    }

    public void setButtonsPanelManager(ButtonsPanelManager buttonsPanelManager, int storyId) {
        buttonsPanelManager.setPageManager(this);
        this.buttonsPanelManager = buttonsPanelManager;
        this.buttonsPanelManager.setStoryId(storyId);
    }

    public void setWebViewManager(StoriesViewManager webViewManager, int storyId) {
        webViewManager.setPageManager(this);
        webViewManager.source = parentManager.source;
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

    public void slideLoadError(int slideIndex) {
        if (this.slideIndex == slideIndex) {
            if (host != null)
                host.slideLoadError();
        }
    }

    public void storyLoadedInCache(Story story) {
        if (checkIfManagersIsNull()) return;
        host.story = story;
        if (story.durations != null && !story.durations.isEmpty()) {
            if (this.durations == null)
                this.durations = new ArrayList<>();
            this.durations.clear();
            this.durations.addAll(story.durations);
            story.setSlidesCount(story.durations.size());
            if (slideIndex < story.durations.size()) {
                timerManager.setCurrentDuration(story.durations.get(slideIndex));
            }
        }


        setStoryInfo(story);
    }

}
