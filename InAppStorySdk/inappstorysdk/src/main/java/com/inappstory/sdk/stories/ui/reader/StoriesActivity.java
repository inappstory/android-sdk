package com.inappstory.sdk.stories.ui.reader;


import static com.inappstory.sdk.game.reader.GameReaderContentFragment.GAME_READER_REQUEST;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

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
import com.inappstory.sdk.stories.ui.widgets.elasticview.ElasticDragDismissFrameLayout;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;


public class StoriesActivity extends AppCompatActivity implements BaseReaderScreen, ShowGoodsCallback {

    public boolean pauseDestroyed = false;


    @Override
    public void onPause() {
        super.onPause();

        unsubscribeClicks();
        if (isFinishing()) {
            // ScreensManager.getInstance().hideGoods();
            ScreensManager.getInstance().closeGameReader();
            InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
            if (inAppStoryManager != null) {
                inAppStoryManager.getOpenStoriesReader().onRestoreStatusBar(this);
            }
            OldStatisticManager.useInstance(
                    launchData.getSessionId(),
                    new GetOldStatisticManagerCallback() {
                        @Override
                        public void get(@NonNull OldStatisticManager manager) {
                            manager.sendStatistic();
                        }
                    }
            );
            cleanReader();
            System.gc();
            pauseDestroyed = true;
        }
    }

    public boolean isFakeActivity = false;

    @Override
    protected void onStop() {
        super.onStop();
    }

    ShowGoodsCallback currentGoodsCallback = null;

    public void unsubscribeClicks() {
        draggableFrame.removeListener(chromeFader);
    }

    public void subscribeClicks() {
        draggableFrame.addListener(chromeFader);
    }


    @Override
    public void finish() {

        ScreensManager.getInstance().closeGameReader();
        if (animateFirst &&
                android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
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
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager != null) {
            inAppStoryManager.getOpenStoriesReader().onHideStatusBar(this);
        }
    }

    public void startAnim(final Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            isAnimation = true;
            setStartAnimations()
                    .setListener(new HandlerAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd() {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            isAnimation = false;
                            createStoriesFragment(savedInstanceState);
                            setStoriesFragment();
                        }
                    })
                    .start();
        } catch (Exception e) {
            finishWithoutAnimation();
        }
    }

    private ReaderAnimation setStartAnimations() {
        Point screenSize = Sizes.getScreenSize(StoriesActivity.this);
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

    private ReaderAnimation setFinishAnimations() {
        Point screenSize = Sizes.getScreenSize(StoriesActivity.this);
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
                        public void onAnimationEnd() {
                            draggableFrame.setVisibility(View.GONE);
                            StoriesActivity.super.finish();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    })
                    .start();
            ScreensManager.getInstance().clearCoordinates();
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


    ElasticDragDismissFrameLayout draggableFrame;
    View blockView;
    View backTintView;
    View animatedContainer;

    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    boolean closeOnSwipe = true;
    boolean closeOnOverscroll = true;

    Story.StoryType type = Story.StoryType.COMMON;

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
    public void pauseReader() {

    }

    @Override
    public void resumeReader() {

    }

    @Override
    public void disableDrag(boolean disable) {
        boolean draggable = appearanceSettings == null || appearanceSettings.csIsDraggable();
        if (draggableFrame != null)
            draggableFrame.dragIsDisabled(draggable && disable);
    }

    @Override
    public void setShowGoodsCallback(ShowGoodsCallback callback) {
        currentGoodsCallback = callback;
    }

    @Override
    public FragmentManager getStoriesReaderFragmentManager() {
        return getSupportFragmentManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState1) {

        cleaned = false;
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState1);
        setContentView(R.layout.cs_mainscreen_stories_draggable);
        if (InAppStoryManager.isNull() || InAppStoryService.isNull()) {
            finish();
            return;
        }

        appearanceSettings = (StoriesReaderAppearanceSettings) getIntent()
                .getSerializableExtra(StoriesReaderAppearanceSettings.SERIALIZABLE_KEY);
        launchData = (StoriesReaderLaunchData) getIntent().
                getSerializableExtra(StoriesReaderLaunchData.SERIALIZABLE_KEY);

        int navColor = appearanceSettings.csNavBarColor();
        if (navColor != 0)
            getWindow().setNavigationBarColor(navColor);

        ScreensManager.getInstance().subscribeReaderScreen(this);
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        closeOnSwipe = appearanceSettings.csCloseOnSwipe();
        closeOnOverscroll = appearanceSettings.csCloseOnOverscroll();

        draggableFrame = findViewById(R.id.draggable_frame);

        blockView = findViewById(R.id.blockView);
        backTintView = findViewById(R.id.background);
        animatedContainer = findViewById(R.id.animatedContainer);
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(StoriesActivity.this) {
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
                ScreensManager.getInstance().closeStoryReader(CloseStory.SWIPE);
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
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) {
            forceFinish();
            return;
        }
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager != null) {
            inAppStoryManager.getOpenStoriesReader().onHideStatusBar(this);
        }
        service.getListReaderConnector().readerIsOpened();
        type = launchData.getType();
        draggableFrame.type = type;
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            if (storiesContentFragment == null) {
                setLoaderFragment();
                startAnim(savedInstanceState1);
            } else {
                setStoriesFragment();
            }
        } else {
            createStoriesFragment(savedInstanceState1);
            setStoriesFragment();
        }
    }

    private void setLoaderFragment() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            StoriesLoaderFragment storiesLoaderFragment = new StoriesLoaderFragment();
            Bundle bundle = new Bundle();
            setAppearanceSettings(bundle);
            storiesLoaderFragment.setArguments(bundle);
            FragmentTransaction t = fragmentManager.beginTransaction()
                    /*.setCustomAnimations(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                    )*/
                    .replace(R.id.stories_fragments_layout, storiesLoaderFragment);
            t.addToBackStack("TEST");
            t.commit();
        } catch (IllegalStateException e) {
            InAppStoryService.createExceptionLog(e);
            finishWithoutAnimation();
        }
    }


    private void setStoriesFragment() {
        if (storiesContentFragment != null) {
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction t = fragmentManager.beginTransaction()
                        .replace(R.id.stories_fragments_layout, storiesContentFragment);
                t.addToBackStack("STORIES_FRAGMENT");
                t.commit();
            } catch (IllegalStateException e) {
                InAppStoryService.createExceptionLog(e);
                finishWithoutAnimation();
            }
        } else {
            finishWithoutAnimation();
        }

        disableDrag(false);
    }

    private void createStoriesFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            storiesContentFragment = new StoriesContentFragment();
            Bundle bundle = new Bundle();
            setAppearanceSettings(bundle);
            storiesContentFragment.setArguments(bundle);
        } else {
            storiesContentFragment = (StoriesContentFragment) getSupportFragmentManager().findFragmentByTag("STORIES_FRAGMENT");
        }

    }

    StoriesContentFragment storiesContentFragment;
    StoriesReaderAppearanceSettings appearanceSettings;
    StoriesReaderLaunchData launchData;

    private void setAppearanceSettings(Bundle bundle) {
        backTintView.setBackgroundColor(appearanceSettings.csReaderBackgroundColor());
        try {
            bundle.putSerializable(appearanceSettings.getSerializableKey(), appearanceSettings);
            bundle.putSerializable(launchData.getSerializableKey(), launchData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean closing = false;

    @Override
    public void onBackPressed() {
        Fragment fragmentById = getStoriesReaderFragmentManager().findFragmentById(R.id.ias_outer_top_container);
        if (fragmentById instanceof IASBackPressHandler && ((IASBackPressHandler) fragmentById).onBackPressed()) {
            return;
        }
        fragmentById = getStoriesReaderFragmentManager().findFragmentById(R.id.ias_dialog_container);
        if (fragmentById instanceof IASBackPressHandler && ((IASBackPressHandler) fragmentById).onBackPressed()) {
            return;
        }
        closeStoryReader(-1);
    }

    @Override
    public void closeStoryReader(int action) {
        if (closing) return;
        closing = true;
        InAppStoryService service = InAppStoryService.getInstance();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        blockView.setVisibility(View.VISIBLE);
        if (service != null) {
            service.getListReaderConnector().readerIsClosed();
            Story story = service.getDownloadManager()
                    .getStoryById(service.getCurrentId(), type);
            if (story != null) {
                if (CallbackManager.getInstance().getCloseStoryCallback() != null) {
                    CallbackManager.getInstance().getCloseStoryCallback().closeStory(
                            new SlideData(
                                    StoryData.getStoryData(
                                            story,
                                            launchData.getFeed(),
                                            launchData.getSourceType(),
                                            type
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

    @Override
    public void forceFinish() {
        InAppStoryService service = InAppStoryService.getInstance();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        blockView.setVisibility(View.VISIBLE);
        if (service != null) {
            service.getListReaderConnector().readerIsClosed();
            Story story = service.getDownloadManager()
                    .getStoryById(service.getCurrentId(), type);
            if (story != null) {
                if (CallbackManager.getInstance().getCloseStoryCallback() != null) {
                    CallbackManager.getInstance().getCloseStoryCallback().closeStory(
                            new SlideData(
                                    StoryData.getStoryData(
                                            story,
                                            launchData.getFeed(),
                                            launchData.getSourceType(),
                                            type
                                    ),
                                    story.lastIndex,
                                    story.getSlideEventPayload(story.lastIndex)
                            ),
                            CallbackManager.getInstance().getCloseTypeFromInt(CloseStory.CUSTOM)
                    );
                }
                StatisticManager.getInstance().sendCloseStory(
                        story.id,
                        StatisticManager.CUSTOM,
                        story.lastIndex,
                        story.getSlidesCount(),
                        launchData.getFeed()
                );
            }
        }
        cleanReader();
        finishWithoutAnimation();
    }

    boolean cleaned = false;

    public void cleanReader() {
        if (cleaned) return;
        Log.e("statisticTests", "closeReader");
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
                service.getDownloadManager().cleanStoriesIndex(type);
                cleaned = true;
            }
        });
    }


    @Override
    public void onDestroy() {
        ScreensManager.getInstance().unsubscribeReaderScreen(this);
        if (!pauseDestroyed) {
            InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
            if (inAppStoryManager != null) {
                inAppStoryManager.getOpenStoriesReader().onRestoreStatusBar(this);
            }
            OldStatisticManager.useInstance(
                    launchData.getSessionId(),
                    new GetOldStatisticManagerCallback() {
                        @Override
                        public void get(@NonNull OldStatisticManager manager) {
                            manager.sendStatistic();
                        }
                    }
            );
            cleanReader();
            System.gc();
            pauseDestroyed = true;
        }
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
