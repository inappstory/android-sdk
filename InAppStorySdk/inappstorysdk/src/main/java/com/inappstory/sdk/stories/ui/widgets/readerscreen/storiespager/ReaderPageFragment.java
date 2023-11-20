package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import static com.inappstory.sdk.AppearanceManager.BOTTOM_END;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_LEFT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_RIGHT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_START;
import static com.inappstory.sdk.AppearanceManager.CS_READER_SETTINGS;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT;
import static com.inappstory.sdk.AppearanceManager.TOP_END;
import static com.inappstory.sdk.AppearanceManager.TOP_LEFT;
import static com.inappstory.sdk.AppearanceManager.TOP_RIGHT;
import static com.inappstory.sdk.AppearanceManager.TOP_START;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.ui.reader.ReaderManager;
import com.inappstory.sdk.stories.ui.reader.StoriesContentFragment;
import com.inappstory.sdk.stories.ui.reader.StoriesGradientObject;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanel;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.StoryTimeline;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.webview.SimpleStoriesWebView;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.List;

public class ReaderPageFragment extends Fragment {
    ReaderPageManager manager;
    StoryTimeline timeline;
    SimpleStoriesView storiesView;
    ButtonsPanel buttonsPanel;
    View aboveButtonsPanel;
    ReaderManager parentManager;

    View blackBottom;
    View blackTop;
    View refresh;
    AppCompatImageView close;
    int storyId;

    boolean setManagers() {
        boolean readerInitSuccess = true;
        if (buttonsPanel != null)
            manager.setButtonsPanelManager(buttonsPanel.getManager(), storyId);
        else
            readerInitSuccess = false;
        if (timeline != null)
            manager.setTimelineManager(timeline.getTimelineManager(), storyId);
        else
            readerInitSuccess = false;
        if (storiesView != null)
            manager.setWebViewManager(storiesView.getManager(), storyId);
        else
            readerInitSuccess = false;
        manager.setTimerManager(new TimerManager());
        return readerInitSuccess;
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
            int cp = appearanceSettings.csClosePosition();
            int viewsMargin = Sizes.dpToPxExt(8, getContext());
            storiesProgressViewLP.leftMargin =
                    storiesProgressViewLP.rightMargin =
                            layoutParams.rightMargin = viewsMargin;

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
                    storiesProgressViewLP.topMargin = viewsMargin;
                    layoutParams.topMargin = viewsMargin;
                    break;
                case BOTTOM_LEFT:
                    storiesProgressViewLP.topMargin = viewsMargin;
                    layoutParams.topMargin = viewsMargin;
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                    break;
                case TOP_START:
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                    storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                    storiesProgressViewLP.addRule(RelativeLayout.END_OF, close.getId());
                    break;
                case TOP_END:
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                    storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                    storiesProgressViewLP.addRule(RelativeLayout.START_OF, close.getId());
                    break;
                case BOTTOM_START:
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                    layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                    storiesProgressViewLP.topMargin = viewsMargin;
                    layoutParams.topMargin = viewsMargin;
                    break;
                case BOTTOM_END:
                    storiesProgressViewLP.topMargin = viewsMargin;
                    layoutParams.topMargin = viewsMargin;
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                    layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                    break;
            }
            close.setLayoutParams(layoutParams);
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
        }
    }

    void setStoryId() {
        storyId = getArguments().getInt("story_id");
    }

    void setViews(View view) {
        if (InAppStoryService.getInstance() == null) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId,
                manager.getStoryType());
        if (story == null) return;
        if (timeline != null) {
            timeline.getTimelineManager().setSlidesCount(story.getSlidesCount());
        }
        if (story.disableClose)
            close.setVisibility(View.GONE);
        if (buttonsPanel != null) {
            buttonsPanel.setButtonsVisibility(
                    appearanceSettings,
                    story.hasLike(),
                    story.hasFavorite(),
                    story.hasShare(),
                    story.hasAudio()
            );
            buttonsPanel.setButtonsStatus(story.getLike(), story.favorite ? 1 : 0);
            aboveButtonsPanel.setVisibility(buttonsPanel.getVisibility());
        }
        setOffsets(view);
        if (storiesView != null)
            storiesView.getManager().setIndex(story.lastIndex);

    }

    public void storyLoadStart() {
        showLoaderContainer();
    }

    public void storyLoadedSuccess() {
        hideLoaderContainer();
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

    public void showLoader() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (loaderContainer == null) return;
                refresh.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);
            }
        });
    }

    public void showLoaderContainer() {
        Log.e("showLoaderContainer", "show");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (loaderContainer == null) return;
                refresh.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);
                showLoaderContainerAnimated();
               /* Animation anim = new AlphaAnimation(0f, 1f);
                anim.setDuration(200);
                loaderContainer.startAnimation(anim);*/
            }
        });

    }

    private void hideLoaderContainer() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                hideLoaderContainerAnimated();
                refresh.setVisibility(View.GONE);
                loader.setVisibility(View.GONE);
            }
        });

    }

    public void storyLoadError() {
        Log.e("hideLoader", "storyLoadError");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
                close.setVisibility(View.VISIBLE);
                showLoaderContainerAnimated();
            }
        });
    }

    public void slideLoadError() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
                close.setVisibility(View.VISIBLE);
                showLoaderContainerAnimated();
            }
        });
    }

    private void showLoaderContainerAnimated() {
        loaderContainer.clearAnimation();
        loaderContainer.animate().alpha(1f).setStartDelay(300).setDuration(300).start();
    }

    private void hideLoaderContainerAnimated() {
        loaderContainer.clearAnimation();
        loaderContainer.animate().alpha(0f).setDuration(300).start();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appearanceSettings = (StoriesReaderAppearanceSettings)
                requireArguments().getSerializable(StoriesReaderAppearanceSettings.SERIALIZABLE_KEY);
        try {
            return createFragmentView(container);
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            return new View(getContext());
        }
    }

    LinearLayout linearLayout;

    View createFragmentView(ViewGroup root) {
        Context context = getContext();

        RelativeLayout res = new RelativeLayout(context);
        res.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        if (!Sizes.isTablet() && appearanceSettings.csReaderBackgroundColor() != Color.BLACK) {
            linearLayout.setBackgroundColor(Color.BLACK);
        }
        setLinearContainer(context, linearLayout);
        res.addView(linearLayout);

        return res;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void createRefreshButton(Context context) {
        refresh = new ImageView(context);
        refresh.setId(R.id.ias_refresh_button);
        RelativeLayout.LayoutParams refreshLp = new RelativeLayout.LayoutParams(
                Sizes.dpToPxExt(40, getContext()),
                Sizes.dpToPxExt(40, getContext())
        );
        refreshLp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        refresh.setElevation(18);
        ((ImageView) refresh).setScaleType(ImageView.ScaleType.FIT_XY);
        refresh.setVisibility(View.GONE);
        ((ImageView) refresh).setImageDrawable(getResources().getDrawable(appearanceSettings.csRefreshIcon()));
        refresh.setLayoutParams(refreshLp);
    }

    private void setLinearContainer(Context context, LinearLayout linearLayout) {
        blackTop = new View(context);
        blackTop.setId(R.id.ias_black_top);
        blackTop.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        blackTop.setBackgroundColor(Color.TRANSPARENT);
        blackBottom = new View(context);
        blackBottom.setId(R.id.ias_black_bottom);
        blackBottom.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        blackBottom.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout content = new RelativeLayout(context);
        content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        ViewGroup main;
        RelativeLayout.LayoutParams contentLP = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        contentLP.addRule(RelativeLayout.ABOVE, R.id.ias_buttons_panel);
        aboveButtonsPanel = new View(context);
        aboveButtonsPanel.setBackgroundColor(Color.BLACK);
        aboveButtonsPanel.setVisibility(View.GONE);
        RelativeLayout.LayoutParams aboveLp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                Sizes.dpToPxExt(appearanceSettings.csReaderRadius(), context));
        aboveLp.addRule(RelativeLayout.ABOVE, R.id.ias_buttons_panel);
        aboveButtonsPanel.setLayoutParams(aboveLp);

        main = new CardView(context);
        main.setLayoutParams(contentLP);
        ((CardView) main).setRadius(Sizes.dpToPxExt(appearanceSettings.csReaderRadius(), getContext()));
        ((CardView) main).setCardBackgroundColor(Color.TRANSPARENT);
        main.setElevation(0);

        RelativeLayout cardContent = new RelativeLayout(context);
        cardContent.setLayoutParams(new CardView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.MATCH_PARENT));
        cardContent.addView(createReaderContainer(context));
        cardContent.addView(createTimelineContainer(context));
        main.addView(cardContent);
        createButtonsPanel(context);
        content.addView(buttonsPanel);
        content.addView(aboveButtonsPanel);
        content.addView(main);
        linearLayout.addView(blackTop);
        linearLayout.addView(content);
        linearLayout.addView(blackBottom);
    }

    private RelativeLayout createReaderContainer(Context context) {
        RelativeLayout readerContainer = new RelativeLayout(context);

        readerContainer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        readerContainer.setElevation(9);

        // readerContainer.addView(createProgressContainer(context));
        readerContainer.addView(createWebViewContainer(context));
        if (appearanceSettings.csTimerGradientEnable())
            addGradient(context, readerContainer);

        createLoader();
        createRefreshButton(context);
        loaderContainer = new RelativeLayout(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loaderContainer.setElevation(28);
        }
        loaderContainer.setAlpha(0.99f);
        loaderContainer.setLayoutParams(
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        loaderContainer.setBackgroundColor(Color.BLACK);
        loaderContainer.addView(loader);
        loaderContainer.addView(refresh);
        readerContainer.addView(loaderContainer);
        return readerContainer;
    }

    RelativeLayout loaderContainer;

    private void createLoader() {
        Context context = getContext();
        loader = new RelativeLayout(context);
        loader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        loader.setElevation(8);
        ((ViewGroup) loader).addView(AppearanceManager.getLoader(context));
    }


    private View createWebViewContainer(Context context) {
        LinearLayout webViewContainer = new LinearLayout(context);
        RelativeLayout.LayoutParams webViewContainerParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT
        );
        webViewContainer.setElevation(4);
        webViewContainer.setOrientation(LinearLayout.VERTICAL);
        webViewContainer.setLayoutParams(webViewContainerParams);
        storiesView = new SimpleStoriesWebView(context);
        ((SimpleStoriesWebView) storiesView).setId(R.id.ias_stories_view);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ((SimpleStoriesWebView) storiesView).setLayoutParams(lp);
        webViewContainer.addView(((SimpleStoriesWebView) storiesView));
        return webViewContainer;
    }


    private View createProgressContainer(Context context) {
        return null;
    }


    private void createButtonsPanel(Context context) {
        buttonsPanel = new ButtonsPanel(context);
        RelativeLayout.LayoutParams buttonsPanelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, Sizes.dpToPxExt(60, context)
        );
        buttonsPanelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        buttonsPanel.setVisibility(View.GONE);
        buttonsPanel.setId(R.id.ias_buttons_panel);
        buttonsPanel.setOrientation(LinearLayout.HORIZONTAL);
        buttonsPanel.setBackgroundColor(Color.BLACK);
        buttonsPanel.setLayoutParams(buttonsPanelParams);
        buttonsPanel.setIcons(appearanceSettings);
    }

    private void addGradient(Context context, RelativeLayout relativeLayout) {
        View gradientView = new View(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        gradientView.setElevation(8);
        gradientView.setOutlineProvider(null);
        gradientView.setClickable(false);
        StoriesGradientObject timerGradient = appearanceSettings.csTimerGradient();
        if (timerGradient != null) {
            List<Integer> colors = timerGradient.csColors;
            List<Float> locations = timerGradient.csLocations;
            final int[] colorsArray = new int[timerGradient.csColors.size()];
            final float[] locationsArray = new float[timerGradient.csColors.size()];

            if (colors == null ||
                    colors.isEmpty()) {
                return;
            }
            if (colors.size() != locations.size()) return;
            int i = 0;
            for (Integer color : colors) {
                colorsArray[i] = color.intValue();
                i++;
            }
            i = 0;
            for (Float location : locations) {
                locationsArray[i] = location.floatValue();
                i++;
            }
            if (timerGradient.csGradientHeight > 0) {
                lp.height = Sizes.dpToPxExt(timerGradient.csGradientHeight, context);
            }
            ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
                @Override
                public Shader resize(int width, int height) {

                    return new LinearGradient(0f, 0f, 0f, 1f * height,
                            colorsArray,
                            locationsArray,
                            Shader.TileMode.REPEAT);
                }
            };
            PaintDrawable paint = new PaintDrawable();
            paint.setShape(new RectShape());
            paint.setShaderFactory(shaderFactory);
            gradientView.setBackground(paint);
        } else {
            gradientView.setBackground(getResources().getDrawable(R.drawable.story_gradient));
        }

        gradientView.setLayoutParams(lp);

        relativeLayout.addView(gradientView);
    }


    private RelativeLayout createTimelineContainer(Context context) {
        RelativeLayout timelineContainer = new RelativeLayout(context);
        RelativeLayout.LayoutParams tclp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        int offset = Sizes.dpToPxExt(Math.max(0, appearanceSettings.csReaderRadius() - 16), getContext()) / 2;
        tclp.setMargins(offset, offset, offset, 0);
        timelineContainer.setLayoutParams(tclp);
        timelineContainer.setId(R.id.ias_timeline_container);
        timelineContainer.setMinimumHeight(Sizes.dpToPxExt(30, getContext()));
        timelineContainer.setElevation(20);
        timeline = new StoryTimeline(context);
        timeline.setId(R.id.ias_timeline);
        timeline.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                Sizes.dpToPxExt(3, getContext())));

        close = new AppCompatImageView(context);
        close.setId(R.id.ias_close_button);
        close.setLayoutParams(new RelativeLayout.LayoutParams(
                Sizes.dpToPxExt(30, getContext()),
                Sizes.dpToPxExt(30, getContext()))
        );
        close.setBackground(null);
        close.setImageDrawable(getResources().getDrawable(appearanceSettings.csCloseIcon()));
        timelineContainer.addView(timeline);
        timelineContainer.addView(close);

        return timelineContainer;
    }


    StoriesReaderAppearanceSettings appearanceSettings = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        manager = new ReaderPageManager();
        setStoryId();
        manager.host = this;
        if (parentManager == null && getParentFragment() instanceof StoriesContentFragment) {
            parentManager = ((StoriesContentFragment) getParentFragment()).readerManager;
        }
        manager.setParentManager(parentManager);
        manager.setStoryId(storyId);
        if (parentManager != null) {
            parentManager.addSubscriber(manager);
        }
        bindViews(view);
        setActions();
        if (setManagers() && InAppStoryService.getInstance() != null
                && InAppStoryService.getInstance().getDownloadManager() != null) {
            if (InAppStoryService.getInstance().getDownloadManager().getStoryById(storyId, manager.getStoryType()) != null)
                manager.setSlideIndex(InAppStoryService.getInstance().getDownloadManager()
                        .getStoryById(storyId, manager.getStoryType()).lastIndex);

            setViews(view);
            InAppStoryService.getInstance().getDownloadManager().addSubscriber(manager);
            manager.storyLoadedInCache();
        } else {
            InAppStoryManager.closeStoryReader();
        }

    }


    @Override
    public void onDestroyView() {
        if (storiesView != null)
            storiesView.destroyView();
        if (manager != null) {
            if (parentManager != null)
                parentManager.removeSubscriber(manager);
            if (InAppStoryService.getInstance() != null)
                InAppStoryService.getInstance().getDownloadManager().removeSubscriber(manager);
        }
        super.onDestroyView();
    }
}
