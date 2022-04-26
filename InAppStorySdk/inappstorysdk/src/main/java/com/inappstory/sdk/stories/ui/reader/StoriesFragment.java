package com.inappstory.sdk.stories.ui.reader;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager.ReaderPagerAdapter;
import com.inappstory.sdk.stories.utils.BackPressHandler;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.stories.utils.StatusBarController;

import java.io.Serializable;
import java.util.List;

import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_OVERSCROLL;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static com.inappstory.sdk.AppearanceManager.CS_READER_SETTINGS;
import static com.inappstory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT;

public class StoriesFragment extends Fragment implements BackPressHandler, ViewPager.OnPageChangeListener {

    public StoriesFragment() {
        super();
    }

    boolean isDestroyed = false;

    boolean created = false;

    boolean backPaused = false;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (isDestroyed) return;
        if (positionOffset == 0f) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    invMask.setVisibility(View.GONE);
                }
            }, 400);
        } else {
            if (invMask.getVisibility() != View.VISIBLE) {
                invMask.setVisibility(View.VISIBLE);
            }
        }
        storiesViewPager.pageScrolled(positionOffset);
    }

    public void removeStoryFromFavorite(int id) {
        if (readerManager != null)
            readerManager.removeStoryFromFavorite(id);
    }


    public void removeAllStoriesFromFavorite() {
        if (readerManager != null)
            readerManager.removeAllStoriesFromFavorite();
    }


    @Override
    public void onPageSelected(int position) {
        if (isDestroyed) return;

        readerManager.onPageSelected(getArguments().getInt("source", 0), position);
        if (getArguments() != null) {
            getArguments().putInt("index", position);
        }

    }

    public ReaderManager readerManager;

    ReaderPagerAdapter outerViewPagerAdapter;
    View invMask;

    List<Integer> currentIds;
    boolean closeOnSwipe = true;
    boolean closeOnOverscroll = true;

    String readerSettings;
    Serializable timerGradient;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (InAppStoryService.isNull() && !Sizes.isTablet()) {
            if (getActivity() != null) getActivity().finish();
            return;
        }
        readerManager = new ReaderManager(getArguments().getString("listID", null));
        readerManager.setParentFragment(this);
        if (isDestroyed) return;
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        readerSettings = getArguments().getString(CS_READER_SETTINGS);
        timerGradient = getArguments().getSerializable(CS_TIMER_GRADIENT);
        storiesViewPager.setParameters(
                getArguments().getInt(CS_STORY_READER_ANIMATION, 0));
        currentIds = getArguments().getIntegerArrayList("stories_ids");
        if (currentIds == null || currentIds.isEmpty()) {
            if (getActivity() != null && !Sizes.isTablet()) getActivity().finish();
            return;
        }
        readerManager.setStoriesIds(currentIds);
        outerViewPagerAdapter =
                new ReaderPagerAdapter(
                        getChildFragmentManager(),
                        readerSettings,
                        timerGradient,
                        currentIds, readerManager);
        storiesViewPager.setAdapter(outerViewPagerAdapter);
        storiesViewPager.addOnPageChangeListener(this);
        int ind = getArguments().getInt("index", 0);
        readerManager.firstStoryId = currentIds.get(ind);
        readerManager.startedSlideInd = getArguments().getInt("slideIndex", 0);
        if (ind > 0) {
            storiesViewPager.setCurrentItem(ind);
        } else {
            try {
                onPageSelected(0);
            } catch (Exception e) {

            }
        }
        storiesViewPager.getAdapter().notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        if (!isDestroyed) {
            if (InAppStoryService.isNotNull()) {
                OldStatisticManager.getInstance().currentEvent = null;
            }
            isDestroyed = true;
        }
        getArguments().putBoolean("isDestroyed", true);
        super.onDestroyView();
    }

    public void pause() {
        if (!isDestroyed) {
            backPaused = true;
            readerManager.pauseCurrent(true);
        }
    }

    public void resume() {
        if (!isDestroyed) {
            backPaused = false;
            if (!created)
                readerManager.resumeCurrent(true);
            if (!Sizes.isTablet())
                StatusBarController.hideStatusBar(getActivity(), true);
            created = false;
            readerManager.resumeWithShareId();
        }
    }

    @Override
    public void onPause() {
        pause();
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    ReaderPager storiesViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isDestroyed = getArguments().getBoolean("isDestroyed");
        created = true;
        closeOnSwipe = getArguments().getBoolean(CS_CLOSE_ON_SWIPE, true);
        closeOnOverscroll = getArguments().getBoolean(CS_CLOSE_ON_OVERSCROLL, true);
        RelativeLayout resView = new RelativeLayout(getContext());
        //   resView.setBackgroundColor(getResources().getColor(R.color.black));
        storiesViewPager = new ReaderPager(getContext());
        storiesViewPager.setHost(this);
        storiesViewPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        invMask = new View(getContext());
        invMask.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        invMask.setVisibility(View.GONE);
        storiesViewPager.setId(R.id.ias_stories_pager);
        invMask.setId(R.id.ias_inv_mask);
        invMask.setClickable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            storiesViewPager.setElevation(4);
            invMask.setElevation(10);
        }
        resView.addView(storiesViewPager);
        resView.addView(invMask);
        return resView;//inflater.inflate(R.layout.cs_fragment_stories, container, false);
    }


    public void swipeUpEvent() {
        swipeUpEvent(storiesViewPager.getCurrentItem());
    }

    public void swipeUpEvent(int position) {
        readerManager.swipeUp(position);
    }

    public void swipeDownEvent() {
        swipeDownEvent(storiesViewPager.getCurrentItem());
    }

    public void swipeDownEvent(int position) {
        swipeCloseEvent(position, closeOnSwipe);
    }


    public void swipeLeftEvent(int position) {
        swipeCloseEvent(position, closeOnOverscroll);
    }

    public void swipeRightEvent(int position) {
        swipeCloseEvent(position, closeOnOverscroll);
    }

    public void swipeCloseEvent(int position, boolean check) {
        if (check) {
            Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(currentIds.get(position));
            if (story == null || story.disableClose) return;
            InAppStoryManager.closeStoryReader(CloseStory.SWIPE);
        }
    }


    @Override
    public void onResume() {
        resume();
        super.onResume();
    }


    private int getCurIndexById(int id) {
        if (InAppStoryService.getInstance().getDownloadManager() == null) return 0;
        Story st = InAppStoryService.getInstance().getDownloadManager().getStoryById(id);
        return st == null ? 0 : st.lastIndex;
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        if (InAppStoryService.isNull()) return;
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (getCurIndexById(readerManager.getCurrentStoryId()) ==
                    readerManager.getCurrentSlideIndex()) {
                readerManager.resumeCurrent(false);
            }

        }
        readerManager.setCurrentSlideIndex(getCurIndexById(readerManager.getCurrentStoryId()));

    }

    public void setCurrentItem(int ind) {
        if (storiesViewPager.getAdapter() != null &&
                storiesViewPager.getAdapter().getCount() > ind) {
            storiesViewPager.setCurrentItem(ind);
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    void defaultUrlClick(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setData(Uri.parse(url));
        try {
            getActivity().startActivity(i);
            getActivity().overridePendingTransition(R.anim.popup_show, R.anim.empty_animation);
        } catch (Exception e) {

        }
    }


    public void showGuardMask(int delay) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                invMask.setVisibility(View.VISIBLE);
            }
        });
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                invMask.setVisibility(View.GONE);
            }
        }, delay);
    }

    public void nextStory() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (storiesViewPager.getCurrentItem() < storiesViewPager.getAdapter().getCount() - 1) {
                    storiesViewPager.cubeAnimation = true;
                    storiesViewPager.setCurrentItem(storiesViewPager.getCurrentItem() + 1);
                } else {
                    InAppStoryManager.closeStoryReader(CloseStory.AUTO);
                }
            }
        });
    }

    public void prevStory() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (storiesViewPager.getCurrentItem() > 0) {
                    storiesViewPager.setCurrentItem(storiesViewPager.getCurrentItem() - 1);
                    storiesViewPager.cubeAnimation = true;
                } else {
                    readerManager.restartCurrentStory();
                }
            }
        });
    }
}
