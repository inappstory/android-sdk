package com.inappstory.sdk.stories.ui.reader.animation;

import static com.inappstory.sdk.AppearanceManager.BOTTOM_END;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_LEFT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_RIGHT;
import static com.inappstory.sdk.AppearanceManager.BOTTOM_START;
import static com.inappstory.sdk.AppearanceManager.TOP_END;
import static com.inappstory.sdk.AppearanceManager.TOP_LEFT;
import static com.inappstory.sdk.AppearanceManager.TOP_RIGHT;
import static com.inappstory.sdk.AppearanceManager.TOP_START;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.ui.IASUICore;
import com.inappstory.sdk.stories.ui.widgets.RoundedOutlineProvider;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.buttonspanel.ButtonsPanel;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.StoryTimeline;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.StoryTimelineManager;
import com.inappstory.sdk.stories.uidomain.reader.IStoriesReaderViewModel;
import com.inappstory.sdk.stories.uidomain.reader.page.IStoriesReaderPageViewModel;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.List;

public class StoriesReaderPreloadFragment extends Fragment implements IStoriesReaderPreload {
    public static final String TAG = "StoriesReaderAnimationFragment";

    IStoriesReaderPageViewModel pageViewModel;
    IStoriesReaderViewModel readerViewModel;


    AppCompatImageView closeButton;
    View roundedContainer;
    StoryTimeline timeline;
    RelativeLayout timelineContainer;
    View topOffset;
    View bottomOffset;
    ButtonsPanel buttonsPanel;
    View parentContainer;

    boolean successfulBind;

    private void bindViews(View view) {
        successfulBind = true;
        roundedContainer = view.findViewById(R.id.ias_rounded_container);
        if (roundedContainer == null) successfulBind = false;
        closeButton = view.findViewById(R.id.ias_close_button);
        if (closeButton == null) successfulBind = false;
        timeline = view.findViewById(R.id.ias_timeline);
        if (timeline == null) successfulBind = false;
        timelineContainer = view.findViewById(R.id.ias_timeline_container);
        if (timelineContainer == null) successfulBind = false;
        parentContainer = view.findViewById(R.id.ias_parent_container);
        if (parentContainer == null) successfulBind = false;
        topOffset = view.findViewById(R.id.ias_black_top);
        if (topOffset == null) successfulBind = false;
        bottomOffset = view.findViewById(R.id.ias_black_bottom);
        if (bottomOffset == null) successfulBind = false;
        buttonsPanel = view.findViewById(R.id.ias_buttons_panel);
        if (buttonsPanel == null) successfulBind = false;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        readerViewModel = IASUICore.getInstance().getStoriesReaderVM();
        pageViewModel = readerViewModel.getLaunchedViewModel();
        View view = inflater.inflate(R.layout.ias_reader_preload_screen, container, false);
        bindViews(view);
        if (successfulBind) {
            StoriesReaderAppearanceSettings appearanceSettings =
                    readerViewModel.getState().appearanceSettings();
            roundedContainer.setOutlineProvider(
                    new RoundedOutlineProvider(
                            appearanceSettings.csReaderRadius()
                    )
            );
            roundedContainer.setClipToOutline(true);
            parentContainer.setBackgroundColor(appearanceSettings.csReaderBackgroundColor());
            setTimelineContainerParams(
                    appearanceSettings,
                    readerViewModel.getState().launchData(),
                    view.getContext()
            );
        }
        return view;
    }

    private void setTimelineContainerParams(
            StoriesReaderAppearanceSettings appearanceSettings,
            StoriesReaderLaunchData launchData,
            Context context
    ) {

        closeButton.setImageResource(appearanceSettings.csCloseIcon());

        int offset = (int) (
                Sizes.typedDpToPx(
                        Math.max(0, appearanceSettings.csReaderRadius() - 16),
                        context
                ) / 2
        );
        RelativeLayout.LayoutParams timelineContainerLp =
                (RelativeLayout.LayoutParams) timelineContainer.getLayoutParams();
        timelineContainerLp.setMargins(offset, offset, offset, 0);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) closeButton.getLayoutParams();
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
                storiesProgressViewLP.addRule(RelativeLayout.LEFT_OF, closeButton.getId());
                break;
            case TOP_LEFT:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                storiesProgressViewLP.addRule(RelativeLayout.RIGHT_OF, closeButton.getId());
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
                storiesProgressViewLP.addRule(RelativeLayout.END_OF, closeButton.getId());
                break;
            case TOP_END:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                storiesProgressViewLP.addRule(RelativeLayout.START_OF, closeButton.getId());
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
        StoryTimelineManager timelineManager = timeline.getTimelineManager();
        IPreviewStoryDTO previewStoryDTO = IASCore.getInstance().getStoriesRepository(
                launchData.getType()
        ).getStoryPreviewById(pageViewModel.getState().storyId());
        if (previewStoryDTO != null) {
            List<Integer> durations = new ArrayList<>();
            for (int i = 0; i < previewStoryDTO.getSlidesCount(); i++, durations.add(0)) {}
            timelineManager.setSlidesCount(previewStoryDTO.getSlidesCount());
            timelineManager.setDurations(durations, true);
        }
        closeButton.setLayoutParams(layoutParams);
        timelineContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        if (!successfulBind) return;

    }
}
