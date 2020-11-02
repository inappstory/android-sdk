package io.casestory.sdk.stories.ui.widgets.readerscreen;

import android.content.Intent;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;


import java.lang.reflect.Type;

import io.casestory.casestorysdk.R;
import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.CsEventBus;
import io.casestory.sdk.eventbus.CsSubscribe;
import io.casestory.sdk.eventbus.CsThreadMode;
import io.casestory.sdk.network.NetworkCallback;
import io.casestory.sdk.network.NetworkClient;
import io.casestory.sdk.stories.api.models.ShareObject;
import io.casestory.sdk.stories.api.models.StatisticSession;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.CloseStoryReaderEvent;
import io.casestory.sdk.stories.events.NextStoryPageEvent;
import io.casestory.sdk.stories.events.NextStoryReaderEvent;
import io.casestory.sdk.stories.events.NoConnectionEvent;
import io.casestory.sdk.stories.events.PageByIdSelectedEvent;
import io.casestory.sdk.stories.events.PageByIndexRefreshEvent;
import io.casestory.sdk.stories.events.PageRefreshEvent;
import io.casestory.sdk.stories.events.PageTaskLoadErrorEvent;
import io.casestory.sdk.stories.events.PageTaskLoadedEvent;
import io.casestory.sdk.stories.events.PageTaskToLoadEvent;
import io.casestory.sdk.stories.events.PauseStoryReaderEvent;
import io.casestory.sdk.stories.events.PrevStoryPageEvent;
import io.casestory.sdk.stories.events.PrevStoryReaderEvent;
import io.casestory.sdk.stories.events.RestartStoryReaderEvent;
import io.casestory.sdk.stories.events.ResumeStoryReaderEvent;
import io.casestory.sdk.stories.events.StoriesErrorEvent;
import io.casestory.sdk.stories.events.StoryCacheLoadedEvent;
import io.casestory.sdk.stories.events.StoryPageLoadedEvent;
import io.casestory.sdk.stories.serviceevents.ChangeIndexEventInFragment;
import io.casestory.sdk.stories.serviceevents.LikeDislikeEvent;
import io.casestory.sdk.stories.serviceevents.PrevStoryFragmentEvent;
import io.casestory.sdk.stories.serviceevents.StoryFavoriteEvent;
import io.casestory.sdk.stories.storieslistenerevents.OnNextEvent;
import io.casestory.sdk.stories.storieslistenerevents.OnPrevEvent;
import io.casestory.sdk.stories.utils.Sizes;

import static io.casestory.sdk.AppearanceManager.BOTTOM_LEFT;
import static io.casestory.sdk.AppearanceManager.BOTTOM_RIGHT;
import static io.casestory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static io.casestory.sdk.AppearanceManager.CS_HAS_FAVORITE;
import static io.casestory.sdk.AppearanceManager.CS_HAS_LIKE;
import static io.casestory.sdk.AppearanceManager.CS_HAS_SHARE;
import static io.casestory.sdk.AppearanceManager.TOP_LEFT;
import static io.casestory.sdk.AppearanceManager.TOP_RIGHT;

public class StoriesReaderPageFragment extends Fragment implements StoriesProgressView.StoriesListener {

    public boolean visible = false;

    public StoriesWebView storiesWebView;
    public StoriesProgressView storiesProgressView;
    RelativeLayout progress;
    View refresh;
    public AppCompatImageView close;
    public View mask;
    View invMask;
    public int storyId;
    View buttonsPanel;
    StoriesReaderPagerAdapter host;

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeIndexEvent(ChangeIndexEventInFragment event) {

        if (event.getCurItem() != storyId) return;
        final int curIndex = event.getIndex();

        int index = storiesProgressView.current;
        storiesProgressView.setActive(true);
        counter = curIndex;
        // storiesProgressView.clearAnimation(index);
        // storiesProgressView.setCurrentCounterAndRestart(curIndex);

        storiesWebView.setCurrentItem(curIndex);
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pageLoadError(PageTaskLoadErrorEvent errorEvent) {
        if (errorEvent.getId() != storyId) return;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                refresh.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        }, 200);
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void refreshPageEvent(PageByIndexRefreshEvent event) {
        if (event.getStoryId() != storyId) return;
        refresh.setVisibility(View.GONE);
        //   refreshFragment();
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void nextStoryPage(NextStoryPageEvent event) {
        final int ind = event.getStoryIndex();
        if (ind != storyId) return;
        Story story = StoryDownloader.getInstance().getStoryById(storyId);
        //storiesProgressView.skip();

        if (story.lastIndex == story.slidesCount - 1) {
            CsEventBus.getDefault().post(new NextStoryReaderEvent());
        } else {
            storiesProgressView.setMax(story.lastIndex);
            CsEventBus.getDefault().post(new OnNextEvent());
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void storyPageLoadedEvent(StoryPageLoadedEvent event) {
        if (this.storyId != event.getStoryId()) return;
        final int ind = event.index;
        CaseStoryService.getInstance().getFullStoryById(new GetStoryByIdCallback() {
            @Override
            public void getStory(Story story) {
                if (CaseStoryService.getInstance().getCurrentId() == storyId
                        && story.lastIndex == ind) {
                    storiesProgressView.setActive(true);
                    storiesProgressView.startProgress(ind);
                    CaseStoryService.getInstance().startTimer(story.getDurations().get(ind), true);
                    if (CaseStoryService.getInstance().currentEvent != null)
                        CaseStoryService.getInstance().currentEvent.timer = System.currentTimeMillis();
                }
            }

            @Override
            public void loadError(int type) {
                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.READER));
            }

            @Override
            public void getPartialStory(Story story) {

            }
        }, storyId);
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void prevStoryPage(PrevStoryPageEvent event) {
        final int ind = event.getStoryIndex();
        if (ind != storyId) return;
        int lind = StoryDownloader.getInstance().getStoryById(storyId).lastIndex;
        if (lind > 0) {
            CsEventBus.getDefault().post(new OnPrevEvent());
            storiesProgressView.clearAnimation(lind);
        } else {
            CsEventBus.getDefault().post(new PrevStoryReaderEvent());
        }
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void resumeStoryEvent(ResumeStoryReaderEvent event) {
        if (CaseStoryService.getInstance().getCurrentId() != storyId) return;
        final boolean isWithBackground = event.isWithBackground();
        storiesProgressView.resumeWithoutRestart(isWithBackground);

    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pauseStoryEvent(PauseStoryReaderEvent event) {
        if (CaseStoryService.getInstance().getCurrentId() != storyId) return;
        final boolean isWithBackground = event.isWithBackground();
        storiesProgressView.pause(isWithBackground);

    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pageByIdSelected(PageByIdSelectedEvent event) {
        if (event.getStoryId() != storyId) return;
        Handler handler = new Handler(Looper.getMainLooper());
        if (event.isOnlyResume()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Story story = StoryDownloader.getInstance().getStoryById(storyId);
                    int prevInd = story.lastIndex;

                    if (storiesProgressView != null) {
                        if (story.durations != null) {
                            storiesProgressView.setStoryDurations(story.durations);
                        }
                        storiesProgressView.setActive(false);
                        storiesProgressView.setCurrentCounter(prevInd);
                        storiesProgressView.pause(false);
                        counter = prevInd;
                    }
                }
            }, 100);
        } else {
            if (storiesProgressView != null) {
                storiesProgressView.setActive(true);
            }
            Story story = StoryDownloader.getInstance().getStoryById(storyId);
            counter = story.lastIndex;
            CaseStoryService.getInstance().setCurrentIndex(counter);
            if (storiesWebView != null && storiesWebView.isWebPageLoaded) {
                CaseStoryService.getInstance().startTimer(story.getDurations().get(counter), true);
                storiesProgressView.setCurrentCounter(counter);
            }
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void prevStoryFragment(PrevStoryFragmentEvent event) {
        if (storyId != event.getId()) return;
        storiesProgressView.same();
        storiesWebView.restartVideo();
        CaseStoryService.getInstance().restartTimer();
    }

    public AppCompatImageView like;
    public AppCompatImageView dislike;
    public AppCompatImageView favorite;
    public AppCompatImageView share;

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void noConnectionEvent(NoConnectionEvent event) {
        //storiesWebView.setVisibility(View.INVISIBLE);
        //refresh.setVisibility(View.VISIBLE);
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void favSuccess(StoryFavoriteEvent event) {
        if (event.getId() != storyId) return;
        if (favorite != null) {
            favorite.setActivated(event.favStatus);
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void likeSuccess(LikeDislikeEvent event) {
        if (storyId != event.getId()) return;
        if (like != null) {
            like.setActivated(event.likeStatus == 1);
        }
        if (dislike != null) {
            dislike.setActivated(event.likeStatus == -1);
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void restartEvent(RestartStoryReaderEvent event) {
        if (storyId == event.getId() && storiesWebView.getCurrentItem() == event.getIndex()) {
            storiesProgressView.setSlideDuration(event.getIndex(), event.getNewDuration());
            storiesProgressView.forceStartProgress();
            CaseStoryService.getInstance().startTimer(event.getNewDuration(), true);
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
        try {
            CsEventBus.getDefault().unregister(this);
        } catch (Exception e) {}
        super.onDestroyView();
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void closeReaderEvent(CloseStoryReaderEvent event) {
        if (storiesWebView != null)
            storiesWebView.destroyWebView();
        try {
            CsEventBus.getDefault().unregister(this);
        } catch (Exception e) {}
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        visible = isVisibleToUser;
        if (isVisibleToUser) {
        } else {

        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pageTaskLoaded(PageTaskToLoadEvent event) {
        if (storiesWebView == null || storiesWebView.storyId != event.getId() || storiesWebView.index != event.getIndex())
            return;
        if (event.isLoaded()) {
            Animation anim = new AlphaAnimation(1f, 0f);
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {

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
                    progress.setVisibility(View.GONE);
                    progress.setAlpha(1f);
                }
            });
            progress.startAnimation(anim);
        } else {
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
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
        CsEventBus.getDefault().register(this);

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
        progress = view.findViewById(R.id.progress);
        refresh = view.findViewById(R.id.refreshButton);
        progress.setVisibility(View.GONE);
        storyId = getArguments().getInt("story_id");
        CsEventBus.getDefault().post(new PageByIdSelectedEvent(storyId, true));
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
                    (getArguments().getBoolean(CS_HAS_LIKE, false) ||
                            getArguments().getBoolean(CS_HAS_FAVORITE, false) ||
                            getArguments().getBoolean(CS_HAS_SHARE, false)) ?
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

                        storiesProgressView.setStoriesCount(story.slidesCount);
                        storiesProgressView.setStoryDurations(story.durations);
                        storiesWebView.loadStory(storyId, story.lastIndex);
                    }
                });

            }

            @Override
            public void loadError(int type) {
                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.READER));
            }

            @Override
            public void getPartialStory(final Story story) {
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
                        storiesWebView.setIndex(0);
                        storiesProgressView.setStoriesCount(story.slidesCount);
                        storiesWebView.loadStory(storyId, story.lastIndex);
                    }
                });
            }
        }, storyId);
        if (like != null) {
            like.setVisibility(getArguments().getBoolean(CS_HAS_LIKE, true) ? View.VISIBLE : View.GONE);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CaseStoryService.getInstance().likeDislikeClick(false, storyId);
                }
            });
        }
        if (dislike != null) {
            dislike.setVisibility(getArguments().getBoolean(CS_HAS_LIKE, true) ? View.VISIBLE : View.GONE);
            dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CaseStoryService.getInstance().likeDislikeClick(true, storyId);
                }
            });
        }
        if (share != null) {
            share.setVisibility(getArguments().getBoolean(CS_HAS_SHARE, true) ? View.VISIBLE : View.GONE);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CsEventBus.getDefault().post(new PauseStoryReaderEvent(false));
                    share.setEnabled(false);
                    share.setClickable(false);
                    NetworkClient.getApi().share(Integer.toString(storyId), StatisticSession.getInstance().id,
                            CaseStoryManager.getInstance().getApiKey(), null).enqueue(new NetworkCallback<ShareObject>() {
                        @Override
                        public void onSuccess(ShareObject response) {
                            share.setEnabled(true);
                            share.setClickable(true);
                            if (CaseStoryManager.getInstance().shareCallback != null) {
                                CaseStoryManager.getInstance().shareCallback.onShare(response.getUrl(), response.getTitle(), response.getDescription());
                            } else {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                sendIntent.putExtra(Intent.EXTRA_SUBJECT, response.getTitle());
                                sendIntent.putExtra(Intent.EXTRA_TEXT, response.getUrl());
                                sendIntent.setType("text/plain");
                                CaseStoryManager.getInstance().getContext().startActivity(sendIntent);
                            }
                        }

                        @Override
                        public Type getType() {
                            return ShareObject.class;
                        }
                    });
                }
            });
        }
        if (favorite != null) {
            favorite.setVisibility(getArguments().getBoolean(CS_HAS_FAVORITE, true) ? View.VISIBLE : View.GONE);
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CaseStoryService.getInstance().favoriteClick(storyId);
                }
            });
        }

        try {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) close.getLayoutParams();
            RelativeLayout.LayoutParams storiesProgressViewLP = (RelativeLayout.LayoutParams) storiesProgressView.getLayoutParams();
            int cp = getArguments().getInt(CS_CLOSE_POSITION, 1);
            switch (cp) {
                case TOP_RIGHT:
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                    storiesProgressViewLP.addRule(RelativeLayout.LEFT_OF, close.getId());
                    break;
                case TOP_LEFT:
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                    storiesProgressViewLP.addRule(RelativeLayout.RIGHT_OF, close.getId());
                    break;
                case BOTTOM_RIGHT:
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    layoutParams.addRule(RelativeLayout.BELOW, storiesProgressView.getId());
                    storiesProgressViewLP.topMargin = Sizes.dpToPxExt(12);
                    layoutParams.topMargin = Sizes.dpToPxExt(8);
                    break;
                case BOTTOM_LEFT:
                    storiesProgressViewLP.topMargin = Sizes.dpToPxExt(12);
                    layoutParams.topMargin = Sizes.dpToPxExt(8);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    layoutParams.addRule(RelativeLayout.BELOW, storiesProgressView.getId());
                    break;
            }

            close.setLayoutParams(layoutParams);
        } catch (Exception e) {

        }
        if (close != null)
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CsEventBus.getDefault().post(new CloseStoryReaderEvent(false));
                }
            });

        // CoreProgressBar progressBar = (CoreProgressBar) view.findViewById(R.id.progress_bar);
        //storiesWebView.setMask(mask, progressBar);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CsEventBus.getDefault().post(new PageByIndexRefreshEvent(storyId, storiesWebView.index));
                CsEventBus.getDefault().post(new PageRefreshEvent(storyId, storiesWebView.index));
            }
        });
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void storyCacheLoaded(StoryCacheLoadedEvent event) {
        if (event != null) {
            if (storyId != event.getStoryId()) return;
            Log.e("eventsLoaded", "StoryCacheLoadedEvent " + StoryDownloader.getInstance().getStoryById(storyId).title + " " + StoryDownloader.getInstance().getStoryById(storyId).durations.get(0));
            storiesProgressView.setStoryDurations(StoryDownloader.getInstance().getStoryById(storyId).durations);
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pageTaskLoaded(PageTaskLoadedEvent event) {
        if (event != null) {
            if (storyId != event.getId()) return;
            refresh.setVisibility(View.GONE);
        }
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
