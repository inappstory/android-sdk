package com.inappstory.sdk.stories.ui.reader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseManagerInstanceCallback;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.game.cache.SessionAssetsIsReadyCallback;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetOldStatisticManagerCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.utils.FragmentAction;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ReaderManager {

    private String listID;
    public Story.StoryType storyType;
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


    public StoriesContentFragment getHost() {
        synchronized (hostLock) {
            return host;
        }
    }

    public SourceType source = SourceType.SINGLE;

    public ReaderManager() {
    }


    public ReaderManager(
            String listID,
            boolean showOnlyNewStories,
            String sessionId,
            String feedId,
            String feedSlug,
            Story.StoryType storyType,
            SourceType source,
            int latestShowStoryAction
    ) {
        this.listID = listID;
        this.showOnlyNewStories = showOnlyNewStories;
        this.feedId = feedId;
        this.sessionId = sessionId;
        this.feedSlug = feedSlug;
        this.storyType = storyType;
        this.source = source;
        this.latestShowStoryAction = latestShowStoryAction;
    }

    private int lastSentId = 0;

    int latestShowStoryAction = ShowStory.ACTION_OPEN;

    public void sendShowStoryEvents(int storyId) {
        if (InAppStoryService.getInstance() == null || InAppStoryService.getInstance().getStoryDownloadManager() == null)
            return;
        if (lastSentId == storyId) return;
        lastSentId = storyId;
        Story story = InAppStoryService.getInstance().getStoryDownloadManager().getStoryById(storyId, storyType);
        if (story != null) {
            if (CallbackManager.getInstance().getShowStoryCallback() != null) {
                CallbackManager.getInstance().getShowStoryCallback().showStory(
                        StoryData.getStoryData(
                                story,
                                feedId,
                                source,
                                storyType
                        ),
                        CallbackManager.getInstance().getShowStoryActionTypeFromInt(latestShowStoryAction));
            }
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
        int storyId = storiesIds.get(position);
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
        InAppStoryManager.useInstance(
                new UseManagerInstanceCallback() {
                    @Override
                    public void use(@NonNull InAppStoryManager manager) throws Exception {
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
                        manager
                                .getScreensHolder()
                                .getStoryScreenHolder()
                                .openGoodsOverlapContainer(
                                        skusString,
                                        widgetId,
                                        slideData
                                );
                    }
                }
        );
    }

    /*public void pause() {
        if (parentFragment != null)
            parentFragment.pause();
    }

    public void resume() {
        if (parentFragment != null)
            parentFragment.resume();
    }*/

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
                ShareProcessHandler shareProcessHandler = ShareProcessHandler.getInstance();
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
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull final InAppStoryService service) throws Exception {
                final StoriesContentFragment host = getHost();
                if (host == null) return;
                if (storyType == Story.StoryType.COMMON)
                    OldStatisticManager.useInstance(getSessionId(), new GetOldStatisticManagerCallback() {
                        @Override
                        public void get(@NonNull OldStatisticManager manager) {
                            manager.addLinkOpenStatistic(storyId, slideIndex);
                        }
                    });
                if (storiesIds.contains(storyId)) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Story.StoryType type = Story.StoryType.COMMON;
                            Story st = service.getStoryDownloadManager()
                                    .getStoryById(storyId, type);
                            if (st != null) {
                                if (st.getSlidesCount() <= slideIndex) {
                                    st.lastIndex = 0;
                                } else {
                                    st.lastIndex = slideIndex;
                                }
                            }
                            latestShowStoryAction = ShowStory.ACTION_CUSTOM;
                            host.setCurrentItem(storiesIds.indexOf(storyId));
                        }
                    });
                } else {
                    InAppStoryManager.useInstance(new UseManagerInstanceCallback() {
                        @Override
                        public void use(@NonNull InAppStoryManager manager) throws Exception {
                            manager.showStoryWithSlide(
                                    storyId + "",
                                    host.getContext(),
                                    slideIndex,
                                    host.getAppearanceSettings(),
                                    storyType,
                                    SourceType.SINGLE,
                                    ShowStory.ACTION_CUSTOM
                            );
                        }
                    });
                }
            }
        });

    }

    void sendStat(int position, SourceType source) {
        if (lastPos < position && lastPos > -1) {
            sendStatBlock(true, StatisticManager.NEXT, storiesIds.get(position));
        } else if (lastPos > position && lastPos > -1) {
            sendStatBlock(true, StatisticManager.PREV, storiesIds.get(position));
        } else if (lastPos == -1) {
            String whence = StatisticManager.DIRECT;
            switch (source) {
                case ONBOARDING:
                    whence = StatisticManager.ONBOARDING;
                    break;
                case LIST:
                    whence = StatisticManager.LIST;
                    break;
                case FAVORITE:
                    whence = StatisticManager.FAVORITE;
                    break;
                default:
                    break;
            }
            sendStatBlock(false, whence, storiesIds.get(position));
        }
    }

    void newStoryTask(int pos) {
        ArrayList<Integer> adds = new ArrayList<>();
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
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        if (!service.getStoryDownloadManager().changePriority(
                storiesIds.get(pos),
                adds,
                storyType
        )) {
            useContentFragment(new StoriesContentFragmentAction() {
                @Override
                public void invoke(StoriesContentFragment host) {
                    host.forceFinish();
                }
            });
            return;
        }
        service.getStoryDownloadManager().addStoryTask(
                storiesIds.get(pos),
                adds,
                storyType);

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
        currentStoryId = storiesIds.get(position);
        Story story = service.getStoryDownloadManager().getStoryById(currentStoryId, storyType);
        if (story != null) {
            if (firstStoryId > 0 && startedSlideInd > 0) {
                if (story.getSlidesCount() > startedSlideInd)
                    story.lastIndex = startedSlideInd;
                cleanFirst();
            }

            ProfilingManager.getInstance().addTask("slide_show",
                    currentStoryId + "_" +
                            story.lastIndex);
        }
        final int pos = position;

        service.getListReaderConnector().changeStory(currentStoryId, listID, showOnlyNewStories);
        service.setCurrentId(currentStoryId);
        if (story != null) {
            currentSlideIndex = story.lastIndex;
        }
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

                if (storiesIds != null && storiesIds.size() > pos) {
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
        final ArrayList<Integer> lst = new ArrayList<>();
        lst.add(currentStoryId);
        if (storyType == Story.StoryType.COMMON)
            OldStatisticManager.useInstance(getSessionId(), new GetOldStatisticManagerCallback() {
                @Override
                public void get(@NonNull OldStatisticManager manager) {
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

        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return;
        Story story2 = service.getStoryDownloadManager().getStoryById(id, storyType);
        if (story2 == null) return;
        StatisticManager.getInstance().sendCurrentState();
        if (hasCloseEvent) {
            Story story = service.getStoryDownloadManager().getStoryById(storiesIds.get(lastPos), storyType);
            StatisticManager.getInstance().sendCloseStory(story.id, whence, story.lastIndex, story.getSlidesCount(), feedId);
        }
        StatisticManager.getInstance().sendViewStory(id, whence, feedId);
        StatisticManager.getInstance().sendOpenStory(id, whence, feedId);
        StatisticManager.getInstance().createCurrentState(story2.id, story2.lastIndex, feedId);
    }

    void shareComplete() {
        synchronized (subscribers) {
            for (ReaderPageManager pageManager : subscribers) {
                pageManager.unlockShareButton();
            }
        }
        ShareProcessHandler shareProcessHandler =
                ShareProcessHandler.getInstance();
        if (shareProcessHandler == null) return;
        shareProcessHandler.shareCompleteListener().complete(true);
        shareProcessHandler.clearShareIds();
    }

    public int getCurrentStoryId() {
        return currentStoryId;
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

    public List<Integer> getStoriesIds() {
        return storiesIds;
    }

    public void setStoriesIds(List<Integer> storiesIds) {
        this.storiesIds = storiesIds;
    }

    private int currentStoryId;
    private int currentSlideIndex;
    private List<Integer> storiesIds;


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
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.getSession().addSessionAssetsIsReadyCallback(assetsIsReadyCallback);
            }
        });
    }

    public void unsubscribeFromAssets() {
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.getSession().removeSessionAssetsIsReadyCallback(assetsIsReadyCallback);
            }
        });
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
        StatisticManager.getInstance().pauseStoryEvent(withBackground);
    }

    public void resumeCurrent(boolean withBackground) {
        ReaderPageManager currentSubscriber = getCurrentSubscriber();
        if (currentSubscriber != null)
            currentSubscriber.resumeSlide(withBackground);
        if (withBackground) {
            OldStatisticManager.useInstance(getSessionId(), new GetOldStatisticManagerCallback() {
                @Override
                public void get(@NonNull OldStatisticManager manager) {
                    manager.refreshTimer();
                }
            });
        }
        StatisticManager.getInstance().resumeStoryEvent(withBackground);
    }

    public void swipeUp() {

    }

    public void swipeDown() {

    }

    public void swipeLeft() {

    }

    public void swipeRight() {

    }

    public void slideLoadedInCache(int storyId, int slideIndex) {
        ReaderPageManager pageManager = getSubscriberByStoryId(storyId);
        if (pageManager != null) pageManager.slideLoadedInCache(slideIndex);
    }
}
