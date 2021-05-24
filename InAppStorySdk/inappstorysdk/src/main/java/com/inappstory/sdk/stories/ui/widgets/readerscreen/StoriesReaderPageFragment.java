package com.inappstory.sdk.stories.ui.widgets.readerscreen;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;


import com.inappstory.sdk.R;
import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.events.ClearDurationEvent;
import com.inappstory.sdk.stories.events.CloseStoryReaderEvent;
import com.inappstory.sdk.stories.events.NextStoryPageEvent;
import com.inappstory.sdk.stories.events.NextStoryReaderEvent;
import com.inappstory.sdk.stories.events.PageByIdSelectedEvent;
import com.inappstory.sdk.stories.events.PageByIndexRefreshEvent;
import com.inappstory.sdk.stories.events.PageRefreshEvent;
import com.inappstory.sdk.stories.events.PageTaskLoadErrorEvent;
import com.inappstory.sdk.stories.events.PageTaskLoadedEvent;
import com.inappstory.sdk.stories.events.PageTaskToLoadEvent;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.PrevStoryPageEvent;
import com.inappstory.sdk.stories.events.PrevStoryReaderEvent;
import com.inappstory.sdk.stories.events.RestartStoryReaderEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.events.SoundOnOffEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.events.StoryCacheLoadedEvent;
import com.inappstory.sdk.stories.events.StoryPageOpenEvent;
import com.inappstory.sdk.stories.events.StoryPageStartedEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.serviceevents.ChangeIndexEventInFragment;
import com.inappstory.sdk.stories.serviceevents.LikeDislikeEvent;
import com.inappstory.sdk.stories.serviceevents.PrevStoryFragmentEvent;
import com.inappstory.sdk.stories.serviceevents.StoryFavoriteEvent;
import com.inappstory.sdk.stories.storieslistenerevents.OnNextEvent;
import com.inappstory.sdk.stories.storieslistenerevents.OnPrevEvent;
import com.inappstory.sdk.stories.utils.Sizes;

import static com.inappstory.sdk.AppearanceManager.BOTTOM_LEFT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_RIGHT;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_FAVORITE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_LIKE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_SHARE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_SOUND;
import static com.inappstory.sdk.AppearanceManager.TOP_LEFT;
import static com.inappstory.sdk.AppearanceManager.TOP_RIGHT;

public class StoriesReaderPageFragment extends Fragment implements StoriesProgressView.StoriesListener {


    StoriesWebView storiesWebView;
    StoriesProgressView storiesProgressView;
    RelativeLayout progress;
    View mask;
    View invMask;
    int storyId;
    View buttonsPanel;
    View blackBottom;
    View blackTop;
    View refresh;
    AppCompatImageView close;

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeIndexEvent(ChangeIndexEventInFragment event) {
        if (event.getCurItem() != storyId) return;
        final int curIndex = event.getIndex();
        storiesProgressView.setActive(true);
        counter = curIndex;
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
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void nextStoryPage(NextStoryPageEvent event) {
        final int ind = event.getStoryIndex();
        if (ind != storyId) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        if (story.durations != null && !story.durations.isEmpty())
            story.slidesCount = story.durations.size();
        StatisticManager.getInstance().sendCurrentState();
        if (story.lastIndex == story.slidesCount - 1) {
            CsEventBus.getDefault().post(new NextStoryReaderEvent());
        } else {
            storiesProgressView.setMax(story.lastIndex);
            CsEventBus.getDefault().post(new OnNextEvent());
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void storyPageLoadedEvent(StoryPageStartedEvent event) {
        if (this.storyId != event.getStoryId()) return;
        final int ind = event.index;
        InAppStoryService.getInstance().getDownloadManager().getFullStoryById(new GetStoryByIdCallback() {
            @Override
            public void getStory(Story story) {
                if (InAppStoryService.getInstance().getCurrentId() == storyId
                        && story.lastIndex == ind) {
                    storiesProgressView.setActive(true);
                    storiesProgressView.startProgress(ind);
                    if (storiesWebView != null && storiesWebView.isWebPageLoaded)
                        InAppStoryService.getInstance().getTimerManager().startTimer(story.getDurations().get(ind), true);
                    if (OldStatisticManager.getInstance().currentEvent != null)
                        OldStatisticManager.getInstance().currentEvent.timer = System.currentTimeMillis();
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
        int lind = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId).lastIndex;
        if (lind > 0) {
            CsEventBus.getDefault().post(new OnPrevEvent());
            StatisticManager.getInstance().sendCurrentState();
            storiesProgressView.clearAnimation(lind);
        } else {
            CsEventBus.getDefault().post(new PrevStoryReaderEvent());
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void resumeStoryEvent(ResumeStoryReaderEvent event) {
        if (InAppStoryService.getInstance().getCurrentId() != storyId) return;
        final boolean isWithBackground = event.isWithBackground();
        storiesProgressView.resumeWithoutRestart(isWithBackground);

    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pauseStoryEvent(PauseStoryReaderEvent event) {
        if (InAppStoryService.getInstance().getCurrentId() != storyId) return;
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
                    Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
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
            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
            counter = story.lastIndex;
            InAppStoryService.getInstance().setCurrentIndex(counter);
            if (storiesWebView != null && storiesWebView.isWebPageLoaded) {
                InAppStoryService.getInstance().getTimerManager().startTimer(story.getDurations().get(counter), true);
                storiesProgressView.setCurrentCounter(counter);
            }
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void prevStoryFragment(PrevStoryFragmentEvent event) {
        if (storyId != event.getId()) return;
        storiesProgressView.same(false);
        storiesWebView.restartVideo();
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        InAppStoryService.getInstance().getTimerManager().restartTimer(story.getDurations().get(0));
    }

    public AppCompatImageView like;
    public AppCompatImageView sound;
    public AppCompatImageView dislike;
    public AppCompatImageView favorite;
    public AppCompatImageView share;

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void favSuccess(StoryFavoriteEvent event) {
        if (event.getId() != storyId) return;
        if (favorite != null) {
            favorite.setActivated(event.favStatus);
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryPageEvent(StoryPageOpenEvent event) {
        if (this.storyId != event.getStoryId()) return;
        if (storiesProgressView.current != event.getIndex())
            storiesProgressView.setCurrentCounter(event.getIndex(), true);
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
            InAppStoryService.getInstance().getTimerManager().startTimer(event.getNewDuration(), true);
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void clearDurationEvent(ClearDurationEvent event) {
        if (storyId == event.getId()) {
            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(event.getId());
            for (int i = 0; i < story.getDurations().size(); i++) {
                storiesProgressView.setSlideDuration(i, story.getDurations().get(i));
            }
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
        } catch (Exception e) {
        }
        super.onDestroyView();
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void closeReaderEvent(CloseStoryReaderEvent event) {
        if (storiesWebView != null)
            storiesWebView.destroyWebView();
        try {
            CsEventBus.getDefault().unregister(this);
        } catch (Exception e) {
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeSoundStatus(SoundOnOffEvent event) {
        if (sound != null) sound.setActivated(InAppStoryManager.getInstance().soundOn);
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pageTaskLoaded(PageTaskToLoadEvent event) {
        Log.e("PageTaskToLoadEvent", event.getId() + " " + event.getIndex() + " " + storiesWebView.storyId + " " + storiesWebView.index);
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
            progress.setAlpha(1f);
        }
    }

    private View getLoader() {
        View v = null;
        RelativeLayout.LayoutParams relativeParams;
        if (AppearanceManager.getInstance() != null && AppearanceManager.getInstance().csLoaderView() != null) {
            v = AppearanceManager.getInstance().csLoaderView().getView();
        } else {
            v = new ProgressBar(getContext()) {{
                setIndeterminate(true);
                getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            }};
        }
        relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        v.setLayoutParams(relativeParams);
        return v;
    }

    StoriesReaderPageFragmentController controller = new StoriesReaderPageFragmentController();

    void initViews(View view) {
        storiesWebView = view.findViewById(R.id.storiesWebView);
        invMask = view.findViewById(R.id.invMask);
        close = (AppCompatImageView) view.findViewById(R.id.close_button);
        blackBottom = view.findViewById(R.id.blackBottom);
        blackTop = view.findViewById(R.id.blackTop);
        like = view.findViewById(R.id.likeButton);
        dislike = view.findViewById(R.id.dislikeButton);
        favorite = view.findViewById(R.id.favoriteButton);
        sound = view.findViewById(R.id.soundButton);
        share = view.findViewById(R.id.shareButton);
        buttonsPanel = view.findViewById(R.id.buttonsPanel);
        storiesProgressView = (StoriesProgressView) view.findViewById(R.id.stories);
        storiesProgressView.setStoriesListener(this);
        mask = view.findViewById(R.id.blackMask);
        ((ViewGroup) mask).addView(getLoader());
        progress = view.findViewById(R.id.progress);
        refresh = view.findViewById(R.id.refreshButton);
        progress.setVisibility(View.VISIBLE);
    }

    private void setCutout(View view, int minusOffset) {
        if (Build.VERSION.SDK_INT >= 28) {
            if (getActivity() != null && getActivity().getWindow() != null &&
                    getActivity().getWindow().getDecorView() != null &&
                    getActivity().getWindow().getDecorView().getRootWindowInsets() != null) {
                DisplayCutout cutout = getActivity().getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                if (cutout != null) {
                    View view1 = view.findViewById(R.id.progress_view_sdk);
                    if (view1 != null) {
                        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) view1.getLayoutParams();
                        lp1.topMargin += Math.max(cutout.getSafeInsetTop() - minusOffset, 0);
                        view1.setLayoutParams(lp1);
                    }
                }
            }
        }
    }

   private void setOffsets(View view) {
        if (!Sizes.isTablet()) {
            if (blackBottom != null) {
                Point screenSize = Sizes.getScreenSize(getContext());
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) blackBottom.getLayoutParams();
                float realProps = screenSize.y / ((float) screenSize.x);
                float sn = 1.85f;
                if (realProps > sn) {
                    lp.height = (int) (screenSize.y - screenSize.x * sn) / 2;
                    setCutout(view, lp.height);
                } else {
                    setCutout(view, 0);
                }
                blackBottom.setLayoutParams(lp);
                blackTop.setLayoutParams(lp);
            }
        }
    }

    private void setButtons(final Story story) {
        if (story.disableClose)
            close.setVisibility(View.GONE);

        boolean hasLike = story.hasLike() && InAppStoryManager.getInstance().hasLike();
        boolean hasFavorite = story.hasFavorite() && InAppStoryManager.getInstance().hasFavorite();
        boolean hasShare = story.hasShare() && InAppStoryManager.getInstance().hasShare();

        if (like != null && dislike != null) {
            like.setVisibility(hasLike ? View.VISIBLE : View.GONE);
            dislike.setVisibility(hasLike ? View.VISIBLE : View.GONE);

            like.setActivated(story.liked());
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    like.setEnabled(false);
                    like.setClickable(false);
                    controller.likeDislikeClick(false, story.id,
                            new StoriesReaderPageFragmentController.LikeDislikeCallback() {
                                @Override
                                public void onSuccess() {
                                    like.setEnabled(true);
                                    like.setClickable(true);
                                }

                                @Override
                                public void onError() {
                                    like.setEnabled(true);
                                    like.setClickable(true);
                                }
                            });
                }
            });

            dislike.setActivated(story.disliked());
            dislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dislike.setEnabled(false);
                    dislike.setClickable(false);
                    controller.likeDislikeClick(true, story.id,
                            new StoriesReaderPageFragmentController.LikeDislikeCallback() {
                                @Override
                                public void onSuccess() {
                                    dislike.setEnabled(true);
                                    dislike.setClickable(true);
                                }

                                @Override
                                public void onError() {
                                    dislike.setEnabled(true);
                                    dislike.setClickable(true);
                                }
                            });
                }
            });
        }
        if (favorite != null) {
            favorite.setVisibility(hasFavorite ? View.VISIBLE : View.GONE);
            favorite.setActivated(story.isFavorite());
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    favorite.setEnabled(false);
                    favorite.setClickable(false);
                    controller.favoriteClick(story.id,
                            new StoriesReaderPageFragmentController.LikeDislikeCallback() {
                                @Override
                                public void onSuccess() {
                                    favorite.setEnabled(true);
                                    favorite.setClickable(true);
                                }

                                @Override
                                public void onError() {
                                    favorite.setEnabled(true);
                                    favorite.setClickable(true);
                                }
                            });
                }
            });
        }
        if (share != null) {
            share.setVisibility(hasShare ? View.VISIBLE : View.GONE);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    controller.shareClick(storyId,
                            new StoriesReaderPageFragmentController.ShareEnableDisableCallback() {

                                @Override
                                public void onChange(boolean isEnable) {
                                    share.setEnabled(isEnable);
                                    share.setClickable(isEnable);
                                }
                            });
                }
            });
        }
        if (sound != null) {
            sound.setVisibility(story.hasAudio() ? View.VISIBLE : View.GONE);
            sound.setActivated(InAppStoryManager.getInstance().soundOn);
            sound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InAppStoryManager.getInstance().soundOn = !InAppStoryManager.getInstance().soundOn;
                    CsEventBus.getDefault().post(new SoundOnOffEvent(InAppStoryManager.getInstance().soundOn, storyId));
                }
            });
        }
        if (buttonsPanel != null)
            if (!hasLike && !hasFavorite && !hasShare && !story.hasAudio()) {
                buttonsPanel.setVisibility(View.GONE);
            } else {
                buttonsPanel.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        if (view == null) return;
        if (getArguments() == null) return;
        if (InAppStoryService.getInstance() == null) return;
        initViews(view);
        if (storiesWebView == null) return;
        CsEventBus.getDefault().register(this);
        storyId = getArguments().getInt("story_id");
        CsEventBus.getDefault().post(new PageByIdSelectedEvent(storyId, true));
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        if (story == null) return;
        setButtons(story);
        setOffsets(view);
        storiesWebView.setStoryId(storyId);
        storiesWebView.setIndex(story.lastIndex);
        if (story.durations != null && !story.durations.isEmpty())
            story.slidesCount = story.durations.size();
        storiesProgressView.setStoriesCount(story.slidesCount);
        storiesProgressView.setStoryDurations(story.durations);
        storiesWebView.loadStory(storyId, story.lastIndex);
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
                    CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.CLICK));
                }
            });
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
            storiesProgressView.setStoryDurations(InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId).durations);
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
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        if (story == null || story.loadedPages == null ||
                story.loadedPages.isEmpty() ||
                story.loadedPages.size() <= index)
            return false;
        return true;
    }
}
