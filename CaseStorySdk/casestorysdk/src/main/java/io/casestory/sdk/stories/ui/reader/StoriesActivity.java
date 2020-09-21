package io.casestory.sdk.stories.ui.reader;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.eventbus.Subscribe;
import io.casestory.sdk.eventbus.ThreadMode;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.CloseStoriesReaderEvent;
import io.casestory.sdk.stories.events.CloseStoryReaderEvent;
import io.casestory.sdk.stories.events.OpenStoriesScreenEvent;
import io.casestory.sdk.stories.events.SwipeDownEvent;
import io.casestory.sdk.stories.events.SwipeLeftEvent;
import io.casestory.sdk.stories.events.SwipeRightEvent;
import io.casestory.sdk.stories.events.WidgetTapEvent;
import io.casestory.sdk.stories.utils.Sizes;
import io.casestory.sdk.stories.utils.StatusBarController;

public class StoriesActivity extends AppCompatActivity {
    @Override
    public void finish() {
        switch (getIntent().getIntExtra("narrativesOpenAnimation", 1)) {
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

    public void finishActivityWithCustomAnimation(int enter, int exit) {
        super.finish();
        overridePendingTransition(enter, exit);
    }

    @Subscribe
    public void widgetTapEvent(WidgetTapEvent event) {
        if (!getIntent().getBooleanExtra("statusBarVisibility", false) && !Sizes.isTablet()) {
           // StatusBarController.hideStatusBar(this, true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState1) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (CaseStoryManager.getInstance() == null) return;
        if (CaseStoryService.getInstance() == null) return;
        super.onCreate(savedInstanceState1);
        setContentView(R.layout.cs_activity_stories);
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
                bundle.putInt("narrativesSwitchAnimation", getIntent().getIntExtra("narrativesSwitchAnimation", 0));
                bundle.putBoolean("closeOnSwipe", getIntent().getBooleanExtra("closeOnSwipe", false));
                bundle.putBoolean("onboarding", getIntent().getBooleanExtra("onboarding", false));
                bundle.putInt("closePosition", getIntent().getIntExtra("closePosition", 1));
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void closeStoryReaderEvent(CloseStoryReaderEvent event) {
        CaseStoryService.getInstance().closeStatisticEvent();
        CaseStoryService.getInstance().setCurrentIndex(0);
        CaseStoryService.getInstance().setCurrentId(0);
        CaseStoryService.getInstance().isBackgroundPause = false;
        for (Story story : StoryDownloader.getInstance().getStories())
            story.lastIndex = 0;
        finish();
    }

    @Subscribe
    public void swipeDownEvent(SwipeDownEvent event) {
        if (getIntent().getBooleanExtra("closeOnSwipe", false)
                && CaseStoryManager.getInstance().closeOnSwipe()) {
            finishActivityWithCustomAnimation(0, R.anim.popup_hide);
            EventBus.getDefault().post(new CloseStoryReaderEvent(false));
        }
    }

    @Subscribe
    public void swipeLeftEvent(SwipeLeftEvent event) {
        if (CaseStoryManager.getInstance().closeOnOverscroll()) {
            finishActivityWithCustomAnimation(0, R.anim.popup_hide_left);
            EventBus.getDefault().post(new CloseStoryReaderEvent(false));
        }
    }

    @Subscribe
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
