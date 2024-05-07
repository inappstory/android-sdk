package com.inappstory.sdk.stories.ui.reader;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.statistic.GetOldStatisticManagerCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
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
        BaseReaderScreen,
        IASBackPressHandler,
        ShowGoodsCallback {

    @Override
    public void disableDrag(boolean disable) {
        boolean draggable = !Sizes.isTablet(getContext()) && (appearanceSettings == null || appearanceSettings.csIsDraggable());
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
    public void resumeReader() {
        useContentFragment(new StoriesContentFragmentAction() {
            @Override
            public void invoke(StoriesContentFragment fragment) {
                if (fragment.readerManager != null)
                    fragment.readerManager.resumeCurrent(true);
            }
        });
    }

    @Override
    public void pauseReader() {
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void goodsIsOpened() {
        timerIsLocked();
        if (currentGoodsCallback != null)
            currentGoodsCallback.goodsIsOpened();
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
                StoryItemCoordinates coordinates = ScreensManager.getInstance().coordinates;
                float pivotX = -screenSize.x / 2f;
                float pivotY = -screenSize.y / 2f;

                if (coordinates != null) {
                    pivotX += coordinates.x();
                    pivotY += coordinates.y();
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
        FragmentManager parentFragmentManager = getStoriesReaderFragmentManager();
        Fragment oldFragment =
                parentFragmentManager.findFragmentById(R.id.ias_dialog_container);
        if (oldFragment != null) {
            parentFragmentManager.beginTransaction().replace(R.id.ias_dialog_container, new Fragment()).commit();
            parentFragmentManager.popBackStack();
        }
    }

    private void clearOverlap() {
        FragmentManager parentFragmentManager = getStoriesReaderFragmentManager();
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
        appearanceSettings = (StoriesReaderAppearanceSettings) arguments.getSerializable(
                StoriesReaderAppearanceSettings.SERIALIZABLE_KEY
        );
        launchData = (StoriesReaderLaunchData) arguments.getSerializable(
                StoriesReaderLaunchData.SERIALIZABLE_KEY
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
        ScreensManager.getInstance().subscribeReaderScreen(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager != null) {
            inAppStoryManager.getOpenStoriesReader().onHideStatusBar(getActivity());
        }
        if (getActivity() == null) {
            return;
        }
        if (InAppStoryManager.isNull() || InAppStoryService.isNull()) {
            forceFinish();
            return;
        }

        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(getActivity()) {
            @Override
            public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {
                super.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels);
                StoriesMainFragment.this.onDrag(rawOffset);
            }

            @Override
            public void onDragDismissed() {
                animateFirst = true;
                ScreensManager.getInstance().closeStoryReader(CloseStory.SWIPE);
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
    public void closeStoryReader(int action) {
        if (closing) return;
        closing = true;
        InAppStoryService service = InAppStoryService.getInstance();
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        blockView.setVisibility(View.VISIBLE);
        if (service != null) {
            service.getListReaderConnector().closeReader();
            Story story = service.getDownloadManager().getStoryById(
                    service.getCurrentId(),
                    launchData.getType()
            );
            if (story != null) {
                if (CallbackManager.getInstance().getCloseStoryCallback() != null) {
                    CallbackManager.getInstance().getCloseStoryCallback().closeStory(
                            new SlideData(
                                    StoryData.getStoryData(
                                            story,
                                            launchData.getFeed(),
                                            launchData.getSourceType(),
                                            launchData.getType()
                                    ),
                                    story.lastIndex,
                                    story.getSlideEventPayload(story.lastIndex)
                            ),
                            CallbackManager.getInstance().getCloseTypeFromInt(action)
                    );
                }
                String cause = StatisticManager.AUTO;
                switch (action) {
                    case CloseStory.CLICK:
                        cause = StatisticManager.CLICK;
                        break;
                    case CloseStory.CUSTOM:
                        cause = StatisticManager.CUSTOM;
                        break;
                    case -1:
                        cause = StatisticManager.BACK;
                        break;
                    case CloseStory.SWIPE:
                        cause = StatisticManager.SWIPE;
                        break;
                }
                StatisticManager.getInstance().sendCloseStory(
                        story.id,
                        cause,
                        story.lastIndex,
                        story.getSlidesCount(),
                        launchData.getFeed()
                );
            }
        }
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
            requireActivity().getSupportFragmentManager().popBackStack();
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
        ScreensManager.getInstance().subscribeReaderScreen(this);
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
        ScreensManager.getInstance().unsubscribeReaderScreen(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager != null) {
            inAppStoryManager.getOpenStoriesReader().onRestoreStatusBar(getActivity());
        }
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
                StoryItemCoordinates coordinates = ScreensManager.getInstance().coordinates;
                float pivotX = -screenSize.x / 2f;
                float pivotY = -screenSize.y / 2f;
                if (coordinates != null) {
                    pivotX += coordinates.x();
                    pivotY += coordinates.y();
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
            ScreensManager.getInstance().clearCoordinates();
        } catch (Exception e) {
            forceFinish();
        }

    }


    boolean cleaned = false;

    void cleanReader() {
        if (cleaned) return;

        OldStatisticManager.useInstance(
                launchData.getSessionId(),
                new GetOldStatisticManagerCallback() {
                    @Override
                    public void get(@NonNull OldStatisticManager manager) {
                        manager.closeStatisticEvent();
                    }
                }
        );
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.setCurrentIndex(0);
                service.setCurrentId(0);
                service.getDownloadManager().cleanStoriesIndex(launchData.getType());
                cleaned = true;
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
    public FragmentManager getStoriesReaderFragmentManager() {
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
        closeStoryReader(-1);
        return true;
    }


    StoriesReaderAppearanceSettings appearanceSettings;
    StoriesReaderLaunchData launchData;


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
