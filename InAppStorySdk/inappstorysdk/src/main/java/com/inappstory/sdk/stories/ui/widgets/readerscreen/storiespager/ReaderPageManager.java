package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.ui.screens.IReaderSlideViewModel;
import com.inappstory.sdk.goods.outercallbacks.GoodsAddToCartProcessCallback;
import com.inappstory.sdk.goods.outercallbacks.GoodsCartData;
import com.inappstory.sdk.goods.outercallbacks.GoodsCartInteractionCallback;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.stories.api.models.SlideLinkObject;
import com.inappstory.sdk.stories.cache.ContentIdAndType;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CallToActionCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ClickAction;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryWidgetCallback;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.ui.reader.ReaderManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanelManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.StoryTimelineManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.webview.StoriesWebView;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.Map;
import java.util.Objects;

public class ReaderPageManager implements IReaderSlideViewModel {


    StoryTimelineManager timelineManager;
    ButtonsPanelManager buttonsPanelManager;
    StoriesViewManager webViewManager;
    TimerManager timerManager;
    ReaderPageFragment host;
    public boolean swipeGestureEnabled = true;
    public boolean backPressEnabled = true;


    public void addGoodsToCart(
            final String goodsCartData,
            final GoodsAddToCartProcessCallback goodsAddToCartProcessCallback
    ) {
        core.callbacksAPI().useCallback(IASCallbackType.GOODS_CART_INTERACTION,
                new UseIASCallback<GoodsCartInteractionCallback>() {
                    @Override
                    public void use(@NonNull GoodsCartInteractionCallback callback) {
                        callback.addToCart(
                                new GoodsCartData(),
                                goodsAddToCartProcessCallback
                        );
                    }
                }
        );
    }

    public void navigateToCart() {
        core.callbacksAPI().useCallback(IASCallbackType.GOODS_CART_INTERACTION,
                new UseIASCallback<GoodsCartInteractionCallback>() {
                    @Override
                    public void use(@NonNull GoodsCartInteractionCallback callback) {
                        callback.navigateToCart();
                    }
                }
        );
    }

    public void handleBackPress() {
        host.storiesView.handleBackPress();
    }

    public boolean isCorrectSubscriber(ContentIdAndType contentIdAndType) {
        return getStoryId() == contentIdAndType.contentId &&
                getViewContentType() == contentIdAndType.contentType;
    }

    private final IASCore core;

    public ReaderPageManager(IASCore core) {
        this.core = core;
    }

    public void unlockShareButton() {
        buttonsPanelManager.unlockShareButton();
    }

    public void swipeVerticalGestureEnabled(boolean enabled) {
        if (parentManager != null) {
            parentManager.swipeVerticalGestureEnabled(enabled);
        }

        swipeGestureEnabled = enabled;
    }

    public void backPressEnabled(boolean enabled) {
        if (parentManager != null) {
            parentManager.backPressEnabled(enabled);
        }
        backPressEnabled = enabled;
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

    public void startCommonTimer() {
        if (webViewManager != null)
            webViewManager.startCommonShowRefresh(slideIndex);
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public int getStoryId() {
        return storyId;
    }

    public ContentType getViewContentType() {
        return parentManager != null ? parentManager.contentType : ContentType.STORY;
    }


    public StoryData getStoryData(IReaderContent story) {
        return StoryData.getStoryData(story, getFeedId(), getSourceType(), getViewContentType());
    }

    public SlideData getSlideData(IReaderContent story) {
        int index = parentManager.getByIdAndIndex(story.id()).index();
        return new SlideData(
                getStoryData(story),
                index,
                story.slideEventPayload(index)
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
        core.contentLoader().storyDownloadManager().reloadStory(
                parentManager.getByIdAndIndex(storyId),
                getViewContentType()
        );
    }

    public void widgetEvent(final String widgetName, String widgetData) {
        final IReaderContent story = core.contentHolder().readerContent().getByIdAndType(
                storyId, getViewContentType()
        );
        if (story == null) return;
        final Map<String, String> widgetEventMap = JsonParser.toMap(widgetData);
        if (widgetEventMap != null)
            widgetEventMap.put("feed_id", getFeedId());

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

    private void tapOnLink(String link) {
        final SlideLinkObject object = JsonParser.fromJson(link, SlideLinkObject.class);
        if (object != null) {
            ClickAction action = ClickAction.BUTTON;
            final IReaderContent story = core.contentHolder().readerContent().getByIdAndType(
                    storyId, getViewContentType()
            );
            switch (object.getLink().getType()) {
                case "url":
                    if (object.getType() != null && !object.getType().isEmpty()) {
                        if ("swipeUpLink".equals(object.getType())) {
                            action = ClickAction.SWIPE;
                        }
                    }
                    if (getViewContentType() == ContentType.STORY)
                        core.statistic().storiesV1(
                                parentManager.getSessionId(),
                                new GetStatisticV1Callback() {
                                    @Override
                                    public void get(@NonNull IASStatisticStoriesV1 manager) {
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
                                showGoods(
                                        object.getLink().getTarget(),
                                        object.getElementId(),
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
            webViewManager.stopStory(true);
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
        webViewManager.stopStory(true);
        isPaused = false;
        //stop timers and timelines
    }

    public void pauseSlideTimerFromJS() {
        pauseTimers();
    }

    public void stopSlideTimerFromJS() {
        stopTimers();
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


    public void setStoryInfo(IReaderContent story) {
        if (checkIfManagersIsNull()) return;
        timelineManager.setSlidesCount(story.slidesCount(), false);

        webViewManager.loadStory(
                story,
                parentManager.getByIdAndIndex(storyId).index()
        );
    }

    public void loadStoryAndSlide(IReaderContent story, int slideIndex) {
        if (checkIfManagersIsNull()) return;
        webViewManager.loadStory(story, slideIndex);
    }

    public void openSlideByIndex(int index) {
        IReaderContent story = core.contentHolder().readerContent()
                .getByIdAndType(storyId, getViewContentType());
        if (index < 0) index = 0;
        if (story == null) return;
        if (story.slidesCount() <= index) index = 0;
        parentManager.getByIdAndIndex(storyId).index(index);
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

    public void nextSlideAuto() {
        if (webViewManager == null) return;
        pauseTimers();
        webViewManager.stopStory(false);
        webViewManager.autoSlideEnd();
    }

    public void nextSlide(int action) {
        if (checkIfManagersIsNull()) return;
        IReaderContent story = core.contentHolder().readerContent()
                .getByIdAndType(storyId, getViewContentType());
        if (story == null) return;
        pauseTimers();
        int lastIndex = slideIndex;
        if (lastIndex < story.slidesCount() - 1) {
            if (webViewManager == null) return;
            webViewManager.stopStory(false);
            lastIndex++;
            parentManager.getByIdAndIndex(storyId).index(lastIndex);
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

    private void stopTimers() {
        timerManager.pauseSlideTimer();
        timelineManager.stopTimer();
    }

    public void clearTimer() {
        timelineManager.clearTimer();
    }

    public void changeCurrentSlide(final int slideIndex) {
        if (checkIfManagersIsNull()) return;
        currentSlideIsLoaded = false;
        core.statistic().profiling().addTask("slide_show",
                storyId + "_" + slideIndex);
        isPaused = false;
        pauseTimers();
        core.statistic().storiesV2().sendCurrentState();
        startCommonTimer();
        core.contentLoader().storyDownloadManager().changePriorityForSingle(
                parentManager.getByIdAndIndex(storyId),
                parentManager.contentType
        );
        if (getViewContentType() == ContentType.STORY) {
            core.statistic().storiesV2().createCurrentState(
                    storyId,
                    slideIndex,
                    parentManager != null ? parentManager.getFeedId() : null
            );
            core.statistic().storiesV1(new GetStatisticV1Callback() {
                @Override
                public void get(@NonNull IASStatisticStoriesV1 manager) {
                    manager.addStatisticBlock(storyId, slideIndex);
                }
            });
        }
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
            IReaderContent story = core.contentHolder().readerContent()
                    .getByIdAndType(storyId, getViewContentType());
            if (story != null)
                parentManager.showShareView(shareData, storyId, slideIndex, null);
        }
    }

    public void sendShowStoryEvents(int storyId) {
        if (parentManager != null) {
            parentManager.sendShowStoryEvents(storyId);
        }
    }

    public void prevSlide(int action) {
        if (checkIfManagersIsNull()) return;
        IReaderContent story = core.contentHolder().readerContent()
                .getByIdAndType(storyId, getViewContentType());

        if (story == null) return;
        pauseTimers();
        int lastIndex = slideIndex;
        if (lastIndex > 0) {
            if (webViewManager == null) return;
            webViewManager.stopStory(false);
            lastIndex--;
            parentManager.getByIdAndIndex(storyId).index(lastIndex);
            slideIndex = lastIndex;
            changeCurrentSlide(lastIndex);
        } else {
            parentManager.prevStory(action);
        }
    }

    public void changeSoundStatus() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.settingsAPI().switchSoundOn();
            }
        });
        if (parentManager != null) {
            parentManager.updateSoundStatus();
        }
    }

    public void updateSoundStatus() {
        if (checkIfManagersIsNull()) return;
        buttonsPanelManager.refreshSoundStatus();
        webViewManager.changeSoundStatus();
    }


    public void slideLoadSuccess(int index, boolean alreadyLoaded) {
        if (slideIndex == index) {
            if (checkIfManagersIsNull()) return;
            Log.e("slidesDownloader", "RPM " + storyId + " " + index + " " + alreadyLoaded);
            webViewManager.storyLoaded(storyId, index, alreadyLoaded);
            //host.storyLoadedSuccess();
        }
    }

    boolean currentSlideIsLoaded = false;


    public void setTimelineManager(StoryTimelineManager timelineManager) {
        this.timelineManager = timelineManager;

        Story story = (Story) core.contentHolder().listsContent()
                .getByIdAndType(storyId, getViewContentType());
        timelineManager.setContentWithTimeline(story);
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

    @Override
    public void contentLoadError() {
        if (host != null)
            host.storyLoadError();
    }


    @Override
    public void slideLoadSuccess(int index) {
        slideLoadSuccess(index, false);
    }

    @Override
    public Integer externalSubscriber() {
        return null;
    }

    @Override
    public void renderReady() {

    }

    @Override
    public boolean loadContent() {
        return true;
    }

    @Override
    public void slideLoadError(int slideIndex) {
        if (this.slideIndex == slideIndex) {
            if (host != null)
                host.slideLoadError();
            timelineManager.setCurrentIndex(slideIndex);
        }
    }

    @Override
    public void contentLoadSuccess(IReaderContent story) {
        if (checkIfManagersIsNull()) return;
        host.story = (Story) story;
        setStoryInfo(story);
    }

    @Override
    public ContentIdAndType contentIdAndType() {
        return new ContentIdAndType(storyId, getViewContentType());
    }

}
