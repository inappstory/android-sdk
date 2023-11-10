package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.stories.IStoriesRepository;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.core.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Story.StoryType;
import com.inappstory.sdk.stories.api.models.StoryLinkObject;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.reader.ReaderManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanelManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.StoryTimelineManager;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.usecase.callbacks.IUseCaseCallbackWithContext;
import com.inappstory.sdk.usecase.callbacks.UseCaseCallbackCallToAction;
import com.inappstory.sdk.utils.ArrayUtil;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.List;

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
        previewStory = repository.getStoryPreviewById(storyId);
        story = repository.getStoryById(storyId);
    }

    public int getStoryId() {
        return storyId;
    }

    public StoryType getStoryType() {
        return parentManager != null ? parentManager.storyType : StoryType.COMMON;
    }

    private int storyId;

    public int getSlideIndex() {
        return slideIndex;
    }

    public void showSingleStory(int storyId, int slideIndex) {
        parentManager.showSingleStory(storyId, slideIndex);
    }

    private IStoryDTO story = null;
    private IPreviewStoryDTO previewStory = null;

    public void setStory(IStoryDTO story) {
        this.story = story;
    }

    public void setSlideIndex(int slideIndex) {

        if (checkIfManagersIsNull()) return;
        this.slideIndex = slideIndex;
        timerManager.stopTimer();
        if (story != null) {
            if (story.getDurations() == null || story.getDurations().length <= slideIndex) return;
            timerManager.setCurrentDuration(story.getDurations()[slideIndex]);
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
            int sz = (
                    !Sizes.isTablet(host.getContext()) ?
                            Sizes.getScreenSize(host.getContext()).x :
                            Sizes.dpToPxExt(400, host.getContext())
            );
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
        IASCore.getInstance().downloadManager.reloadStory(storyId, getStoryType());
    }

    public void widgetEvent(String widgetName, String widgetData) {
        int lastIndex = repository.getStoryLastIndex(storyId);
        if (CallbackManager.getInstance().getStoryWidgetCallback() != null) {
            CallbackManager.getInstance().getStoryWidgetCallback().widgetEvent(
                    new SlideData(
                            new StoryData(
                                    previewStory,
                                    getFeedId(),
                                    getSourceType()
                            ),
                            lastIndex
                    ),
                    StringsUtils.getNonNull(widgetName),
                    JsonParser.toMap(widgetData)
            );
        }
    }

    private void tapOnLink(String link) {
        StoryLinkObject object = JsonParser.fromJson(link, StoryLinkObject.class);
        int lastIndex = repository.getStoryLastIndex(storyId);
        if (object != null) {

            ClickAction action = ClickAction.BUTTON;
            switch (object.getLink().getType()) {
                case "url":
                    if (object.getType() != null && !object.getType().isEmpty()) {
                        if ("swipeUpLink".equals(object.getType())) {
                            action = ClickAction.SWIPE;
                        }
                    }
                    if (getStoryType() == StoryType.COMMON)
                        OldStatisticManager.getInstance().addLinkOpenStatistic();
                    IUseCaseCallbackWithContext callbackWithContext = new UseCaseCallbackCallToAction(
                            object.getLink().getTarget(),
                            new SlideData(
                                    new StoryData(
                                            previewStory,
                                            getFeedId(),
                                            getSourceType()
                                    ),
                                    lastIndex
                            ),
                            action
                    );
                    callbackWithContext.invoke(host != null ? host.getContext() : null);
                    break;
                case "json":
                    if (object.getType() != null && !object.getType().isEmpty()) {
                        if ("swipeUpItems".equals(object.getType())) {
                            showGoods(
                                    object.getLink().getTarget(),
                                    object.getElementId(),
                                    previewStory.getId(),
                                    lastIndex
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
        Log.e("updateProgress",
                "restartSlide " + durations
        );
        if (checkIfManagersIsNull()) return;
        if (durations.size() <= slideIndex) return;

        timelineManager.setDurations(durations, false);
        timelineManager.startSegment(slideIndex);
        timerManager.restartTimer(durations.get(slideIndex));
    }

    List<Integer> durations = new ArrayList<>();

    public void loadStoryAndSlide(IStoryDTO story, int slideIndex) {
        if (checkIfManagersIsNull()) return;
        webViewManager.loadStory(story, slideIndex);
    }

    public void openSlideByIndex(int index) {
        if (index < 0) index = 0;
        if (story == null) return;
        if (story.getSlidesCount() <= index) index = 0;
        repository.setStoryLastIndex(storyId, index);
        if (slideIndex != index) {
            slideIndex = index;
            changeCurrentSlide(index);
        }
    }

    public String getFeedId() {
        if (parentManager != null) return parentManager.getFeedId();
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


    public void resetCurrentDuration() {
        if (checkIfManagersIsNull()) return;
        if (story == null) return;
        this.durations.clear();
        this.durations.addAll(ArrayUtil.toIntegerList(story.getDurations()));
        Log.e("updateProgress",
                "resetCurrentDuration " + durations
        );
        timelineManager.setDurations(durations, false);
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
        if (story == null) return;
        timerManager.setTimerDuration(0);
        int lastIndex = slideIndex;
        if (lastIndex < story.getSlidesCount() - 1) {
            if (webViewManager == null) return;
            webViewManager.stopStory();
            lastIndex++;
            repository.setStoryLastIndex(storyId, lastIndex);
            slideIndex = lastIndex;
            changeCurrentSlide(lastIndex);
            Log.e("nextSlide", "" + lastIndex);
        } else {
            parentManager.nextStory(action);
        }
    }

    public void changeCurrentSlide(int slideIndex) {
        if (checkIfManagersIsNull()) return;
        if (durations == null) return;
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
        IASCore.getInstance().downloadManager.changePriorityForSingle(storyId,
                parentManager.storyType);
        if (getStoryType() == StoryType.COMMON) {
            OldStatisticManager.getInstance().addStatisticBlock(storyId, slideIndex);
            StatisticManager.getInstance().createCurrentState(storyId, slideIndex,
                    parentManager != null ? parentManager.getFeedId() : null);
        }
        loadStoryAndSlide(story, slideIndex);
    }

    public void setParentManager(ReaderManager parentManager) {
        this.parentManager = parentManager;
        repository = IASCore.getInstance().getStoriesRepository(parentManager.storyType);
        story = repository.getStoryById(storyId);
    }

    IStoriesRepository repository;

    public SourceType getSourceType() {
        if (parentManager != null)
            return parentManager.source;
        return SourceType.SINGLE;
    }

    public ReaderManager getParentManager() {
        return parentManager;
    }

    private ReaderManager parentManager;

    public void showShareView(InnerShareData shareData) {
        if (parentManager != null) {
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
        if (story == null) return;

        timerManager.setTimerDuration(0);
        int lastIndex = slideIndex;
        if (lastIndex > 0) {
            if (webViewManager == null) return;
            webViewManager.stopStory();
            lastIndex--;
            repository.setStoryLastIndex(storyId, lastIndex);
            slideIndex = lastIndex;
            changeCurrentSlide(lastIndex);
        } else {
            parentManager.prevStory(action);
        }
    }

    public void closeReader() {

    }

    public void changeSoundStatus() {
        IASCore.getInstance().changeSoundStatus();
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
            webViewManager.storyLoaded(story, index, alreadyLoaded);
            //host.storyLoadedSuccess();
        }
    }

    boolean currentSlideIsLoaded = false;

    public void setTimelineManager(StoryTimelineManager timelineManager, int storyId) {
        this.timelineManager = timelineManager;
    }

    public void setButtonsPanelManager(ButtonsPanelManager buttonsPanelManager, int storyId) {
        buttonsPanelManager.setParentManager(this);
        this.buttonsPanelManager = buttonsPanelManager;
        this.buttonsPanelManager.setStoryIdAndType(storyId, getStoryType());
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

    public void storyLoadedInCache() {
        if (checkIfManagersIsNull()) return;
        story = repository.getStoryById(storyId);
        if (story == null) return;
        webViewManager.setStory(story);
        if (this.durations == null)
            this.durations = new ArrayList<>();
        if (this.durations.isEmpty()) {
            if (story.getDurations() != null && !(story.getDurations().length == 0)) {
                this.durations.clear();
                this.durations.addAll(ArrayUtil.toIntegerList(story.getDurations()));
                story.setSlidesCount(story.getDurations().length);
                //TODO divide slides count and durations (return minimum) if necessary
            }
        }
        if (this.durations.size() > slideIndex) {
            timerManager.setCurrentDuration(this.durations.get(slideIndex));
        }
        timelineManager.setSlidesCount(story.getSlidesCount());
        timelineManager.setDurations(this.durations, true);
        int lastIndex = repository.getStoryLastIndex(storyId);
        webViewManager.loadStory(story, lastIndex);
    }

}
