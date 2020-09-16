package io.casestory.sdk.stories.ui.reader;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.eventbus.Subscribe;
import io.casestory.sdk.stories.events.CloseStoryReaderEvent;
import io.casestory.sdk.stories.events.SwipeRightEvent;
import io.casestory.sdk.stories.events.WidgetTapEvent;
import io.casestory.sdk.stories.utils.Sizes;

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
        setContentView(R.layout.activity_stories);
        final Bundle savedInstanceState = savedInstanceState1;
        try {
            if (!getIntent().getBooleanExtra("statusBarVisibility", false) && !Sizes.isTablet(StoriesActivity.this)) {
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
                //bundle.putStringArrayList("content", getIntent().getStringArrayListExtra("contentLayouts"));
                storiesFragment.setArguments(bundle);
            }

        } else {
            storiesFragment = (StoriesFragment) getSupportFragmentManager().findFragmentByTag("STORIES_FRAGMENT");
        }

        FragmentController.openFragment(StoriesActivity.this, storiesFragment);
    }

    @Subscribe
    public void closeNarrativeEvent(CloseNarrativeEvent event) {

        //Log.e("closeStatistic", "closeNarrativeEvent");
        StoriesManager.getInstance().closeStatisticEvent();
        StoriesManager.getInstance().currentId = 0;
        StoriesManager.getInstance().isBackgroundPause = false;
        StoriesManager.getInstance().currentIndex = 0;
        StoriesManager.getInstance().currentNarrativeFragment.storiesProgressView.current = 0;
        ArrayList<Narrative> currentNarratives = (getIntent().getBooleanExtra("onboarding", false) ?
                StoriesManager.getInstance().onboardNarratives : StoriesManager.getInstance().narratives);
        for (Narrative narrative : currentNarratives) {
            narrative.lastIndex = 0;
        }
        finish();
    }

    @Subscribe
    public void swipeDownEvent(SwipeDownEvent event) {
        if (getIntent().getBooleanExtra("closeOnSwipe", false) && StoriesManager.getInstance().closeOnSwipe) {
            boolean isOnboarding = StoriesManager.getInstance().isOnboardingOpened;
            finishActivityWithCustomAnimation(0, R.anim.popup_hide);
            EventBus.getDefault().post(new CloseNarrativeEvent(isOnboarding));
        }
    }

    @Subscribe
    public void swipeLeftEvent(SwipeLeftEvent event) {
        if (StoriesManager.getInstance().closeOnOverscroll) {
            boolean isOnboarding = StoriesManager.getInstance().isOnboardingOpened;
            finishActivityWithCustomAnimation(0, R.anim.popup_hide_left);
            EventBus.getDefault().post(new CloseNarrativeEvent(isOnboarding));
        }
    }

    @Subscribe
    public void swipeRightEvent(SwipeRightEvent event) {
        if (StoriesManager.getInstance().closeOnOverscroll) {
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
