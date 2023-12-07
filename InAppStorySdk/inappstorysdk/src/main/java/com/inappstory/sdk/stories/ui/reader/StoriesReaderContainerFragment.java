package com.inappstory.sdk.stories.ui.reader;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.repository.statistic.StatisticV2Manager;
import com.inappstory.sdk.databinding.IasReaderContainerBinding;
import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;
import com.inappstory.sdk.stories.ui.IASUICore;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.oldreader.animations.DisabledReaderAnimation;
import com.inappstory.sdk.stories.ui.oldreader.animations.FadeReaderAnimation;
import com.inappstory.sdk.stories.ui.oldreader.animations.HandlerAnimatorListenerAdapter;
import com.inappstory.sdk.stories.ui.oldreader.animations.PopupReaderAnimation;
import com.inappstory.sdk.stories.ui.oldreader.animations.ReaderAnimation;
import com.inappstory.sdk.stories.ui.oldreader.animations.ZoomReaderAnimation;
import com.inappstory.sdk.stories.ui.reader.animation.StoriesReaderPreloadFragment;
import com.inappstory.sdk.stories.ui.widgets.elasticview.ElasticDragDismissFrameLayout;
import com.inappstory.sdk.stories.uidomain.reader.IStoriesReaderViewModel;
import com.inappstory.sdk.stories.utils.Sizes;

public final class StoriesReaderContainerFragment extends Fragment implements IStoriesReaderContainer {
    public static final String TAG = "StoriesReaderContainerFragment";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return IasReaderContainerBinding.inflate(inflater, container, false).getRoot();
    }


    private ElasticDragDismissFrameLayout draggableFrame;
    private View blockView;
    private View backTintView;
    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    @Override
    public void forceClose() {
        IStoriesReaderScreen parentScreen = getStoriesReaderScreen();
        if (parentScreen != null) parentScreen.forceClose();
    }

    @Override
    public void close(CloseReader action, String cause) {
        final Activity activity = getActivity();
        if (activity != null)
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            getCloseAnimations()
                    .setListener(new HandlerAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd() {
                            draggableFrame.setVisibility(View.GONE);
                            forceClose();
                        }
                    })
                    .start();
            ScreensManager.getInstance().coordinates = null;
        } catch (Exception e) {
            if (activity != null)
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            forceClose();
        }
    }

    Observer<Boolean> openAnimationStatusObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean animated) {
            if (!animated) {
                Fragment fragment = getChildFragmentManager().findFragmentByTag(
                        StoriesReaderPagerFragment.TAG
                );
                if (fragment == null)
                    getChildFragmentManager()
                            .beginTransaction()
                            .replace(
                                    R.id.container_fragments_layout,
                                    new StoriesReaderPagerFragment(),
                                    StoriesReaderPagerFragment.TAG
                            )
                            .commit();
            }
        }
    };

    IStoriesReaderViewModel storiesReaderViewModel;

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        storiesReaderViewModel = IASUICore.getInstance().getStoriesReaderVM();
        storiesReaderViewModel
                .isOpenAnimation()
                .observe(
                        getViewLifecycleOwner(),
                        openAnimationStatusObserver
                );

        bindViews(view);
        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.container_fragments_layout,
                            new StoriesReaderPreloadFragment(),
                            StoriesReaderPreloadFragment.TAG
                    )
                    .commit();
            startOpenAnimation();
        }
    }

    private void bindViews(View view) {
        draggableFrame = view.findViewById(R.id.draggable_frame);
        blockView = view.findViewById(R.id.screen_block);
        backTintView = view.findViewById(R.id.screen_background);
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(getActivity()) {
            @Override
            public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {
                super.onDrag(elasticOffset, elasticOffsetPixels, rawOffset, rawOffsetPixels);
                backTintView.setAlpha(Math.min(1f, Math.max(0f, 1f - rawOffset)));
            }

            @Override
            public void onDragDismissed() {
                close(CloseReader.SWIPE, StatisticV2Manager.SWIPE);
            }

            @Override
            public void onDragDropped() {
            }

            @Override
            public void touchPause() {

            }

            @Override
            public void touchResume() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        };
        draggableFrame.addListener(chromeFader);
    }

    private ReaderAnimation getOpenAnimations() {
        Point screenSize = Sizes.getScreenSize(getContext());
        switch (
                storiesReaderViewModel
                        .getState()
                        .appearanceSettings()
                        .csStoryReaderPresentationStyle()
        ) {
            case AppearanceManager.DISABLE:
                return new DisabledReaderAnimation().setAnimations(true);
            case AppearanceManager.FADE:
                return new FadeReaderAnimation(draggableFrame)
                        .setAnimations(true);
            case AppearanceManager.POPUP:
                return new PopupReaderAnimation(draggableFrame, screenSize.y, 0f)
                        .setAnimations(true);
            default:
                Point coordinates = ScreensManager.getInstance().coordinates;
                float pivotX = screenSize.x / 2f;
                float pivotY = screenSize.y / 2f;
                if (coordinates != null) {
                    pivotY = coordinates.y;
                    pivotX = coordinates.x;
                }
                return new ZoomReaderAnimation(
                        draggableFrame,
                        pivotX,
                        pivotY
                ).setAnimations(true);
        }
    }

    private void startOpenAnimation() {
        final Activity activity = getActivity();
        if (activity != null)
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        try {
            getOpenAnimations()
                    .setListener(new HandlerAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd() {
                            if (activity != null)
                                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            storiesReaderViewModel.openAnimationStatus(false);
                        }
                    })
                    .start();
        } catch (Exception e) {
            IStoriesReaderScreen readerScreen = getStoriesReaderScreen();
            if (readerScreen != null)
                readerScreen.forceClose();
        }

    }


    private ReaderAnimation getCloseAnimations() {
        Point screenSize = Sizes.getScreenSize(getContext());
        switch (
                storiesReaderViewModel
                        .getState()
                        .appearanceSettings()
                        .csStoryReaderPresentationStyle()
        ) {
            case AppearanceManager.DISABLE:
                return new DisabledReaderAnimation().setAnimations(false);
            case AppearanceManager.FADE:
                return new FadeReaderAnimation(draggableFrame).setAnimations(false);
            case AppearanceManager.POPUP:
                return new PopupReaderAnimation(
                        draggableFrame,
                        draggableFrame.getY(),
                        screenSize.y
                ).setAnimations(false);
            default:
                float pivotX = (screenSize.x - draggableFrame.getX()) / 2f;
                float pivotY = (screenSize.y - draggableFrame.getY()) / 2f;
                Point coordinates = ScreensManager.getInstance().coordinates;
                if (coordinates != null) {
                    pivotX = coordinates.x - draggableFrame.getX();
                    pivotY = coordinates.y - draggableFrame.getY();
                }

                return new ZoomReaderAnimation(draggableFrame,
                        pivotX,
                        pivotY
                ).setAnimations(false);
        }
    }

    @Override
    public IStoriesReaderScreen getStoriesReaderScreen() {
        Activity activity = getActivity();
        if (activity instanceof IStoriesReaderScreen) {
            return (IStoriesReaderScreen) activity;
        } else {
            Fragment parent = getParentFragment();
            if (parent instanceof IStoriesReaderScreen) {
                return (IStoriesReaderScreen) parent;
            }
        }
        return null;
    }
}
