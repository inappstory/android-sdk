package com.inappstory.sdk.stories.ui.reader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.R;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseReader;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.events.CloseStoryReaderEvent;

import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.widgets.elasticview.ElasticDragDismissFrameLayout;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StatusBarController;

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
import static com.inappstory.sdk.game.reader.GameActivity.GAME_READER_REQUEST;

public class StoriesActivity extends AppCompatActivity {

    public static long created = 0;
    public boolean pauseDestroyed = false;


    @Override
    public void onPause() {
        super.onPause();
        if (isFinishing()) {
            StatusBarController.showStatusBar(this);
            try {
                CsEventBus.getDefault().unregister(this);
            } catch (Exception e) {

            }
            created = 0;
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
            loadAnim();
        } else {
            switch (getIntent().getIntExtra(CS_READER_OPEN_ANIM, 1)) {
                case 0:
                    finishActivityWithCustomAnimation(R.anim.empty_animation, R.anim.alpha_fade_out);
                    break;
                case 1:
                    super.finish();
                    break;
                case 2:
                    finishActivityWithCustomAnimation(R.anim.empty_animation, R.anim.popup_hide);
                    break;
                default:
                    super.finish();
                    break;
            }
        }

    }

    boolean animateFirst = true;

    boolean isAnimation = false;

    public void loadAnim() {
        try {
            isAnimation = true;
            float x = draggableFrame.getX() + draggableFrame.getRight() / 2;
            float y = draggableFrame.getY();
            AnimationSet animationSet = new AnimationSet(true);
            Animation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, x, y);
            anim.setDuration(200);
            animationSet.addAnimation(anim);
            Point coordinates = ScreensManager.getInstance().coordinates;
            if (coordinates != null) {
                Animation anim2 = new TranslateAnimation(draggableFrame.getX(), coordinates.x -
                        Sizes.getScreenSize(StoriesActivity.this).x / 2,
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
                    StoriesActivity.super.finish();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
            draggableFrame.startAnimation(animationSet);
        } catch (Exception e) {
            finishActivityWithoutAnimation();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GAME_READER_REQUEST && resultCode == RESULT_OK) {
            storiesFragment.readerManager.gameComplete(
                    data.getStringExtra("gameState"),
                    Integer.parseInt(data.getStringExtra("storyId")),
                    data.getIntExtra("slideIndex", 0)
            );
        }
    }

    @Override
    public void onBackPressed() {
        if (isAnimation) return;
        blockView.setVisibility(View.VISIBLE);
        if (ScreensManager.getInstance().coordinates != null) animateFirst = true;
        else animateFirst = false;

        if (InAppStoryService.isNotNull()) {
            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(InAppStoryService.getInstance().getCurrentId());
            if (story != null) {
                CsEventBus.getDefault().post(new CloseStory(story.id,
                        story.title, story.tags, story.slidesCount,
                        story.lastIndex, CloseStory.CUSTOM,
                        getIntent().getIntExtra("source", 0)));
                if (CallbackManager.getInstance().getCloseStoryCallback() != null) {
                    CallbackManager.getInstance().getCloseStoryCallback().closeStory(
                            story.id,
                            story.title, story.tags, story.slidesCount,
                            story.lastIndex, CloseReader.CUSTOM,
                            CallbackManager.getInstance().getSourceFromInt(
                                    getIntent().getIntExtra("source", 0))
                    );
                }
            }
            String cause = StatisticManager.BACK;
            if (story != null)
                StatisticManager.getInstance().sendCloseStory(story.id, cause, story.lastIndex, story.slidesCount);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    public void finishActivityWithCustomAnimation(int enter, int exit) {
        super.finish();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        overridePendingTransition(enter, exit);
    }

    public void finishActivityWithoutAnimation() {
        super.finish();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        overridePendingTransition(0, 0);
    }


    ElasticDragDismissFrameLayout draggableFrame;
    View blockView;

    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    boolean closeOnSwipe = true;
    boolean closeOnOverscroll = true;


    public void shareComplete() {
        storiesFragment.readerManager.shareComplete();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState1) {

        cleaned = false;

        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        super.onCreate(savedInstanceState1);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int navColor = getIntent().getIntExtra(CS_NAVBAR_COLOR, Color.TRANSPARENT);
            if (navColor != 0)
                getWindow().setNavigationBarColor(navColor);
        }
        if (InAppStoryService.isNull()) {
            finishActivityWithoutAnimation();
            return;
        }

        ScreensManager.getInstance().currentActivity = this;
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        closeOnSwipe = getIntent().getBooleanExtra(CS_CLOSE_ON_SWIPE, true);
        closeOnOverscroll = getIntent().getBooleanExtra(CS_CLOSE_ON_OVERSCROLL, true);

        setContentView(R.layout.cs_activity_stories_draggable);
        draggableFrame = findViewById(R.id.draggable_frame);
        blockView = findViewById(R.id.blockView);
        //scrollView = findViewById(R.id.scrollContainer);
        if (Build.VERSION.SDK_INT >= 21) {
            chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(StoriesActivity.this) {
                @Override
                public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {
                    super.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels);
                }

                @Override
                public void onDragDismissed() {
                    if (ScreensManager.getInstance().coordinates != null) animateFirst = true;
                    else animateFirst = false;
                    CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.SWIPE));
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
        }
        draggableFrame.addListener(chromeFader);
        final Bundle savedInstanceState = savedInstanceState1;
        try {
            if (!getIntent().getBooleanExtra("statusBarVisibility", false) && !Sizes.isTablet()) {
                StatusBarController.hideStatusBar(StoriesActivity.this, true);
            }
        } catch (Exception e) {
            finish();
            return;
        }
        CsEventBus.getDefault().register(StoriesActivity.this);
        InAppStoryService.getInstance().getListReaderConnector().openReader();
        if (savedInstanceState == null) {
            //overridePendingTransition(R.anim.alpha_fade_in, R.anim.alpha_fade_out);
            //Log.e("stories_indexes", getIntent().getIntegerArrayListExtra("stories_ids").toString());
            storiesFragment = new StoriesFragment();
            if (getIntent().getExtras() != null) {
                Bundle bundle = new Bundle();
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
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment f = fragmentManager.findFragmentById(R.id.fragments_layout);
                    //     if (f != null && f.getFragmentTag().equals(newFragment.getFragmentTag())) return;
                    FragmentTransaction t = fragmentManager.beginTransaction()
                            .replace(R.id.fragments_layout, storiesFragment);
                    t.addToBackStack("STORIES_FRAGMENT");
                    t.commit();
                } else {
                    finish();
                }
            }
        }, 300);

        //      FragmentController.openFragment(StoriesActivity.this, storiesFragment);
    }

    StoriesFragment storiesFragment;
    StoriesReaderSettings storiesReaderSettings;

    private void setAppearanceSettings(Bundle bundle) {
        storiesReaderSettings = new StoriesReaderSettings(
                getIntent().getBooleanExtra(CS_CLOSE_ON_SWIPE, true),
                getIntent().getBooleanExtra(CS_CLOSE_ON_OVERSCROLL, true),
                getIntent().getIntExtra(CS_CLOSE_POSITION, 1),
                //,
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
                getIntent().getBooleanExtra(CS_TIMER_GRADIENT, true)
        );
        try {
            bundle.putInt(CS_STORY_READER_ANIMATION, getIntent().getIntExtra(CS_STORY_READER_ANIMATION, 0));
            bundle.putString(CS_READER_SETTINGS, JsonParser.getJson(storiesReaderSettings));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean closing = false;

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void closeStoryReaderEvent(CloseStoryReaderEvent event) {
        if (closing) return;
        closing = true;
        InAppStoryService.getInstance().getListReaderConnector().closeReader();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        blockView.setVisibility(View.VISIBLE);
        if (InAppStoryService.isNotNull()) {
            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(InAppStoryService.getInstance().getCurrentId());

            CsEventBus.getDefault().post(new CloseStory(story.id,
                    story.title, story.tags, story.slidesCount,
                    story.lastIndex, event.getAction(),
                    getIntent().getIntExtra("source", 0)));
            if (CallbackManager.getInstance().getCloseStoryCallback() != null) {
                CallbackManager.getInstance().getCloseStoryCallback().closeStory(
                        story.id,
                        story.title, story.tags, story.slidesCount,
                        story.lastIndex, CallbackManager.getInstance().getCloseTypeFromInt(
                                event.getAction()),
                        CallbackManager.getInstance().getSourceFromInt(
                                getIntent().getIntExtra("source", 0))
                );
            }
            String cause = StatisticManager.AUTO;
            switch (event.getAction()) {
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
            StatisticManager.getInstance().sendCloseStory(story.id, cause, story.lastIndex, story.slidesCount);
        }
        cleanReader();
        CsEventBus.getDefault().unregister(this);

        if (ScreensManager.getInstance().coordinates != null) animateFirst = true;
        else animateFirst = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    boolean cleaned = false;

    public void cleanReader() {
        if (InAppStoryService.isNull()) return;
        if (cleaned) return;
        //  OldStatisticManager.getInstance().closeStatisticEvent();
        InAppStoryService.getInstance().setCurrentIndex(0);
        InAppStoryService.getInstance().setCurrentId(0);
        if (InAppStoryService.getInstance().getDownloadManager() != null)
            for (Story story : InAppStoryService.getInstance().getDownloadManager().getStories()) {
                story.setLastIndex(0);
            }
        cleaned = true;
    }


    @Override
    public void onDestroy() {
        if (ScreensManager.getInstance().currentActivity == this)
            ScreensManager.getInstance().currentActivity = null;
        if (!pauseDestroyed) {

            StatusBarController.showStatusBar(this);

            OldStatisticManager.getInstance().sendStatistic();
            try {
                CsEventBus.getDefault().unregister(this);
            } catch (Exception e) {

            }

            created = 0;
            cleanReader();
            System.gc();
            pauseDestroyed = true;
        }
        super.onDestroy();
    }
}
