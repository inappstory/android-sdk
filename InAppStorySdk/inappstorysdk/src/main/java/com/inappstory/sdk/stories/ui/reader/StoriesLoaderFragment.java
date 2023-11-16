package com.inappstory.sdk.stories.ui.reader;


import static com.inappstory.sdk.AppearanceManager.BOTTOM_END;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_LEFT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_RIGHT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_START;
import static com.inappstory.sdk.AppearanceManager.TOP_END;
import static com.inappstory.sdk.AppearanceManager.TOP_LEFT;
import static com.inappstory.sdk.AppearanceManager.TOP_RIGHT;
import static com.inappstory.sdk.AppearanceManager.TOP_START;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanel;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.StoryTimeline;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.StoryTimelineManager;
import com.inappstory.sdk.stories.utils.Sizes;


public class StoriesLoaderFragment extends Fragment {

    ButtonsPanel buttonsPanel;
    View aboveButtonsPanel;

    View blackBottom;
    View blackTop;
    View refresh;
    StoryTimeline timeline;
    AppCompatImageView close;


    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setViews(view);
    }

    void setViews(View view) {
        if (InAppStoryService.getInstance() == null) return;
        Story story = InAppStoryService.getInstance().getDownloadManager().getStoryById(
                launchData.getStoriesIds().get(launchData.getListIndex()),
                launchData.getType()
        );
        if (story == null) return;
        if (buttonsPanel != null) {
            buttonsPanel.setButtonsVisibility(appearanceSettings,
                    story.hasLike(), story.hasFavorite(), story.hasShare(), story.hasAudio());
            buttonsPanel.setButtonsStatus(story.getLike(), story.favorite ? 1 : 0);
            aboveButtonsPanel.setVisibility(buttonsPanel.getVisibility());
        }
        if (timeline != null) {
            StoryTimelineManager timelineManager = timeline.getTimelineManager();
            timelineManager.setSlidesCount(story.getSlidesCount());
        }
        setOffsets(view);

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

    void bindViews(View view) {
        refresh = view.findViewById(R.id.ias_refresh_button);
        blackBottom = view.findViewById(R.id.ias_black_bottom);
        blackTop = view.findViewById(R.id.ias_black_top);
        buttonsPanel = view.findViewById(R.id.ias_buttons_panel);
        timeline = view.findViewById(R.id.ias_timeline);
        close = view.findViewById(R.id.ias_close_button);
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
        View emptyView = new View(context);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setClickable(true);
        res.addView(emptyView);
        return res;
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
        ((CardView) main).setCardBackgroundColor(Color.BLACK);
        main.setElevation(0);

        RelativeLayout cardContent = new RelativeLayout(context);
        cardContent.setLayoutParams(new CardView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.MATCH_PARENT));
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
        StoryTimeline timeline = new StoryTimeline(context);
        timeline.setId(R.id.ias_timeline);
        timeline.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                Sizes.dpToPxExt(3, getContext())));

        AppCompatImageView close = new AppCompatImageView(context);
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

    View loader;
    RelativeLayout loaderContainer;

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

    StoriesReaderAppearanceSettings appearanceSettings;
    StoriesReaderLaunchData launchData;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        Bundle arguments = requireArguments();
        appearanceSettings = (StoriesReaderAppearanceSettings) arguments.getSerializable(
                StoriesReaderAppearanceSettings.SERIALIZABLE_KEY
        );
        launchData = (StoriesReaderLaunchData) arguments.getSerializable(
                StoriesReaderLaunchData.SERIALIZABLE_KEY
        );
        try {
            return createFragmentView(container);
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            return new View(getContext());
        }
    }


}
