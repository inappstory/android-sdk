package com.inappstory.sdk.stories.ui.reader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.ProfilingManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ReaderManager {

    private String listID;
    public Story.StoryType storyType;
    public StoriesContentFragment host;

    public SourceType source = SourceType.SINGLE;

    public ReaderManager() {
    }

    public ReaderManager(String listID,
                         String feedId,
                         String feedSlug,
                         Story.StoryType storyType,
                         SourceType source,
                         int latestShowStoryAction) {
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
        if (InAppStoryService.getInstance() == null || InAppStoryService.getInstance().getDownloadManager() == null)
            return;
        if (lastSentId == storyId) return;
        lastSentId = storyId;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, storyType);
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
        Activity activity = parentFragment.getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    public void unsubscribeClicks() {
        Activity activity = parentFragment.getActivity();
        if (activity instanceof StoriesActivity) {
            ((StoriesActivity) activity).unsubscribeClicks();
        }
    }

    public void subscribeClicks() {
        Activity activity = parentFragment.getActivity();
        if (activity instanceof StoriesActivity) {
            ((StoriesActivity) activity).subscribeClicks();
        }
    }


    public void swipeUp(int position) {
        int storyId = storiesIds.get(position);
        ReaderPageManager manager = getSubscriberByStoryId(storyId);
        if (manager != null)
            manager.swipeUp();
    }

    public void showGoods(
            String skusString,
            String widgetId,
            final ShowGoodsCallback showGoodsCallback,
            SlideData slideData
    ) {
        BaseReaderScreen screen = getReaderScreen();
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
        FragmentManager fragmentManager = screen.getStoriesReaderFragmentManager();
        if (fragmentManager.findFragmentById(R.id.ias_outer_top_container) != null) {
            showGoodsCallback.goodsIsCanceled(widgetId);
            Log.d("InAppStory_SDK_error", "Top container is busy");
            return;
        }
        if (screen instanceof ShowGoodsCallback) {
            screen.setShowGoodsCallback(showGoodsCallback);
            ((ShowGoodsCallback) screen).goodsIsOpened();
        }
        ScreensManager.getInstance().showGoods(
                skusString,
                screen,
                widgetId,
                slideData
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
            InAppStoryService service = InAppStoryService.getInstance();
            if (service != null)
                service.isShareProcess(false);
        }
    }

    public void removeStoryFromFavorite(int storyId) {
        ReaderPageManager pageManager = getSubscriberByStoryId(storyId);
        if (pageManager != null) pageManager.removeStoryFromFavorite();
    }

    public void showSingleStory(final int storyId, final int slideIndex) {
        if (InAppStoryService.isNull()) return;
        if (storyType == Story.StoryType.COMMON)
            OldStatisticManager.getInstance().addLinkOpenStatistic();
        if (storiesIds.contains(storyId)) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Story.StoryType type = Story.StoryType.COMMON;
                    Story st = InAppStoryService.getInstance().getDownloadManager()
                            .getStoryById(storyId, type);
                    if (st != null) {
                        if (st.getSlidesCount() <= slideIndex) {
                            st.lastIndex = 0;
                        } else {
                            st.lastIndex = slideIndex;
                        }
                    }
                    latestShowStoryAction = ShowStory.ACTION_CUSTOM;
                    parentFragment.setCurrentItem(storiesIds.indexOf(storyId));
                }
            });
        } else {
            InAppStoryManager.getInstance().showStoryWithSlide(
                    storyId + "",
                    parentFragment.getContext(),
                    slideIndex,
                    parentFragment.getAppearanceSettings(),
                    storyType,
                    SourceType.SINGLE,
                    ShowStory.ACTION_CUSTOM
            );
        }
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
        if (InAppStoryService.isNull()) return;
        if (!InAppStoryService.getInstance().getDownloadManager().changePriority(
                storiesIds.get(pos),
                adds,
                storyType
        )) {
            host.forceFinish();
            return;
        }
        InAppStoryService.getInstance().getDownloadManager().addStoryTask(
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
        if (InAppStoryService.isNull()) return;
        sendStat(position, source);

        lastPos = position;
        lastSentId = 0;
        currentStoryId = storiesIds.get(position);
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(currentStoryId, storyType);
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

        InAppStoryService.getInstance().getListReaderConnector().changeStory(currentStoryId, listID);
        if (Sizes.isTablet()) {
            if (parentFragment.getParentFragment() instanceof StoriesDialogFragment) {
                ((StoriesDialogFragment) parentFragment.getParentFragment()).changeStory(position);
            }
        }
        InAppStoryService.getInstance().setCurrentId(currentStoryId);
        if (story != null) {
            currentSlideIndex = story.lastIndex;
        }
        parentFragment.showGuardMask(600);
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
        parentFragment.showGuardMask(300);
    }

    void changeStory() {
        if (storyType == Story.StoryType.COMMON)
            OldStatisticManager.getInstance().addStatisticBlock(currentStoryId,
                    currentSlideIndex);

        ArrayList<Integer> lst = new ArrayList<>();
        lst.add(currentStoryId);
        if (storyType == Story.StoryType.COMMON)
            OldStatisticManager.getInstance().previewStatisticEvent(lst);
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
        if (InAppStoryService.isNull()) return;
        Story story2 = InAppStoryService.getInstance()
                .getDownloadManager().getStoryById(id, storyType);
        if (story2 == null) return;
        StatisticManager.getInstance().sendCurrentState();
        if (hasCloseEvent) {
            Story story = InAppStoryService.getInstance()
                    .getDownloadManager().getStoryById(storiesIds.get(lastPos), storyType);
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

        ScreensManager.getInstance().shareCompleteListener().complete(true);

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

    public void setParentFragment(StoriesContentFragment parentFragment) {
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

    public BaseReaderScreen getReaderScreen() {
        return parentFragment.getStoriesReader();
    }

    private StoriesContentFragment parentFragment;
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
        StatisticManager.getInstance().pauseStoryEvent(withBackground);
    }

    public void resumeCurrent(boolean withBackground) {
        if (getCurrentSubscriber() != null)
            getCurrentSubscriber().resumeSlide(withBackground);
        if (withBackground && OldStatisticManager.getInstance() != null) {
            OldStatisticManager.getInstance().refreshTimer();
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
