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
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.statistic.GetOldStatisticManagerCallback;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.ui.widgets.elasticview.ElasticDragDismissFrameLayout;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;

public class StoriesDialogFragment extends DialogFragment implements IASBackPressHandler, BaseReaderScreen, ShowGoodsCallback {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        appearanceSettings = (StoriesReaderAppearanceSettings) getArguments()
                .getSerializable(StoriesReaderAppearanceSettings.SERIALIZABLE_KEY);
        launchData = (StoriesReaderLaunchData) getArguments().
                getSerializable(StoriesReaderLaunchData.SERIALIZABLE_KEY);
        return inflater.inflate(R.layout.cs_mainscreen_stories_draggable, container, false);
    }

    ElasticDragDismissFrameLayout draggableFrame;
    FrameLayout dialogContainer;

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        ScreensManager.getInstance().closeGameReader();
        OldStatisticManager.useInstance(
                launchData.getSessionId(),
                new GetOldStatisticManagerCallback() {
                    @Override
                    public void get(@NonNull OldStatisticManager manager) {
                        manager.sendStatistic();
                    }
                }
        );
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null) {
            Story story = service.getStoryDownloadManager()
                    .getStoryById(service.getCurrentId(), type);

            if (story != null) {
                if (CallbackManager.getInstance().getCloseStoryCallback() != null) {
                    CallbackManager.getInstance().getCloseStoryCallback().closeStory(
                            new SlideData(
                                    StoryData.getStoryData(
                                            story,
                                            launchData.getFeed(),
                                            launchData.getSourceType(),
                                            type
                                    ),
                                    story.lastIndex,
                                    story.getSlideEventPayload(story.lastIndex)
                            ),
                            CloseReader.CLICK
                    );
                }
                String cause = StatisticManager.CLICK;
                StatisticManager.getInstance().sendCloseStory(
                        story.id,
                        cause,
                        story.lastIndex,
                        story.getSlidesCount(),
                        launchData.getFeed()
                );
            }

        }
        cleanReader();
        super.onDismiss(dialogInterface);
        ScreensManager.getInstance().unsubscribeReaderScreen(this);
    }

    boolean cleaned = false;

    public void cleanReader() {
        if (cleaned) return;

        OldStatisticManager.useInstance(
                launchData.getSessionId(),
                new GetOldStatisticManagerCallback() {
                    @Override
                    public void get(@NonNull OldStatisticManager manager) {
                        manager.closeStatisticEvent();
                    }
                }
        );
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.setCurrentIndex(0);
                service.setCurrentId(0);
                service.getStoryDownloadManager().cleanStoriesIndex(type);
                cleaned = true;
            }
        });

    }

    @Override
    public void closeStoryReader(int action) {
        InAppStoryService.getInstance().getListReaderConnector().readerIsClosed();
        dismissAllowingStateLoss();
    }

    @Override
    public void forceFinish() {
        dismissAllowingStateLoss();
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
    public void pauseReader() {

    }

    @Override
    public void resumeReader() {

    }

    ShowGoodsCallback currentGoodsCallback = null;


    @Override
    public void disableDrag(boolean disable) {
        if (draggableFrame != null)
            draggableFrame.dragIsDisabled(true);
    }

    @Override
    public void setShowGoodsCallback(ShowGoodsCallback callback) {
        currentGoodsCallback = callback;
    }

    @Override
    public FragmentManager getStoriesReaderFragmentManager() {
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
        int dialogWidth = Math.round((dialogHeight - Sizes.dpToPxExt(60, getContext()))/ 1.55f);
        screenRectangle = new Rect(0, 0, dialogWidth, dialogHeight);
        if (dialogContainer != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) dialogContainer.getLayoutParams();
            layoutParams.width = dialogWidth;
            layoutParams.height = dialogHeight;
            dialogContainer.requestLayout();
            /*dialogContainer.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            dialogWidth, dialogHeight
                    )
            );*/
        }
        Window window = getDialog().getWindow();
        if (window == null) return;
        WindowManager.LayoutParams windowParams = window.getAttributes();
        window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        windowParams.dimAmount = 0f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLUE));

    }

    public Rect screenRectangle = new Rect();

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

        OldStatisticManager.useInstance(
                launchData.getSessionId(),
                new GetOldStatisticManagerCallback() {
                    @Override
                    public void get(@NonNull OldStatisticManager manager) {
                        manager.sendStatistic();
                    }
                }
        );
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

    Story.StoryType type = Story.StoryType.COMMON;


    StoriesReaderAppearanceSettings appearanceSettings;
    StoriesReaderLaunchData launchData;

    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cleaned = false;
        view.setBackgroundColor(
                adjustAlpha(
                        appearanceSettings.csReaderBackgroundColor(),
                        0.3f
                )
        );
        type = launchData.getType();
        draggableFrame = view.findViewById(R.id.draggable_frame);
        dialogContainer = view.findViewById(R.id.shrinkableDialogContainer);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeStoryReader(CloseStory.CLICK);
            }
        });
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


    @Override
    public void goodsIsOpened() {
        timerIsLocked();
        if (currentGoodsCallback != null) {
            currentGoodsCallback.goodsIsOpened();
        }
    }

    @Override
    public void goodsIsClosed(String widgetId) {
        timerIsUnlocked();
        if (currentGoodsCallback != null) {
            currentGoodsCallback.goodsIsClosed(widgetId);
        }
        currentGoodsCallback = null;
    }

    @Override
    public void goodsIsCanceled(String widgetId) {
        timerIsUnlocked();
        if (currentGoodsCallback != null) {
            currentGoodsCallback.goodsIsCanceled(widgetId);
        }
        currentGoodsCallback = null;
    }
}