package com.inappstory.sdk.stories.ui.reader;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.inner.share.InnerShareFilesPrepare;
import com.inappstory.sdk.inner.share.ShareFilesPrepareCallback;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.share.IASShareManager;
import com.inappstory.sdk.share.ShareListener;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.ShareCallback;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.events.GameCompleteEventObserver;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.GetOldStatisticManagerCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPagerAdapter;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;

import java.util.HashMap;
import java.util.List;

public class StoriesContentFragment extends Fragment
        implements
        IASBackPressHandler,
        ViewPager.OnPageChangeListener,
        OverlapFragmentObserver,
        GameCompleteEventObserver {

    public StoriesContentFragment() {
        super();
    }

    boolean isDestroyed = false;

    boolean created = false;

    public String getReaderUniqueId() {
        return getLaunchData().getReaderUniqueId();
    }

    public void observeGameReader() {

    }

    public BaseReaderScreen getStoriesReader() {
        BaseReaderScreen screen = null;
        if (getActivity() instanceof BaseReaderScreen) {
            screen = (BaseReaderScreen) getActivity();
        } else if (getParentFragment() instanceof BaseReaderScreen) {
            screen = (BaseReaderScreen) getParentFragment();
        }
        return screen;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (isDestroyed) return;
        if (positionOffset == 0f) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    invMask.setVisibility(View.GONE);
                }
            }, 400);
        } else {
            if (invMask.getVisibility() != View.VISIBLE) {
                invMask.setVisibility(View.VISIBLE);
            }
        }
        storiesViewPager.pageScrolled(positionOffset);
    }

    public void removeStoryFromFavorite(int id) {
        if (readerManager != null)
            readerManager.removeStoryFromFavorite(id);
    }


    public void removeAllStoriesFromFavorite() {
        if (readerManager != null)
            readerManager.removeAllStoriesFromFavorite();
    }

    public void showShareView(final InnerShareData shareData,
                              final int storyId, final int slideIndex) {
        Context context = getContext();
        if (shareData.getFiles().isEmpty()) {
            shareCustomOrDefault(
                    shareData.getPayload(),
                    new IASShareData(
                            shareData.getText(),
                            shareData.getPayload()
                    ),
                    storyId,
                    slideIndex
            );
        } else {
            new InnerShareFilesPrepare().prepareFiles(context, new ShareFilesPrepareCallback() {
                @Override
                public void onPrepared(List<String> files) {
                    shareCustomOrDefault(
                            shareData.getPayload(),
                            new IASShareData(
                                    shareData.getText(),
                                    files,
                                    shareData.getPayload()
                            ),
                            storyId,
                            slideIndex
                    );
                }
            }, shareData.getFiles());
        }
    }

    private void shareCustomOrDefault(String slidePayload,
                                      IASShareData shareObject,
                                      int storyId,
                                      int slideIndex) {
        final InAppStoryService service = InAppStoryService.getInstance();
        if (service != null)
            service.isShareProcess(false);
        Context context = getContext();
        ShareCallback callback = CallbackManager.getInstance().getShareCallback();
        if (context == null) return;
        if (callback != null) {
            ScreensManager.getInstance().openOverlapContainerForShare(
                    new ShareListener() {
                        @Override
                        public void onSuccess(boolean shared) {
                            getStoriesReader().timerIsUnlocked();
                            readerManager.resumeCurrent(false);
                            readerManager.shareComplete(shared);
                        }

                        @Override
                        public void onCancel() {
                            getStoriesReader().timerIsUnlocked();
                            readerManager.resumeCurrent(false);
                            readerManager.shareComplete(false);
                        }

                    },
                    getStoriesReader().getStoriesReaderFragmentManager(),
                    this,
                    slidePayload,
                    storyId,
                    slideIndex,
                    shareObject
            );
            getStoriesReader().timerIsLocked();
            readerManager.pauseCurrentForced(false);
        } else {
            new IASShareManager().shareDefault(
                    StoryShareBroadcastReceiver.class,
                    context,
                    shareObject
            );
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (isDestroyed) return;
        readerManager.onPageSelected(source, position);
        if (getArguments() != null) {
            getArguments().putInt("index", position);
        }
        StoriesReaderLaunchData launchData = getLaunchData();
        if (launchData == null
                || launchData.getStoriesIds() == null
                || launchData.getStoriesIds().size() <= position) {
            return;
        }
        disableDrag(launchData.getStoriesIds().get(position), launchData.getType());
    }

    public void disableDrag(int storyId, Story.StoryType type) {
        StoriesReaderLaunchData launchData = getLaunchData();
        if (storiesViewPager == null || launchData == null) return;
        if (launchData.getStoriesIds().get(
                storiesViewPager.getCurrentItem()
        ) != storyId) return;
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) {
            return;
        }
        Story st = service.getStoryDownloadManager().getStoryById(
                storyId,
                type
        );
        if (st == null) return;
        BaseReaderScreen screen = getStoriesReader();
        if (screen != null) screen.disableDrag(st.disableClose || st.hasSwipeUp());
    }


    public ReaderManager readerManager;

    ReaderPagerAdapter outerViewPagerAdapter;
    View invMask;

    List<Integer> currentIds;
    boolean closeOnSwipe = true;
    boolean closeOnOverscroll = true;

    private StoriesReaderAppearanceSettings appearanceSettings;
    private StoriesReaderLaunchData launchData;

    public void forceFinish() {
        BaseReaderScreen readerScreen = getStoriesReader();
        if (readerScreen != null)
            readerScreen.forceFinish();
    }


    @Override
    public void onDestroyView() {
        OldStatisticManager.useInstance(
                launchData.getSessionId(),
                new GetOldStatisticManagerCallback() {
                    @Override
                    public void get(@NonNull OldStatisticManager manager) {
                        manager.currentEvent = null;
                    }
                }
        );
        if (readerManager != null) readerManager.unsubscribeFromAssets();
        super.onDestroyView();
    }

    public void pause() {
        if (readerManager != null)
            readerManager.pauseCurrentForced(true);
    }

    public void disableClicksSlideChange() {
        storiesViewPager.clicksDisabledSlideChange = true;
    }

    public boolean clicksIsDisabled() {
        return storiesViewPager.clicksDisabled();
    }

    public void enableClicksSlideChange() {
        storiesViewPager.clicksDisabledSlideChange = false;
    }

    public void resume() {
        if (!created && readerManager != null) {
            readerManager.resumeCurrent(true);
            if (ScreensManager.getInstance().shareCompleteListener() != null) {
                Boolean shareStatus = ScreensManager.getInstance().getTempShareStatus();
                readerManager.shareComplete(shareStatus != null ? shareStatus : false);
            }
        }
        created = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setRetainInstance(true);
    }

    int ind;
    int readerAnimation;

    ReaderPager storiesViewPager;


    private StoriesReaderLaunchData getLaunchData() {
        if (launchData == null) {
            Bundle arguments = requireArguments();
            launchData = (StoriesReaderLaunchData) arguments
                    .getSerializable(StoriesReaderLaunchData.SERIALIZABLE_KEY);
        }
        return launchData;
    }

    public StoriesReaderAppearanceSettings getAppearanceSettings() {
        if (appearanceSettings == null) {
            Bundle arguments = requireArguments();
            appearanceSettings = (StoriesReaderAppearanceSettings) arguments
                    .getSerializable(StoriesReaderAppearanceSettings.SERIALIZABLE_KEY);
        }
        return appearanceSettings;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = requireContext();
        try {
            Bundle arguments = requireArguments();
            StoriesReaderAppearanceSettings appearanceSettings = getAppearanceSettings();
            StoriesReaderLaunchData launchData = getLaunchData();
            currentIds = launchData.getStoriesIds();
            readerAnimation = appearanceSettings.csStoryReaderAnimation();
            ind = launchData.getListIndex();

            Story.StoryType type = launchData.getType();
            readerManager = new ReaderManager(
                    launchData.getListUniqueId(),
                    launchData.shownOnlyNewStories(),
                    launchData.getSessionId(),
                    launchData.getFeed(),
                    launchData.getFeed(),
                    type,
                    launchData.getSourceType() != null ? launchData.getSourceType() : SourceType.SINGLE,
                    launchData.getFirstAction()
            );
            if (currentIds != null && !currentIds.isEmpty()) {
                readerManager.setStoriesIds(currentIds);
                readerManager.firstStoryId = currentIds.get(ind);
                readerManager.startedSlideInd = arguments.getInt("slideIndex", 0);
            }

            closeOnSwipe = appearanceSettings.csCloseOnSwipe();
            closeOnOverscroll = appearanceSettings.csCloseOnOverscroll();

            created = true;
        } catch (Exception e) {
            forceFinish();
            return new View(context);
        }


        FrameLayout resView = new FrameLayout(context);
        storiesViewPager = new ReaderPager(context);
        storiesViewPager.setHost(this);
        storiesViewPager.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT,
                MATCH_PARENT));
        invMask = new View(context);
        invMask.setLayoutParams(new FrameLayout.LayoutParams(MATCH_PARENT,
                MATCH_PARENT));
        invMask.setVisibility(View.GONE);
        storiesViewPager.setId(R.id.ias_stories_pager);
        invMask.setId(R.id.ias_inv_mask);
        invMask.setClickable(true);
        storiesViewPager.setElevation(4);
        invMask.setElevation(10);
        resView.addView(storiesViewPager);
        resView.addView(invMask);
        return resView;//inflater.inflate(R.layout.cs_fragment_stories, container, false);
    }

    SourceType source = SourceType.SINGLE;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (readerManager != null) readerManager.setHost(this);
        ScreensManager.getInstance().putGameObserver(getReaderUniqueId(), this);
    }

    @Override
    public void onDetach() {
        if (readerManager != null)
            readerManager.clearHost(this);
        ScreensManager.getInstance().removeGameObserver(getReaderUniqueId());
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (InAppStoryService.isNull() || currentIds == null || currentIds.isEmpty()) {
            forceFinish();
            return;
        }
        if (readerManager == null) return;
        readerManager.subscribeToAssets();
        readerManager.setHost(this);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        storiesViewPager.setParameters(readerAnimation);
        source = getLaunchData().getSourceType();
        outerViewPagerAdapter =
                new ReaderPagerAdapter(
                        getChildFragmentManager(),
                        source,
                        getAppearanceSettings(),
                        ((Rect) getArguments().getParcelable("readerContainer")),
                        currentIds,
                        readerManager
                );
        storiesViewPager.setAdapter(outerViewPagerAdapter);
        storiesViewPager.addOnPageChangeListener(this);
        if (ind > 0) {
            storiesViewPager.setCurrentItem(ind);
        } else {
            try {
                onPageSelected(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void swipeUpEvent() {
        swipeUpEvent(storiesViewPager.getCurrentItem());
    }

    public void swipeUpEvent(int position) {
        readerManager.swipeUp(position);
    }

    public void swipeDownEvent() {
        swipeDownEvent(storiesViewPager.getCurrentItem());
    }

    public void swipeDownEvent(int position) {
        swipeCloseEvent(position, closeOnSwipe);
    }


    public void swipeLeftEvent(int position) {
        swipeCloseEvent(position, closeOnOverscroll);
    }

    public void swipeRightEvent(int position) {
        swipeCloseEvent(position, closeOnOverscroll);
    }

    public void swipeCloseEvent(int position, boolean check) {
        if (check) {
            Story story = InAppStoryService.getInstance().getStoryDownloadManager()
                    .getStoryById(currentIds.get(position), readerManager.storyType);
            if (story == null || story.disableClose) return;
            ScreensManager.getInstance().closeStoryReader(CloseStory.SWIPE);
        }
    }

    void timerIsLocked() {
        timerIsLocked = true;
    }

    void timerIsUnlocked() {
        timerIsLocked = false;
    }

    boolean timerIsLocked = false;

    @Override
    public void onResume() {
        if (!timerIsLocked)
            resume();
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (readerManager != null) {
            readerManager.pauseCurrentForced(false);
        }
    }

    private int getCurIndexById(int id) {
        if (InAppStoryService.getInstance().getStoryDownloadManager() == null) return 0;
        Story st = InAppStoryService.getInstance().getStoryDownloadManager().getStoryById(id, readerManager.storyType);
        return st == null ? 0 : st.lastIndex;
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_DRAGGING)
            readerManager.latestShowStoryAction = ShowStory.ACTION_SWIPE;
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (getCurIndexById(readerManager.getCurrentStoryId()) ==
                    readerManager.getCurrentSlideIndex()) {
                readerManager.resumeCurrent(false);
            }
            readerManager.clearInactiveTimers();
        }
        readerManager.setCurrentSlideIndex(getCurIndexById(readerManager.getCurrentStoryId()));

    }

    public void setCurrentItem(int ind) {
        if (storiesViewPager.getAdapter() != null &&
                storiesViewPager.getAdapter().getCount() > ind) {
            storiesViewPager.setCurrentItem(ind);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    void defaultUrlClick(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse(url));
        try {
            getActivity().startActivity(i);
            getActivity().overridePendingTransition(R.anim.popup_show, R.anim.empty_animation);
        } catch (Exception e) {

        }
    }


    public void showGuardMask(int delay) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                invMask.setVisibility(View.VISIBLE);
            }
        });
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                invMask.setVisibility(View.GONE);
            }
        }, delay);
    }

    public void nextStory(int action) {
        readerManager.latestShowStoryAction = action;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (storiesViewPager.getAdapter() != null &&
                        storiesViewPager.getCurrentItem() < storiesViewPager.getAdapter().getCount() - 1) {
                    storiesViewPager.clicksDisabledAnimation = true;
                    storiesViewPager.setCurrentItem(storiesViewPager.getCurrentItem() + 1);
                } else {
                    ScreensManager.getInstance().closeStoryReader(CloseStory.AUTO);
                }
            }
        });
    }

    public void prevStory(int action) {
        readerManager.latestShowStoryAction = action;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (storiesViewPager.getCurrentItem() > 0) {
                    storiesViewPager.setCurrentItem(storiesViewPager.getCurrentItem() - 1);
                    storiesViewPager.clicksDisabledAnimation = true;
                } else {
                    readerManager.restartCurrentStory();
                }
            }
        });
    }

    @Override
    public void closeView(final HashMap<String, Object> data) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                viewIsClosed();
                boolean shared = false;
                if (data.containsKey("shared")) shared = (boolean) data.get("shared");
                if (!shared)
                    resume();
            }
        });

    }

    @Override
    public void viewIsOpened() {

    }

    @Override
    public void viewIsClosed() {
        readerManager.unlockShareButton();
    }

    @Override
    public void gameComplete(GameCompleteEvent event) {
        readerManager.gameComplete(
                event.getGameState(),
                event.getStoryId(),
                event.getSlideIndex()
        );
    }
}
