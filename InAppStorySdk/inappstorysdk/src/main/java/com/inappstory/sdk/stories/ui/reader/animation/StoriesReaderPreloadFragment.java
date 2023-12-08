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
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.databinding.IasReaderPreloadScreenBinding;
import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.ui.IASUICore;
import com.inappstory.sdk.stories.ui.reader.IStoriesReaderScreen;
import com.inappstory.sdk.stories.ui.reader.IStoriesReaderScreenChild;
import com.inappstory.sdk.stories.ui.widgets.RoundedOutlineProvider;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.timeline.StoryTimelineManager;
import com.inappstory.sdk.stories.uidomain.reader.IStoriesReaderViewModel;
import com.inappstory.sdk.stories.uidomain.reader.page.IStoriesReaderPageViewModel;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.ArrayList;
import java.util.List;

public final class StoriesReaderPreloadFragment extends Fragment implements IStoriesReaderPreload {
    public static final String TAG = "StoriesReaderAnimationFragment";

    IStoriesReaderPageViewModel pageViewModel;
    IStoriesReaderViewModel readerViewModel;

    IasReaderPreloadScreenBinding binding;
    @NonNull
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding =
                IasReaderPreloadScreenBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        readerViewModel = IASUICore.getInstance().getStoriesReaderVM();
        pageViewModel = readerViewModel.getLaunchedViewModel();
        StoriesReaderAppearanceSettings appearanceSettings =
                readerViewModel.getState().appearanceSettings();
        binding.iasRoundedContainer.setOutlineProvider(
                new RoundedOutlineProvider(
                        appearanceSettings.csReaderRadius()
                )
        );
        binding.iasButtonsPanel.setIcons(appearanceSettings);
        binding.iasButtonsPanel.setViewModel(pageViewModel.getBottomPanelViewModel());
        binding.iasRoundedContainer.setClipToOutline(true);
        binding.iasParentContainer.setBackgroundColor(appearanceSettings.csReaderBackgroundColor());
        setTimelineContainerParams(
                appearanceSettings,
                readerViewModel.getState().launchData(),
                view.getContext()
        );
        view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                if (v.isAttachedToWindow()) {
                    setOffsets(v.getContext());
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {

            }
        });
        return view;
    }

    private void setOffsets(Context context) {
        if (!Sizes.isTablet(context)) {
            Point screenSize = Sizes.getScreenSize(context);
            LinearLayout.LayoutParams lp =
                    (LinearLayout.LayoutParams) binding.iasBlackBottomOffset.getLayoutParams();
            float realProps = screenSize.y / ((float) screenSize.x);
            float sn = 1.85f;
            if (realProps > sn) {
                lp.height = (int) (screenSize.y - screenSize.x * sn) / 2;
                setCutout(lp.height);
            } else {
                setCutout(0);
            }
            binding.iasBlackBottomOffset.setLayoutParams(lp);
            binding.iasBlackTopOffset.setLayoutParams(lp);
        }
    }

    private void setCutout(int minusOffset) {
        if (Build.VERSION.SDK_INT >= 28) {
            if (getActivity() != null && getActivity().getWindow() != null &&
                    getActivity().getWindow().getDecorView().getRootWindowInsets() != null) {
                DisplayCutout cutout = getActivity().getWindow()
                        .getDecorView().getRootWindowInsets().getDisplayCutout();
                if (cutout != null) {
                    RelativeLayout.LayoutParams lp1 =
                            (RelativeLayout.LayoutParams) binding.iasTimelineContainer.getLayoutParams();
                    lp1.topMargin += Math.max(cutout.getSafeInsetTop() - minusOffset, 0);
                    binding.iasTimelineContainer.setVisibility(View.VISIBLE);
                    binding.iasTimelineContainer.requestLayout();
                }
            }
        }
    }

    private void setTimelineContainerParams(
            StoriesReaderAppearanceSettings appearanceSettings,
            StoriesReaderLaunchData launchData,
            Context context
    ) {

        binding.iasCloseButton.setImageResource(appearanceSettings.csCloseIcon());

        int offset = (int) (
                Sizes.typedDpToPx(
                        Math.max(0, appearanceSettings.csReaderRadius() - 16),
                        context
                ) / 2
        );
        RelativeLayout.LayoutParams timelineContainerLp =
                (RelativeLayout.LayoutParams) binding.iasTimelineContainer.getLayoutParams();
        timelineContainerLp.setMargins(offset, offset, offset, 0);
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) binding.iasCloseButton.getLayoutParams();
        RelativeLayout.LayoutParams storiesProgressViewLP =
                (RelativeLayout.LayoutParams) binding.iasTimeline.getLayoutParams();
        int cp = appearanceSettings.csClosePosition();
        int viewsMargin = Sizes.dpToPxExt(8, getContext());
        storiesProgressViewLP.leftMargin =
                storiesProgressViewLP.rightMargin =
                        layoutParams.rightMargin = viewsMargin;

        switch (cp) {
            case TOP_RIGHT:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                storiesProgressViewLP.addRule(RelativeLayout.LEFT_OF, binding.iasCloseButton.getId());
                break;
            case TOP_LEFT:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                storiesProgressViewLP.addRule(RelativeLayout.RIGHT_OF, binding.iasCloseButton.getId());
                break;
            case BOTTOM_RIGHT:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.addRule(RelativeLayout.BELOW, binding.iasTimeline.getId());
                storiesProgressViewLP.topMargin = viewsMargin;
                layoutParams.topMargin = viewsMargin;
                break;
            case BOTTOM_LEFT:
                storiesProgressViewLP.topMargin = viewsMargin;
                layoutParams.topMargin = viewsMargin;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.addRule(RelativeLayout.BELOW, binding.iasTimeline.getId());
                break;
            case TOP_START:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                storiesProgressViewLP.addRule(RelativeLayout.END_OF, binding.iasCloseButton.getId());
                break;
            case TOP_END:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                storiesProgressViewLP.addRule(RelativeLayout.CENTER_VERTICAL);
                storiesProgressViewLP.addRule(RelativeLayout.START_OF, binding.iasCloseButton.getId());
                break;
            case BOTTOM_START:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                layoutParams.addRule(RelativeLayout.BELOW, binding.iasTimeline.getId());
                storiesProgressViewLP.topMargin = viewsMargin;
                layoutParams.topMargin = viewsMargin;
                break;
            case BOTTOM_END:
                storiesProgressViewLP.topMargin = viewsMargin;
                layoutParams.topMargin = viewsMargin;
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                layoutParams.addRule(RelativeLayout.BELOW, binding.iasTimeline.getId());
                break;
        }
        StoryTimelineManager timelineManager = binding.iasTimeline.getTimelineManager();
        IPreviewStoryDTO previewStoryDTO = IASCore.getInstance().getStoriesRepository(
                launchData.getType()
        ).getStoryPreviewById(pageViewModel.getState().storyId());
        if (previewStoryDTO != null) {
            List<Integer> durations = new ArrayList<>();
            for (int i = 0; i < previewStoryDTO.getSlidesCount(); i++, durations.add(0)) {
                durations.add(0);
            }
            timelineManager.setSlidesCount(previewStoryDTO.getSlidesCount());
            timelineManager.setDurations(durations, true);
        }
        binding.iasCloseButton.setLayoutParams(layoutParams);
        binding.iasTimelineContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public IStoriesReaderScreen getStoriesReaderScreen() {
        Fragment parent = getParentFragment();
        if (parent instanceof IStoriesReaderScreenChild)
            return ((IStoriesReaderScreenChild) parent).getStoriesReaderScreen();
        return null;
    }
}
