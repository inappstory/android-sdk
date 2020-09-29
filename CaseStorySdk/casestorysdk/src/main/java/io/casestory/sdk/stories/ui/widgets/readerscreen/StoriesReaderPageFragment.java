package io.casestory.sdk.stories.ui.widgets.readerscreen;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;


import io.casestory.casestorysdk.R;
import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.eventbus.Subscribe;
import io.casestory.sdk.eventbus.ThreadMode;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.CloseStoryReaderEvent;
import io.casestory.sdk.stories.events.NextStoryPageEvent;
import io.casestory.sdk.stories.events.NoConnectionEvent;
import io.casestory.sdk.stories.events.PageByIdSelectedEvent;
import io.casestory.sdk.stories.events.PageByIndexRefreshEvent;
import io.casestory.sdk.stories.events.PageRefreshEvent;
import io.casestory.sdk.stories.events.PauseStoryReaderEvent;
import io.casestory.sdk.stories.events.PrevStoryPageEvent;
import io.casestory.sdk.stories.events.RestartStoryReaderEvent;
import io.casestory.sdk.stories.events.ResumeStoryReaderEvent;
import io.casestory.sdk.stories.events.StoryPageLoadedEvent;
import io.casestory.sdk.stories.events.StoryTimerReverseEvent;
import io.casestory.sdk.stories.events.StoryTimerSkipEvent;
import io.casestory.sdk.stories.serviceevents.ChangeIndexEventInFragment;
import io.casestory.sdk.stories.serviceevents.LikeDislikeEvent;
import io.casestory.sdk.stories.serviceevents.PrevStoryFragmentEvent;
import io.casestory.sdk.stories.serviceevents.StoryFavoriteEvent;
import io.casestory.sdk.stories.ui.widgets.CoreProgressBar;
import io.casestory.sdk.stories.utils.Sizes;

public class StoriesReaderPageFragment extends Fragment implements StoriesProgressView.StoriesListener {

    public boolean visible = false;

    public StoriesWebView storiesWebView;
    public StoriesProgressView storiesProgressView;
    LinearLayout refresh;
    LinearLayout refresh2;
    public AppCompatImageView close;
    public View mask;
    View invMask;
    public int storyId;
    View buttonsPanel;
    StoriesReaderPagerAdapter host;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeIndexEvent(ChangeIndexEventInFragment event) {

        if (event.getCurItem() != storyId) return;
        final int curIndex = event.getIndex();

        int index = storiesProgressView.current;

        Log.e("loadStory", "SPFchangeIndexEvent " + event.getIndex());
        storiesProgressView.setActive(true);
        counter = curIndex;
        storiesProgressView.clearAnimation(index);
        storiesProgressView.setCurrentCounterAndRestart(curIndex);

        storiesWebView.setCurrentItem(curIndex);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPageEvent(PageByIndexRefreshEvent event) {
        if (event.getStoryId() != storyId) return;
        refreshFragment();
    }


    public void refreshFragment() {
        if (CaseStoryService.getInstance().isConnected()) {
            storiesWebView.setVisibility(View.VISIBLE);
            mask.setVisibility(View.GONE);
            refresh.setVisibility(View.GONE);
            CaseStoryService.getInstance().getFullStoryById(new GetStoryByIdCallback() {
                @Override
                public void getStory(Story response) {
                    if (response.disableClose)
                        close.setVisibility(View.GONE);
                    if (!response.hasLike() && like != null && dislike != null) {
                        like.setVisibility(View.GONE);
                        dislike.setVisibility(View.GONE);
                    }
                    if (!response.hasFavorite() && favorite != null)
                        favorite.setVisibility(View.GONE);
                    if (!response.hasShare() && share != null)
                        share.setVisibility(View.GONE);
                    if (!response.hasShare() && !response.hasFavorite() && !response.hasLike() && buttonsPanel != null)
                        buttonsPanel.setVisibility(View.GONE);
                    storiesProgressView.setStoriesCount(response.pages.size());
                    storiesProgressView.setStoryDurations(response.durations);
                    storiesWebView.loadStory(response.id, response.lastIndex);
                }
            }, storyId);
        } else {
            try {
                if (getUserVisibleHint()) {
                    // Toast.makeText(getContext(), getResources().getString(R.string.nar_noInternetError), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            }
            mask.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void nextStoryPage(NextStoryPageEvent event) {
        final int ind = event.getStoryIndex();
        if (ind != storyId) return;
        if (storiesProgressView.skip()) {
            EventBus.getDefault().post(new StoryTimerSkipEvent(storyId, ind));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void storyPageLoadedEvent(StoryPageLoadedEvent event) {
        if (this.storyId != event.getNarrativeId()) return;
        final int ind = event.index;
        final int localStoryId = event.getNarrativeId();
        CaseStoryService.getInstance().getFullStoryById(new GetStoryByIdCallback() {
            @Override
            public void getStory(Story story) {
                story.loadedPages.set(ind, true);
                if (CaseStoryService.getInstance().getCurrentId() == localStoryId
                        && CaseStoryService.getInstance().getCurrentIndex() == ind) {
                    storiesProgressView.setActive(true);
                    if (storiesProgressView.started) {
                        storiesProgressView.startProgress();
                    }
                    storiesProgressView.resume();
                    if (CaseStoryService.getInstance().currentEvent != null)
                        CaseStoryService.getInstance().currentEvent.timer = System.currentTimeMillis();
                }
            }
        }, storyId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void prevStoryPage(PrevStoryPageEvent event) {
        final int ind = event.getStoryIndex();
        if (ind != storyId) return;
        if (storiesProgressView.reverse()) {
            EventBus.getDefault().post(new StoryTimerReverseEvent(storyId, ind));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void resumeStoryEvent(ResumeStoryReaderEvent event) {
        if (CaseStoryService.getInstance().getCurrentId() != storyId) return;
        final boolean isWithBackground = event.isWithBackground();
        storiesProgressView.resumeWithoutRestart(isWithBackground);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pauseStoryEvent(PauseStoryReaderEvent event) {
        if (CaseStoryService.getInstance().getCurrentId() != storyId) return;
        final boolean isWithBackground = event.isWithBackground();
        storiesProgressView.pause(isWithBackground);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pageByIdSelected(PageByIdSelectedEvent event) {
        Log.e("PageByIdSelectedEvent", event.getStoryId() + " " + storyId);
        if (event.getStoryId() != storyId) return;
        Handler handler = new Handler(Looper.getMainLooper());
        if (event.isOnlyResume()) {
                    if (storiesProgressView != null)
                        storiesProgressView.resumeWithoutRestart(false);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int prevInd = StoryDownloader.getInstance().getStoryById(storyId).lastIndex;
                    if (storiesProgressView != null) {
                        storiesProgressView.setActive(false);
                        storiesProgressView.destroy();
                        counter = prevInd;
                        if (storiesWebView != null) {
                            storiesProgressView.setCurrentCounter(prevInd);
                        }
                    }
                }
            }, 150);
        } else {
            CaseStoryService.getInstance().setCurrentIndex(0);
            if (storiesProgressView != null) {
                storiesProgressView.setActive(true);
            }
            int ind = StoryDownloader.getInstance().getStoryById(storyId).lastIndex;
            counter = ind;
            if (storiesWebView != null) {
                CaseStoryService.getInstance().setCurrentIndex(ind);

                Log.e("loadStory", "PageByIdSelectedEvent");
                storiesWebView.setCurrentItem(ind);
                storiesProgressView.setCurrentCounter(ind);
                if (CaseStoryService.getInstance().isConnected()) {
                    if (storiesProgressView.getStoriesListener() == null)
                        storiesProgressView.setStoriesListener(this);
                    storiesProgressView.resumeWithPause();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void prevStoryFragment(PrevStoryFragmentEvent event) {
        if (storyId != event.getId()) return;
        storiesProgressView.same();
        storiesWebView.restartVideo();
    }

    public AppCompatImageView like;
    public AppCompatImageView dislike;
    public AppCompatImageView favorite;
    public AppCompatImageView share;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void noConnectionEvent(NoConnectionEvent event) {
        storiesWebView.setVisibility(View.INVISIBLE);
        refresh.setVisibility(View.VISIBLE);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void favSuccess(StoryFavoriteEvent event) {
        if (event.getId() != storyId) return;
        if (favorite != null) {
            favorite.setActivated(event.favStatus);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void likeSuccess(LikeDislikeEvent event) {
        if (storyId != event.getId()) return;
        if (like != null) {
            like.setActivated(event.likeStatus == 1);
        }
        if (dislike != null) {
            dislike.setActivated(event.likeStatus == -1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void restartEvent(RestartStoryReaderEvent event) {
        if (storyId == event.getId() && storiesWebView.getCurrentItem() == event.getIndex()) {
            storiesProgressView.setSlideDuration(event.getIndex(), event.getNewDuration());
            storiesProgressView.forceStartProgress();
        }
    }

    public int counter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            return inflater.inflate(R.layout.cs_fragment_story, container, false);
        } catch (Exception e) {
            e.printStackTrace();
            return new View(getContext());
        }
    }

    @Override
    public void onDestroyView() {
        if (storiesWebView != null)
            storiesWebView.destroyWebView();
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visible = isVisibleToUser;
        if (isVisibleToUser) {
        } else {

        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (view == null) return;
        storiesWebView = view.findViewById(R.id.storiesWebView);
        if (getArguments() == null) return;
        if (CaseStoryService.getInstance() == null) return;
        if (storiesWebView == null) return;
        if (Build.VERSION.SDK_INT >= 28) {
            if (getActivity() != null && getActivity().getWindow() != null &&
                    getActivity().getWindow().getDecorView() != null &&
                    getActivity().getWindow().getDecorView().getRootWindowInsets() != null) {
                DisplayCutout cutout = getActivity().getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                if (cutout != null) {
                    View view1 = view.findViewById(R.id.progress_view_sdk);
                    if (view1 != null) {
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view1.getLayoutParams();
                        lp.topMargin += cutout.getSafeInsetTop();
                        view1.setLayoutParams(lp);
                    }
                }
            }
        }
        EventBus.getDefault().register(this);
        invMask = view.findViewById(R.id.invMask);
        close = (AppCompatImageView) view.findViewById(R.id.close_button);

        like = view.findViewById(R.id.likeButton);
        dislike = view.findViewById(R.id.dislikeButton);
        favorite = view.findViewById(R.id.favoriteButton);
        share = view.findViewById(R.id.shareButton);
        buttonsPanel = view.findViewById(R.id.buttonsPanel);
        storiesProgressView = (StoriesProgressView) view.findViewById(R.id.stories);
        storiesProgressView.setStoriesListener(this);
        mask = view.findViewById(R.id.blackMask);
        refresh = view.findViewById(R.id.refresh);
        refresh.setVisibility(View.INVISIBLE);
        storyId = getArguments().getInt("story_id");

        Log.e("PageByIdSelectedEvent", "created " + storyId);
        EventBus.getDefault().post(new PageByIdSelectedEvent(storyId, true));
        if (!Sizes.isTablet() && !CaseStoryManager.getInstance().hasLike() &&
                !CaseStoryManager.getInstance().hasShare() &&
                !CaseStoryManager.getInstance().hasFavorite()) {
            View blackBottom = view.findViewById(R.id.blackBottom);
            if (blackBottom != null) {
                Point screenSize = Sizes.getScreenSize();
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) blackBottom.getLayoutParams();
                float realProps = screenSize.y / ((float) screenSize.x);
                float sn = 1.85f;
                if (realProps > sn) {
                    lp.height = (int) (screenSize.y - screenSize.x * sn);
                }
                blackBottom.setLayoutParams(lp);
            }
        }
        if (buttonsPanel != null) {
            buttonsPanel.setVisibility(
                    (getArguments().getBoolean("hasLike", false) ||
                            getArguments().getBoolean("hasFavorite", false) ||
                            getArguments().getBoolean("hasShare", false)) ?
                            View.VISIBLE :
                            View.GONE);
        }
        CaseStoryService.getInstance().getFullStoryById(new GetStoryByIdCallback() {
            @Override
            public void getStory(final Story story) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (story.disableClose)
                            close.setVisibility(View.GONE);
                        if (!story.hasLike() && like != null && dislike != null) {
                            like.setVisibility(View.GONE);
                            dislike.setVisibility(View.GONE);
                        }
                        if (!story.hasFavorite() && favorite != null)
                            favorite.setVisibility(View.GONE);
                        if (!story.hasShare() && share != null)
                            share.setVisibility(View.GONE);
                        if (!story.hasShare() && !story.hasFavorite() && !story.hasLike() && buttonsPanel != null)
                            buttonsPanel.setVisibility(View.GONE);
                        if (like != null) {
                            like.setActivated(story.liked());
                        }
                        if (dislike != null) {
                            dislike.setActivated(story.disliked());
                        }
                        if (favorite != null) {
                            favorite.setActivated(story.favorite);
                        }
                        storiesWebView.setStoryId(storyId);
                        storiesWebView.setIndex(story.lastIndex);

                        storiesProgressView.setStoriesCount(story.pages.size());
                        storiesProgressView.setStoryDurations(story.durations);
                        storiesWebView.loadStory(storyId, story.lastIndex);
                    }
                });

            }
        }, storyId);
        if (like != null) {
            like.setVisibility(getArguments().getBoolean("hasLike", true) ? View.VISIBLE : View.GONE);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CaseStoryService.getInstance().likeDislikeClick(false, storyId);
                }
            });
        }
        if (dislike != null) {
            dislike.setVisibility(getArguments().getBoolean("hasLike", true) ? View.VISIBLE : View.GONE);
            dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CaseStoryService.getInstance().likeDislikeClick(true, storyId);
                }
            });
        }
        if (share != null) {
            share.setVisibility(getArguments().getBoolean("hasShare", true) ? View.VISIBLE : View.GONE);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new PauseStoryReaderEvent(false));
                    share.setEnabled(false);
                    share.setClickable(false);
                }
            });
        }
        if (favorite != null) {
            favorite.setVisibility(getArguments().getBoolean("hasFavorite", true) ? View.VISIBLE : View.GONE);
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CaseStoryService.getInstance().favoriteClick(storyId);
                }
            });
        }

        try {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) close.getLayoutParams();
            layoutParams.addRule(getArguments().getInt("closePosition", 1) == 1 ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
            close.setLayoutParams(layoutParams);
        } catch (Exception e) {

        }
        if (close != null)
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new CloseStoryReaderEvent(false));
                }
            });

        CoreProgressBar progressBar = (CoreProgressBar) view.findViewById(R.id.progress_bar);
        //storiesWebView.setMask(mask, progressBar);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PageRefreshEvent(storyId));
            }
        });
    }

    @Override
    public boolean webViewLoaded(int index) {
        Story story = StoryDownloader.getInstance().getStoryById(storyId);
        if (story == null || story.loadedPages == null ||
                story.loadedPages.isEmpty() ||
                story.loadedPages.size() <= index)
            return false;
        return true;
    }
}
