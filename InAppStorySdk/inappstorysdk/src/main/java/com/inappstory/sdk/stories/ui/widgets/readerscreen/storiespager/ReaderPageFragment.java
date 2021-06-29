package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
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
import com.inappstory.sdk.stories.events.SoundOnOffEvent;
import com.inappstory.sdk.stories.events.StoryCacheLoadedEvent;
import com.inappstory.sdk.stories.events.StoryOpenEvent;
import com.inappstory.sdk.stories.events.StoryPageStartedEvent;
import com.inappstory.sdk.stories.events.StoryPageOpenEvent;
import com.inappstory.sdk.stories.events.SyncTimerEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.serviceevents.ChangeIndexEventInFragment;
import com.inappstory.sdk.stories.ui.reader.StoriesReaderSettings;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanel;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.Timeline;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.webview.SimpleStoriesWebView;
import com.inappstory.sdk.stories.utils.Sizes;

import static com.inappstory.sdk.AppearanceManager.BOTTOM_LEFT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_RIGHT;
import static com.inappstory.sdk.AppearanceManager.CS_READER_SETTINGS;
import static com.inappstory.sdk.AppearanceManager.TOP_LEFT;
import static com.inappstory.sdk.AppearanceManager.TOP_RIGHT;
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
                // timeline.getManager().start(story.lastIndex);
            }
        }
    }

    void bindViews(View view) {
        close = view.findViewById(R.id.ias_close_button);
        refresh = view.findViewById(R.id.ias_refresh_button);
        blackBottom = view.findViewById(R.id.ias_black_bottom);
        blackTop = view.findViewById(R.id.ias_black_top);
        buttonsPanel = view.findViewById(R.id.ias_buttons_panel);
        storiesView = view.findViewById(R.id.ias_stories_view);
        timeline = view.findViewById(R.id.ias_timeline);


        try {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) close.getLayoutParams();
            RelativeLayout.LayoutParams storiesProgressViewLP = (RelativeLayout.LayoutParams) timeline.getLayoutParams();
            int cp = readerSettings.closePosition;
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
                    layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                    storiesProgressViewLP.topMargin = Sizes.dpToPxExt(12);
                    layoutParams.topMargin = Sizes.dpToPxExt(8);
                    break;
                case BOTTOM_LEFT:
                    storiesProgressViewLP.topMargin = Sizes.dpToPxExt(12);
                    layoutParams.topMargin = Sizes.dpToPxExt(8);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                    break;
            }

            close.setLayoutParams(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setStoryId() {
        storyId = getArguments().getInt("story_id");
    }

    Story story;

    void setViews(View view) {
        story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        if (story == null) return;
        if (story.disableClose)
            close.setVisibility(View.GONE);
        buttonsPanel.setButtonsVisibility(readerSettings,
                story.hasLike(), story.hasFavorite(), story.hasShare(), story.hasAudio());
        buttonsPanel.setButtonsStatus(story.getLike(), story.favorite ? 1 : 0);
        setOffsets(view);
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

    private void setCutout(View view, int minusOffset) {
        if (Build.VERSION.SDK_INT >= 28) {
            if (getActivity() != null && getActivity().getWindow() != null &&
                    getActivity().getWindow().getDecorView() != null &&
                    getActivity().getWindow().getDecorView().getRootWindowInsets() != null) {
                DisplayCutout cutout = getActivity().getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                if (cutout != null) {
                    View view1 = view.findViewById(R.id.ias_timeline_container);
                    if (view1 != null) {
                        RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) view1.getLayoutParams();
                        lp1.topMargin += Math.max(cutout.getSafeInsetTop() - minusOffset, 0);
                        view1.setLayoutParams(lp1);
                    }
                }
            }
        }
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void storyPageStartedEvent(StoryPageStartedEvent event) {
        if (this.storyId != event.getStoryId()) return;
        final int ind = event.index;
        if (story != null)
            story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        if (InAppStoryService.getInstance().getCurrentId() == storyId
                && story.lastIndex == ind) {
            // timeline.setActive(story.lastIndex);
            if (story.durations != null) {
                timeline.getManager().setStoryDurations(story.durations);
            }
            Log.e("Story_VisualTimers", "StoryPageStartedEvent");
            timeline.getManager().start(story.lastIndex);
            InAppStoryService.getInstance().getTimerManager().startTimer(story.getDurations().get(ind), true);
            if (OldStatisticManager.getInstance().currentEvent != null)
                OldStatisticManager.getInstance().currentEvent.timer = System.currentTimeMillis();
        }
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void prevStoryPage(PrevStoryPageEvent event) {
        if (event.getStoryId() != storyId) return;
        if (story == null)
            story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        if (story == null)
            return;
        int lind = story.lastIndex;
        if (lind > 0) {
            manager.prevSlide();
            StatisticManager.getInstance().sendCurrentState();
            openPrevSlide(story);
        } else {
            CsEventBus.getDefault().post(new PrevStoryReaderEvent());
        }
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void nextStoryPage(NextStoryPageEvent event) {
        if (event.getStoryId() != storyId) return;
        if (story == null)
            story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        //storiesProgressView.skip();
        if (story == null) return;
        if (story.durations != null && !story.durations.isEmpty())
            story.slidesCount = story.durations.size();

        StatisticManager.getInstance().sendCurrentState();
        if (story.lastIndex == story.slidesCount - 1) {
            CsEventBus.getDefault().post(new NextStoryReaderEvent());

        } else {
            manager.nextSlide();
            StatisticManager.getInstance().sendCurrentState();
            openNextSlide(story);
        }
    }

    private void openNextSlide(Story st) {
        if (st.lastIndex >= st.slidesCount - 1) return;
        st.setLastIndex(st.lastIndex + 1);
        InAppStoryService.getInstance().getDownloadManager().changePriorityForSingle(storyId);
        Log.e("Story_VisualTimers", "openNextSlide");
        timeline.getManager().setCurrentSlide(st.lastIndex);
        InAppStoryService.getInstance().sendPageOpenStatistic(storyId, st.lastIndex);
        manager.loadStoryAndSlide(st.id, st.lastIndex);
    }

    private void openPrevSlide(Story st) {
        if (st.lastIndex <= 0) return;
        st.setLastIndex(st.lastIndex - 1);
        Log.e("changePriority", "openPrevSlide " + st.id + " " + st.lastIndex + " " + st.slidesCount);
        Log.e("Story_VisualTimers", "openPrevSlide");
        timeline.getManager().setCurrentSlide(st.lastIndex);
        InAppStoryService.getInstance().sendPageOpenStatistic(storyId, st.lastIndex);
        manager.loadStoryAndSlide(st.id, st.lastIndex);
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeSoundStatus(SoundOnOffEvent event) {
        buttonsPanel.refreshSoundStatus();
        storiesView.changeSoundStatus();
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void restartEvent(RestartStoryReaderEvent event) {
        if (storyId == event.getId()) {
            if (event.getIndex() > 0) {
                timeline.setSlideDuration(event.getIndex(), event.getNewDuration());
                timeline.forceStartProgress();
                InAppStoryService.getInstance().getTimerManager().startTimer(event.getNewDuration(), true);
            } else {
     //           timeline.forceRestartProgress();
                InAppStoryService.getInstance().getTimerManager().restartTimer(event.getNewDuration());
                manager.restartSlide();
            }
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
        readerSettings = JsonParser.fromJson(getArguments().getString(CS_READER_SETTINGS),
                StoriesReaderSettings.class);
        try {
            if (testGenerated) {
                return inflater.inflate(R.layout.cs_fragment_generated_story, container, false);
            } else {
                return createFragmentView(container);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new View(getContext());
        }
    }

    View createFragmentView(ViewGroup root) {
        Context context = getContext();

        RelativeLayout res = new RelativeLayout(context);
        res.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        createRefreshButton(context);
        setLinearContainer(context, linearLayout);
        res.addView(linearLayout);
        res.addView(refresh);

        return res;
    }

    private void createRefreshButton(Context context) {
        refresh = new ImageView(context);
        refresh.setId(R.id.ias_refresh_button);
        RelativeLayout.LayoutParams refreshLp = new RelativeLayout.LayoutParams(Sizes.dpToPxExt(32), Sizes.dpToPxExt(32));
        refreshLp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            refresh.setElevation(18);
        }
        ((ImageView) refresh).setScaleType(ImageView.ScaleType.FIT_CENTER);
        refresh.setVisibility(View.GONE);
        ((ImageView) refresh).setImageDrawable(getResources().getDrawable(readerSettings.refreshIcon));
        refresh.setLayoutParams(refreshLp);
    }

    private void setLinearContainer(Context context, LinearLayout linearLayout) {
        blackTop = new View(context);
        blackTop.setId(R.id.ias_black_top);
        blackTop.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        blackTop.setBackgroundColor(Color.BLACK);
        blackBottom = new View(context);
        blackBottom.setId(R.id.ias_black_bottom);
        blackBottom.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        blackBottom.setBackgroundColor(Color.BLACK);
        RelativeLayout main = new RelativeLayout(context);
        main.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        main.addView(createReaderContainer(context));
        main.addView(createTimelineContainer(context));
        linearLayout.addView(blackTop);
        linearLayout.addView(main);
        linearLayout.addView(blackBottom);

    }

    private RelativeLayout createReaderContainer(Context context) {
        RelativeLayout readerContainer = new RelativeLayout(context);

        readerContainer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            readerContainer.setElevation(9);
        }

        addButtonsPanel(context, readerContainer);
        // readerContainer.addView(createProgressContainer(context));
        readerContainer.addView(createWebViewContainer(context));
        addGradient(context, readerContainer);

        return readerContainer;
    }

    private View createWebViewContainer(Context context) {
        LinearLayout webViewContainer = new LinearLayout(context);
        RelativeLayout.LayoutParams webViewContainerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
        );
        webViewContainerParams.addRule(RelativeLayout.ABOVE, R.id.ias_buttons_panel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webViewContainer.setElevation(4);
        }
        webViewContainer.setOrientation(LinearLayout.VERTICAL);
        webViewContainer.setLayoutParams(webViewContainerParams);
        storiesView = new SimpleStoriesWebView(context);
        ((SimpleStoriesWebView) storiesView).setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((SimpleStoriesWebView) storiesView).setId(R.id.ias_stories_view);
        webViewContainer.addView(((SimpleStoriesWebView) storiesView));
        return webViewContainer;
    }

    private View createProgressContainer(Context context) {
        return null;
    }

    private void addButtonsPanel(Context context, RelativeLayout relativeLayout) {
        buttonsPanel = new ButtonsPanel(context);
        RelativeLayout.LayoutParams buttonsPanelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, Sizes.dpToPxExt(60)
        );
        buttonsPanelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        buttonsPanel.setVisibility(View.GONE);
        buttonsPanel.setId(R.id.ias_buttons_panel);
        buttonsPanel.setOrientation(LinearLayout.HORIZONTAL);
        buttonsPanel.setBackgroundColor(Color.BLACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            buttonsPanel.setElevation(9);
        }
        buttonsPanel.setLayoutParams(buttonsPanelParams);
        buttonsPanel.setIcons(readerSettings);
        relativeLayout.addView(buttonsPanel);
    }

    private void addGradient(Context context, RelativeLayout relativeLayout) {
        View gradientView = new View(context);
        gradientView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        ));
        gradientView.setClickable(false);
        gradientView.setBackground(getResources().getDrawable(R.drawable.story_gradient));
        relativeLayout.addView(gradientView);
    }

    private RelativeLayout createTimelineContainer(Context context) {
        RelativeLayout timelineContainer = new RelativeLayout(context);
        timelineContainer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        timelineContainer.setId(R.id.ias_timeline_container);
        timelineContainer.setMinimumHeight(Sizes.dpToPxExt(40));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            timelineContainer.setElevation(20);
        }
        timeline = new Timeline(context);
        timeline.setId(R.id.ias_timeline);
        timeline.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                Sizes.dpToPxExt(3)));
        timeline.setPadding(Sizes.dpToPxExt(8), 0, Sizes.dpToPxExt(8), 0);

        close = new AppCompatImageView(context);
        close.setId(R.id.ias_close_button);
        close.setLayoutParams(new RelativeLayout.LayoutParams(Sizes.dpToPxExt(40), Sizes.dpToPxExt(40)));
        close.setPadding(0, Sizes.dpToPxExt(8), 0, Sizes.dpToPxExt(8));
        close.setBackground(null);
        close.setImageDrawable(getResources().getDrawable(readerSettings.closeIcon));
        timelineContainer.addView(timeline);
        timelineContainer.addView(close);
        return timelineContainer;
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pageTaskLoaded(PageTaskLoadedEvent event) {
        manager.storyLoaded(event.getId(), event.getIndex());
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void closeReaderEvent(CloseStory event) {
        try {
            CsEventBus.getDefault().unregister(this);
        } catch (Exception ignored) {

        }
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryPageEvent(StoryPageOpenEvent event) {
        if (this.storyId != event.getStoryId()) return;
        manager.loadStoryAndSlide(event.storyId, event.index);
    }

    StoriesReaderSettings readerSettings = null;

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
        setViews(view);
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryEvent(StoryOpenEvent event) {
        manager.storyOpen(event.getStoryId());
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void syncTimer(SyncTimerEvent event) {
        if (InAppStoryService.getInstance().getCurrentId() != storyId) return;
        manager.syncTime(event.getCurrentTimeLeft(), event.getEventTimer());
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
