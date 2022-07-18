package com.inappstory.sdk.stories.ui.reader;

import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_OVERSCROLL;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static com.inappstory.sdk.AppearanceManager.CS_DISLIKE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_FAVORITE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_FAVORITE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_LIKE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_SHARE;
import static com.inappstory.sdk.AppearanceManager.CS_LIKE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_NAVBAR_COLOR;
import static com.inappstory.sdk.AppearanceManager.CS_READER_OPEN_ANIM;
import static com.inappstory.sdk.AppearanceManager.CS_READER_SETTINGS;
import static com.inappstory.sdk.AppearanceManager.CS_REFRESH_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_SHARE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_SOUND_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT_ENABLE;
import static com.inappstory.sdk.game.reader.GameActivity.GAME_READER_REQUEST;

import android.app.Activity;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseReader;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.widgets.elasticview.ElasticDragDismissFrameLayout;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StatusBarController;

public class StoriesFixedActivity extends AppCompatActivity implements BaseReaderScreen {

    public boolean pauseDestroyed = false;


    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isFinishing()) {

            ScreensManager.getInstance().hideGoods();
            ScreensManager.getInstance().closeGameReader();
            StatusBarController.showStatusBar(this);

            OldStatisticManager.getInstance().sendStatistic();
            ScreensManager.created = 0;
            cleanReader();
            System.gc();
            pauseDestroyed = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    StoriesFragment storiesFragment;

    public void shareComplete() {
        storiesFragment.readerManager.shareComplete();
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
    public void finish() {

        ScreensManager.getInstance().hideGoods();
        ScreensManager.getInstance().closeGameReader();
        switch (getIntent().getIntExtra(CS_READER_OPEN_ANIM, 1)) {
            case 0:
                finishWithCustomAnimation(R.anim.empty_animation, R.anim.alpha_fade_out);
                break;
            case 1:
                super.finish();
                break;
            case 2:
                finishWithCustomAnimation(R.anim.empty_animation, R.anim.popup_hide);
                break;
            default:
                super.finish();
                break;
        }
    }

    boolean animateFirst = true;


    public void loadAnim() {
        try {
            float x = draggableFrame.getX() + draggableFrame.getRight() / 2;
            float y = draggableFrame.getY();
            AnimationSet animationSet = new AnimationSet(true);
            Animation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, x, y);
            anim.setDuration(200);
            animationSet.addAnimation(anim);
            Point coordinates = ScreensManager.getInstance().coordinates;
            if (coordinates != null) {
                Animation anim2 = new TranslateAnimation(draggableFrame.getX(), coordinates.x
                        - Sizes.getScreenSize(StoriesFixedActivity.this).x / 2,
                        0f, coordinates.y - draggableFrame.getY());
                anim2.setDuration(200);
                animationSet.addAnimation(anim2);

            }
            animationSet.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    draggableFrame.setVisibility(View.GONE);
                    StoriesFixedActivity.super.finish();
                }
            });
            draggableFrame.startAnimation(animationSet);
        } catch (Exception e) {
            finishWithoutAnimation();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarController.hideStatusBar(this, true);
    }


    @Override
    public void onBackPressed() {

        if (ScreensManager.getInstance().coordinates != null) animateFirst = true;
        else animateFirst = false;

        if (InAppStoryService.isNotNull()) {
            Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(InAppStoryService.getInstance().getCurrentId());
            if (story != null) {
                CsEventBus.getDefault().post(new CloseStory(story.id,
                        story.title, story.tags, story.getSlidesCount(),
                        story.lastIndex, CloseStory.CUSTOM,
                        getIntent().getIntExtra("source", 0)));
                if (CallbackManager.getInstance().getCloseStoryCallback() != null) {
                    CallbackManager.getInstance().getCloseStoryCallback().closeStory(
                            story.id,
                            story.title, story.tags, story.getSlidesCount(),
                            story.lastIndex, CloseReader.CUSTOM,
                            CallbackManager.getInstance().getSourceFromInt(
                                    getIntent().getIntExtra("source", 0))
                    );
                }
                String cause = StatisticManager.BACK;
                StatisticManager.getInstance().sendCloseStory(story.id, cause, story.lastIndex,
                        story.getSlidesCount(),
                        getIntent().getStringExtra("feedId"));
            }
        }
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GAME_READER_REQUEST && resultCode == RESULT_OK) {
            if (storiesFragment == null || storiesFragment.readerManager == null) return;
            storiesFragment.readerManager.gameComplete(
                    data.getStringExtra("gameState"),
                    Integer.parseInt(data.getStringExtra("storyId")),
                    data.getIntExtra("slideIndex", 0)
            );
        }
    }

    public void finishWithCustomAnimation(int enter, int exit) {
        super.finish();
        overridePendingTransition(enter, exit);
    }

    public void finishWithoutAnimation() {
        super.finish();
        overridePendingTransition(0, 0);
    }


    ElasticDragDismissFrameLayout draggableFrame;

    private void setAppearanceSettings(Bundle bundle) {
        StoriesReaderSettings storiesReaderSettings = new StoriesReaderSettings(
                getIntent().getBooleanExtra(CS_CLOSE_ON_SWIPE, true),
                getIntent().getBooleanExtra(CS_CLOSE_ON_OVERSCROLL, true),
                getIntent().getIntExtra(CS_CLOSE_POSITION, 1),
                getIntent().getBooleanExtra(CS_HAS_LIKE, false),
                getIntent().getBooleanExtra(CS_HAS_FAVORITE, false),
                getIntent().getBooleanExtra(CS_HAS_SHARE, false),
                getIntent().getIntExtra(CS_FAVORITE_ICON, R.drawable.ic_stories_status_favorite),
                getIntent().getIntExtra(CS_LIKE_ICON, R.drawable.ic_stories_status_like),
                getIntent().getIntExtra(CS_DISLIKE_ICON, R.drawable.ic_stories_status_dislike),
                getIntent().getIntExtra(CS_SHARE_ICON, R.drawable.ic_share_status),
                getIntent().getIntExtra(CS_CLOSE_ICON, R.drawable.ic_stories_close),
                getIntent().getIntExtra(CS_REFRESH_ICON, R.drawable.ic_refresh),
                getIntent().getIntExtra(CS_SOUND_ICON, R.drawable.ic_stories_status_sound),
                getIntent().getBooleanExtra(CS_TIMER_GRADIENT_ENABLE, true)
        );
        try {
            bundle.putSerializable(CS_TIMER_GRADIENT,
                    getIntent().getSerializableExtra(CS_TIMER_GRADIENT));
            bundle.putInt(CS_STORY_READER_ANIMATION,
                    getIntent().getIntExtra(CS_STORY_READER_ANIMATION, 0));
            bundle.putString(CS_READER_SETTINGS, JsonParser.getJson(storiesReaderSettings));
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState1) {

        cleaned = false;
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onCreate(savedInstanceState1);
        if (InAppStoryService.isNull()) {
            finishWithoutAnimation();
            return;
        }

        ScreensManager.getInstance().currentScreen = this;
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 21) {
            int navColor = getIntent().getIntExtra(CS_NAVBAR_COLOR, Color.TRANSPARENT);
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            if (navColor != 0)
                getWindow().setNavigationBarColor(navColor);
        }

        closeOnSwipe = getIntent().getBooleanExtra(CS_CLOSE_ON_SWIPE, true);
        closeOnOverscroll = getIntent().getBooleanExtra(CS_CLOSE_ON_OVERSCROLL, true);

        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        setContentView(R.layout.cs_activity_stories);

        final Bundle savedInstanceState = savedInstanceState1;
        try {
            if (!getIntent().getBooleanExtra("statusBarVisibility", false)) {
                StatusBarController.hideStatusBar(StoriesFixedActivity.this, true);
            }
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            finish();
            return;
        }
        InAppStoryService.getInstance().getListReaderConnector().openReader();
        if (savedInstanceState == null) {
            storiesFragment = new StoriesFragment();
            if (getIntent().getExtras() != null) {
                Bundle bundle = new Bundle();
                bundle.putString("listID", getIntent().getStringExtra("listID"));
                bundle.putString("feedId", getIntent().getStringExtra("feedId"));
                bundle.putInt("source", getIntent().getIntExtra("source", 0));
                bundle.putInt("index", getIntent().getIntExtra("index", 0));
                bundle.putInt("slideIndex", getIntent().getIntExtra("slideIndex", 0));
                setAppearanceSettings(bundle);
                bundle.putIntegerArrayList("stories_ids", getIntent().getIntegerArrayListExtra("stories_ids"));
                storiesFragment.setArguments(bundle);
            }

        } else {
            storiesFragment = (StoriesFragment) getSupportFragmentManager().findFragmentByTag("STORIES_FRAGMENT");
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (storiesFragment != null) {
                    try {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction t = fragmentManager.beginTransaction()
                                .replace(R.id.fragments_layout, storiesFragment);
                        t.addToBackStack("STORIES_FRAGMENT");
                        t.commit();
                    } catch (IllegalStateException e) {
                        InAppStoryService.createExceptionLog(e);
                        finishWithoutAnimation();
                    }
                } else {
                    finishWithoutAnimation();
                }
            }
        }, 300);

        //      FragmentController.openFragment(StoriesActivity.this, storiesFragment);
    }

    @Override
    public void closeStoryReader(int action) {
        if (InAppStoryService.isNotNull()) {

            InAppStoryService.getInstance().getListReaderConnector().closeReader();
            Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(InAppStoryService.getInstance().getCurrentId());

            CsEventBus.getDefault().post(new CloseStory(story.id,
                    story.title, story.tags, story.getSlidesCount(),
                    story.lastIndex, action,
                    getIntent().getIntExtra("source", 0)));
            if (CallbackManager.getInstance().getCloseStoryCallback() != null) {
                CallbackManager.getInstance().getCloseStoryCallback().closeStory(
                        story.id,
                        story.title, story.tags, story.getSlidesCount(),
                        story.lastIndex, CallbackManager.getInstance().getCloseTypeFromInt(
                                action),
                        CallbackManager.getInstance().getSourceFromInt(
                                getIntent().getIntExtra("source", 0))
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
                case CloseStory.SWIPE:
                    cause = StatisticManager.SWIPE;
                    break;
            }
            StatisticManager.getInstance().sendCloseStory(story.id, cause,
                    story.lastIndex,
                    story.getSlidesCount(),
                    getIntent().getStringExtra("feedId"));
        }
        cleanReader();

        if (ScreensManager.getInstance().coordinates != null) animateFirst = true;
        else animateFirst = false;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
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

    public void cleanReader() {
        if (InAppStoryService.isNull()) return;
        if (cleaned) return;
        OldStatisticManager.getInstance().closeStatisticEvent();
        InAppStoryService.getInstance().setCurrentIndex(0);
        InAppStoryService.getInstance().setCurrentId(0);
        if (InAppStoryService.getInstance().getDownloadManager() != null) {
            InAppStoryService.getInstance().getDownloadManager().cleanStoriesIndex();
        }
        cleaned = true;
    }


    boolean closeOnSwipe = true;
    boolean closeOnOverscroll = true;

    @Override
    public void onDestroy() {
        if (ScreensManager.getInstance().currentScreen == this)
            ScreensManager.getInstance().currentScreen = null;
        if (!pauseDestroyed) {
            StatusBarController.showStatusBar(this);

            OldStatisticManager.getInstance().sendStatistic();
            ScreensManager.created = 0;
            cleanReader();
            System.gc();
            pauseDestroyed = true;
        }
        super.onDestroy();
    }
}
