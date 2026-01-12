package com.inappstory.sdk.stories.ui.reader;


import static com.inappstory.sdk.game.reader.GameReaderContentFragment.GAME_READER_REQUEST;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.ui.screens.IASActivity;
import com.inappstory.sdk.core.ui.screens.ScreenType;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.core.ui.widgets.elasticview.DraggableElasticLayout;
import com.inappstory.sdk.core.utils.CallbackTypesConverter;
import com.inappstory.sdk.core.utils.ColorUtils;
import com.inappstory.sdk.stories.api.models.ContentIdWithIndex;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.IOpenStoriesReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.statistic.IASStatisticStoriesV2Impl;
import com.inappstory.sdk.stories.ui.reader.animations.DisabledReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.FadeReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.HandlerAnimatorListenerAdapter;
import com.inappstory.sdk.stories.ui.reader.animations.PopupReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.ReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.ZoomReaderCenterAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.ZoomReaderFromCellAnimation;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPageManager;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.utils.SystemUiUtils;


public class StoriesTabletActivity extends IASActivity implements BaseStoryScreen, ShowGoodsCallback {

    public boolean pauseDestroyed = false;


    @Override
    public void onPause() {
        super.onPause();
        unsubscribeClicks();
        if (isFinishing()) {
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    ((IOpenStoriesReader) core
                            .screensManager()
                            .getOpenReader(ScreenType.STORY))
                            .onRestoreStatusBar(StoriesTabletActivity.this);
                    core
                            .screensManager()
                            .getGameScreenHolder()
                            .forceCloseScreen(null);
                    if (launchData != null)
                        core
                                .statistic()
                                .storiesV1(
                                        launchData.getSessionId(),
                                        new GetStatisticV1Callback() {
                                            @Override
                                            public void get(@NonNull IASStatisticStoriesV1 manager) {
                                                manager.sendStatistic();
                                            }
                                        }
                                );
                }
            });
            cleanReader();
            System.gc();
            pauseDestroyed = true;
        }
    }

    public boolean isFakeActivity = false;

    @Override
    protected void onStop() {
        try {
            clearDialog();
            clearOverlap();
        } catch (Exception e) {

        }
        super.onStop();
    }

    ShowGoodsCallback currentGoodsCallback = null;

    public void unsubscribeClicks() {
        if (draggableFrame != null)
            draggableFrame.removeListener(fader);
    }

    public void subscribeClicks() {
        if (draggableFrame != null)
            draggableFrame.addListener(fader);
    }


    @Override
    public void finish() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core
                        .screensManager()
                        .getGameScreenHolder()
                        .forceCloseScreen(null);
            }
        });
        if (animateFirst &&
                Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            animateFirst = false;
            closeAnim();
        } else {
            super.finish();
        }

    }

    boolean animateFirst = true;

    boolean isAnimation = false;


    @Override
    protected void onResume() {
        super.onResume();
        subscribeClicks();
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                ((IOpenStoriesReader) core
                        .screensManager()
                        .getOpenReader(ScreenType.STORY))
                        .onHideStatusBar(StoriesTabletActivity.this);
            }
        });

    }

    public void startAnim(final Bundle savedInstanceState, final Rect readerContainer) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            isAnimation = true;
            setStartAnimations()
                    .setListener(new HandlerAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart() {
                            super.onAnimationStart();
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    animatedContainer.setVisibility(View.VISIBLE);
                                }
                            }, 100);
                        }

                        @Override
                        public void onAnimationEnd() {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            isAnimation = false;
                            createStoriesFragment(savedInstanceState, readerContainer);
                            setStoriesFragment();
                        }
                    })
                    .start();
        } catch (Exception e) {
            finishWithoutAnimation();
        }
    }

    private ReaderAnimation setStartAnimations() {
        Point screenSize = Sizes.getScreenSize(StoriesTabletActivity.this);
        if (launchData == null || appearanceSettings == null) {
            return new DisabledReaderAnimation().setAnimations(true);
        }
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
                        coordinates[0] = launchData.getInitCoordinates();
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

    private ReaderAnimation setFinishAnimations() {
        Point screenSize = Sizes.getScreenSize(StoriesTabletActivity.this);
        if (launchData == null || appearanceSettings == null) {
            return new DisabledReaderAnimation().setAnimations(true);
        }
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
                        if (launchData.getInitCoordinates() != null)
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
                        public void onAnimationEnd() {
                            draggableFrame.setVisibility(View.GONE);
                            finishWithoutAnimation();
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
            finishWithoutAnimation();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GAME_READER_REQUEST && resultCode == RESULT_OK) {
            if (storiesContentFragment == null || storiesContentFragment.readerManager == null)
                return;
            if (data != null) {
                String storyId = data.getStringExtra("storyId");
                storiesContentFragment.readerManager.gameComplete(
                        data.getStringExtra("gameState"),
                        storyId != null ? Integer.parseInt(storyId) : 0,
                        data.getIntExtra("slideIndex", 0)
                );
            }
        }
    }


    public void finishWithCustomAnimation(int enter, int exit) {
        super.finish();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        overridePendingTransition(enter, exit);
    }

    public void finishWithoutAnimation() {
        super.finish();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        overridePendingTransition(0, 0);
    }


    DraggableElasticLayout draggableFrame;
    View blockView;
    View backTintView;
    View animatedContainer;

    private DraggableElasticLayout.DraggableElasticFader fader;

    boolean closeOnSwipe = true;
    boolean closeOnOverscroll = true;

    ContentType type = ContentType.STORY;

    @Override
    public void removeStoryFromFavorite(int id) {
        if (storiesContentFragment != null)
            storiesContentFragment.removeStoryFromFavorite(id);
    }

    @Override
    public void removeAllStoriesFromFavorite() {
        if (storiesContentFragment != null)
            storiesContentFragment.removeAllStoriesFromFavorite();
    }

    @Override
    public void timerIsLocked() {
        if (storiesContentFragment != null) storiesContentFragment.timerIsLocked();
    }

    @Override
    public void timerIsUnlocked() {
        if (storiesContentFragment != null) storiesContentFragment.timerIsUnlocked();
    }

    @Override
    public void pauseScreen() {

    }

    @Override
    public void resumeScreen() {

    }

    @Override
    public void disableDrag(boolean disable) {
        boolean draggable = appearanceSettings == null ||
                appearanceSettings.csIsDraggable();
        if (draggableFrame != null)
            draggableFrame.dragIsDisabled(draggable && disable);
    }

    @Override
    public void disableSwipeUp(boolean disable) {

        if (draggableFrame != null)
            draggableFrame.swipeUpIsDisabled(disable);
    }

    @Override
    public void disableClose(boolean disable) {
        if (draggableFrame != null)
            draggableFrame.disableClose(true);
    }

    @Override
    public void swipeVerticalGestureEnabled(boolean enabled) {
        if (draggableFrame != null)
            draggableFrame.verticalGesturesEnabled(enabled);
    }

    @Override
    public void backPressEnabled(boolean enabled) {

    }

    @Override
    public Point getContainerSize() {
        return new Point(maxWidth, maxHeight);
    }

    @Override
    public void setShowGoodsCallback(ShowGoodsCallback callback) {
        currentGoodsCallback = callback;
    }

    @Override
    public void permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    int maxHeight = 0;
    int maxWidth = 0;

    @Override
    public FragmentManager getScreenFragmentManager() {
        return getSupportFragmentManager();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState1) {

        cleaned = false;
        super.onCreate(savedInstanceState1);
        setContentView(R.layout.cs_mainscreen_stories_draggable_tablet);
        Point size = Sizes.getScreenSize(this);
        float coeff = 680 / 400f;
        int xWithOffset = size.x - Sizes.dpToPxExt(80, this);
        int yWithOffset = size.y - Sizes.dpToPxExt(80, this);
        int cleanSize;
        if (coeff * xWithOffset > yWithOffset) {
            cleanSize = yWithOffset;
        } else {
            cleanSize = xWithOffset;
        }
        maxHeight = Math.min(
                cleanSize,
                Sizes.dpToPxExt(680, this)
        );
        maxWidth = Math.min(
                400 * cleanSize / 680,
                Sizes.dpToPxExt(400, this)
        );

        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();

        launchData = (LaunchStoryScreenData) getIntent().
                getSerializableExtra(LaunchStoryScreenData.SERIALIZABLE_KEY);
        appearanceSettings = (LaunchStoryScreenAppearance) getIntent()
                .getSerializableExtra(LaunchStoryScreenAppearance.SERIALIZABLE_KEY);
        if (inAppStoryManager == null) {
            forceFinish();
            return;
        }
        IASCore core = inAppStoryManager.iasCore();
        int navColor = appearanceSettings.csNavBarColor();
        core.screensManager().getStoryScreenHolder().subscribeScreen(StoriesTabletActivity.this);
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        closeOnSwipe = appearanceSettings.csCloseOnSwipe();
        closeOnOverscroll = appearanceSettings.csCloseOnOverscroll();

        draggableFrame = findViewById(R.id.draggable_frame);
        draggableFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        View scrollView = findViewById(R.id.scrollContainer);
        ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
        layoutParams.width = maxWidth;
        layoutParams.height = maxHeight;
        scrollView.requestLayout();
        View goodsView = findViewById(R.id.ias_outer_top_container);
        View dialogsView = findViewById(R.id.ias_dialog_container);

        ViewGroup.LayoutParams goodsLayoutParams = goodsView.getLayoutParams();
        goodsLayoutParams.width = maxWidth;
        goodsLayoutParams.height = maxHeight;
        ViewGroup.LayoutParams dialogsLayoutParams = dialogsView.getLayoutParams();
        dialogsLayoutParams.width = maxWidth;
        dialogsLayoutParams.height = maxHeight;
        dialogsView.requestLayout();
        goodsView.requestLayout();
        blockView = findViewById(R.id.blockView);
        backTintView = findViewById(R.id.background);
        animatedContainer = findViewById(R.id.animatedContainer);
        fader = new DraggableElasticLayout.DraggableElasticFader(StoriesTabletActivity.this) {
            @Override
            public void onDrag(
                    float elasticOffset,
                    float elasticOffsetPixels,
                    float rawOffset,
                    float rawOffsetPixels
            ) {
                super.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels);
                backTintView.setAlpha(Math.min(1f, Math.max(0f, 1f - rawOffset)));
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
                if (storiesContentFragment != null && storiesContentFragment.readerManager != null)
                    storiesContentFragment.readerManager.pauseCurrent(false);
            }

            @Override
            public void touchResume() {
                if (storiesContentFragment != null && storiesContentFragment.readerManager != null)
                    storiesContentFragment.readerManager.resumeCurrent(false);
            }

            @Override
            public void swipeDown() {
                if (storiesContentFragment != null) {
                    storiesContentFragment.swipeDownEvent();
                }
            }

            @Override
            public void swipeUp() {
                if (storiesContentFragment != null) {
                    storiesContentFragment.swipeUpEvent();
                }
            }
        };

        ((IOpenStoriesReader) core
                .screensManager()
                .getOpenReader(ScreenType.STORY))
                .onHideStatusBar(StoriesTabletActivity.this);
        InAppStoryService.getInstance().getListReaderConnector().readerIsOpened();
        type = launchData.getType();
        if (savedInstanceState1 != null)
            storiesContentFragment = (StoriesContentFragment) getSupportFragmentManager().findFragmentByTag("STORIES_FRAGMENT");
        if (storiesContentFragment != null) {
            setAppearanceSettings(storiesContentFragment.getArguments());
        }
        draggableFrame.post(new Runnable() {
            @Override
            public void run() {
                animatedContainer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animatedContainer.setVisibility(View.VISIBLE);
                    }
                }, 100);
                final Rect readerContainer = new Rect();
                draggableFrame.getGlobalVisibleRect(readerContainer);
                if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
                    if (storiesContentFragment == null) {
                        setLoaderFragment(readerContainer);
                        startAnim(savedInstanceState1, readerContainer);
                    } else {
                        setStoriesFragment();
                    }
                } else {
                    createStoriesFragment(savedInstanceState1, readerContainer);
                    setStoriesFragment();
                }
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    onBackPressed();
                }
            };
            getOnBackPressedDispatcher().addCallback(this, callback);
        }
    }

    private void setLoaderFragment(Rect readerContainer) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            StoriesLoaderFragment storiesLoaderFragment = new StoriesLoaderFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("readerContainer", readerContainer);
            setAppearanceSettings(bundle);
            storiesLoaderFragment.setArguments(bundle);
            FragmentTransaction t = fragmentManager.beginTransaction()
                    .replace(R.id.stories_fragments_layout, storiesLoaderFragment, "TEST");
            t.addToBackStack(null);
            t.commit();
        } catch (IllegalStateException e) {
            InAppStoryManager.handleException(e);
            finishWithoutAnimation();
        }
    }


    private void setStoriesFragment() {
        if (storiesContentFragment != null) {
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction t = fragmentManager.beginTransaction()
                        .replace(R.id.stories_fragments_layout, storiesContentFragment, "STORIES_FRAGMENT");
                t.addToBackStack(null);
                t.commit();
            } catch (IllegalStateException e) {
                InAppStoryManager.handleException(e);
                finishWithoutAnimation();
            }
        } else {
            finishWithoutAnimation();
        }

        disableDrag(!closeOnSwipe);
    }


    private void createStoriesFragment(Bundle savedInstanceState, Rect readerContainer) {
        if (savedInstanceState == null) {
            storiesContentFragment = new StoriesContentFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("readerContainer", readerContainer);
            setAppearanceSettings(bundle);
            storiesContentFragment.setArguments(bundle);
        } else {
            storiesContentFragment = (StoriesContentFragment) getSupportFragmentManager().findFragmentByTag("STORIES_FRAGMENT");
        }

    }

    StoriesContentFragment storiesContentFragment;
    LaunchStoryScreenAppearance appearanceSettings;
    LaunchStoryScreenData launchData;

    private void setAppearanceSettings(Bundle bundle) {

        try {
            backTintView.setBackgroundColor(
                    ColorUtils.modifyAlpha(
                            appearanceSettings.csReaderBackgroundColor(),
                            0.3f
                    )
            );
            bundle.putSerializable(appearanceSettings.getSerializableKey(), appearanceSettings);
            bundle.putSerializable(launchData.getSerializableKey(), launchData);
        } catch (Exception e) {
            forceFinish();
        }
    }

    boolean closing = false;

    public boolean backPressEnabled = true;

    @Override
    public void onBackPressed() {
        if (!backPressEnabled && storiesContentFragment != null) {
            ReaderPageManager page = storiesContentFragment.getCurrentPage();
            if (page != null) {
                page.handleBackPress();
                return;
            }
        }
        Fragment fragmentById = getScreenFragmentManager().findFragmentById(R.id.ias_outer_top_container);
        if (fragmentById instanceof IASBackPressHandler && ((IASBackPressHandler) fragmentById).onBackPressed()) {
            return;
        }
        fragmentById = getScreenFragmentManager().findFragmentById(R.id.ias_dialog_container);
        if (fragmentById instanceof IASBackPressHandler && ((IASBackPressHandler) fragmentById).onBackPressed()) {
            return;
        }

        if (storiesContentFragment != null && storiesContentFragment.onBackPressed()) return;
        closeWithAction(-1);
    }

    private void clearDialog() {

        Fragment fragmentById = getScreenFragmentManager().findFragmentById(R.id.ias_dialog_container);
        if (fragmentById instanceof IASBackPressHandler && ((IASBackPressHandler) fragmentById).onBackPressed()) {
            return;
        }

       /* FragmentManager parentFragmentManager = getScreenFragmentManager();
        Fragment oldFragment =
                parentFragmentManager.findFragmentById(R.id.ias_dialog_container);
        if (oldFragment != null) {
            parentFragmentManager.beginTransaction().replace(R.id.ias_dialog_container, new Fragment()).commit();
            parentFragmentManager.popBackStack();
        }*/
    }

    private void clearOverlap() {
        Fragment fragmentById = getScreenFragmentManager().findFragmentById(R.id.ias_outer_top_container);
        if (fragmentById instanceof IASBackPressHandler && ((IASBackPressHandler) fragmentById).onBackPressed()) {
            return;
        }
    }

    @Override
    public void closeWithAction(int action) {
        if (closing) return;
        closing = true;
        final IReaderContent[] story = new IReaderContent[1];
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                int storyId = core
                        .screensManager()
                        .getStoryScreenHolder()
                        .currentOpenedStoryId();
                story[0] = core.contentHolder().readerContent()
                        .getByIdAndType(storyId, type);
                core.inAppStoryService().getListReaderConnector().readerIsClosed();
            }
        });
        ContentIdWithIndex idWithIndex = null;
        if (storiesContentFragment != null && story[0] != null) {
            ReaderManager readerManager = storiesContentFragment.readerManager;
            if (readerManager != null)
                idWithIndex = readerManager.getByIdAndIndex(story[0].id()).copy();
        }
        cleanReader();
        animateFirst = true;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                blockView.setVisibility(View.VISIBLE);
                finishAfterTransition();
            }
        });
        if (story[0] != null) {
            sendCloseStatistic(story[0], action, idWithIndex);
        }
    }

    private void sendCloseStatistic(
            final @NonNull IReaderContent story,
            final int action,
            final ContentIdWithIndex idWithIndex
    ) {
        if (launchData != null) {
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
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
                                                            type
                                                    ),
                                                    idWithIndex.index(),
                                                    story.slideEventPayload(idWithIndex.index())
                                            ),
                                            new CallbackTypesConverter().getCloseTypeFromInt(action)
                                    );
                                }
                            });
                    String cause = IASStatisticStoriesV2Impl.AUTO;
                    switch (action) {
                        case CloseStory.CLICK:
                            cause = IASStatisticStoriesV2Impl.CLICK;
                            break;
                        case CloseStory.CUSTOM:
                            cause = IASStatisticStoriesV2Impl.CUSTOM;
                            break;
                        case -1:
                            cause = IASStatisticStoriesV2Impl.BACK;
                            break;
                        case CloseStory.SWIPE:
                            cause = IASStatisticStoriesV2Impl.SWIPE;
                            break;
                    }
                    core.statistic().storiesV2().sendCloseStory(
                            idWithIndex.id(),
                            cause,
                            idWithIndex.index(),
                            story.slidesCount(),
                            launchData.getFeed()
                    );
                }
            });
        }
    }

    @Override
    public void forceFinish() {
        InAppStoryService service = InAppStoryService.getInstance();

        final IReaderContent[] story = {null};
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                int storyId = core
                        .screensManager()
                        .getStoryScreenHolder()
                        .currentOpenedStoryId();
                story[0] = core
                        .contentHolder()
                        .readerContent().getByIdAndType(storyId, type);
            }
        });
        if (service != null) {
            service.getListReaderConnector().readerIsClosed();
        }
        ContentIdWithIndex idWithIndex = null;
        if (storiesContentFragment != null && story[0] != null) {
            ReaderManager readerManager = storiesContentFragment.readerManager;
            if (readerManager != null)
                idWithIndex = readerManager.getByIdAndIndex(story[0].id()).copy();
        }
        cleanReader();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                blockView.setVisibility(View.VISIBLE);
                finishWithoutAnimation();
            }
        });
        if (story[0] != null) {
            sendCloseStatistic(story[0], CloseStory.CUSTOM, idWithIndex);
        }
    }

    @Override
    public void close() {
        closeWithAction(CloseStory.CUSTOM);
    }

    boolean cleaned = false;

    public void cleanReader() {
        if (cleaned) return;
        if (launchData != null)
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.statistic().storiesV1(
                            launchData.getSessionId(),
                            new GetStatisticV1Callback() {
                                @Override
                                public void get(@NonNull IASStatisticStoriesV1 manager) {
                                    manager.closeStatisticEvent();
                                }
                            }
                    );
                    core.screensManager().getStoryScreenHolder().currentOpenedStoryId(0);
                    cleaned = true;
                }
            });
        if (storiesContentFragment != null) {
            storiesContentFragment.readerManager.refreshStoriesIds();
        }
    }


    @Override
    public void onDestroy() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager()
                        .getStoryScreenHolder()
                        .unsubscribeScreen(StoriesTabletActivity.this);
                if (!pauseDestroyed) {

                    ((IOpenStoriesReader) core
                            .screensManager()
                            .getOpenReader(ScreenType.STORY))
                            .onRestoreStatusBar(StoriesTabletActivity.this);
                    if (launchData != null) {
                        core.statistic().storiesV1(
                                launchData.getSessionId(),
                                new GetStatisticV1Callback() {
                                    @Override
                                    public void get(@NonNull IASStatisticStoriesV1 manager) {
                                        manager.sendStatistic();
                                    }
                                }
                        );
                    }
                    cleanReader();
                    System.gc();
                }
            }
        });
        pauseDestroyed = true;
        super.onDestroy();
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
}
