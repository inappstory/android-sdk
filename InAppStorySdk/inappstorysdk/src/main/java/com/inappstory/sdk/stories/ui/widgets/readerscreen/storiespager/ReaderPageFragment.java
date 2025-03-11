package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_END;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_LEFT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_RIGHT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_START;
import static com.inappstory.sdk.AppearanceManager.TOP_END;
import static com.inappstory.sdk.AppearanceManager.TOP_LEFT;
import static com.inappstory.sdk.AppearanceManager.TOP_RIGHT;
import static com.inappstory.sdk.AppearanceManager.TOP_START;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.graphics.Rect;
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
import android.view.WindowInsets;
import android.widget.FrameLayout;
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
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.stories.managers.TimerManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.ui.reader.ReaderManager;
import com.inappstory.sdk.stories.ui.reader.StoriesContentFragment;
import com.inappstory.sdk.stories.ui.reader.StoriesGradientObject;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanel;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.StoryTimeline;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.webview.StoriesWebView;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.List;

public class ReaderPageFragment extends Fragment {
    ReaderPageManager manager;
    StoryTimeline timeline;
    StoriesWebView storiesView;
    ButtonsPanel buttonsPanel;
    View aboveButtonsPanel;
    ReaderManager parentManager;

    View blackTop;
    View blackBottom;
    View refresh;
    AppCompatImageView close;
    int storyId;

    boolean setManagers(IASCore core) {
        boolean readerInitSuccess = true;
        if (buttonsPanel != null)
            manager.setButtonsPanelManager(buttonsPanel.getManager(), storyId);
        else
            readerInitSuccess = false;
        if (timeline != null)
            manager.setTimelineManager(timeline.getTimelineManager());
        else
            readerInitSuccess = false;
        if (storiesView != null)
            manager.setWebViewManager(storiesView.getManager(), storyId);
        else
            readerInitSuccess = false;
        manager.setTimerManager(new TimerManager(core));
        return readerInitSuccess;
    }


    void bindViews(View view) {
        close = view.findViewById(R.id.ias_close_button);
        refresh = view.findViewById(R.id.ias_refresh_button);
        blackTop = view.findViewById(R.id.ias_black_top);
        blackBottom = view.findViewById(R.id.ias_black_bottom);
        buttonsPanel = view.findViewById(R.id.ias_buttons_panel);
        storiesView = view.findViewById(R.id.ias_stories_view);
        timeline = view.findViewById(R.id.ias_timeline);

        try {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) close.getLayoutParams();
            RelativeLayout.LayoutParams storiesProgressViewLP = (RelativeLayout.LayoutParams) timeline.getLayoutParams();
            int cp = appearanceSettings.csClosePosition();
            int viewsMargin = Sizes.dpToPxExt(8, getContext());
            storiesProgressViewLP.leftMargin =
                    storiesProgressViewLP.rightMargin = viewsMargin;

            switch (cp) {
                case TOP_RIGHT:
                    layoutParams.rightMargin = viewsMargin;
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                    storiesProgressViewLP.addRule(RelativeLayout.LEFT_OF, close.getId());
                    break;
                case TOP_LEFT:
                    layoutParams.leftMargin = viewsMargin;
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                    storiesProgressViewLP.addRule(RelativeLayout.RIGHT_OF, close.getId());
                    break;
                case BOTTOM_RIGHT:
                    layoutParams.rightMargin = viewsMargin;
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                    storiesProgressViewLP.topMargin = viewsMargin;
                    layoutParams.topMargin = viewsMargin;
                    break;
                case BOTTOM_LEFT:
                    layoutParams.leftMargin = viewsMargin;
                    storiesProgressViewLP.topMargin = viewsMargin;
                    layoutParams.topMargin = viewsMargin;
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                    break;
                case TOP_START:
                    layoutParams.leftMargin = viewsMargin;
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                    storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                    storiesProgressViewLP.addRule(RelativeLayout.END_OF, close.getId());
                    break;
                case TOP_END:
                    layoutParams.rightMargin = viewsMargin;
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                    storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                    storiesProgressViewLP.addRule(RelativeLayout.START_OF, close.getId());
                    break;
                case BOTTOM_START:
                    layoutParams.leftMargin = viewsMargin;
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                    layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                    storiesProgressViewLP.topMargin = viewsMargin;
                    layoutParams.topMargin = viewsMargin;
                    break;
                case BOTTOM_END:
                    layoutParams.rightMargin = viewsMargin;
                    storiesProgressViewLP.topMargin = viewsMargin;
                    layoutParams.topMargin = viewsMargin;
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                    layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                    break;
            }
            close.setLayoutParams(layoutParams);
        } catch (Exception e) {
            InAppStoryManager.handleException(e);
        }
    }

    void setStoryId() {
        storyId = getArguments().getInt("story_id");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }


    Story story;

    void setViews(IReaderContent story) {
        if (timeline != null) {
            timeline.getTimelineManager().setSlidesCount(story.slidesCount(), true);
        }
        if (story.disableClose())
            close.setVisibility(View.GONE);
        if (buttonsPanel != null) {
            buttonsPanel.setButtonsVisibility(
                    appearanceSettings,
                    story.hasLike(),
                    story.hasFavorite(),
                    story.hasShare(),
                    story.hasAudio(),
                    Sizes.isTablet(getContext())
            );
            buttonsPanel.setButtonsStatus(story.like(), story.favorite() ? 1 : 0);
            aboveButtonsPanel.setVisibility(buttonsPanel.getVisibility());
        }
        if (storiesView != null)
            storiesView.getManager().setIndex(manager.parentManager.getByIdAndIndex(storyId).index());

    }

    public void storyLoadStart() {
        showLoaderContainer();
    }

    public void storyLoadedSuccess() {
        hideLoaderContainer();
    }

    private void setOffsets(View view) {
        Context context = view.getContext();
        if (!Sizes.isTablet(context)) {
            if (blackTop != null) {
                Point screenSize;
                Rect readerContainer = getArguments().getParcelable("readerContainer");
                int phoneHeight = Sizes.getFullPhoneHeight(context);
                int width = Sizes.getFullPhoneWidth(context);
                int windowHeight = Sizes.getScreenSize(context).y;
                int topInsetOffset = 0;
                int bottomInsetOffset = 0;
                if (Build.VERSION.SDK_INT >= 23) {
                    if (context instanceof Activity && ((Activity) context).getWindow() != null) {
                        WindowInsets windowInsets = ((Activity) context).getWindow().getDecorView().getRootWindowInsets();
                        if (windowInsets != null) {
                            topInsetOffset = Math.max(0, windowInsets.getStableInsetTop());
                            bottomInsetOffset = Math.max(0, windowInsets.getStableInsetBottom());
                        }
                    }
                }

                if (readerContainer != null) {
                    screenSize = new Point(
                            Math.min(readerContainer.width(), width),
                            Math.min(readerContainer.height(), phoneHeight - topInsetOffset - bottomInsetOffset)
                    );
                } else {
                    screenSize = new Point(
                            width,
                            phoneHeight - topInsetOffset - bottomInsetOffset
                    );
                }

                if (phoneHeight - topInsetOffset - bottomInsetOffset < windowHeight) {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) blackBottom.getLayoutParams();
                    lp.height = bottomInsetOffset;
                    blackBottom.requestLayout();
                }
                int maxRatioHeight = (int) (screenSize.x * 2f);
                int restHeight = Math.max(0, screenSize.y - maxRatioHeight) + topInsetOffset;
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) blackTop.getLayoutParams();
                lp.height = restHeight;
                blackTop.requestLayout();
            }

        }
    }

    private int getPanelHeight(Context context) {
        return Sizes.dpToPxExt(60, context);
    }

    View loader;

    void setActions() {
        if (close != null)
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (parentManager != null) {
                        StoriesContentFragment contentFragment = parentManager.getHost();
                        if (contentFragment != null) {
                            BaseStoryScreen screen = contentFragment.getStoriesReader();
                            if (screen != null) {
                                screen.closeWithAction(CloseStory.CLICK);
                            }
                        }

                    }
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
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (loaderContainer == null) return;
                refresh.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);
                showLoaderContainerAnimated();
            }
        });
    }

    public void showLoaderOnly() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(View.VISIBLE);
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
        Log.e("hideLoader", "showLoaderContainerAnimated");
        loaderContainer.clearAnimation();
        loaderContainer.animate().alpha(1f).setStartDelay(300).setDuration(300).start();
    }

    private void hideLoaderContainerAnimated() {
        Log.e("hideLoader", "hideLoaderContainerAnimated");
        loaderContainer.clearAnimation();
        loaderContainer.animate().alpha(0f).setDuration(300).start();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        appearanceSettings = (LaunchStoryScreenAppearance)
                requireArguments().getSerializable(LaunchStoryScreenAppearance.SERIALIZABLE_KEY);
        try {
            return createFragmentView(container);
        } catch (Exception e) {
            InAppStoryManager.handleException(e);
            return new View(getContext());
        }
    }

    LinearLayout linearLayout;

    View createFragmentView(ViewGroup root) {
        Context context = getContext();

        RelativeLayout res = new RelativeLayout(context);
        res.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT,
                MATCH_PARENT));

        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT,
                MATCH_PARENT));

        if (!Sizes.isTablet(getContext()) && appearanceSettings.csReaderBackgroundColor() != Color.BLACK) {
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
        blackTop.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 0));
        blackTop.setBackgroundColor(Color.TRANSPARENT);

        blackBottom = new View(context);
        blackBottom.setId(R.id.ias_black_bottom);
        blackBottom.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, 0));
        blackBottom.setBackgroundColor(Color.TRANSPARENT);


        RelativeLayout content = new RelativeLayout(context);
        content.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT,
                MATCH_PARENT, 1));
        ViewGroup main;
        RelativeLayout.LayoutParams contentLP = new RelativeLayout.LayoutParams(MATCH_PARENT,
                MATCH_PARENT);
        contentLP.addRule(RelativeLayout.ABOVE, R.id.ias_buttons_panel);
        aboveButtonsPanel = new View(context);
        aboveButtonsPanel.setBackgroundColor(Color.BLACK);
        aboveButtonsPanel.setVisibility(View.GONE);
        RelativeLayout.LayoutParams aboveLp = new RelativeLayout.LayoutParams(MATCH_PARENT,
                Sizes.dpToPxExt(appearanceSettings.csReaderRadius(), context));
        aboveLp.addRule(RelativeLayout.ABOVE, R.id.ias_buttons_panel);
        aboveButtonsPanel.setLayoutParams(aboveLp);

        main = new CardView(context);
        main.setLayoutParams(contentLP);
        ((CardView) main).setRadius(Sizes.dpToPxExt(appearanceSettings.csReaderRadius(), getContext()));
        ((CardView) main).setCardBackgroundColor(Color.TRANSPARENT);
        main.setElevation(0);

        RelativeLayout cardContent = new RelativeLayout(context);
        cardContent.setLayoutParams(new CardView.LayoutParams(MATCH_PARENT,
                MATCH_PARENT));
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

        readerContainer.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT,
                MATCH_PARENT));
        readerContainer.setElevation(9);

        // readerContainer.addView(createProgressContainer(context));
        readerContainer.addView(createWebViewContainer(context));
        if (appearanceSettings.csTimerGradientEnable())
            addGradient(context, readerContainer);

        createLoader();
        createRefreshButton(context);
        loaderContainer = new RelativeLayout(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loaderContainer.setElevation(10);
            loader.setElevation(11);
        }
        loaderContainer.setAlpha(0.99f);
        loaderContainer.setLayoutParams(
                new RelativeLayout.LayoutParams(
                        MATCH_PARENT,
                        MATCH_PARENT
                )
        );
        loaderContainer.setBackgroundColor(Color.BLACK);
        //   loaderContainer.addView(loader);
        loaderContainer.addView(refresh);
        readerContainer.addView(loaderContainer);
        readerContainer.addView(loader);
        return readerContainer;
    }

    RelativeLayout loaderContainer;

    private void createLoader() {
        Context context = getContext();
        loader = new FrameLayout(context);
        loader.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT,
                MATCH_PARENT));
        loader.setElevation(8);
        ((ViewGroup) loader).addView(AppearanceManager.getLoader(context, Color.WHITE));
    }


    private View createWebViewContainer(Context context) {
        LinearLayout webViewContainer = new LinearLayout(context);
        RelativeLayout.LayoutParams webViewContainerParams = new RelativeLayout.LayoutParams(
                MATCH_PARENT, MATCH_PARENT
        );
        webViewContainer.setElevation(4);
        webViewContainer.setOrientation(LinearLayout.VERTICAL);
        webViewContainer.setLayoutParams(webViewContainerParams);
        storiesView = new StoriesWebView(context);
        ((StoriesWebView) storiesView).setId(R.id.ias_stories_view);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                MATCH_PARENT, MATCH_PARENT);
        ((StoriesWebView) storiesView).setLayoutParams(lp);
        webViewContainer.addView(((StoriesWebView) storiesView));
        return webViewContainer;
    }


    private View createProgressContainer(Context context) {
        return null;
    }


    private void createButtonsPanel(Context context) {
        buttonsPanel = new ButtonsPanel(context, getArguments().getInt("story_id"));
        RelativeLayout.LayoutParams buttonsPanelParams = new RelativeLayout.LayoutParams(
                MATCH_PARENT, getPanelHeight(context)
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
                MATCH_PARENT,
                MATCH_PARENT
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
        RelativeLayout.LayoutParams tclp = new RelativeLayout.LayoutParams(MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        int offset = Sizes.dpToPxExt(Math.max(0, appearanceSettings.csReaderRadius() - 16), getContext()) / 2;
        tclp.setMargins(offset, Sizes.dpToPxExt(8, getContext()) + offset, offset, 0);
        timelineContainer.setLayoutParams(tclp);
        timelineContainer.setId(R.id.ias_timeline_container);
        timelineContainer.setMinimumHeight(Sizes.dpToPxExt(30, getContext()));
        timelineContainer.setElevation(20);
        timeline = new StoryTimeline(context);
        timeline.setId(R.id.ias_timeline);
        timeline.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT,
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


    LaunchStoryScreenAppearance appearanceSettings = null;

    @Override
    public void onViewCreated(final @NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOffsets(view);
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                manager = new ReaderPageManager(core);
                setStoryId();
                manager.host = ReaderPageFragment.this;
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
                if (setManagers(core)) {
                    core.contentLoader().storyDownloadManager().addSubscriber(manager);
                    Story story = (Story) core.contentHolder().readerContent().getByIdAndType(
                            storyId,
                            manager.getViewContentType()
                    );
                    if (story != null) {
                        manager.setSlideIndex(parentManager.getByIdAndIndex(storyId).index());
                        manager.contentLoadSuccess(story);
                    }
                    if (story == null) {
                        story = (Story) core.contentHolder().getByIdAndType(storyId, manager.getViewContentType());
                    }
                    if (story != null) {
                        setViews(story);
                    }
                } else {
                    InAppStoryManager.closeStoryReader();
                }
            }

            @Override
            public void error() {
                if (getParentFragment() instanceof StoriesContentFragment) {
                    ((StoriesContentFragment) getParentFragment()).forceFinish();
                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
       /* boolean storyIsEmpty = (story == null);
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager != null && storyIsEmpty) {
            story = (Story) inAppStoryManager.iasCore().contentHolder().listsContent().getByIdAndType(
                    storyId,
                    manager.getViewContentType()
            );
            if (story == null) {
                story = (Story) inAppStoryManager.iasCore().contentHolder().readerContent().getByIdAndType(
                        storyId,
                        manager.getViewContentType()
                );
            }
        }
        if (story != null) {
            loadIfStoryIsNotNull();
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        if (storiesView != null)
            storiesView.destroyView();
        if (manager != null) {
            manager.timerManager.pauseSlideTimer();
            if (parentManager != null) {
                parentManager.removeSubscriber(manager);
            }
            InAppStoryManager.useCore(new UseIASCoreCallback() {
                @Override
                public void use(@NonNull IASCore core) {
                    core.contentLoader().storyDownloadManager().removeSubscriber(manager);
                }
            });
        }
        super.onDestroyView();
    }
}
