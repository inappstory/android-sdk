package com.inappstory.sdk.stories.ui.reader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.models.js.StoryIdSlideIndex;
import com.inappstory.sdk.core.repository.statistic.IStatisticV1Repository;
import com.inappstory.sdk.core.repository.stories.IStoriesRepository;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.utils.network.JsonParser;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.core.models.api.Story.StoryType;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.core.repository.statistic.ProfilingManager;
import com.inappstory.sdk.core.repository.statistic.StatisticV2Manager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.usecase.callbacks.IUseCaseCallback;
import com.inappstory.sdk.usecase.callbacks.UseCaseCallbackShowStory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ReaderManager {

    public String listID;
    public StoryType storyType;

    public SourceType source = SourceType.SINGLE;

    public ReaderManager() {
    }


    public ReaderManager(
            String listID,
            String feedId,
            String feedSlug,
            StoryType storyType,
            SourceType source,
            int latestShowStoryAction
    ) {
        this.listID = listID;
        this.feedId = feedId;
        this.feedSlug = feedSlug;
        this.storyType = storyType;
        this.source = source;
        this.latestShowStoryAction = latestShowStoryAction;
    }

    private int lastSentId = 0;

    int latestShowStoryAction = ShowStory.ACTION_OPEN;

    public void sendShowStoryEvents(int storyId) {
        if (lastSentId == storyId) return;
        lastSentId = storyId;
        IStoriesRepository storiesRepository = IASCore.getInstance().getStoriesRepository(storyType);
        IPreviewStoryDTO story = storiesRepository.getStoryPreviewById(storyId);
        IUseCaseCallback useCaseCallbackShowStory = new UseCaseCallbackShowStory(
                new StoryData(
                        story,
                        feedId,
                        source
                ),
                CallbackManager.getInstance().getShowStoryActionTypeFromInt(latestShowStoryAction)
        );
        useCaseCallbackShowStory.invoke();
    }

    public void close() {
        parentFragment.getActivity().finish();
    }


    public void swipeUp(int position) {
        int storyId = storiesIds.get(position);
        ReaderPageManager manager = getSubscriberByStoryId(storyId);
        if (manager != null)
            manager.swipeUp();
    }

    public void hideGoods() {
        ScreensManager.getInstance().hideGoods();
    }

    public void showGoods(String skusString, String widgetId, ShowGoodsCallback showGoodsCallback, int storyId, int slideIndex) {
        ScreensManager.getInstance().showGoods(skusString,
                parentFragment.getActivity(), showGoodsCallback, false,
                widgetId, storyId, slideIndex, feedId);
    }

    public void pause() {
        if (parentFragment != null)
            parentFragment.pause();
    }

    public void resume() {
        if (parentFragment != null)
            parentFragment.resume();
    }

    public void gameComplete(String data, int storyId, int slideIndex) {
        ReaderPageManager pageManager = getSubscriberByStoryId(storyId);
        if (pageManager != null) pageManager.gameComplete(data);
    }


    public void removeAllStoriesFromFavorite() {
        if (subscribers == null) return;
        for (ReaderPageManager subscriber : subscribers) {
            subscriber.removeStoryFromFavorite();
        }
    }

    public void showShareView(InnerShareData shareData,
                              int storyId, int slideIndex) {
        //pause();
        if (parentFragment != null) {
            parentFragment.showShareView(shareData, storyId, slideIndex);
        } else {
            IASCore.getInstance().isShareProcess(false);
        }
    }

    public void removeStoryFromFavorite(int storyId) {
        ReaderPageManager pageManager = getSubscriberByStoryId(storyId);
        if (pageManager != null) pageManager.removeStoryFromFavorite();
    }

    public void showSingleStory(final int storyId, final int slideIndex) {
        if (storyType == StoryType.COMMON)
            IASCore.getInstance().statisticV1Repository.setTypeToTransition(
                    new StoryIdSlideIndex(
                            storyId,
                            slideIndex
                    )
            );
        if (storiesIds.contains(storyId)) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    IStoriesRepository storiesRepository = IASCore.getInstance().getStoriesRepository(storyType);
                    IPreviewStoryDTO story = storiesRepository.getStoryPreviewById(storyId);
                    if (story != null) {
                        if (story.getSlidesCount() <= slideIndex) {
                            storiesRepository.setStoryLastIndex(storyId, 0);
                        } else {
                            storiesRepository.setStoryLastIndex(storyId, slideIndex);
                        }
                    }
                    latestShowStoryAction = ShowStory.ACTION_CUSTOM;
                    parentFragment.setCurrentItem(storiesIds.indexOf(storyId));
                }
            });
        } else {
            IASCore.getInstance().showSingleStory(
                    storyId + "",
                    false,
                    parentFragment.getContext(),
                    getAppearanceManagerFromReaderSettings(
                            parentFragment.readerSettings
                    ),
                    null,
                    slideIndex,
                    storyType,
                    SourceType.SINGLE,
                    ShowStory.ACTION_CUSTOM
            );
        }
    }

    private AppearanceManager getAppearanceManagerFromReaderSettings(
            String managerSettings
    ) {
        AppearanceManager appearanceManager = new AppearanceManager();
        if (managerSettings != null) {
            StoriesReaderSettings settings = JsonParser.fromJson(managerSettings, StoriesReaderSettings.class);
            appearanceManager.csHasLike(settings.hasLike);
            appearanceManager.csHasFavorite(settings.hasFavorite);
            appearanceManager.csHasShare(settings.hasShare);
            appearanceManager.csClosePosition(settings.closePosition);
            appearanceManager.csCloseOnOverscroll(settings.closeOnOverscroll);
            appearanceManager.csCloseOnSwipe(settings.closeOnSwipe);
            appearanceManager.csIsDraggable(true);
            appearanceManager.csTimerGradientEnable(settings.timerGradientEnable);
            appearanceManager.csStoryReaderAnimation(settings.readerAnimation);
            appearanceManager.csCloseIcon(settings.closeIcon);
            appearanceManager.csDislikeIcon(settings.dislikeIcon);
            appearanceManager.csLikeIcon(settings.likeIcon);
            appearanceManager.csRefreshIcon(settings.refreshIcon);
            appearanceManager.csFavoriteIcon(settings.favoriteIcon);
            appearanceManager.csShareIcon(settings.shareIcon);
            appearanceManager.csSoundIcon(settings.soundIcon);
        }
        return appearanceManager;
    }

    void sendStat(int position, SourceType source) {
        if (lastPos < position && lastPos > -1) {
            sendStatBlock(true, StatisticV2Manager.NEXT, storiesIds.get(position));
        } else if (lastPos > position && lastPos > -1) {
            sendStatBlock(true, StatisticV2Manager.PREV, storiesIds.get(position));
        } else if (lastPos == -1) {
            String whence = StatisticV2Manager.DIRECT;
            switch (source) {
                case ONBOARDING:
                    whence = StatisticV2Manager.ONBOARDING;
                    break;
                case LIST:
                    whence = StatisticV2Manager.LIST;
                    break;
                case FAVORITE:
                    whence = StatisticV2Manager.FAVORITE;
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
        IASCore.getInstance().downloadManager.addStoryTask(
                storiesIds.get(pos),
                adds,
                storyType
        );
        IASCore.getInstance().downloadManager.changePriority(
                storiesIds.get(pos),
                adds,
                storyType
        );

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
        sendStat(position, source);

        lastPos = position;
        lastSentId = 0;
        currentStoryId = storiesIds.get(position);
        IStoriesRepository storiesRepository = IASCore.getInstance().getStoriesRepository(storyType);
        IPreviewStoryDTO story = storiesRepository.getStoryPreviewById(currentStoryId);
        int lastIndex = 0;
        if (story != null) {
            if (firstStoryId > 0 && startedSlideInd > 0) {
                if (story.getSlidesCount() > startedSlideInd)
                    storiesRepository.setStoryLastIndex(currentStoryId, startedSlideInd);
                cleanFirst();
            }
            lastIndex = storiesRepository.getStoryLastIndex(currentStoryId);
            ProfilingManager.getInstance().addTask("slide_show",
                    currentStoryId + "_" + lastIndex);

        }

        final int pos = position;

        IASCore.getInstance().getStoriesRepository(storyType).openStory(currentStoryId);
        IASCore.getInstance().getListNotifier().changeStory(currentStoryId, storyType, listID);
        if (parentFragment != null && parentFragment.getParentFragment() instanceof StoriesDialogFragment) {
            if (Sizes.isTablet(parentFragment.getContext())) {
                ((StoriesDialogFragment) parentFragment.getParentFragment()).changeStory(position);
            }
        }
        storiesRepository.setCurrentStory(currentStoryId);
        currentSlideIndex = lastIndex;
        parentFragment.showGuardMask(600);
        new Thread(new Runnable() {
            @Override
            public void run() {
                newStoryTask(pos);
                if (storiesIds != null && storiesIds.size() > pos) {
                    changeStory();
                }
            }
        }).start();
    }

    public void storyClick() {
        parentFragment.showGuardMask(300);
    }

    void changeStory() {
        IStatisticV1Repository statisticV1Repository = IASCore.getInstance().statisticV1Repository;
        if (storyType == StoryType.COMMON)
            statisticV1Repository.addStatisticEvent(
                    new StoryIdSlideIndex(
                            currentStoryId,
                            currentSlideIndex
                    )
            );
        List<Integer> lst = new ArrayList<>();
        lst.add(currentStoryId);
        if (storyType == StoryType.COMMON)
            statisticV1Repository.setViewedStoryIds(lst);
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

    public String getFeedSlug() {
        return feedId;
    }

    private String feedId;
    private String feedSlug;

    private void sendStatBlock(boolean hasCloseEvent, String whence, int id) {

        IStoriesRepository storiesRepository = IASCore.getInstance().getStoriesRepository(storyType);
        IPreviewStoryDTO story = storiesRepository.getStoryPreviewById(id);
        IPreviewStoryDTO lastStory = null;
        int lastStoryLastIndex = 0;
        if (lastPos >= 0 && hasCloseEvent) {
            lastStory = storiesRepository.getStoryPreviewById(storiesIds.get(lastPos));
            lastStoryLastIndex = storiesRepository.getStoryLastIndex(storiesIds.get(lastPos));
        }
        int lastIndex = storiesRepository.getStoryLastIndex(id);
        if (story == null) return;
        StatisticV2Manager.getInstance().sendCurrentState();
        if (lastStory != null) {
            StatisticV2Manager.getInstance().sendCloseStory(
                    lastStory.getId(),
                    whence,
                    lastStoryLastIndex,
                    story.getSlidesCount(),
                    feedId
            );
        }
        StatisticV2Manager.getInstance().sendViewStory(id, whence, feedId);
        StatisticV2Manager.getInstance().sendOpenStory(id, whence, feedId);
        StatisticV2Manager.getInstance().createCurrentState(story.getId(), lastIndex, feedId);
    }

    public void shareComplete(boolean shared) {
        ScreensManager.getInstance().setTempShareStatus(shared);
    }

    void resumeWithShareId() {
        synchronized (subscribers) {
            for (ReaderPageManager pageManager : subscribers) {
                pageManager.unlockShareButton();
            }
        }
        if (ScreensManager.getInstance().getOldTempShareId() != null) {
            ReaderPageManager rm = getSubscriberByStoryId(ScreensManager.getInstance().getOldTempShareStoryId());
            if (rm != null) {
                rm.shareComplete("" + ScreensManager.getInstance().getOldTempShareStoryId(),
                        true);
            }
        } else if (ScreensManager.getInstance().getTempShareId() != null) {
            ReaderPageManager rm = getSubscriberByStoryId(ScreensManager.getInstance().getTempShareStoryId());
            if (rm != null) {
                rm.shareComplete("" + ScreensManager.getInstance().getTempShareId(),
                        ScreensManager.getInstance().getTempShareStatus());
            }
        }
        ScreensManager.getInstance().clearShareIds();
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

    public void setParentFragment(StoriesFragment parentFragment) {
        this.parentFragment = parentFragment;
    }


    public int startedSlideInd;
    public int firstStoryId = -1;

    public void cleanFirst() {
        Bundle bundle = parentFragment.getArguments();
        bundle.remove("slideIndex");
        parentFragment.setArguments(bundle);
        startedSlideInd = 0;
        firstStoryId = -1;
    }

    private StoriesFragment parentFragment;
    private HashSet<ReaderPageManager> subscribers = new HashSet<>();

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

    public void nextStory(int action) {
        parentFragment.nextStory(action);
    }

    public void prevStory(int action) {
        parentFragment.prevStory(action);
    }


    public void defaultTapOnLink(String url) {
        parentFragment.defaultUrlClick(url);
    }

    public void pauseCurrent(boolean withBackground) {
        if (getCurrentSubscriber() != null)
            getCurrentSubscriber().pauseSlide(withBackground);
        StatisticV2Manager.getInstance().pauseStoryEvent(withBackground);
    }

    public void resumeCurrent(boolean withBackground) {
        if (getCurrentSubscriber() != null)
            getCurrentSubscriber().resumeSlide(withBackground);
        if (withBackground) {
            IASCore.getInstance().statisticV1Repository.refreshTimer();
        }
        StatisticV2Manager.getInstance().resumeStoryEvent(withBackground);
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
