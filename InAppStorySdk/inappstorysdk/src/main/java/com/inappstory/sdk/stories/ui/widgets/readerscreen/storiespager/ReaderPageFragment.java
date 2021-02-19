package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.api.models.callbacks.GetStoryByIdCallback;
import com.inappstory.sdk.stories.cache.StoryDownloader;
import com.inappstory.sdk.stories.events.NextStoryPageEvent;
import com.inappstory.sdk.stories.events.NextStoryReaderEvent;
import com.inappstory.sdk.stories.events.PageTaskLoadErrorEvent;
import com.inappstory.sdk.stories.events.PageTaskLoadedEvent;
import com.inappstory.sdk.stories.events.StoriesErrorEvent;
import com.inappstory.sdk.stories.events.StoryOpenEvent;
import com.inappstory.sdk.stories.events.StoryPageOpenEvent;
import com.inappstory.sdk.stories.serviceevents.ChangeIndexEventInFragment;
import com.inappstory.sdk.stories.serviceevents.GeneratedWebPageEvent;
import com.inappstory.sdk.stories.storieslistenerevents.OnNextEvent;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanel;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.Timeline;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.webview.SimpleStoriesWebView;
import com.inappstory.sdk.stories.utils.Sizes;

public class ReaderPageFragment extends Fragment {
    ReaderPageManager manager;
    Timeline timeline;
    SimpleStoriesWebView storiesWebView;
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
        if (storiesWebView != null)
            manager.setWebViewManager(storiesWebView.getManager(), storyId);
    }

    void bindViews(View view) {
        close = view.findViewById(R.id.closeButton);
        refresh = view.findViewById(R.id.refreshButton);
        blackBottom = view.findViewById(R.id.blackBottom);
        blackTop = view.findViewById(R.id.blackTop);
        buttonsPanel = view.findViewById(R.id.buttonsPanel);
        storiesWebView = view.findViewById(R.id.storiesWebView);
        timeline = view.findViewById(R.id.timeline);
    }

    void setStoryId() {
        storyId = getArguments().getInt("story_id");
    }

    void setViews() {
        InAppStoryService.getInstance().getFullStoryById(new GetStoryByIdCallback() {
            @Override
            public void getStory(final Story story) {
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
                        manager.setStoryInfo(story, true);
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
                        manager.setStoryInfo(story, false);
                    }
                });
            }
        }, storyId);
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void nextStoryPage(NextStoryPageEvent event) {
      /*  final int ind = event.getStoryIndex();
        if (ind != storyId) return;
        Story story = StoryDownloader.getInstance().getStoryById(storyId);
        //storiesProgressView.skip();
        if (story.durations != null && !story.durations.isEmpty())
            story.slidesCount = story.durations.size();

        InAppStoryService.getInstance().sendCurrentState();
        if (story.lastIndex == story.slidesCount - 1) {
            CsEventBus.getDefault().post(new NextStoryReaderEvent());

        } else {
            storiesProgressView.setMax(story.lastIndex);
            CsEventBus.getDefault().post(new OnNextEvent());
        }
        */
    }

    void setActions() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            return inflater.inflate(R.layout.cs_fragment_simple_story, container, false);
        } catch (Exception e) {
            e.printStackTrace();
            return new View(getContext());
        }
    }


    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void generatedWebPageEvent(final GeneratedWebPageEvent event) {
        if (storyId != event.getStoryId()) return;
        storiesWebView.loadWebData(event.getLayout(), event.getWebData());
    }

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void pageTaskLoaded(PageTaskLoadedEvent event) {
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
        if (storiesWebView != null)
            storiesWebView.destroyWebView();
        try {
            CsEventBus.getDefault().unregister(this);
        } catch (Exception e) {
        }
        super.onDestroyView();
    }
}
