package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.events.CloseStoryReaderEvent;
import com.inappstory.sdk.stories.events.NextStoryPageEvent;
import com.inappstory.sdk.stories.events.NextStoryReaderEvent;
import com.inappstory.sdk.stories.events.PageByIdSelectedEvent;
import com.inappstory.sdk.stories.events.PageTaskLoadErrorEvent;
import com.inappstory.sdk.stories.events.PageTaskLoadedEvent;
import com.inappstory.sdk.stories.events.PauseStoryReaderEvent;
import com.inappstory.sdk.stories.events.PrevStoryPageEvent;
import com.inappstory.sdk.stories.events.PrevStoryReaderEvent;
import com.inappstory.sdk.stories.events.RestartStoryReaderEvent;
import com.inappstory.sdk.stories.events.ResumeStoryReaderEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.events.StoryCacheLoadedEvent;
import com.inappstory.sdk.stories.events.StoryOpenEvent;
import com.inappstory.sdk.stories.events.StoryPageStartedEvent;
import com.inappstory.sdk.stories.events.StoryPageOpenEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.serviceevents.ChangeIndexEventInFragment;
import com.inappstory.sdk.stories.serviceevents.GeneratedWebPageEvent;
import com.inappstory.sdk.stories.storieslistenerevents.OnNextEvent;
import com.inappstory.sdk.stories.storieslistenerevents.OnPrevEvent;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanel;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.Timeline;
import com.inappstory.sdk.stories.utils.Sizes;

import static com.inappstory.sdk.InAppStoryManager.testGenerated;

public class ReaderPageFragment extends Fragment {
    ReaderPageManager manager;
    Timeline timeline;
    SimpleStoriesView storiesView;
    ButtonsPanel buttonsPanel;



    View blackBottom;
    View blackTop;
    View refresh;
    AppCompatImageView close;
    int storyId;

    void setManagers() {
        if (buttonsPanel != null)
            manager.setButtonsPanelManager(buttonsPanel.getManager(), storyId);
        if (timeline != null)
            manager.setTimelineManager(timeline.getManager(), storyId);
        if (storiesView != null)
            manager.setWebViewManager(storiesView.getManager(), storyId);
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
                    if (timeline != null) {
                        if (story.durations != null) {
                            timeline.getManager().setStoryDurations(story.durations);
                        }
                        timeline.setActive(story.lastIndex);
                        timeline.setActive(-1);
                    }
                }
            }, 100);
        } else {
            Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
            InAppStoryService.getInstance().setCurrentIndex(story.lastIndex);
            if (storiesView != null) {
                InAppStoryService.getInstance().getTimerManager().startTimer(story.getDurations().get(story.lastIndex), true);
                if (story.durations != null) {
                    timeline.getManager().setStoryDurations(story.durations);
                }
                timeline.getManager().start(story.lastIndex);
            }
        }
    }

    void bindViews(View view) {
        close = view.findViewById(R.id.closeButton);
        refresh = view.findViewById(R.id.refreshButton);
        blackBottom = view.findViewById(R.id.blackBottom);
        blackTop = view.findViewById(R.id.blackTop);
        buttonsPanel = view.findViewById(R.id.buttonsPanel);
        storiesView = view.findViewById(R.id.storiesView);
        timeline = view.findViewById(R.id.timeline);
    }

    void setStoryId() {
        storyId = getArguments().getInt("story_id");
    }

    void setViews() {
        InAppStoryService.getInstance().getDownloadManager().getFullStoryById(new GetStoryByIdCallback() {
            @Override
            public void getStory(final Story story) {
                if (story == null) return;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (story.disableClose)
                            close.setVisibility(View.GONE);
                        buttonsPanel.setButtonsVisibility(story.hasLike(), story.hasFavorite(), story.hasShare(), story.hasAudio());
                        buttonsPanel.setButtonsStatus(story.getLike(), story.favorite ? 1 : 0);
                        if (!Sizes.isTablet()) {
                            if (blackBottom != null) {
                                Point screenSize = Sizes.getScreenSize();
                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) blackBottom.getLayoutParams();
                                float realProps = screenSize.y / ((float) screenSize.x);
                                float sn = 1.85f;
                                if (realProps > sn) {
                                    lp.height = (int) (screenSize.y - screenSize.x * sn) / 2;
                                }
                                blackBottom.setLayoutParams(lp);
                                blackTop.setLayoutParams(lp);

                            }
                        }
                        if (story.durations != null && !story.durations.isEmpty())
                            story.slidesCount = story.durations.size();

                        storiesView.getManager().setIndex(story.lastIndex);
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                manager.setStoryInfo(story, true);
                            }
                        });
                    }
                });

            }

            @Override
            public void loadError(int type) {
                CsEventBus.getDefault().post(new StoriesErrorEvent(StoriesErrorEvent.READER));
            }

            @Override
            public void getPartialStory(final Story story) {
                if (story == null) return;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (story.disableClose)
                            close.setVisibility(View.GONE);
                        buttonsPanel.setButtonsVisibility(story.hasLike(), story.hasFavorite(), story.hasShare(), story.hasAudio());
                        buttonsPanel.setButtonsStatus(story.getLike(), story.favorite ? 1 : 0);
                        if (!Sizes.isTablet()) {
                            if (blackBottom != null) {
                                Point screenSize = Sizes.getScreenSize();
                                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) blackBottom.getLayoutParams();
                                float realProps = screenSize.y / ((float) screenSize.x);
                                float sn = 1.85f;
                                if (realProps > sn) {
                                    lp.height = (int) (screenSize.y - screenSize.x * sn) / 2;
                                }
                                blackBottom.setLayoutParams(lp);
                                blackTop.setLayoutParams(lp);
                            }
                        }
                        if (story.durations != null && !story.durations.isEmpty())
                            story.slidesCount = story.durations.size();
                        storiesView.getManager().setIndex(0);
                        manager.setStoryInfo(story, false);
                    }
                });
            }
        }, storyId);
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
                    // timeline.setActive(story.lastIndex);
                    if (story.durations != null) {
                        timeline.getManager().setStoryDurations(story.durations);
                    }
                    timeline.getManager().start(story.lastIndex);
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
        if (InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId) == null) return;
        int lind = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId).lastIndex;
        if (lind > 0) {
            CsEventBus.getDefault().post(new OnPrevEvent());
            StatisticManager.getInstance().sendCurrentState();
            timeline.getManager().start(lind - 1);
        } else {
            CsEventBus.getDefault().post(new PrevStoryReaderEvent());
        }
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void nextStoryPage(NextStoryPageEvent event) {
        final int ind = event.getStoryIndex();
        if (ind != storyId) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        //storiesProgressView.skip();
        if (story == null) return;
        if (story.durations != null && !story.durations.isEmpty())
            story.slidesCount = story.durations.size();

        StatisticManager.getInstance().sendCurrentState();
        if (story.lastIndex == story.slidesCount - 1) {
            CsEventBus.getDefault().post(new NextStoryReaderEvent());

        } else {
            timeline.getManager().start(story.lastIndex + 1);
            CsEventBus.getDefault().post(new OnNextEvent());
        }

    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void restartEvent(RestartStoryReaderEvent event) {
        if (storyId == event.getId()) {

            timeline.setSlideDuration(event.getIndex(), event.getNewDuration());
            timeline.forceStartProgress();
            InAppStoryService.getInstance().getTimerManager().startTimer(event.getNewDuration(), true);
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void storyCacheLoaded(StoryCacheLoadedEvent event) {
        Log.e("animationDur", "cache " + event.storyId + " " + storyId);
        if (storyId != event.getStoryId()) return;
        manager.storyInfoLoaded();
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pauseStoryEvent(PauseStoryReaderEvent event) {
        if (InAppStoryService.getInstance().getCurrentId() != storyId) return;
        final boolean isWithBackground = event.isWithBackground();
        manager.pauseSlide();

    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void resumeStoryEvent(ResumeStoryReaderEvent event) {
        if (InAppStoryService.getInstance().getCurrentId() != storyId) return;
        final boolean isWithBackground = event.isWithBackground();
        manager.resumeSlide();

    }


    void setActions() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CsEventBus.getDefault().post(new CloseStoryReaderEvent(CloseStory.CLICK));
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //clicks
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeIndexEvent(ChangeIndexEventInFragment event) {
        if (event.getCurItem() != storyId) return;
        final int curIndex = event.getIndex();
        manager.loadStoryAndSlide(storyId, curIndex);
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pageLoadError(PageTaskLoadErrorEvent errorEvent) {
        if (errorEvent.getId() != storyId) return;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh.setVisibility(View.VISIBLE);
            }
        }, 200);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            if (testGenerated) {
                return inflater.inflate(R.layout.cs_fragment_generated_story, container, false);
            } else {
                return inflater.inflate(R.layout.cs_fragment_simple_story, container, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new View(getContext());
        }
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void generatedWebPageEvent(final GeneratedWebPageEvent event) {
        if (storyId != event.getStoryId()) return;
        storiesView.getManager().loadWebData(event.getLayout(), event.getWebData());
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pageTaskLoaded(PageTaskLoadedEvent event) {
        Log.e("storyLoaded", "PageTaskLoadedEvent");
        manager.storyLoaded(event.getId(), event.getIndex());
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryPageEvent(StoryPageOpenEvent event) {
        if (this.storyId != event.getStoryId()) return;
        manager.loadStoryAndSlide(event.storyId, event.index);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = new ReaderPageManager();
        CsEventBus.getDefault().register(this);
        setStoryId();
        bindViews(view);
        setActions();
        setManagers();
        manager.setStoryId(storyId);
        setViews();
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryEvent(StoryOpenEvent event) {
        manager.storyOpen(event.getStoryId());
    }

    @Override
    public void onDestroyView() {
        if (storiesView != null)
            storiesView.destroyView();
        try {
            CsEventBus.getDefault().unregister(this);
        } catch (Exception e) {
        }
        super.onDestroyView();
    }
}
