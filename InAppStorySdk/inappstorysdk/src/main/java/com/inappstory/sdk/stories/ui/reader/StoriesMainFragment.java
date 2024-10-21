package com.inappstory.sdk.stories.ui.reader;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASStatisticV1;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.dataholders.IReaderContent;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.core.utils.CallbackTypesConverter;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.statistic.IASStatisticV2Impl;
import com.inappstory.sdk.stories.ui.reader.animations.DisabledReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.FadeReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.HandlerAnimatorListenerAdapter;
import com.inappstory.sdk.stories.ui.reader.animations.PopupReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.ReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.ZoomReaderCenterAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.ZoomReaderFromCellAnimation;
import com.inappstory.sdk.stories.ui.utils.FragmentAction;
import com.inappstory.sdk.stories.ui.widgets.elasticview.ElasticDragDismissFrameLayout;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;


public abstract class StoriesMainFragment extends Fragment implements
        BaseStoryScreen,
        IASBackPressHandler,
        ShowGoodsCallback {

    @Override
    public void disableDrag(boolean disable) {
        boolean draggable = !Sizes.isTablet(getContext()) &&
                (appearanceSettings == null || appearanceSettings.csIsDraggable());
        if (draggableFrame != null)
            draggableFrame.dragIsDisabled(draggable && disable);

    }


    ElasticDragDismissFrameLayout draggableFrame;
    View blockView;
    View backTintView;
    View animatedContainer;


    ShowGoodsCallback currentGoodsCallback = null;

    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    @Override
    public void resumeScreen() {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment fragment) {
                if (fragment.readerManager != null)
                    fragment.readerManager.resumeCurrent(true);
            }
        });
    }

    @Override
    public void pauseScreen() {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment fragment) {
                if (fragment.readerManager != null)
                    fragment.readerManager.pauseCurrent(true);
            }
        });
    }

    public static StoriesMainFragment newInstance(Bundle bundle, Context context) {
        StoriesMainFragment fragment;
        if (Sizes.isTablet(context)) fragment = new StoriesMainTabletFragment();
        else fragment = new StoriesMainPhoneFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void goodsIsOpened() {
        timerIsLocked();
        if (currentGoodsCallback != null)
            currentGoodsCallback.goodsIsOpened();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void goodsIsClosed(String widgetId) {
        timerIsUnlocked();
        if (currentGoodsCallback != null)
            currentGoodsCallback.goodsIsClosed(widgetId);
        currentGoodsCallback = null;
    }

    @Override
    public void goodsIsCanceled(String widgetId) {
        if (currentGoodsCallback != null)
            currentGoodsCallback.goodsIsCanceled(widgetId);
        currentGoodsCallback = null;
    }

    private void useContentFragment(FragmentAction<StoriesContentFragment> action) {
        if (!isAdded()) {
            return;
        }
        if (action != null) {
            try {
                Fragment fragmentById = getChildFragmentManager().findFragmentByTag("STORIES_FRAGMENT");
                if (fragmentById instanceof StoriesContentFragment) {
                    action.invoke((StoriesContentFragment) fragmentById);
                    return;
                }
            } catch (IllegalStateException e) {

            }
            action.error();
        }
    }


    public void unsubscribeClicks() {
        draggableFrame.removeListener(chromeFader);
    }

    public void subscribeClicks() {
        draggableFrame.addListener(chromeFader);
    }

    @Override
    public void onPause() {
        super.onPause();
        unsubscribeClicks();
    }

    private ReaderAnimation setStartAnimations() {
        Point screenSize = Sizes.getScreenSize(getContext());
        switch (appearanceSettings.csStoryReaderPresentationStyle()) {
            case AppearanceManager.DISABLE:
                return new DisabledReaderAnimation().setAnimations(true);
            case AppearanceManager.FADE:
                return new FadeReaderAnimation(animatedContainer).setAnimations(true);
            case AppearanceManager.POPUP:
                return new PopupReaderAnimation(animatedContainer, screenSize.y, 0f).setAnimations(true);
            default:
                final StoryItemCoordinates[] coordinates = {null};
                InAppStoryManager.useCore(new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull IASCore core) {
                        coordinates[0] = core.screensManager().getStoryScreenHolder().coordinates();
                    }
                });

                float pivotX = -screenSize.x / 2f;
                float pivotY = -screenSize.y / 2f;

                if (coordinates[0] != null) {
                    pivotX += coordinates[0].x();
                    pivotY += coordinates[0].y();
                    return new ZoomReaderFromCellAnimation(animatedContainer,
                            pivotX,
                            pivotY
                    ).setAnimations(true);
                } else {
                    return new ZoomReaderCenterAnimation(animatedContainer,
                            -pivotX,
                            -pivotY
                    ).setAnimations(true);
                }
        }
    }

    private void createStoriesFragment(Bundle savedInstanceState, Rect readerContainer) {
        if (savedInstanceState == null) {
            StoriesContentFragment storiesContentFragment = new StoriesContentFragment();
            Bundle args = new Bundle();
            args.putAll(getArguments());
            args.putParcelable("readerContainer", readerContainer);
            storiesContentFragment.setArguments(args);
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction t = fragmentManager.beginTransaction()
                    .replace(R.id.stories_fragments_layout, storiesContentFragment, "STORIES_FRAGMENT");
            t.addToBackStack("STORIES_FRAGMENT");
            t.commitAllowingStateLoss();
        }

        disableDrag(false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        clearDialog();
        clearOverlap();
    }

    private void clearDialog() {
        FragmentManager parentFragmentManager = getScreenFragmentManager();
        Fragment oldFragment =
                parentFragmentManager.findFragmentById(R.id.ias_dialog_container);
        if (oldFragment != null) {
            parentFragmentManager.beginTransaction().replace(R.id.ias_dialog_container, new Fragment()).commit();
            parentFragmentManager.popBackStack();
        }
    }

    private void clearOverlap() {
        FragmentManager parentFragmentManager = getScreenFragmentManager();
        Fragment oldFragment =
                parentFragmentManager.findFragmentById(R.id.ias_outer_top_container);
        if (oldFragment != null) {
            parentFragmentManager.beginTransaction().replace(R.id.ias_outer_top_container, new Fragment()).commit();
            parentFragmentManager.popBackStack();
        }
    }

    public void startAnim(final Bundle savedInstanceState, final Rect readerContainer) {
        try {
            Activity activity = requireActivity();
            final Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            isAnimation = true;
            setStartAnimations()
                    .setListener(new HandlerAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationProgress(float progress) {
                            super.onAnimationProgress(progress);
                            openAnimationProgress(progress);
                        }

                        @Override
                        public void onAnimationStart() {
                            super.onAnimationStart();
                            openAnimationStart();
                        }

                        @Override
                        public void onAnimationEnd() {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            isAnimation = false;
                            createStoriesFragment(savedInstanceState, readerContainer);
                        }
                    })
                    .start();
        } catch (Exception e) {
            forceFinish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        subscribeClicks();
    }


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        Bundle arguments = requireArguments();
        appearanceSettings = (LaunchStoryScreenAppearance) arguments.getSerializable(
                LaunchStoryScreenAppearance.SERIALIZABLE_KEY
        );
        launchData = (LaunchStoryScreenData) arguments.getSerializable(
                LaunchStoryScreenData.SERIALIZABLE_KEY
        );
        View view = inflater.inflate(R.layout.cs_mainscreen_stories_draggable, container, false);
        draggableFrame = view.findViewById(R.id.draggable_frame);
        blockView = view.findViewById(R.id.blockView);
        backTintView = view.findViewById(R.id.background);
        animatedContainer = view.findViewById(R.id.animatedContainer);

        if (savedInstanceState == null) {
            animatedContainer.setAlpha(0f);
        } else {
            reInitUI();
        }
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager().getStoryScreenHolder().subscribeScreen(StoriesMainFragment.this);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) {
            forceFinish();
            return;
        }
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager().getOpenReader(ScreenType.STORY)
                        .onHideStatusBar(getActivity());
            }
        });
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(getActivity()) {
            @Override
            public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {
                super.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels);
                StoriesMainFragment.this.onDrag(rawOffset);
            }

            @Override
            public void onDragDismissed() {
                animateFirst = true;
                closeWithAction(CloseStory.SWIPE);
            }

            @Override
            public void onDragDropped() {
            }

            @Override
            public void touchPause() {
                useContentFragment(new StoriesContentFragmentAction() {
                    @Override
                    public void invoke(StoriesContentFragment fragment) {
                        if (fragment.readerManager != null)
                            fragment.readerManager.pauseCurrent(false);
                    }
                });
            }

            @Override
            public void touchResume() {
                useContentFragment(new StoriesContentFragmentAction() {
                    @Override
                    public void invoke(StoriesContentFragment fragment) {
                        if (fragment.readerManager != null)
                            fragment.readerManager.resumeCurrent(false);
                    }
                });

            }

            @Override
            public void swipeDown() {
                useContentFragment(new StoriesContentFragmentAction() {
                    @Override
                    public void invoke(StoriesContentFragment fragment) {
                        fragment.swipeDownEvent();
                    }
                });
            }

            @Override
            public void swipeUp() {
                useContentFragment(new StoriesContentFragmentAction() {
                    @Override
                    public void invoke(StoriesContentFragment fragment) {
                        fragment.swipeUpEvent();
                    }
                });
            }
        };
        backTintView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outsideClick();
            }
        });
        final Context context = view.getContext();

        draggableFrame.post(new Runnable() {
            @Override
            public void run() {
                final Rect readerContainer = new Rect();
                draggableFrame.getGlobalVisibleRect(readerContainer);
                final int height = draggableFrame.getHeight();
                final int width = draggableFrame.getWidth();

                useContentFragment(new FragmentAction<StoriesContentFragment>() {
                    @Override
                    public void invoke(StoriesContentFragment fragment) {
                        createStoriesFragment(savedInstanceState, readerContainer);
                    }

                    @Override
                    public void error() {
                        setLoaderFragment(savedInstanceState, readerContainer);
                        try {
                            startAnim(savedInstanceState, readerContainer);
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });

    }


    boolean animateFirst = true;
    boolean isAnimation = false;

    @Override
    public void closeWithAction(final int action) {
        if (closing) return;
        closing = true;
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        blockView.setVisibility(View.VISIBLE);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(final @NonNull IASCore core) {
                InAppStoryService service = core.inAppStoryService();
                service.getListReaderConnector().readerIsClosed();
                useContentFragment(new StoriesContentFragmentAction() {
                    @Override
                    public void invoke(StoriesContentFragment fragment) {
                        int storyId = fragment.readerManager.getCurrentStoryId();
                        final IReaderContent story = core
                                .contentHolder()
                                .readerContent()
                                .getByIdAndType(storyId, launchData.getType());
                        final int slideIndex = fragment.readerManager.getByIdAndIndex(storyId).index();
                        if (story != null) {
                            core.callbacksAPI().useCallback(
                                    IASCallbackType.CLOSE_STORY,
                                    new UseIASCallback<CloseStoryCallback>() {
                                        @Override
                                        public void use(@NonNull CloseStoryCallback callback) {
                                            callback.closeStory(
                                                    new SlideData(
                                                            StoryData.getStoryData(
                                                                    story,
                                                                    launchData.getFeed(),
                                                                    launchData.getSourceType(),
                                                                    launchData.getType()
                                                            ),
                                                            slideIndex,
                                                            story.slideEventPayload(slideIndex)
                                                    ),
                                                    new CallbackTypesConverter().getCloseTypeFromInt(action)
                                            );
                                        }
                                    });
                            String cause = IASStatisticV2Impl.AUTO;
                            switch (action) {
                                case CloseStory.CLICK:
                                    cause = IASStatisticV2Impl.CLICK;
                                    break;
                                case CloseStory.CUSTOM:
                                    cause = IASStatisticV2Impl.CUSTOM;
                                    break;
                                case -1:
                                    cause = IASStatisticV2Impl.BACK;
                                    break;
                                case CloseStory.SWIPE:
                                    cause = IASStatisticV2Impl.SWIPE;
                                    break;
                            }
                            core.statistic().v2().sendCloseStory(
                                    storyId,
                                    cause,
                                    slideIndex,
                                    story.slidesCount(),
                                    launchData.getFeed()
                            );
                        }
                    }
                });


            }
        });
        cleanReader();
        animateFirst = true;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                finishAfterTransition();
            }
        });
    }

    void finishAfterTransition() {
        if (animateFirst) {
            animateFirst = false;
            closeAnim();
        } else {
            try {
                requireActivity().getSupportFragmentManager().popBackStack();
            } catch (Exception ignored) {

            }
        }
    }

    int oldOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (orientationChangeIsLocked()) {
            oldOrientation = getActivity().getRequestedOrientation();
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager().getStoryScreenHolder().subscribeScreen(StoriesMainFragment.this);
            }
        });
    }

    boolean orientationChangeIsLocked() {
        return !Sizes.isTablet(getContext());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (orientationChangeIsLocked()) {
            getActivity().setRequestedOrientation(oldOrientation);
        }
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager().getStoryScreenHolder().unsubscribeScreen(StoriesMainFragment.this);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {

        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager()
                        .getOpenReader(ScreenType.STORY)
                        .onRestoreStatusBar(getActivity());
            }
        });

        super.onDestroyView();
    }

    private ReaderAnimation setFinishAnimations() {
        Point screenSize = Sizes.getScreenSize(getContext());
        switch (appearanceSettings.csStoryReaderPresentationStyle()) {
            case AppearanceManager.DISABLE:
                return new DisabledReaderAnimation().setAnimations(false);
            case AppearanceManager.FADE:
                return new FadeReaderAnimation(animatedContainer).setAnimations(false);
            case AppearanceManager.POPUP:
                return new PopupReaderAnimation(
                        animatedContainer,
                        draggableFrame.getY(),
                        screenSize.y
                ).setAnimations(false);
            default:
                final StoryItemCoordinates[] coordinates = {null};
                InAppStoryManager.useCore(new UseIASCoreCallback() {
                    @Override
                    public void use(@NonNull IASCore core) {
                        coordinates[0] = core.screensManager().getStoryScreenHolder().coordinates();
                    }
                });
                float pivotX = -screenSize.x / 2f;
                float pivotY = -screenSize.y / 2f;
                if (coordinates[0] != null) {
                    pivotX += coordinates[0].x();
                    pivotY += coordinates[0].y();
                    return new ZoomReaderFromCellAnimation(animatedContainer,
                            pivotX,
                            pivotY
                    ).setAnimations(false);
                } else {
                    return new ZoomReaderCenterAnimation(animatedContainer,
                            -pivotX,
                            -pivotY
                    ).setAnimations(false);
                }
        }
    }

    public void closeAnim() {
        try {
            isAnimation = true;
            setFinishAnimations()
                    .setListener(new HandlerAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationProgress(float progress) {
                            super.onAnimationProgress(progress);
                            closeAnimationProgress(progress);
                        }

                        @Override
                        public void onAnimationEnd() {
                            draggableFrame.setVisibility(View.GONE);
                            if (getActivity() != null)
                                getActivity().
                                        getWindow().
                                        clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            forceFinish();
                        }
                    })
                    .start();

            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.screensManager().getStoryScreenHolder().clearCoordinates();
                }
            });
        } catch (Exception e) {
            forceFinish();
        }

    }


    boolean cleaned = false;

    void cleanReader() {
        if (cleaned) return;
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.statistic().v1(
                        launchData.getSessionId(),
                        new GetStatisticV1Callback() {
                            @Override
                            public void get(@NonNull IASStatisticV1 manager) {
                                manager.closeStatisticEvent();
                            }
                        }
                );
                core.screensManager().getStoryScreenHolder().currentOpenedStoryId(0);
                cleaned = true;
            }
        });
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment fragment) {
                fragment.readerManager.refreshStoriesIds();
            }
        });
    }

    @Override
    public void forceFinish() {
        finishAfterTransition();
    }


    @Override
    public void removeStoryFromFavorite(final int id) {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment fragment) {
                fragment.removeStoryFromFavorite(id);
            }
        });
    }

    @Override
    public void removeAllStoriesFromFavorite() {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment fragment) {
                fragment.removeAllStoriesFromFavorite();
            }
        });
    }

    @Override
    public void timerIsLocked() {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment fragment) {
                fragment.timerIsLocked();
            }
        });
    }

    @Override
    public void timerIsUnlocked() {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment fragment) {
                fragment.timerIsUnlocked();
            }
        });
    }

    @Override
    public void setShowGoodsCallback(ShowGoodsCallback callback) {
        currentGoodsCallback = callback;
    }

    @Override
    public FragmentManager getScreenFragmentManager() {
        return getChildFragmentManager();
    }

    boolean closing = false;

    public boolean onBackPressed() {
        if (isAdded()) {
            Fragment fragmentById = getChildFragmentManager().findFragmentById(R.id.ias_outer_top_container);
            if (fragmentById instanceof IASBackPressHandler && ((IASBackPressHandler) fragmentById).onBackPressed()) {
                return true;
            }
            fragmentById = getChildFragmentManager().findFragmentById(R.id.ias_dialog_container);
            if (fragmentById instanceof IASBackPressHandler && ((IASBackPressHandler) fragmentById).onBackPressed()) {
                return true;
            }
        }
        closeWithAction(-1);
        return true;
    }


    LaunchStoryScreenAppearance appearanceSettings;
    LaunchStoryScreenData launchData;


    private void setLoaderFragment(Bundle savedInstanceState, Rect readerContainer) {
        if (savedInstanceState != null) return;
        try {
            FragmentManager fragmentManager = getChildFragmentManager();
            StoriesLoaderFragment storiesLoaderFragment = new StoriesLoaderFragment();
            Bundle args = new Bundle();
            args.putAll(getArguments());
            args.putParcelable("readerContainer", readerContainer);
            storiesLoaderFragment.setArguments(args);
            FragmentTransaction t = fragmentManager.beginTransaction()
                    .replace(R.id.stories_fragments_layout, storiesLoaderFragment, "STORIES_LOADER_FRAGMENT");
            t.addToBackStack("STORIES_LOADER_FRAGMENT");
            t.commitAllowingStateLoss();
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            forceFinish();
        }
    }

    abstract void openAnimationProgress(float progress);

    abstract void openAnimationStart();

    abstract void closeAnimationProgress(float progress);

    abstract void reInitUI();

    abstract void onDrag(float rawOffset);

    abstract void outsideClick();
}
