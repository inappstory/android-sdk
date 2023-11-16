package com.inappstory.sdk.stories.ui.reader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.inner.share.InnerShareFilesPrepare;
import com.inappstory.sdk.inner.share.ShareFilesPrepareCallback;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.share.IASShareManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.callbacks.ShareCallback;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPagerAdapter;
import com.inappstory.sdk.stories.utils.BackPressHandler;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;

import java.util.HashMap;
import java.util.List;

public class StoriesContentFragment extends Fragment
        implements BackPressHandler, ViewPager.OnPageChangeListener, OverlapFragmentObserver {

    public StoriesContentFragment() {
        super();
    }

    boolean isDestroyed = false;

    boolean created = false;


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
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null)
            service.isShareProcess(false);
        Context context = getContext();
        ShareCallback callback = CallbackManager.getInstance().getShareCallback();
        if (context == null) return;
        if (callback != null) {
            ScreensManager.getInstance().openOverlapContainerForShare(
                    context,
                    this,
                    slidePayload,
                    storyId,
                    slideIndex,
                    shareObject
            );
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

    }

    public ReaderManager readerManager;

    ReaderPagerAdapter outerViewPagerAdapter;
    View invMask;

    List<Integer> currentIds;
    boolean closeOnSwipe = true;
    boolean closeOnOverscroll = true;

    StoriesReaderAppearanceSettings appearanceSettings;
    StoriesReaderLaunchData launchData;

    private void closeFragment() {
        if (ScreensManager.getInstance() != null && ScreensManager.getInstance().currentStoriesReaderScreen != null)
            ScreensManager.getInstance().currentStoriesReaderScreen.forceFinish();
        else if (!Sizes.isTablet()) {
            Activity activity = getActivity();
            if (activity instanceof BaseReaderScreen)
                ((BaseReaderScreen) activity).forceFinish();
        }
    }


    @Override
    public void onDestroyView() {
        OldStatisticManager.getInstance().currentEvent = null;
        super.onDestroyView();
    }

    public void pause() {
        readerManager.pauseCurrent(true);
    }

    public void resume() {
        if (!created) {
            readerManager.resumeCurrent(true);
            readerManager.resumeWithShareId();
        }
        created = false;
    }

    @Override
    public void onPause() {
        if (!timerIsLocked)
            pause();
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setRetainInstance(true);
    }

    int ind;
    int readerAnimation;

    ReaderPager storiesViewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = requireContext();
        try {
            Bundle arguments = requireArguments();
            appearanceSettings = (StoriesReaderAppearanceSettings) arguments
                    .getSerializable(StoriesReaderAppearanceSettings.SERIALIZABLE_KEY);
            launchData = (StoriesReaderLaunchData) arguments
                    .getSerializable(StoriesReaderLaunchData.SERIALIZABLE_KEY);
            currentIds = launchData.getStoriesIds();
            readerAnimation = appearanceSettings.csStoryReaderAnimation();
            ind = launchData.getListIndex();

            Story.StoryType type = launchData.getType();
            readerManager = new ReaderManager(
                    launchData.getListUniqueId(),
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
            closeFragment();
            return new View(context);
        }




        RelativeLayout resView = new RelativeLayout(context);
        storiesViewPager = new ReaderPager(context);
        storiesViewPager.setHost(this);
        storiesViewPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        invMask = new View(context);
        invMask.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (InAppStoryService.isNull() || currentIds == null || currentIds.isEmpty()) {
            closeFragment();
            return;
        }
        readerManager.setParentFragment(this);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        storiesViewPager.setParameters(readerAnimation);
        source = launchData.getSourceType();
        outerViewPagerAdapter =
                new ReaderPagerAdapter(
                        getChildFragmentManager(),
                        source,
                        appearanceSettings,
                        currentIds, readerManager);
        storiesViewPager.setAdapter(outerViewPagerAdapter);
        storiesViewPager.addOnPageChangeListener(this);
        if (ind > 0) {
            storiesViewPager.setCurrentItem(ind);
        } else {
            try {
                onPageSelected(0);
            } catch (Exception e) {

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
            Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(currentIds.get(position), readerManager.storyType);
            if (story == null || story.disableClose) return;
            InAppStoryManager.closeStoryReader(CloseStory.SWIPE);
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


    private int getCurIndexById(int id) {
        if (InAppStoryService.getInstance().getDownloadManager() == null) return 0;
        Story st = InAppStoryService.getInstance().getDownloadManager().getStoryById(id, readerManager.storyType);
        return st == null ? 0 : st.lastIndex;
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        if (InAppStoryService.isNull()) return;
        if (state == ViewPager.SCROLL_STATE_DRAGGING)
            readerManager.latestShowStoryAction = ShowStory.ACTION_SWIPE;
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (getCurIndexById(readerManager.getCurrentStoryId()) ==
                    readerManager.getCurrentSlideIndex()) {
                readerManager.resumeCurrent(false);
            }

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
                    storiesViewPager.cubeAnimation = true;
                    storiesViewPager.setCurrentItem(storiesViewPager.getCurrentItem() + 1);
                } else {
                    InAppStoryManager.closeStoryReader(CloseStory.AUTO);
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
                    storiesViewPager.cubeAnimation = true;
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

                if (readerManager != null) readerManager.resumeWithShareId();
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
}
