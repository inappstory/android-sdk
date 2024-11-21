package com.inappstory.sdk.stories.ui.reader;

import static com.inappstory.sdk.AppearanceManager.ANIMATION_CUBE;
import static com.inappstory.sdk.AppearanceManager.CS_READER_SETTINGS;
import static com.inappstory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.UseIASCoreCallback;
import com.inappstory.sdk.core.api.IASCallbackType;
import com.inappstory.sdk.core.api.IASStatisticStoriesV1;
import com.inappstory.sdk.core.api.UseIASCallback;
import com.inappstory.sdk.core.data.IReaderContent;
import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenAppearance;
import com.inappstory.sdk.core.ui.screens.storyreader.LaunchStoryScreenData;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseStoryCallback;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.statistic.GetStatisticV1Callback;
import com.inappstory.sdk.stories.statistic.IASStatisticStoriesV2Impl;
import com.inappstory.sdk.core.ui.widgets.elasticview.DraggableElasticLayout;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoriesDialogFragment extends DialogFragment implements IASBackPressHandler, BaseStoryScreen {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        appearanceSettings = (LaunchStoryScreenAppearance) getArguments()
                .getSerializable(LaunchStoryScreenAppearance.SERIALIZABLE_KEY);
        launchData = (LaunchStoryScreenData) getArguments().
                getSerializable(LaunchStoryScreenData.SERIALIZABLE_KEY);
        return inflater.inflate(R.layout.cs_mainscreen_stories_draggable, container, false);
    }

    DraggableElasticLayout draggableFrame;

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.screensManager()
                        .getGameScreenHolder().forceCloseScreen(null);
                core.statistic().storiesV1(
                        launchData.getSessionId(),
                        new GetStatisticV1Callback() {
                            @Override
                            public void get(@NonNull IASStatisticStoriesV1 manager) {
                                manager.sendStatistic();
                            }
                        }
                );
                int storyId = storiesContentFragment.readerManager.getCurrentStoryId();
                final IReaderContent story = core
                        .contentHolder()
                        .readerContent()
                        .getByIdAndType(storyId, type);
                final int slideIndex =
                        storiesContentFragment.readerManager.getByIdAndIndex(storyId).index();
                if (story != null) {
                    core.callbacksAPI().useCallback(
                            IASCallbackType.CLOSE_STORY,
                            new UseIASCallback<CloseStoryCallback>() {
                                @Override
                                public void use(@NonNull CloseStoryCallback callback) {
                                    callback.closeStory(
                                            new SlideData(
                                                    StoryData.getStoryData(
                                                            story,
                                                            launchData.getFeed(),
                                                            launchData.getSourceType(),
                                                            type
                                                    ),
                                                    slideIndex,
                                                    story.slideEventPayload(slideIndex)
                                            ),
                                            CloseReader.CLICK
                                    );
                                }
                            });
                    String cause = IASStatisticStoriesV2Impl.CLICK;
                    core.statistic().storiesV2().sendCloseStory(storyId, cause, slideIndex,
                            story.slidesCount(),
                            launchData.getFeed());
                }
                cleanReader(core);
                core.screensManager().getStoryScreenHolder()
                        .unsubscribeScreen(StoriesDialogFragment.this);
            }
        });

        super.onDismiss(dialogInterface);
    }

    boolean cleaned = false;

    public void cleanReader(IASCore core) {
        if (cleaned) return;
        core.statistic().storiesV1(
                launchData.getSessionId(),
                new GetStatisticV1Callback() {
                    @Override
                    public void get(@NonNull IASStatisticStoriesV1 manager) {
                        manager.closeStatisticEvent();
                    }
                }
        );
        core.screensManager().getStoryScreenHolder().currentOpenedStoryId(0);
        if (storiesContentFragment != null)
            storiesContentFragment.readerManager.refreshStoriesIds();
        cleaned = true;
    }

    @Override
    public void closeWithAction(int action) {
        InAppStoryService.getInstance().getListReaderConnector().readerIsClosed();
        dismissAllowingStateLoss();
    }

    @Override
    public void forceFinish() {
        dismissAllowingStateLoss();
    }

    @Override
    public void close() {

    }

    @Override
    public void removeStoryFromFavorite(int id) {
        if (storiesContentFragment != null)
            storiesContentFragment.removeStoryFromFavorite(id);
    }

    @Override
    public void removeAllStoriesFromFavorite() {
        if (storiesContentFragment != null)
            storiesContentFragment.removeAllStoriesFromFavorite();
    }

    @Override
    public void timerIsLocked() {
        if (storiesContentFragment != null) storiesContentFragment.timerIsLocked();
    }

    @Override
    public void timerIsUnlocked() {
        if (storiesContentFragment != null) storiesContentFragment.timerIsUnlocked();
    }

    @Override
    public void pauseScreen() {

    }

    @Override
    public void resumeScreen() {

    }

    ShowGoodsCallback currentGoodsCallback = null;


    @Override
    public void disableDrag(boolean disable) {
        if (draggableFrame != null)
            draggableFrame.dragIsDisabled(true);
    }

    @Override
    public void disableClose(boolean disable) {
        if (draggableFrame != null)
            draggableFrame.disableClose(true);
    }

    @Override
    public void setShowGoodsCallback(ShowGoodsCallback callback) {
        currentGoodsCallback = callback;
    }

    @Override
    public void permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public FragmentManager getScreenFragmentManager() {
        return getChildFragmentManager();
    }


    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null)
            return;
        int dialogHeight = getResources().getDimensionPixelSize(R.dimen.cs_tablet_height);

        Point size = Sizes.getScreenSize(getContext());
        if (Build.VERSION.SDK_INT >= 28) {
            if (getContext() instanceof Activity) {
                WindowInsets insets =
                        ((Activity) getContext()).getWindow()
                                .getDecorView().getRootWindowInsets();
                if (insets != null) {
                    size.y -= (insets.getSystemWindowInsetTop() +
                            insets.getSystemWindowInsetBottom());
                }
            }
        }
        dialogHeight = Math.min(dialogHeight, size.y);
        int dialogWidth = Math.round(dialogHeight / 1.78f);
        screenRectangle = new Rect(0, 0, dialogWidth, dialogHeight);
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    Rect screenRectangle = new Rect();

    @Override
    public boolean onBackPressed() {
        Fragment frag = getChildFragmentManager().findFragmentById(R.id.stories_fragments_layout);
        if (frag != null && frag instanceof IASBackPressHandler) {
            if (((IASBackPressHandler) frag).onBackPressed())
                return true;
        }
        dismissAllowingStateLoss();
        return true;
    }

    public void onDestroyView() {
        InAppStoryManager.useCore(new UseIASCoreCallback() {
            @Override
            public void use(@NonNull IASCore core) {
                core.statistic().storiesV1(
                        launchData.getSessionId(),
                        new GetStatisticV1Callback() {
                            @Override
                            public void get(@NonNull IASStatisticStoriesV1 manager) {
                                manager.sendStatistic();
                            }
                        }
                );
            }
        });

        super.onDestroyView();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.stories_fragments_layout);
        if (null != currentFragment && currentFragment.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {

            case android.R.id.home:

                dismissAllowingStateLoss();
                return true;
        }
        return false;
    }


    public void changeStory(int index) {
        // getArguments().putInt("index", index);
    }

    ContentType type = ContentType.STORY;


    LaunchStoryScreenAppearance appearanceSettings;
    LaunchStoryScreenData launchData;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cleaned = false;
        int color = getArguments().getInt(AppearanceManager.CS_READER_BACKGROUND_COLOR, Color.BLACK);
        view.setBackgroundColor(color);
        type = launchData.getType();
        draggableFrame = view.findViewById(R.id.draggable_frame);
        if (savedInstanceState == null) {
            storiesContentFragment = new StoriesContentFragment();
            Bundle args = new Bundle();
            args.putParcelable("readerContainer", screenRectangle);
            args.putSerializable(appearanceSettings.getSerializableKey(), appearanceSettings);
            args.putSerializable(launchData.getSerializableKey(), launchData);
            setAppearanceSettings(args);
            storiesContentFragment.setArguments(args);
        } else {
            storiesContentFragment =
                    (StoriesContentFragment) getChildFragmentManager().findFragmentByTag("STORIES_FRAGMENT");
        }
        if (storiesContentFragment != null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction t = fragmentManager.beginTransaction()
                    .replace(R.id.stories_fragments_layout, storiesContentFragment);
            t.addToBackStack("STORIES_FRAGMENT");
            t.commitAllowingStateLoss();
        } else {
            dismissAllowingStateLoss();
        }

    }

    StoriesContentFragment storiesContentFragment;

    private void setAppearanceSettings(Bundle bundle) {
        try {
            Bundle fragmentArgs = requireArguments();
            StoriesReaderSettings storiesReaderSettings = new StoriesReaderSettings(fragmentArgs);
            bundle.putSerializable(CS_TIMER_GRADIENT,
                    fragmentArgs.getSerializable(CS_TIMER_GRADIENT));
            bundle.putInt(CS_STORY_READER_ANIMATION,
                    fragmentArgs.getInt(CS_STORY_READER_ANIMATION, ANIMATION_CUBE));
            bundle.putString(CS_READER_SETTINGS, JsonParser.getJson(storiesReaderSettings));
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            e.printStackTrace();
        }

    }


}