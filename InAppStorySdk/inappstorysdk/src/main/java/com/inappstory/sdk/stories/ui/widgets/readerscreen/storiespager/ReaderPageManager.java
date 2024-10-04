package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASStatisticV1;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.StoryLinkObject;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.statistic.IASStatisticProfilingImpl;
import com.inappstory.sdk.stories.ui.reader.ReaderManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanelManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.StoryTimelineManager;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.Map;
import java.util.Objects;

public class ReaderPageManager {


    StoryTimelineManager timelineManager;
    ButtonsPanelManager buttonsPanelManager;
    StoriesViewManager webViewManager;
    TimerManager timerManager;
    ReaderPageFragment host;

    private final IASCore core;

    public ReaderPageManager(IASCore core) {
        this.core = core;
    }

    public void unlockShareButton() {
        buttonsPanelManager.unlockShareButton();
    }

    public void removeStoryFromFavorite() {
        if (checkIfManagersIsNull()) return;
        buttonsPanelManager.removeStoryFromFavorite();
    }

    public void showLoader(boolean showBackground) {
        if (showBackground) {
            host.showLoaderContainer();
        } else {
            host.showLoaderOnly();
        }
    }


    public void screenshotShare() {
        if (checkIfManagersIsNull()) return;
        webViewManager.screenshotShare(storyId + "");
    }

    public void screenshotShareCallback(String shareId) {
        if (Objects.equals(Integer.toString(storyId), shareId)) {
            unlockShareButton();
        }
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
        timerManager.stopTimer();
        timelineManager.stopTimer();

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
            int sz = (!Sizes.isTablet(host.getContext()) ?
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
        InAppStoryService.getInstance().getStoryDownloadManager().reloadStory(storyId, getStoryType());
    }

    public void widgetEvent(final String widgetName, String widgetData) {
        final Story story = InAppStoryService.getInstance()
                .getStoryDownloadManager().getStoryById(storyId, getStoryType());
        if (story == null) return;
        final Map<String, String> widgetEventMap = JsonParser.toMap(widgetData);
        if (widgetEventMap != null)
            widgetEventMap.put("feed_id", getFeedId());

        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.callbacksAPI().useCallback(IASCallbackType.STORY_WIDGET,
                        new UseIASCallback<StoryWidgetCallback>() {
                            @Override
                            public void use(@NonNull StoryWidgetCallback callback) {
                                callback.widgetEvent(
                                        getSlideData(story),
                                        StringsUtils.getNonNull(widgetName),
                                        widgetEventMap
                                );
                            }
                        }
                );
            }
        });
    }

    private void tapOnLink(String link) {
        final StoryLinkObject object = JsonParser.fromJson(link, StoryLinkObject.class);
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (object != null) {

            ClickAction action = ClickAction.BUTTON;
            final Story story = service.getStoryDownloadManager().getStoryById(
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
                        core.statistic().v1(
                                parentManager.getSessionId(),
                                new GetStatisticV1Callback() {
                                    @Override
                                    public void get(@NonNull IASStatisticV1 manager) {
                                        manager.addLinkOpenStatistic(storyId, slideIndex);
                                    }
                                }
                        );

                    final ClickAction finalAction = action;
                    core.callbacksAPI().useCallback(
                            IASCallbackType.CALL_TO_ACTION,
                            new UseIASCallback<CallToActionCallback>() {
                                @Override
                                public void use(@NonNull CallToActionCallback callback) {
                                    if (story != null) {
                                        callback.callToAction(
                                                host != null ? host.getContext() : null,
                                                getSlideData(story),
                                                object.getLink().getTarget(),
                                                finalAction
                                        );
                                    }
                                }

                                @Override
                                public void onDefault() {
                                    parentManager.defaultTapOnLink(object.getLink().getTarget());
                                }
                            }
                    );
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
                    break;
            }
        }
    }

    public void storyOpen(int storyId) {
        if (checkIfManagersIsNull()) return;
        isPaused = false;
        if (storyId != this.storyId) {
            pauseTimers();
            webViewManager.stopStory();
        } else {
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
        pauseTimers();
        webViewManager.stopStory();
        isPaused = false;
        //stop timers and timelines
    }

    public void pauseSlideTimerFromJS() {
        pauseTimers();
    }

    public void clearSlideTimerFromJS() {
        pauseTimers();
        clearTimer();
    }

    public void pauseSlide(boolean withBackground) {
        if (checkIfManagersIsNull()) return;
        if (!withBackground && isPaused) return;
        isPaused = true;
        if (withBackground) {
            timerManager.pauseTimerAndRefreshStat();
        }
        webViewManager.pauseStory();
    }

    boolean isPaused;

    public void resumeSlide(boolean withBackground) {
        if (checkIfManagersIsNull()) return;
        if (!isPaused) return;
        isPaused = false;
        if (withBackground) {
            timerManager.resumeTimerAndRefreshStat();
        }
        webViewManager.resumeStory();
    }

    public void startSlideTimerFromJS(long newDuration, long currentTime, int slideIndex) {
        timerManager.startSlideTimer(newDuration, currentTime);
        timelineManager.startTimer(currentTime, slideIndex, newDuration);
    }

    public void restartSlide() {
        if (checkIfManagersIsNull()) return;
        webViewManager.restartStory();
    }


    public void setStoryInfo(Story story) {
        if (checkIfManagersIsNull()) return;
        timelineManager.setSlidesCount(story.getSlidesCount());
        webViewManager.loadStory(story, story.lastIndex);
    }

    public void loadStoryAndSlide(Story story, int slideIndex) {
        if (checkIfManagersIsNull()) return;
        webViewManager.loadStory(story, slideIndex);
    }

    public void openSlideByIndex(int index) {
        Story story = InAppStoryService.getInstance().getStoryDownloadManager()
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
        timelineManager.stopTimer();
        parentManager.nextStory(action);
    }

    public void prevStory(int action) {
        if (checkIfManagersIsNull()) return;
        timerManager.setTimerDuration(0);
        timelineManager.stopTimer();
        parentManager.prevStory(action);
    }

    public void nextSlide(int action) {
        if (checkIfManagersIsNull()) return;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        Story story = service.getStoryDownloadManager().getStoryById(storyId, getStoryType());
        if (story == null) return;
        pauseTimers();
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

    private void pauseTimers() {
        timerManager.pauseSlideTimer();
        timelineManager.stopTimer();
    }

    public void clearTimer() {
        timelineManager.clearTimer();
    }

    public void changeCurrentSlide(int slideIndex) {
        if (checkIfManagersIsNull()) return;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        host.showLoader();
        currentSlideIsLoaded = false;
        core.statistic().profiling().addTask("slide_show",
                storyId + "_" + slideIndex);
        isPaused = false;
        pauseTimers();
        core.statistic().v2().sendCurrentState();
        service.getStoryDownloadManager().changePriorityForSingle(storyId,
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

            Story story = InAppStoryService.getInstance().getStoryDownloadManager().getStoryById(storyId, getStoryType());
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
        Story story = service.getStoryDownloadManager().getStoryById(storyId, getStoryType());

        if (story == null) return;
        pauseTimers();
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


    public void setTimelineManager(StoryTimelineManager timelineManager) {
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
        setStoryInfo(story);
    }

}
