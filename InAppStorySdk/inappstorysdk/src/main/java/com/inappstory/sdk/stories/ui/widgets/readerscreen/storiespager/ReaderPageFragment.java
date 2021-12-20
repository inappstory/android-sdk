package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.ui.reader.ReaderManager;
import com.inappstory.sdk.stories.ui.reader.StoriesFragment;
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
    ReaderManager parentManager;

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
        manager.setTimerManager(new TimerManager());
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

    void setViews(View view) {
        if (InAppStoryService.getInstance() == null) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId);
        if (story == null) return;
        if (story.disableClose)
            close.setVisibility(View.GONE);
        if (buttonsPanel != null) {
            buttonsPanel.setButtonsVisibility(readerSettings,
                    story.hasLike(), story.hasFavorite(), story.hasShare(), story.hasAudio());
            buttonsPanel.setButtonsStatus(story.getLike(), story.favorite ? 1 : 0);
        }
        setOffsets(view);
        if (storiesView != null)
            storiesView.getManager().setIndex(story.lastIndex);

    }

    public void storyLoadStart() {
        showLoader();
    }

    public void storyLoadedSuccess() {
        refresh.setVisibility(View.GONE);
        hideLoader();
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


    View loader;

    void setActions() {
        if (close != null)
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InAppStoryManager.closeStoryReader(CloseStory.CLICK);
                }
            });
        if (refresh != null)
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                    if (loader == null) return;
                    loader.setAlpha(1f);
                    loader.setVisibility(View.VISIBLE);
                    manager.reloadStory();
                }
            });
    }

    private void showLoader() {
        if (loader == null) return;
        loader.setAlpha(1f);
        loader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
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
                if (loader == null) return;
                loader.setVisibility(View.GONE);
                loader.setAlpha(1f);
            }
        });
        if (loader == null) return;
        loader.startAnimation(anim);
    }

    public void storyLoadError() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                refresh.setVisibility(View.VISIBLE);
                hideLoader();
                close.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        readerSettings = JsonParser.fromJson(getArguments().getString(CS_READER_SETTINGS),
                StoriesReaderSettings.class);
        try {
            return createFragmentView(container);
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
        if (readerSettings.timerGradient)
            addGradient(context, readerContainer);

        createLoader();
        readerContainer.addView(loader);
        return readerContainer;
    }

    private void createLoader() {
        Context context = getContext();
        loader = new RelativeLayout(context);
        loader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loader.setElevation(8);
        }
        ((ViewGroup) loader).addView(AppearanceManager.getLoader(context));
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
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ((SimpleStoriesWebView) storiesView).setLayoutParams(lp);
        ((SimpleStoriesWebView) storiesView).setId(R.id.ias_stories_view);
        webViewContainer.addView(((SimpleStoriesWebView) storiesView));

        View gradient = new View(context);
        gradient.setClickable(false);
        gradient.setLayoutParams(lp);
        gradient.setBackground(AppCompatResources.getDrawable(context, R.drawable.story_gradient));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gradient.setElevation(8);
        }
        webViewContainer.addView(gradient);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gradientView.setElevation(8);
        }
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

    StoriesReaderSettings readerSettings = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = new ReaderPageManager();
        setStoryId();
        manager.host = this;
        if (parentManager == null && getParentFragment() instanceof StoriesFragment) {
            parentManager = ((StoriesFragment) getParentFragment()).readerManager;
        }
        manager.parentManager = parentManager;
        if (parentManager != null) {
            parentManager.addSubscriber(manager);
        }
        bindViews(view);
        setActions();
        setManagers();
        if (InAppStoryService.getInstance() != null) {
            if (InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId) != null)
                manager.setSlideIndex(InAppStoryService.getInstance().getDownloadManager()
                        .getStoryById(storyId).lastIndex);

            manager.setStoryId(storyId);
            setViews(view);
            InAppStoryService.getInstance().getDownloadManager().addSubscriber(manager);
            manager.storyLoadedInCache();
        }

    }


    @Override
    public void onDestroyView() {
        if (storiesView != null)
            storiesView.destroyView();

        parentManager.removeSubscriber(manager);
        InAppStoryService.getInstance().getDownloadManager().removeSubscriber(manager);
        super.onDestroyView();
    }
}
