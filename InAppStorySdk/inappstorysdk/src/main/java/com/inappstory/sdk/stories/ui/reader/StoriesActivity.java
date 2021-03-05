package com.inappstory.sdk.stories.ui.reader;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.cache.StoryDownloader;
import com.inappstory.sdk.stories.events.CloseStoryReaderEvent;
import com.inappstory.sdk.stories.events.OpenStoriesScreenEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.events.SwipeDownEvent;
import com.inappstory.sdk.stories.events.SwipeLeftEvent;
import com.inappstory.sdk.stories.events.SwipeRightEvent;
import com.inappstory.sdk.stories.events.WidgetTapEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.ui.widgets.elasticview.ElasticDragDismissFrameLayout;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StatusBarController;

import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static com.inappstory.sdk.AppearanceManager.CS_READER_OPEN_ANIM;
import static com.inappstory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;

public class StoriesActivity extends AppCompatActivity {

    public static long destroyed = 0;
    public boolean pauseDestroyed = false;


    @Override
    public void onPause() {
        super.onPause();
        if (isFinishing()) {
            StatusBarController.showStatusBar(this);

            OldStatisticManager.getInstance().sendStatistic();
            try {
                CsEventBus.getDefault().unregister(this);
            } catch (Exception e) {

            }
            if (!isFakeActivity) {
                destroyed = 0;
                cleanReader();
            }
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
        if (animateFirst) {
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


    public void loadAnim() {
        try {
            float x = draggableFrame.getX() + draggableFrame.getRight() / 2;
            float y = draggableFrame.getY();
            AnimationSet animationSet = new AnimationSet(true);
            Animation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, x, y);
            anim.setDuration(200);
            animationSet.addAnimation(anim);
            if (InAppStoryManager.getInstance().coordinates != null) {
                Animation anim2 = new TranslateAnimation(draggableFrame.getX(), InAppStoryManager.getInstance().coordinates.x - Sizes.getScreenSize().x / 2,
                        0f, InAppStoryManager.getInstance().coordinates.y - draggableFrame.getY());
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
                }
            });
            draggableFrame.startAnimation(animationSet);
        } catch (Exception e) {
            finishActivityWithoutAnimation();
        }

    }

    @Override
    public void onBackPressed() {

        if (InAppStoryManager.getInstance().coordinates != null) animateFirst = true;
        else animateFirst = false;

        if (InAppStoryService.getInstance() != null) {
            Story story = StoryDownloader.getInstance().getStoryById(InAppStoryService.getInstance().getCurrentId());

            CsEventBus.getDefault().post(new CloseStory(story.id,
                    story.title, story.tags, story.slidesCount,
                    story.lastIndex, CloseStory.CUSTOM,
                    getIntent().getIntExtra("source", 0)));
            String cause = StatisticManager.BACK;
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
        overridePendingTransition(enter, exit);
    }

    public void finishActivityWithoutAnimation() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @CsSubscribe
    public void widgetTapEvent(WidgetTapEvent event) {
        if (!getIntent().getBooleanExtra("statusBarVisibility", false) && !Sizes.isTablet()) {
            // StatusBarController.hideStatusBar(this, true);
        }
    }


    ElasticDragDismissFrameLayout draggableFrame;

    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    @Override
    protected void onCreate(Bundle savedInstanceState1) {

        cleaned = false;
        if (destroyed == -1) {
            isFakeActivity = true;
            super.onCreate(savedInstanceState1);
            finishActivityWithoutAnimation();
            return;
        }
        destroyed = -1;
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (InAppStoryManager.getInstance() == null) return;
        if (InAppStoryService.getInstance() == null) return;
        super.onCreate(savedInstanceState1);

        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            CsEventBus.getDefault().post(new ResumeStoryReaderEvent(true));
        }
        if (AppearanceManager.getInstance() != null) {
            setContentView(AppearanceManager.getInstance().csIsDraggable() ?
                    R.layout.cs_activity_stories_draggable : R.layout.cs_activity_stories);
        } else {
            setContentView(R.layout.cs_activity_stories_draggable);
        }
        draggableFrame = findViewById(R.id.draggable_frame);
        //scrollView = findViewById(R.id.scrollContainer);
        if (Build.VERSION.SDK_INT >= 21) {
            chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(StoriesActivity.this) {
                @Override
                public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {
                    super.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels);
                }

                @Override
                public void onDragDismissed() {
                    if (InAppStoryManager.getInstance().coordinates != null) animateFirst = true;
                    else animateFirst = false;
                    CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.SWIPE));
                }

                @Override
                public void onDragDropped() {
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
        final StoriesFragment storiesFragment;

        CsEventBus.getDefault().register(StoriesActivity.this);
        CsEventBus.getDefault().post(new OpenStoriesScreenEvent());
        if (savedInstanceState == null) {
            //overridePendingTransition(R.anim.alpha_fade_in, R.anim.alpha_fade_out);
            //Log.e("stories_indexes", getIntent().getIntegerArrayListExtra("stories_ids").toString());
            storiesFragment = new StoriesFragment();
            if (getIntent().getExtras() != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("source", getIntent().getIntExtra("source", 0));
                bundle.putInt("index", getIntent().getIntExtra("index", 0));
                bundle.putBoolean("canUseNotLoaded", getIntent().getBooleanExtra("canUseNotLoaded", false));
                bundle.putInt(CS_STORY_READER_ANIMATION, getIntent().getIntExtra(CS_STORY_READER_ANIMATION, 0));
                bundle.putBoolean(CS_CLOSE_ON_SWIPE, getIntent().getBooleanExtra(CS_CLOSE_ON_SWIPE, false));
                bundle.putBoolean("onboarding", getIntent().getBooleanExtra("onboarding", false));
                bundle.putInt(CS_CLOSE_POSITION, getIntent().getIntExtra(CS_CLOSE_POSITION, 1));
                bundle.putIntegerArrayList("stories_ids", getIntent().getIntegerArrayListExtra("stories_ids"));
                storiesFragment.setArguments(bundle);
            }

        } else {
            storiesFragment = (StoriesFragment) getSupportFragmentManager().findFragmentByTag("STORIES_FRAGMENT");
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment f = fragmentManager.findFragmentById(R.id.fragments_layout);
                //     if (f != null && f.getFragmentTag().equals(newFragment.getFragmentTag())) return;
                FragmentTransaction t = fragmentManager.beginTransaction()
                        .replace(R.id.fragments_layout, storiesFragment);
                t.addToBackStack("STORIES_FRAGMENT");
                t.commit();
            }
        }, 300);

        //      FragmentController.openFragment(StoriesActivity.this, storiesFragment);
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void closeStoryReaderEvent(CloseStoryReaderEvent event) {
        if (InAppStoryService.getInstance() != null) {
            Story story = StoryDownloader.getInstance().getStoryById(InAppStoryService.getInstance().getCurrentId());

            CsEventBus.getDefault().post(new CloseStory(story.id,
                    story.title, story.tags, story.slidesCount,
                    story.lastIndex, event.getAction(),
                    getIntent().getIntExtra("source", 0)));
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

        if (InAppStoryManager.getInstance().coordinates != null) animateFirst = true;
        else animateFirst = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    boolean cleaned = false;

    public void cleanReader() {
        if (InAppStoryService.getInstance() == null) return;
        if (cleaned) return;
        OldStatisticManager.getInstance().closeStatisticEvent();
        InAppStoryService.getInstance().setCurrentIndex(0);
        InAppStoryService.getInstance().setCurrentId(0);
        InAppStoryService.getInstance().isBackgroundPause = false;
        for (Story story : StoryDownloader.getInstance().getStories())
            story.lastIndex = 0;
        cleaned = true;
    }

    @CsSubscribe
    public void swipeDownEvent(SwipeDownEvent event) {
        if (getIntent().getBooleanExtra(CS_CLOSE_ON_SWIPE, false)
                && InAppStoryManager.getInstance().closeOnSwipe()) {
            //finishActivityWithCustomAnimation(0, R.anim.popup_hide);
            CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.SWIPE));
        }
    }

    @CsSubscribe
    public void swipeLeftEvent(SwipeLeftEvent event) {
        if (InAppStoryManager.getInstance().closeOnOverscroll()) {
            // finishActivityWithCustomAnimation(0, R.anim.popup_hide_left);
            CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.SWIPE));
        }
    }

    @CsSubscribe
    public void swipeRightEvent(SwipeRightEvent event) {
        if (InAppStoryManager.getInstance().closeOnOverscroll()) {
            //  finishActivityWithCustomAnimation(0, R.anim.popup_hide_right);
            CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.SWIPE));
        }
    }


    @Override
    public void onDestroy() {
        if (!pauseDestroyed) {
            StatusBarController.showStatusBar(this);

            OldStatisticManager.getInstance().sendStatistic();
            try {
                CsEventBus.getDefault().unregister(this);
            } catch (Exception e) {

            }
            if (!isFakeActivity) {
                destroyed = 0;
                cleanReader();
            }
            System.gc();
            pauseDestroyed = true;
        }
        super.onDestroy();
    }
}
