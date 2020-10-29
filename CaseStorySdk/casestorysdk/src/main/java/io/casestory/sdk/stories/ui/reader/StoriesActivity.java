package io.casestory.sdk.stories.ui.reader;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
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

import io.casestory.casestorysdk.R;
import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.eventbus.CsSubscribe;
import io.casestory.sdk.eventbus.ThreadMode;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.CloseStoriesReaderEvent;
import io.casestory.sdk.stories.events.CloseStoryReaderEvent;
import io.casestory.sdk.stories.events.OpenStoriesScreenEvent;
import io.casestory.sdk.stories.events.ResumeStoryReaderEvent;
import io.casestory.sdk.stories.events.SwipeDownEvent;
import io.casestory.sdk.stories.events.SwipeLeftEvent;
import io.casestory.sdk.stories.events.SwipeRightEvent;
import io.casestory.sdk.stories.events.WidgetTapEvent;
import io.casestory.sdk.stories.ui.widgets.elasticview.ElasticDragDismissFrameLayout;
import io.casestory.sdk.stories.utils.Sizes;
import io.casestory.sdk.stories.utils.StatusBarController;

import static io.casestory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static io.casestory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static io.casestory.sdk.AppearanceManager.CS_READER_OPEN_ANIM;
import static io.casestory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;

public class StoriesActivity extends AppCompatActivity {

    public static long destroyed = 0;

    @Override
    public void onPause() {
        Log.e("startTimer", "onPauseA");
        super.onPause();
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
        float x = draggableFrame.getX() + draggableFrame.getRight() / 2;
        float y = draggableFrame.getY();
        AnimationSet animationSet = new AnimationSet(true);
        Animation anim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, x, y);
        anim.setDuration(200);
        animationSet.addAnimation(anim);
        if (CaseStoryManager.getInstance().coordinates != null) {
            Animation anim2 = new TranslateAnimation(draggableFrame.getX(), CaseStoryManager.getInstance().coordinates.x - Sizes.getScreenSize().x/2,
                    0f, CaseStoryManager.getInstance().coordinates.y - draggableFrame.getY());
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

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void finishActivityWithCustomAnimation(int enter, int exit) {
        super.finish();
        overridePendingTransition(enter, exit);
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
        destroyed = -1;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (CaseStoryManager.getInstance() == null) return;
        if (CaseStoryService.getInstance() == null) return;
        super.onCreate(savedInstanceState1);

        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            EventBus.getDefault().post(new ResumeStoryReaderEvent(true));
        }

        setContentView(R.layout.cs_activity_stories);
        draggableFrame = findViewById(R.id.draggable_frame);
        if (Build.VERSION.SDK_INT >= 21) {
            chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(StoriesActivity.this) {
                @Override
                public void onDragDismissed() {
                    if (CaseStoryManager.getInstance().coordinates != null) animateFirst = true;
                    else animateFirst = false;
                    finishAfterTransition();
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
        StoriesFragment storiesFragment;

        EventBus.getDefault().register(StoriesActivity.this);
        EventBus.getDefault().post(new OpenStoriesScreenEvent());
        if (savedInstanceState == null) {
            //overridePendingTransition(R.anim.alpha_fade_in, R.anim.alpha_fade_out);
            storiesFragment = new StoriesFragment();
            if (getIntent().getExtras() != null) {
                Bundle bundle = new Bundle();
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.fragments_layout);
        //     if (f != null && f.getFragmentTag().equals(newFragment.getFragmentTag())) return;
        FragmentTransaction t = fragmentManager.beginTransaction()
                .replace(R.id.fragments_layout, storiesFragment);
        t.addToBackStack("STORIES_FRAGMENT");
        t.commit();

        //      FragmentController.openFragment(StoriesActivity.this, storiesFragment);
    }

    @CsSubscribe(threadMode = ThreadMode.MAIN)
    public void closeStoryReaderEvent(CloseStoryReaderEvent event) {
        CaseStoryService.getInstance().closeStatisticEvent();
        CaseStoryService.getInstance().setCurrentIndex(0);
        CaseStoryService.getInstance().setCurrentId(0);
        CaseStoryService.getInstance().isBackgroundPause = false;
        for (Story story : StoryDownloader.getInstance().getStories())
            story.lastIndex = 0;
        finish();
    }

    @CsSubscribe
    public void swipeDownEvent(SwipeDownEvent event) {
        if (getIntent().getBooleanExtra(CS_CLOSE_ON_SWIPE, false)
                && CaseStoryManager.getInstance().closeOnSwipe()) {
            finishActivityWithCustomAnimation(0, R.anim.popup_hide);
            EventBus.getDefault().post(new CloseStoryReaderEvent(false));
        }
    }

    @CsSubscribe
    public void swipeLeftEvent(SwipeLeftEvent event) {
        if (CaseStoryManager.getInstance().closeOnOverscroll()) {
            finishActivityWithCustomAnimation(0, R.anim.popup_hide_left);
            EventBus.getDefault().post(new CloseStoryReaderEvent(false));
        }
    }

    @CsSubscribe
    public void swipeRightEvent(SwipeRightEvent event) {
        if (CaseStoryManager.getInstance().closeOnOverscroll()) {
            finishActivityWithCustomAnimation(0, R.anim.popup_hide_right);
            EventBus.getDefault().post(new CloseStoryReaderEvent(false));
        }
    }


    @Override
    public void onDestroy() {
        StatusBarController.showStatusBar(this);
        EventBus.getDefault().post(new CloseStoriesReaderEvent());
        EventBus.getDefault().unregister(this);
        System.gc();
        super.onDestroy();
    }
}
