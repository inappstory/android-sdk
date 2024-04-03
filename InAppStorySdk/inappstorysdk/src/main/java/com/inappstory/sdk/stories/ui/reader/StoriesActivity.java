package com.inappstory.sdk.stories.ui.reader;

import static com.inappstory.sdk.AppearanceManager.ANIMATION_CUBE;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_OVERSCROLL;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static com.inappstory.sdk.AppearanceManager.CS_NAVBAR_COLOR;
import static com.inappstory.sdk.AppearanceManager.CS_READER_BACKGROUND_COLOR;
import static com.inappstory.sdk.AppearanceManager.CS_READER_PRESENTATION_STYLE;
import static com.inappstory.sdk.AppearanceManager.CS_READER_SETTINGS;
import static com.inappstory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT;
import static com.inappstory.sdk.game.reader.GameActivity.GAME_READER_REQUEST;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.utils.network.JsonParser;
import com.inappstory.sdk.core.repository.stories.IStoriesRepository;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.models.api.Story;
import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.core.repository.statistic.StatisticV2Manager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.reader.animations.DisabledReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.FadeReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.HandlerAnimatorListenerAdapter;
import com.inappstory.sdk.stories.ui.reader.animations.PopupReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.ReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animations.ZoomReaderAnimation;
import com.inappstory.sdk.stories.ui.widgets.elasticview.ElasticDragDismissFrameLayout;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StatusBarController;
import com.inappstory.sdk.usecase.callbacks.IUseCaseCallback;
import com.inappstory.sdk.usecase.callbacks.UseCaseCallbackCloseStory;

public class StoriesActivity extends AppCompatActivity implements BaseReaderScreen {

    public boolean pauseDestroyed = false;


    @Override
    public void onPause() {
        super.onPause();
        if (isFinishing()) {
            ScreensManager.getInstance().hideGoods();
            ScreensManager.getInstance().closeGameReader();
            StatusBarController.showStatusBar(this);
            IASCore.getInstance().statisticV1Repository.forceSend();
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

    @Override
    public void finish() {

        ScreensManager.getInstance().hideGoods();
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
        StatusBarController.hideStatusBar(this, true);
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
        switch (getIntent().getIntExtra(CS_READER_PRESENTATION_STYLE, 0)) {
            case AppearanceManager.DISABLE:
                return new DisabledReaderAnimation().setAnimations(true);
            case AppearanceManager.FADE:
                return new FadeReaderAnimation(animatedContainer).setAnimations(true);
            case AppearanceManager.POPUP:
                return new PopupReaderAnimation(animatedContainer, screenSize.y, 0f).setAnimations(true);
            default:
                Point coordinates = ScreensManager.getInstance().coordinates;
                float pivotX = screenSize.x / 2f;
                float pivotY = screenSize.y / 2f;
                if (coordinates != null) {
                    pivotY = coordinates.y;
                    pivotX = coordinates.x;
                }
                return new ZoomReaderAnimation(animatedContainer, pivotX, pivotY).setAnimations(true);
        }
    }

    private ReaderAnimation setFinishAnimations() {
        Point screenSize = Sizes.getScreenSize(StoriesActivity.this);
        switch (getIntent().getIntExtra(CS_READER_PRESENTATION_STYLE, 0)) {
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
                float pivotX = (screenSize.x - draggableFrame.getX()) / 2f;
                float pivotY = (screenSize.y - draggableFrame.getY()) / 2f;
                Point coordinates = ScreensManager.getInstance().coordinates;
                if (coordinates != null) {
                    pivotX = coordinates.x - draggableFrame.getX();
                    pivotY = coordinates.y - draggableFrame.getY();
                }

                return new ZoomReaderAnimation(animatedContainer,
                        pivotX,
                        pivotY
                ).setAnimations(false);
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
            ScreensManager.getInstance().coordinates = null;
        } catch (Exception e) {
            finishWithoutAnimation();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GAME_READER_REQUEST && resultCode == RESULT_OK) {
            if (storiesFragment == null || storiesFragment.readerManager == null) return;
            if (data != null) {
                String storyId = data.getStringExtra("storyId");
                storiesFragment.readerManager.gameComplete(
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

    public void shareComplete(boolean shared) {
        storiesFragment.readerManager.shareComplete(shared);
    }

    @Override
    public void removeStoryFromFavorite(int id) {
        if (storiesFragment != null)
            storiesFragment.removeStoryFromFavorite(id);
    }

    @Override
    public void removeAllStoriesFromFavorite() {
        if (storiesFragment != null)
            storiesFragment.removeAllStoriesFromFavorite();
    }

    @Override
    public void storyIsOpened(int currentStoryId) {
        IPreviewStoryDTO previewStoryDTO =
                IASCore.getInstance().getStoriesRepository(type).getStoryPreviewById(currentStoryId);
        if (draggableFrame != null) {
            draggableFrame.setDisableClose(previewStoryDTO.disableClose());
            draggableFrame.setHasSwipeUp(previewStoryDTO.hasSwipeUp());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState1) {

        cleaned = false;
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState1);
        setContentView(R.layout.cs_activity_stories_draggable);
        if (InAppStoryManager.isNull()) {
            finish();
            return;
        }
        int navColor = getIntent().getIntExtra(CS_NAVBAR_COLOR, Color.TRANSPARENT);
        if (navColor != 0)
            getWindow().setNavigationBarColor(navColor);
        ScreensManager.getInstance().currentStoriesReaderScreen = this;
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        closeOnSwipe = getIntent().getBooleanExtra(CS_CLOSE_ON_SWIPE, true);
        closeOnOverscroll = getIntent().getBooleanExtra(CS_CLOSE_ON_OVERSCROLL, true);

        draggableFrame = findViewById(R.id.draggable_frame);
        blockView = findViewById(R.id.blockView);
        backTintView = findViewById(R.id.background);
        animatedContainer = findViewById(R.id.animatedContainer);
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(StoriesActivity.this) {
            @Override
            public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {
                super.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels);
                backTintView.setAlpha(Math.min(1f, Math.max(0f, 1f - rawOffset)));
            }

            @Override
            public void onDragDismissed() {
                animateFirst = true;
                InAppStoryManager.closeStoryReader(CloseReader.SWIPE, StatisticV2Manager.SWIPE);
            }

            @Override
            public void onDragDropped() {
            }

            @Override
            public void touchPause() {
                if (storiesFragment != null && storiesFragment.readerManager != null)
                    storiesFragment.readerManager.pauseCurrent(false);
            }

            @Override
            public void touchResume() {
                if (storiesFragment != null && storiesFragment.readerManager != null)
                    storiesFragment.readerManager.resumeCurrent(false);
            }

            @Override
            public void swipeDown() {
                if (storiesFragment != null) {
                    storiesFragment.swipeDownEvent();
                }
            }

            @Override
            public void swipeUp() {
                if (storiesFragment != null) {
                    storiesFragment.swipeUpEvent();
                }
            }
        };
        draggableFrame.addListener(chromeFader);
        try {
            if (!getIntent().getBooleanExtra("statusBarVisibility", false)) {
                StatusBarController.hideStatusBar(StoriesActivity.this, true);
            }
        } catch (Exception e) {
            finish();
            return;
        }
        IASCore.getInstance().getListNotifier().openReader(getIntent().getStringExtra("listID"));
        type = (Story.StoryType) getIntent().getSerializableExtra("storiesType");
        if (type == null) type = Story.StoryType.COMMON;
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            if (storiesFragment == null) {
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
            bundle.putSerializable(
                    "launchData",
                    getIntent().getSerializableExtra("launchData")
            );
            bundle.putSerializable(
                    "appearanceSettings",
                    getIntent().getSerializableExtra("appearanceSettings")
            );
            storiesLoaderFragment.setArguments(bundle);
            FragmentTransaction t = fragmentManager.beginTransaction()
                    /*.setCustomAnimations(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out,
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                    )*/
                    .replace(R.id.fragments_layout, storiesLoaderFragment);
            t.addToBackStack("TEST");
            t.commit();
        } catch (IllegalStateException e) {
            finishWithoutAnimation();
        }
    }


    private void setStoriesFragment() {
        if (storiesFragment != null) {
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction t = fragmentManager.beginTransaction()
                        /*.setCustomAnimations(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                        )*/
                        .replace(R.id.fragments_layout, storiesFragment);
                t.addToBackStack("STORIES_FRAGMENT");
                t.commit();
            } catch (IllegalStateException e) {
                finishWithoutAnimation();
            }
        } else {
            finishWithoutAnimation();
        }
    }

    private void createStoriesFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            storiesFragment = new StoriesFragment();
            if (getIntent().getExtras() != null) {
                Bundle bundle = new Bundle();

                bundle.putSerializable("source", getIntent().getSerializableExtra("source"));
                bundle.putSerializable("storiesType", type);
                bundle.putInt("firstAction", getIntent().getIntExtra("firstAction", ShowStory.ACTION_OPEN));
                bundle.putString("listID", getIntent().getStringExtra("listID"));
                bundle.putString("feedId", getIntent().getStringExtra("feedId"));
                bundle.putInt("slideIndex", getIntent().getIntExtra("slideIndex", 0));
                bundle.putInt("index", getIntent().getIntExtra("index", 0));
                setAppearanceSettings(bundle);
                bundle.putIntegerArrayList("stories_ids", getIntent().getIntegerArrayListExtra("stories_ids"));
                storiesFragment.setArguments(bundle);
            }

        } else {
            storiesFragment = (StoriesFragment) getSupportFragmentManager().findFragmentByTag("STORIES_FRAGMENT");
        }

    }

    StoriesFragment storiesFragment;
    StoriesReaderSettings storiesReaderSettings;

    private void setAppearanceSettings(Bundle bundle) {
        int color = getIntent().getIntExtra(CS_READER_BACKGROUND_COLOR,
                getResources().getColor(R.color.black)
        );
        backTintView.setBackgroundColor(color);
        storiesReaderSettings = new StoriesReaderSettings(
                getIntent().getExtras()
        );
        try {
            bundle.putSerializable(CS_TIMER_GRADIENT, getIntent().getSerializableExtra(CS_TIMER_GRADIENT));
            bundle.putInt(CS_STORY_READER_ANIMATION, getIntent().getIntExtra(CS_STORY_READER_ANIMATION, ANIMATION_CUBE));
            bundle.putString(CS_READER_SETTINGS, JsonParser.getJson(storiesReaderSettings));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean closing = false;

    @Override
    public void onBackPressed() {
        closeStoryReader(CloseReader.CUSTOM, StatisticV2Manager.BACK);
    }

    @Override
    public void closeStoryReader(CloseReader action, String cause) {
        if (closing) return;
        closing = true;
        IASCore.getInstance().getListNotifier().closeReader(
                getIntent().getStringExtra("listID")
        );
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        blockView.setVisibility(View.VISIBLE);
        IStoriesRepository storiesRepository = IASCore.getInstance().getStoriesRepository(type);
        IPreviewStoryDTO story = storiesRepository.getCurrentStory();
        if (story != null) {
            int lastIndex = storiesRepository.getStoryLastIndex(story.getId());
            IUseCaseCallback useCaseCallbackCloseStory = new UseCaseCallbackCloseStory(
                    new SlideData(
                            new StoryData(
                                    story,
                                    getIntent().getStringExtra("feedId"),
                                    (SourceType) getIntent().getSerializableExtra("source")
                            ),
                            lastIndex
                    ),
                    action
            );
            useCaseCallbackCloseStory.invoke();
            StatisticV2Manager.getInstance().sendCloseStory(
                    story.getId(),
                    cause,
                    lastIndex,
                    story.getSlidesCount(),
                    getIntent().getStringExtra("feedId")
            );
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
        finishWithoutAnimation();
    }

    @Override
    public void observeGameReader(String observableUID) {

    }

    boolean cleaned = false;

    private void cleanReader() {
        if (cleaned) return;
        IASCore.getInstance().getStoriesRepository(type).clearReaderModels();
        IASCore.getInstance().downloadManager.cleanTasks();
        cleaned = true;
    }


    @Override
    public void onDestroy() {
        if (ScreensManager.getInstance().currentStoriesReaderScreen == this)
            ScreensManager.getInstance().currentStoriesReaderScreen = null;
        if (!pauseDestroyed) {

            StatusBarController.showStatusBar(this);
            IASCore.getInstance().statisticV1Repository.forceSend();
            cleanReader();
            System.gc();
            pauseDestroyed = true;
        }
        super.onDestroy();
    }
}
