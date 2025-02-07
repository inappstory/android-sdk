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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.ui.screens.ShareProcessHandler;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.core.ui.screens.storyreader.StoryReaderOverlapContainerDataForShare;
import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.inner.share.InnerShareFilesPrepare;
import com.inappstory.sdk.inner.share.ShareFilesPrepareCallback;
import com.inappstory.sdk.share.IASShareData;
import com.inappstory.sdk.share.IASShareManager;
import com.inappstory.sdk.share.ShareListener;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.callbacks.ShareCallback;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.events.GameCompleteEventObserver;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPagerAdapter;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.StoryShareBroadcastReceiver;

import java.util.ArrayList;
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
    boolean firstStoryLaunched = false;

    public String getReaderUniqueId() {
        return getLaunchData().getReaderUniqueId();
    }

    public void observeGameReader() {

    }

    public BaseStoryScreen getStoriesReader() {
        BaseStoryScreen screen = null;
        if (getActivity() instanceof BaseStoryScreen) {
            screen = (BaseStoryScreen) getActivity();
        } else if (getParentFragment() instanceof BaseStoryScreen) {
            screen = (BaseStoryScreen) getParentFragment();
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        List<ContentIdWithIndex> idWithIndices = readerManager.getStoriesIdsWithIndex();
        for (ContentIdWithIndex idWithIndex : idWithIndices) {
            ids.add(idWithIndex.id());
            indices.add(idWithIndex.index());
        }
        outState.putIntegerArrayList("storyIds", ids);
        outState.putIntegerArrayList("storyIndices", indices);

        Bundle args = getArguments();
        if (args != null) {
            args.putIntegerArrayList("storyIds", ids);
            args.putIntegerArrayList("storyIndices", indices);
        }
        //setArguments(args);

        super.onSaveInstanceState(outState);
    }

    private void restoreIndices(Bundle arguments) {
        ArrayList<Integer> ids = arguments.getIntegerArrayList("storyIds");
        ArrayList<Integer> indices = arguments.getIntegerArrayList("storyIndices");
        if (ids == null) {
            return;
        }
        List<ContentIdWithIndex> idWithIndices = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            idWithIndices.add(new ContentIdWithIndex(ids.get(i), indices.get(i)));
        }
        readerManager.setStoriesIdsWithIndex(idWithIndices);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }

    }

    private void launchFirstStory() {
        firstStoryLaunched = true;
        if (!created) return;
        LaunchStoryScreenData launchData = getLaunchData();

    }

    private void shareCustomOrDefault(final String slidePayload,
                                      final IASShareData shareObject,
                                      final int storyId,
                                      final int slideIndex) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(final @NonNull IASCore core) {
                final Context context = getContext();
                if (context == null) return;
                core.screensManager().getShareProcessHandler().isShareProcess(false);
                core.callbacksAPI().useCallback(IASCallbackType.SHARE_ADDITIONAL,
                        new UseIASCallback<ShareCallback>() {
                            @Override
                            public void use(@NonNull ShareCallback callback) {
                                core.screensManager()
                                        .getStoryScreenHolder()
                                        .openShareOverlapContainer(
                                                new StoryReaderOverlapContainerDataForShare()
                                                        .shareData(shareObject)
                                                        .slideIndex(slideIndex)
                                                        .storyId(storyId)
                                                        .slidePayload(slidePayload)
                                                        .shareListener(
                                                                new ShareListener() {
                                                                    @Override
                                                                    public void onSuccess(boolean shared) {
                                                                        getStoriesReader().timerIsUnlocked();
                                                                        readerManager.shareComplete(true);
                                                                    }

                                                                    @Override
                                                                    public void onCancel() {
                                                                        getStoriesReader().timerIsUnlocked();
                                                                        readerManager.resumeCurrent(false);
                                                                        readerManager.shareComplete(false);
                                                                    }

                                                                }
                                                        ),
                                                getStoriesReader()
                                                        .getScreenFragmentManager(),
                                                StoriesContentFragment.this
                                        );
                                getStoriesReader().timerIsLocked();
                                readerManager.pauseCurrent(false);
                            }

                            @Override
                            public void onDefault() {
                                new IASShareManager().shareDefault(
                                        StoryShareBroadcastReceiver.class,
                                        context,
                                        shareObject
                                );
                            }
                        });
            }
        });
    }

    @Override
    public void onPageSelected(int position) {
        if (isDestroyed) return;
        readerManager.onPageSelected(source, position);
        if (getArguments() != null) {
            getArguments().putInt("index", position);
        }
        LaunchStoryScreenData launchData = getLaunchData();
        if (launchData == null
                || launchData.getStoriesIds() == null
                || launchData.getStoriesIds().size() <= position) {
            return;
        }
        setDraggableAndCloseable(launchData.getStoriesIds().get(position), launchData.getType());
    }

    public void setDraggableAndCloseable(int storyId, ContentType type) {
        LaunchStoryScreenData launchData = getLaunchData();
        if (storiesViewPager == null || launchData == null) return;
        if (launchData.getStoriesIds().get(
                storiesViewPager.getCurrentItem()
        ) != storyId) return;
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) {
            return;
        }
        IReaderContent st = inAppStoryManager.iasCore()
                .contentHolder()
                .readerContent().getByIdAndType(
                        storyId,
                        type
                );
        if (st == null) return;
        BaseStoryScreen screen = getStoriesReader();
        if (screen != null) {
            screen.disableDrag(!appearanceSettings.csCloseOnSwipe() || st.disableClose() || st.hasSwipeUp());
            screen.disableClose(st.disableClose());
        }
    }


    public ReaderManager readerManager;

    ReaderPagerAdapter outerViewPagerAdapter;
    View invMask;

    List<Integer> currentIds;
    boolean closeOnSwipe = true;
    boolean closeOnOverscroll = true;

    private LaunchStoryScreenAppearance appearanceSettings;
    private LaunchStoryScreenData launchData;

    public void forceFinish() {
        BaseStoryScreen readerScreen = getStoriesReader();
        if (readerScreen != null)
            readerScreen.forceFinish();
    }


    @Override
    public void onDestroyView() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.statistic().storiesV1(launchData.getSessionId(),
                        new GetStatisticV1Callback() {
                            @Override
                            public void get(@NonNull IASStatisticStoriesV1 manager) {
                                manager.clearCurrentState();
                            }
                        }
                );
            }
        });
        if (readerManager != null) readerManager.unsubscribeFromAssets();
        super.onDestroyView();
    }

    public void pause() {
        if (readerManager != null)
            readerManager.pauseCurrent(true);
    }

    public void resume() {
        if (!created && readerManager != null) {
            readerManager.resumeCurrent(true);
            /*InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    ShareProcessHandler shareProcessHandler = core.screensManager().getShareProcessHandler();
                    if (shareProcessHandler != null && shareProcessHandler.shareCompleteListener() != null) {
                        readerManager.shareComplete(true);
                    }
                }
            });*/

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


    private LaunchStoryScreenData getLaunchData() {
        if (launchData == null) {
            Bundle arguments = requireArguments();
            launchData = (LaunchStoryScreenData) arguments
                    .getSerializable(LaunchStoryScreenData.SERIALIZABLE_KEY);
        }
        return launchData;
    }

    public LaunchStoryScreenAppearance getAppearanceSettings() {
        if (appearanceSettings == null) {
            Bundle arguments = requireArguments();
            appearanceSettings = (LaunchStoryScreenAppearance) arguments
                    .getSerializable(LaunchStoryScreenAppearance.SERIALIZABLE_KEY);
        }
        return appearanceSettings;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Context context = requireContext();
        FrameLayout resView = new FrameLayout(context);
        storiesViewPager = new ReaderPager(context);
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
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager()
                        .getGameScreenHolder()
                        .putGameObserver(
                                getReaderUniqueId(),
                                StoriesContentFragment.this
                        );
            }
        });
    }

    @Override
    public void onDetach() {
        if (readerManager != null && readerManager.hostIsEqual(this))
            readerManager.setHost(null);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager()
                        .getGameScreenHolder()
                        .removeGameObserver(
                                getReaderUniqueId()
                        );
            }
        });
        super.onDetach();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                try {
                    Bundle arguments = requireArguments();
                    LaunchStoryScreenAppearance appearanceSettings = getAppearanceSettings();
                    LaunchStoryScreenData launchData = getLaunchData();
                    currentIds = launchData.getStoriesIds();
                    readerAnimation = appearanceSettings.csStoryReaderAnimation();

                    if (currentIds == null || currentIds.isEmpty()) {
                        forceFinish();
                        return;
                    }
                    ContentType type = launchData.getType();
                    readerManager = new ReaderManager(
                            core,
                            launchData.getListUniqueId(),
                            launchData.shownOnlyNewStories(),
                            launchData.getSessionId(),
                            launchData.getFeed(),
                            launchData.getFeed(),
                            type,
                            launchData.getSourceType() != null ? launchData.getSourceType() : SourceType.SINGLE,
                            launchData.getFirstAction()
                    );

                    storiesViewPager.setHost(StoriesContentFragment.this);
                    readerManager.subscribeToAssets();
                    readerManager.setHost(StoriesContentFragment.this);
                    readerManager.setStoriesIds(currentIds);
                    readerManager.firstStoryId = currentIds.get(ind);

                    readerManager.startedSlideInd =
                            launchData.getSlideIndex() != null ?
                                    launchData.getSlideIndex() : 0;

                    closeOnSwipe = appearanceSettings.csCloseOnSwipe();
                    closeOnOverscroll = appearanceSettings.csCloseOnOverscroll();


                    requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    storiesViewPager.setParameters(readerAnimation);
                    source = getLaunchData().getSourceType();
                    restoreIndices(arguments);
                    outerViewPagerAdapter =
                            new ReaderPagerAdapter(
                                    getChildFragmentManager(),
                                    source,
                                    getAppearanceSettings(),
                                    ((Rect) arguments.getParcelable("readerContainer")),
                                    currentIds,
                                    readerManager
                            );
                    restoreIndices(arguments);
                    storiesViewPager.setAdapter(outerViewPagerAdapter);
                    storiesViewPager.addOnPageChangeListener(StoriesContentFragment.this);

                    ind = launchData.getListIndex();
                    if (ind > 0) {
                        storiesViewPager.setCurrentItem(ind);
                    } else {
                        try {
                            onPageSelected(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    launchData.clearSingleTimeParameters();
                    created = true;
                } catch (Exception e) {
                    forceFinish();
                }

            }

            @Override
            public void error() {
                forceFinish();
            }
        });
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

    public void swipeCloseEvent(final int position, boolean check) {
        if (check) {
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    IReaderContent story = core.contentHolder()
                            .readerContent()
                            .getByIdAndType(
                                    currentIds.get(position), readerManager.contentType
                            );
                    if (story == null || story.disableClose()) return;
                    BaseStoryScreen screen = getStoriesReader();
                    if (screen != null)
                        screen.closeWithAction(CloseStory.SWIPE);
                }
            });

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
            readerManager.pauseCurrent(false);
        }
    }

    private int getCurIndexById(int id) {
        return readerManager.getByIdAndIndex(id).index();
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        readerManager.onPageScrollStateChanged(state);
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
                    BaseStoryScreen screen = getStoriesReader();
                    if (screen != null)
                        screen.closeWithAction(CloseStory.AUTO);
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
    public void gameComplete(GameCompleteEvent event) {
        readerManager.gameComplete(
                event.getGameState(),
                event.getStoryId(),
                event.getSlideIndex()
        );
    }
}
