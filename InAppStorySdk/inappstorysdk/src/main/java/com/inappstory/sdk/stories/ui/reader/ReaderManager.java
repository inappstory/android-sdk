package com.inappstory.sdk.stories.ui.reader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASStatisticV1;
import com.inappstory.sdk.core.api.IASStatisticV2;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.api.impl.IASSingleStoryImpl;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.utils.CallbackTypesConverter;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.ShowStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.statistic.IASStatisticV2Impl;
import com.inappstory.sdk.stories.ui.utils.FragmentAction;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ReaderManager {

    private String listID;
    public ContentType contentType;
    public StoriesContentFragment host;

    private final Object hostLock = new Object();

    public void setHost(StoriesContentFragment host) {
        synchronized (hostLock) {
            this.host = host;
        }
    }

    public boolean hostIsEqual(StoriesContentFragment host) {
        synchronized (hostLock) {
            return this.host == host;
        }
    }

    public void onPageScrollStateChanged(int state) {
        int index = getCurrentStoryIndex();
        if (state == ViewPager.SCROLL_STATE_DRAGGING)
            latestShowStoryAction = ShowStory.ACTION_SWIPE;
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (index == currentSlideIndex) {
                resumeCurrent(false);
            }
            clearInactiveTimers();
        }
        setCurrentSlideIndex(index);

    }

    public StoriesContentFragment getHost() {
        synchronized (hostLock) {
            return host;
        }
    }

    public SourceType source = SourceType.SINGLE;
    private final IASCore core;

    public ReaderManager(
            IASCore core,
            String listID,
            boolean showOnlyNewStories,
            String sessionId,
            String feedId,
            String feedSlug,
            ContentType contentType,
            SourceType source,
            int latestShowStoryAction
    ) {
        this.core = core;
        this.listID = listID;
        this.showOnlyNewStories = showOnlyNewStories;
        this.feedId = feedId;
        this.sessionId = sessionId;
        this.feedSlug = feedSlug;
        this.contentType = contentType;
        this.source = source;
        this.latestShowStoryAction = latestShowStoryAction;
    }

    private int lastSentId = 0;

    int latestShowStoryAction = ShowStory.ACTION_OPEN;

    public void sendShowStoryEvents(int storyId) {
        if (lastSentId == storyId) return;
        lastSentId = storyId;
        final IListItemContent story = core.contentHolder()
                .listsContent()
                .getByIdAndType(
                        storyId,
                        contentType
                );
        if (story != null) {
            core.callbacksAPI().useCallback(
                    IASCallbackType.SHOW_STORY,
                    new UseIASCallback<ShowStoryCallback>() {
                        @Override
                        public void use(@NonNull ShowStoryCallback callback) {
                            callback.showStory(
                                    StoryData.getStoryData(
                                            story,
                                            feedId,
                                            source,
                                            contentType
                                    ),
                                    new CallbackTypesConverter()
                                            .getShowStoryActionTypeFromInt(latestShowStoryAction)
                            );
                        }
                    }
            );
        }
    }

    public void close() {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment host) {
                Activity activity = host.getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }
        });

    }

    public void unsubscribeClicks() {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment host) {
                Activity activity = host.getActivity();
                if (activity instanceof StoriesActivity) {
                    ((StoriesActivity) activity).unsubscribeClicks();
                }
            }
        });

    }

    public void subscribeClicks() {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment host) {
                Activity activity = host.getActivity();
                if (activity instanceof StoriesActivity) {
                    ((StoriesActivity) activity).subscribeClicks();
                }
            }
        });

    }


    public void swipeUp(int position) {
        int storyId = storiesIds.get(position).id();
        ReaderPageManager manager = getSubscriberByStoryId(storyId);
        if (manager != null)
            manager.swipeUp();
    }

    public void showGoods(
            final String skusString,
            final String widgetId,
            final ShowGoodsCallback showGoodsCallback,
            final SlideData slideData
    ) {
        BaseStoryScreen screen = getReaderScreen();
        if (screen == null) {
            showGoodsCallback.goodsIsCanceled(widgetId);
            Log.d("InAppStory_SDK_error", "Something wrong");
            return;
        }
        if (AppearanceManager.getCommonInstance().csCustomGoodsWidget() == null) {
            showGoodsCallback.goodsIsCanceled(widgetId);
            Log.d("InAppStory_SDK_error", "Empty goods widget");
            return;
        }
        FragmentManager fragmentManager = screen.getScreenFragmentManager();
        if (fragmentManager.findFragmentById(R.id.ias_outer_top_container) != null) {
            showGoodsCallback.goodsIsCanceled(widgetId);
            Log.d("InAppStory_SDK_error", "Top container is busy");
            return;
        }
        if (screen instanceof ShowGoodsCallback) {
            screen.setShowGoodsCallback(showGoodsCallback);
            ((ShowGoodsCallback) screen).goodsIsOpened();
        }
        core
                .screensManager()
                .getStoryScreenHolder()
                .openGoodsOverlapContainer(
                        skusString,
                        widgetId,
                        slideData
                );
    }


    public void gameComplete(String data, int storyId, int slideIndex) {
        ReaderPageManager pageManager = getSubscriberByStoryId(storyId);
        if (pageManager != null) pageManager.gameComplete(data);
    }


    public void removeAllStoriesFromFavorite() {
        for (ReaderPageManager subscriber : subscribers) {
            subscriber.removeStoryFromFavorite();
        }
    }

    public void showShareView(final InnerShareData shareData,
                              final int storyId, final int slideIndex) {
        //pause();
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment host) {
                host.showShareView(shareData, storyId, slideIndex);
            }

            @Override
            public void error() {
                ShareProcessHandler shareProcessHandler = core
                        .screensManager().getShareProcessHandler();
                if (shareProcessHandler != null)
                    shareProcessHandler.isShareProcess(false);
            }
        });

    }

    public void removeStoryFromFavorite(int storyId) {
        ReaderPageManager pageManager = getSubscriberByStoryId(storyId);
        if (pageManager != null) pageManager.removeStoryFromFavorite();
    }

    public void showSingleStory(final int storyId, final int slideIndex) {
        final StoriesContentFragment host = getHost();
        if (host == null) return;
        if (contentType == ContentType.STORY)
            core.statistic().v1(getSessionId(), new GetStatisticV1Callback() {
                @Override
                public void get(@NonNull IASStatisticV1 manager) {
                    manager.addLinkOpenStatistic(storyId, slideIndex);
                }
            });
        for (int i = 0; i < storiesIds.size(); i++) {
            final int tempIndex = i;
            if (storiesIds.get(tempIndex).id() == storyId) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        storiesIds.get(tempIndex).index(slideIndex);
                        latestShowStoryAction = ShowStory.ACTION_CUSTOM;
                        host.setCurrentItem(tempIndex);
                    }
                });
                return;
            }
        }

        LaunchStoryScreenAppearance appearance = host.getAppearanceSettings();
        final AppearanceManager appearanceManager;
        if (appearance != null) {
            appearanceManager = appearance.toAppearanceManager();
        } else {
            appearanceManager = new AppearanceManager();
        }
        ((IASSingleStoryImpl) core.singleStoryAPI()).show(
                host.getContext(),
                storyId + "",
                appearanceManager,
                null,
                slideIndex,
                true,
                SourceType.SINGLE,
                ShowStory.ACTION_CUSTOM
        );
    }

    void sendStat(int position, SourceType source) {
        if (lastPos < position && lastPos > -1) {
            sendStatBlock(true, IASStatisticV2Impl.NEXT, storiesIds.get(position).id());
        } else if (lastPos > position && lastPos > -1) {
            sendStatBlock(true, IASStatisticV2Impl.PREV, storiesIds.get(position).id());
        } else if (lastPos == -1) {
            String whence = IASStatisticV2Impl.DIRECT;
            switch (source) {
                case ONBOARDING:
                    whence = IASStatisticV2Impl.ONBOARDING;
                    break;
                case LIST:
                    whence = IASStatisticV2Impl.LIST;
                    break;
                case FAVORITE:
                    whence = IASStatisticV2Impl.FAVORITE;
                    break;
                default:
                    break;
            }
            sendStatBlock(false, whence, storiesIds.get(position).id());
        }
    }

    void newStoryTask(int pos) {
        ArrayList<ContentIdWithIndex> adds = new ArrayList<>();
        if (storiesIds.size() > 1) {
            if (pos == 0) {
                adds.add(storiesIds.get(pos + 1));
            } else if (pos == storiesIds.size() - 1) {
                adds.add(storiesIds.get(pos - 1));
            } else {
                adds.add(storiesIds.get(pos + 1));
                adds.add(storiesIds.get(pos - 1));
            }
        }
        if (!core.contentLoader().storyDownloadManager().changePriority(
                storiesIds.get(pos),
                adds,
                contentType
        )) {
            useContentFragment(new StoriesContentFragmentAction() {
                @Override
                public void invoke(StoriesContentFragment host) {
                    host.forceFinish();
                }
            });
            return;
        }
        core.contentLoader().storyDownloadManager().addStoryTask(
                storiesIds.get(pos),
                adds,
                contentType);

    }

    void restartCurrentStory() {
        ReaderPageManager subscriber = getCurrentSubscriber();
        if (subscriber == null) return;
        subscriber.restartSlide();
    }

    public void updateSoundStatus() {
        synchronized (subscribers) {
            for (ReaderPageManager pageManager : subscribers) {
                pageManager.updateSoundStatus();
            }
        }
    }

    void onPageSelected(SourceType source, int position) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        sendStat(position, source);

        lastPos = position;
        lastSentId = 0;
        ContentIdWithIndex contentIdWithIndex = storiesIds.get(position);
        currentStoryId = contentIdWithIndex.id();
        IReaderContent story = core.contentHolder().readerContent()
                .getByIdAndType(currentStoryId, contentType);
        if (story != null) {
            if (firstStoryId > 0 && startedSlideInd > 0) {
                if (story.slidesCount() > startedSlideInd)
                    contentIdWithIndex.index(startedSlideInd);
                cleanFirst();
            }
            core.statistic().profiling().addTask("slide_show",
                    currentStoryId + "_" +
                            contentIdWithIndex.index()
            );
        }
        final int pos = position;
        service.getListReaderConnector().changeStory(currentStoryId, listID, showOnlyNewStories);
        core.screensManager().getStoryScreenHolder().currentOpenedStoryId(currentStoryId);
        currentSlideIndex = contentIdWithIndex.index();
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment host) {
                host.showGuardMask(600);
            }

        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {

                }
                newStoryTask(pos);
                if (storiesIds.size() > pos) {
                    changeStory();
                }
            }
        }).start();
    }

    public void storyClick() {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment host) {
                host.showGuardMask(300);
            }

        });
    }

    public void clearInactiveTimers() {
        synchronized (subscribers) {
            ReaderPageManager currentSubscriber = getCurrentSubscriber();
            for (ReaderPageManager subscriber : subscribers) {
                if (subscriber != currentSubscriber) {
                    subscriber.clearTimer();
                }
            }
        }
    }

    void changeStory() {
        final List<Integer> lst = new ArrayList<>();
        lst.add(currentStoryId);
        if (contentType == ContentType.STORY)
            core.statistic().v1(getSessionId(), new GetStatisticV1Callback() {
                @Override
                public void get(@NonNull IASStatisticV1 manager) {
                    manager.addStatisticBlock(currentStoryId,
                            currentSlideIndex);
                    manager.previewStatisticEvent(lst);
                }
            });
        synchronized (subscribers) {
            for (ReaderPageManager pageManager : subscribers) {
                if (pageManager.getStoryId() != currentStoryId) {
                    pageManager.stopStory(currentStoryId);
                } else {
                    pageManager.setSlideIndex(currentSlideIndex);
                    pageManager.storyOpen(currentStoryId);
                }
            }
        }
    }

    int lastPos = -1;

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public void setFeedSlug(String feedId) {
        this.feedSlug = feedSlug;
    }

    public String getFeedId() {
        return feedId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getFeedSlug() {
        return feedId;
    }

    private String feedId;
    private boolean showOnlyNewStories;
    private String sessionId;
    private String feedSlug;

    private void sendStatBlock(boolean hasCloseEvent, String whence, int id) {
        IReaderContent story2 = core.contentHolder().readerContent().getByIdAndType(
                id,
                contentType
        );
        if (story2 == null) return;
        IASStatisticV2 statisticV2 = core.statistic().v2();
        statisticV2.sendCurrentState();
        ContentIdWithIndex contentIdWithIndex = storiesIds.get(lastPos);
        if (hasCloseEvent) {
            IReaderContent story = core.contentHolder().readerContent().getByIdAndType(
                    contentIdWithIndex.id(),
                    contentType
            );
            statisticV2.sendCloseStory(contentIdWithIndex.id(),
                    whence,
                    contentIdWithIndex.index(),
                    story.slidesCount(),
                    feedId
            );
        }
        statisticV2.sendViewStory(id, whence, feedId);
        statisticV2.sendOpenStory(id, whence, feedId);
        for (ContentIdWithIndex contentId : storiesIds) {
            if (story2.id() == contentId.id()) {
                statisticV2.createCurrentState(contentId.id(), contentId.index(), feedId);
            }
        }
    }

    void shareComplete() {
        synchronized (subscribers) {
            for (ReaderPageManager pageManager : subscribers) {
                pageManager.unlockShareButton();
            }
        }
        ShareProcessHandler shareProcessHandler = core.screensManager().getShareProcessHandler();
        if (shareProcessHandler == null) return;
        shareProcessHandler.shareCompleteListener().complete(true);
        shareProcessHandler.clearShareIds();
    }

    public int getCurrentStoryId() {
        return currentStoryId;
    }

    public int getCurrentStoryIndex() {
        return getByIdAndIndex(currentStoryId).index();
    }

    public void setCurrentStoryId(int currentStoryId) {
        this.currentStoryId = currentStoryId;
    }

    public int getCurrentSlideIndex() {
        return currentSlideIndex;
    }

    public void setCurrentSlideIndex(int currentSlideIndex) {
        this.currentSlideIndex = currentSlideIndex;
    }

    public void refreshStoriesIds() {
        for (ContentIdWithIndex contentIdWithIndex : storiesIds) {
            contentIdWithIndex.index(0);
        }
    }

    public void setStoriesIds(List<Integer> storiesIds) {
        this.storiesIds.clear();
        if (storiesIds == null) return;
        for (int storyId : storiesIds) {
            this.storiesIds.add(new ContentIdWithIndex(storyId, 0));
        }
    }

    private int currentStoryId;
    private int currentSlideIndex;


    public void setStoriesIdsWithIndex(List<ContentIdWithIndex> storiesIds) {
        this.storiesIds.clear();
        this.storiesIds.addAll(storiesIds);
    }

    public List<ContentIdWithIndex> getStoriesIdsWithIndex() {
        return storiesIds;
    }

    private final List<ContentIdWithIndex> storiesIds = new ArrayList<>();

    public ContentIdWithIndex getByIdAndIndex(int storyId) {
        for (ContentIdWithIndex contentIdWithIndex : storiesIds) {
            if (contentIdWithIndex.id() == storyId)
                return contentIdWithIndex;
        }
        return null;
    }

    public int startedSlideInd;
    public int firstStoryId = -1;

    public void cleanFirst() {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment host) {
                Bundle bundle = host.getArguments();
                bundle.remove("slideIndex");
                host.setArguments(bundle);
            }
        });
        startedSlideInd = 0;
        firstStoryId = -1;
    }

    public BaseStoryScreen getReaderScreen() {
        return getHost().getStoriesReader();
    }

    private final HashSet<ReaderPageManager> subscribers = new HashSet<>();

    private final SessionAssetsIsReadyCallback assetsIsReadyCallback = new SessionAssetsIsReadyCallback() {
        @Override
        public void isReady() {

        }
    };

    public void subscribeToAssets() {
        core.sessionManager().getSession().addSessionAssetsIsReadyCallback(assetsIsReadyCallback);
    }

    public void unsubscribeFromAssets() {
        core.sessionManager().getSession().removeSessionAssetsIsReadyCallback(assetsIsReadyCallback);
    }

    public void addSubscriber(ReaderPageManager manager) {
        synchronized (subscribers) {
            for (ReaderPageManager readerPageManager : subscribers) {
                if (readerPageManager.getStoryId() == manager.getStoryId()) return;
            }
            subscribers.add(manager);
        }
    }

    public void removeSubscriber(ReaderPageManager manager) {
        synchronized (subscribers) {
            subscribers.remove(manager);
        }
    }

    private ReaderPageManager getSubscriberByStoryId(int storyId) {
        for (ReaderPageManager subscriber : subscribers) {
            if (subscriber.getStoryId() == storyId)
                return subscriber;
        }
        return null;
    }

    private ReaderPageManager getCurrentSubscriber() {
        return getSubscriberByStoryId(currentStoryId);
    }

    private void useContentFragment(FragmentAction<StoriesContentFragment> action) {
        StoriesContentFragment host = getHost();
        if (host != null) {
            action.invoke(host);
        } else {
            action.error();
        }
    }


    public void nextStory(final int action) {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment host) {
                host.nextStory(action);
            }
        });

    }

    public void prevStory(final int action) {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment host) {
                host.prevStory(action);
            }
        });
    }


    public void defaultTapOnLink(final String url) {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment host) {
                host.defaultUrlClick(url);
            }
        });
    }

    public void pauseCurrent(boolean withBackground) {
        ReaderPageManager currentSubscriber = getCurrentSubscriber();
        if (currentSubscriber != null) {
            currentSubscriber.pauseSlide(withBackground);
        }
        core.statistic().v2().pauseStoryEvent(withBackground);
    }

    public void resumeCurrent(boolean withBackground) {
        ReaderPageManager currentSubscriber = getCurrentSubscriber();
        if (currentSubscriber != null)
            currentSubscriber.resumeSlide(withBackground);
        if (withBackground) {
            core.statistic().v1(
                    getSessionId(),
                    new GetStatisticV1Callback() {
                        @Override
                        public void get(@NonNull IASStatisticV1 manager) {
                            manager.refreshTimer();
                        }
                    }
            );
        }
        core.statistic().v2().resumeStoryEvent(withBackground);
    }

}
