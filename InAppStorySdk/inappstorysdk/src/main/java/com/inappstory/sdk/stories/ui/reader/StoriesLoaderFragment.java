package com.inappstory.sdk.stories.ui.reader;


import static com.inappstory.sdk.AppearanceManager.BOTTOM_END;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_LEFT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_RIGHT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_START;
import static com.inappstory.sdk.AppearanceManager.TOP_END;
import static com.inappstory.sdk.AppearanceManager.TOP_LEFT;
import static com.inappstory.sdk.AppearanceManager.TOP_RIGHT;
import static com.inappstory.sdk.AppearanceManager.TOP_START;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import android.util.SizeF;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.core.ui.widgets.customicons.CustomIconWithoutStates;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.data.IContentWithTimeline;
import com.inappstory.sdk.core.data.IListItemContent;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.ui.widgets.TouchFrameLayout;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanel;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.StoryTimeline;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.progresstimeline.StoryTimelineState;
import com.inappstory.sdk.stories.utils.Sizes;


public class StoriesLoaderFragment extends Fragment {

    int storyId = -1;

    void setViews(View view) {
        InAppStoryManager inAppStoryManager = InAppStoryManager.getInstance();
        if (inAppStoryManager == null) return;
        IListItemContent story = inAppStoryManager.iasCore().contentHolder().listsContent()
                .getByIdAndType(
                        launchData.getStoriesIds().get(launchData.getListIndex()),
                        launchData.getType()
                );
        if (story == null) return;
        storyId = story.id();
        ButtonsPanel buttonsPanel = view.findViewById(R.id.ias_buttons_panel);
        View aboveButtonsPanel = view.findViewById(R.id.ias_above_buttons_panel);
        View closeButton = view.findViewById(R.id.ias_close_button);
        int mirrorScale = view.getResources().getInteger(R.integer.mirrorScaleX);
        closeButton.setScaleX(mirrorScale);
        if (story.disableClose())
            closeButton.setVisibility(View.GONE);
        if (buttonsPanel != null && aboveButtonsPanel != null) {
            buttonsPanel.setButtonsVisibility(appearanceSettings,
                    story.hasLike(),
                    story.hasFavorite(),
                    story.hasShare(),
                    story.hasAudio(),
                    Sizes.isTablet(view.getContext())
            );
            buttonsPanel.setButtonsStatus(story.like(), story.favorite() ? 1 : 0);
            aboveButtonsPanel.setVisibility(buttonsPanel.getVisibility());
        }
        StoryTimeline timeline = view.findViewById(R.id.ias_timeline);
        if (timeline != null) {
            timeline.setScaleX(mirrorScale);
            if (story instanceof IContentWithTimeline) {
                timeline.setState(
                        new StoryTimelineState(
                                story.slidesCount(),
                                0,
                                0,
                                0,
                                ((IContentWithTimeline) story).timelineIsHidden(),
                                ((IContentWithTimeline) story).timelineForegroundColor(0),
                                ((IContentWithTimeline) story).timelineBackgroundColor(0)
                        )
                );
            } else {
                timeline.setState(
                        new StoryTimelineState(
                                story.slidesCount(),
                                0,
                                0,
                                0
                        )
                );
            }
        }
        setOffsets(view);
    }


    private void setOffsets(View view) {
        View blackTop = view.findViewById(R.id.ias_black_top);
        View blackBottom = view.findViewById(R.id.ias_black_bottom);
        Activity fragmentActivity = getActivity();
        if (fragmentActivity == null) return;
        if (!Sizes.isTablet(fragmentActivity)) {
            if (blackTop != null) {
                Point screenSize;
                Rect readerContainer = getArguments().getParcelable("readerContainer");
                int phoneHeight = Sizes.getFullPhoneHeight(fragmentActivity);
                int width = Sizes.getFullPhoneWidth(fragmentActivity);
                int windowHeight = Sizes.getScreenSize(fragmentActivity).y;
                int topInsetOffset = 0;
                int bottomInsetOffset = 0;
                if (Build.VERSION.SDK_INT >= 23) {
                    if (fragmentActivity.getWindow() != null) {
                        WindowInsets windowInsets = fragmentActivity.getWindow().getDecorView().getRootWindowInsets();
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
                if (phoneHeight - Math.min(topInsetOffset, bottomInsetOffset) < windowHeight) {
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

    void bindViews(View view) {
        View timeline = view.findViewById(R.id.ias_timeline);
        View close = view.findViewById(R.id.ias_close_button);
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
                    layoutParams.setMarginStart(viewsMargin);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                    storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                    storiesProgressViewLP.addRule(RelativeLayout.END_OF, close.getId());
                    break;
                case TOP_END:
                    layoutParams.setMarginEnd(viewsMargin);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                    storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                    storiesProgressViewLP.addRule(RelativeLayout.START_OF, close.getId());
                    break;
                case BOTTOM_START:
                    layoutParams.setMarginStart(viewsMargin);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                    layoutParams.addRule(RelativeLayout.BELOW, timeline.getId());
                    storiesProgressViewLP.topMargin = viewsMargin;
                    layoutParams.topMargin = viewsMargin;
                    break;
                case BOTTOM_END:
                    layoutParams.setMarginEnd(viewsMargin);
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

    View createFragmentView(ViewGroup root) {
        Context context = getContext();

        RelativeLayout res = new RelativeLayout(context);
        res.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        if (!Sizes.isTablet(context) && appearanceSettings.csReaderBackgroundColor() != Color.BLACK) {
            linearLayout.setBackgroundColor(Color.BLACK);
        }
        setLinearContainer(context, linearLayout);
        res.addView(linearLayout);
        View emptyView = new View(context);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setClickable(true);
        res.addView(emptyView);
        return res;
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

    }

    private void setLinearContainer(Context context, LinearLayout linearLayout) {
        View blackTop = new View(context);
        blackTop.setId(R.id.ias_black_top);
        blackTop.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        blackTop.setBackgroundColor(Color.TRANSPARENT);

        View blackBottom = new View(context);
        blackBottom.setId(R.id.ias_black_bottom);
        blackBottom.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        blackBottom.setBackgroundColor(Color.TRANSPARENT);

        RelativeLayout content = new RelativeLayout(context);
        content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        ViewGroup main;
        RelativeLayout.LayoutParams contentLP = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        contentLP.addRule(RelativeLayout.ABOVE, R.id.ias_buttons_panel);
        View aboveButtonsPanel = new View(context);
        aboveButtonsPanel.setId(R.id.ias_above_buttons_panel);
        aboveButtonsPanel.setBackgroundColor(Color.BLACK);
        aboveButtonsPanel.setVisibility(View.GONE);
        RelativeLayout.LayoutParams aboveLp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                Sizes.dpToPxExt(appearanceSettings.csReaderRadius(), context));
        aboveLp.addRule(RelativeLayout.ABOVE, R.id.ias_buttons_panel);
        aboveButtonsPanel.setLayoutParams(aboveLp);
        main = new CardView(context);
        main.setLayoutParams(contentLP);
        ((CardView) main).setRadius(Sizes.dpToPxExt(appearanceSettings.csReaderRadius(), getContext()));
        ((CardView) main).setCardBackgroundColor(Color.BLACK);
        main.setElevation(0);

        RelativeLayout cardContent = new RelativeLayout(context);
        cardContent.setLayoutParams(new CardView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.MATCH_PARENT));
        cardContent.addView(createTimelineContainer(context));
        main.addView(cardContent);
        content.addView(createButtonsPanel(context));
        content.addView(aboveButtonsPanel);
        content.addView(main);
        linearLayout.addView(blackTop);
        linearLayout.addView(content);
        linearLayout.addView(blackBottom);
    }

    private BaseStoryScreen getStoriesReader() {
        BaseStoryScreen screen = null;
        if (getActivity() instanceof BaseStoryScreen) {
            screen = (BaseStoryScreen) getActivity();
        } else if (getParentFragment() instanceof BaseStoryScreen) {
            screen = (BaseStoryScreen) getParentFragment();
        }
        return screen;
    }

    private RelativeLayout createTimelineContainer(Context context) {
        RelativeLayout timelineContainer = new RelativeLayout(context);
        RelativeLayout.LayoutParams tclp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        int offset = Sizes.dpToPxExt(Math.max(0, appearanceSettings.csReaderRadius() - 16), getContext()) / 2;
        int maxSize = Sizes.dpToPxExt(30, getContext());
        tclp.setMargins(offset, Sizes.dpToPxExt(8, getContext()) + offset, offset, 0);
        timelineContainer.setLayoutParams(tclp);
        timelineContainer.setId(R.id.ias_timeline_container);
        timelineContainer.setMinimumHeight(maxSize);
        timelineContainer.setElevation(20);
        StoryTimeline timeline = new StoryTimeline(context);
        timeline.setId(R.id.ias_timeline);
        timeline.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                Sizes.dpToPxExt(3, getContext())));
        TouchFrameLayout close = new TouchFrameLayout(context);
        final CustomIconWithoutStates customCloseIconInterface = AppearanceManager.
                getCommonInstance().
                csCustomIcons().
                closeIcon();
        final View customCloseView = customCloseIconInterface.createIconView(context, new SizeF(maxSize, maxSize));
        close.setId(R.id.ias_close_button);
        close.setLayoutParams(new RelativeLayout.LayoutParams(
                maxSize,
                maxSize)
        );
        close.addView(customCloseView);
        customCloseView.setClickable(false);
        close.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseStoryScreen screen = getStoriesReader();
                if (screen != null)
                    screen.closeWithAction(CloseStory.CLICK);
                try {
                    customCloseIconInterface.clickEvent(customCloseView);
                } catch (Exception e) {

                }
            }
        });
        close.setTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                customCloseIconInterface.touchEvent(customCloseView, event);
                return false;
            }
        });
        timelineContainer.addView(timeline);
        timelineContainer.addView(close);
        return timelineContainer;
    }

    private ButtonsPanel createButtonsPanel(Context context) {
        ButtonsPanel buttonsPanel = new ButtonsPanel(context, storyId);
        RelativeLayout.LayoutParams buttonsPanelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, Sizes.dpToPxExt(60, context)
        );
        buttonsPanelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        //     buttonsPanel.setVisibility(View.GONE);
        buttonsPanel.setId(R.id.ias_buttons_panel);
        buttonsPanel.setOrientation(LinearLayout.HORIZONTAL);
        buttonsPanel.setBackgroundColor(Color.BLACK);
        buttonsPanel.setLayoutParams(buttonsPanelParams);
        //buttonsPanel.setIcons(appearanceSettings);
        return buttonsPanel;
    }

    LaunchStoryScreenAppearance appearanceSettings;
    LaunchStoryScreenData launchData;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        Bundle arguments = requireArguments();
        appearanceSettings = (LaunchStoryScreenAppearance) arguments.getSerializable(
                LaunchStoryScreenAppearance.SERIALIZABLE_KEY
        );
        launchData = (LaunchStoryScreenData) arguments.getSerializable(
                LaunchStoryScreenData.SERIALIZABLE_KEY
        );
        View view = new View(getContext());
        try {
            view = createFragmentView(container);
        } catch (Exception e) {
            InAppStoryManager.handleException(e);
        }
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                if (v.isAttachedToWindow()) {
                    bindViews(v);
                    setViews(v);
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });
        return view;
    }


}
