package com.inappstory.sdk.stories.ui.reader;

import static com.inappstory.sdk.AppearanceManager.ANIMATION_CUBE;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_OVERSCROLL;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static com.inappstory.sdk.AppearanceManager.CS_NAVBAR_COLOR;
import static com.inappstory.sdk.AppearanceManager.CS_READER_BACKGROUND_COLOR;
import static com.inappstory.sdk.AppearanceManager.CS_READER_OPEN_ANIM;
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
import android.util.Log;
import android.view.View;
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
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.inappstory.sdk.InAppStoryManager;
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
import com.inappstory.sdk.utils.StringsUtils;

import java.util.Set;

public class StoriesActivity extends AppCompatActivity implements BaseReaderScreen {

    public boolean pauseDestroyed = false;


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

    }

    boolean animateFirst = true;

    boolean isAnimation = false;

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarController.hideStatusBar(this, true);
    }

    public void startAnim() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        backTintView.setVisibility(View.GONE);
        try {
            isAnimation = true;
            draggableFrame.setVisibility(View.INVISIBLE);
            float x = Sizes.getScreenSize().x / 2f;
            float y = draggableFrame.getY();
            AnimationSet animationSet = new AnimationSet(true);
            Animation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, x, y);
            animationSet.addAnimation(anim);
            animationSet.setDuration(300);
            animationSet.setStartOffset(200);
            animationSet.setInterpolator(new LinearOutSlowInInterpolator());
            Point coordinates = ScreensManager.getInstance().coordinates;
            if (coordinates != null) {
                Animation anim2 = new TranslateAnimation(coordinates.x -
                        Sizes.getScreenSize(StoriesActivity.this).x / 2, 0f,
                        coordinates.y - draggableFrame.getY(), 0f);
                animationSet.addAnimation(anim2);

            }
            animationSet.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    draggableFrame.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    draggableFrame.setVisibility(View.VISIBLE);
                    backTintView.setVisibility(View.VISIBLE);
                    isAnimation = false;
                }
            });
            draggableFrame.startAnimation(animationSet);
        } catch (Exception e) {
            finishWithoutAnimation();
        }
    }

    public void closeAnim() {
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
            finishWithoutAnimation();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GAME_READER_REQUEST && resultCode == RESULT_OK) {
            if (storiesFragment == null || storiesFragment.readerManager == null) return;
            Set<String> keys = data.getExtras().keySet();
            for (String key: keys) {
                Log.e("onActivityResult", key + ": " + data.getExtras().get(key).toString());
            }
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
            Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(InAppStoryService.getInstance().getCurrentId(), type);
            if (story != null) {
                CsEventBus.getDefault().post(new CloseStory(story.id,
                        story.title, story.tags, story.getSlidesCount(),
                        story.lastIndex, CloseStory.CUSTOM,
                        getIntent().getIntExtra("source", 0)));
                if (CallbackManager.getInstance().getCloseStoryCallback() != null) {
                    CallbackManager.getInstance().getCloseStoryCallback().closeStory(
                            story.id,
                            StringsUtils.getNonNull(story.title), StringsUtils.getNonNull(story.tags), story.getSlidesCount(),
                            story.lastIndex, CloseReader.CUSTOM,
                            CallbackManager.getInstance().getSourceFromInt(
                                    getIntent().getIntExtra("source", 0))
                    );
                }
            }
            String cause = StatisticManager.BACK;
            if (story != null)
                StatisticManager.getInstance().sendCloseStory(story.id, cause,
                        story.lastIndex, story.getSlidesCount(),
                        getIntent().getStringExtra("feedId"));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
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

    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    boolean closeOnSwipe = true;
    boolean closeOnOverscroll = true;

    Story.StoryType type = Story.StoryType.COMMON;

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
    protected void onCreate(Bundle savedInstanceState1) {

        cleaned = false;
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onCreate(savedInstanceState1);
        setContentView(R.layout.cs_activity_stories_draggable);
        if (InAppStoryManager.isNull() || InAppStoryService.isNull()) {
            finish();
            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int navColor = getIntent().getIntExtra(CS_NAVBAR_COLOR, Color.TRANSPARENT);
            if (navColor != 0)
                getWindow().setNavigationBarColor(navColor);
        }
        ScreensManager.getInstance().currentScreen = this;
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
        //scrollView = findViewById(R.id.scrollContainer);
        if (Build.VERSION.SDK_INT >= 21) {
            chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(StoriesActivity.this) {
                @Override
                public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {
                    super.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels);
                    backTintView.setAlpha(Math.min(1f, Math.max(0f, 1f - rawOffset)));
                }

                @Override
                public void onDragDismissed() {
                    if (ScreensManager.getInstance().coordinates != null) animateFirst = true;
                    else animateFirst = false;
                    InAppStoryManager.closeStoryReader(CloseStory.SWIPE);
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
            if (!getIntent().getBooleanExtra("statusBarVisibility", false)) {
                StatusBarController.hideStatusBar(StoriesActivity.this, true);
            }
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            finish();
            return;
        }
        InAppStoryService.getInstance().getListReaderConnector().openReader();
        String stStoriesType = getIntent().getStringExtra("storiesType");
        if (stStoriesType != null) {
            if (stStoriesType.equals(Story.StoryType.UGC.name()))
                type = Story.StoryType.UGC;
            draggableFrame.type = type;
        }
        if (savedInstanceState == null) {
            storiesFragment = new StoriesFragment();
            if (getIntent().getExtras() != null) {
                Bundle bundle = new Bundle();

                bundle.putString("storiesType", getIntent().getStringExtra("storiesType"));
                bundle.putInt("source", getIntent().getIntExtra("source", 0));
                bundle.putString("listID", getIntent().getStringExtra("listID"));
                bundle.putString("feedId", getIntent().getStringExtra("feedId"));
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
    public void closeStoryReader(int action) {
        if (closing) return;
        backTintView.setVisibility(View.GONE);
        closing = true;
        InAppStoryService.getInstance().getListReaderConnector().closeReader();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        blockView.setVisibility(View.VISIBLE);
        if (InAppStoryService.isNotNull()) {
            Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(InAppStoryService.getInstance().getCurrentId(), type);
            if (story != null) {
                CsEventBus.getDefault().post(new CloseStory(story.id,
                        story.title, story.tags, story.getSlidesCount(),
                        story.lastIndex, action,
                        getIntent().getIntExtra("source", 0)));
                if (CallbackManager.getInstance().getCloseStoryCallback() != null) {
                    CallbackManager.getInstance().getCloseStoryCallback().closeStory(
                            story.id,
                            StringsUtils.getNonNull(story.title), StringsUtils.getNonNull(story.tags), story.getSlidesCount(),
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
                StatisticManager.getInstance().sendCloseStory(story.id, cause, story.lastIndex,
                        story.getSlidesCount(),
                        getIntent().getStringExtra("feedId"));
            }
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
        //  OldStatisticManager.getInstance().closeStatisticEvent();
        InAppStoryService.getInstance().setCurrentIndex(0);
        InAppStoryService.getInstance().setCurrentId(0);
        if (InAppStoryService.getInstance().getDownloadManager() != null) {
            InAppStoryService.getInstance().getDownloadManager().cleanStoriesIndex(type);
        }
        cleaned = true;
    }


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
