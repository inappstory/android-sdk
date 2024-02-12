package com.inappstory.sdk.stories.ui.reader;

import static com.inappstory.sdk.AppearanceManager.ANIMATION_CUBE;
import static com.inappstory.sdk.AppearanceManager.CS_READER_SETTINGS;
import static com.inappstory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.UseServiceInstanceCallback;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoriesReaderLaunchData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.utils.IASBackPressHandler;
import com.inappstory.sdk.stories.utils.ShowGoodsCallback;
import com.inappstory.sdk.stories.utils.Sizes;

import java.util.List;

public class StoriesDialogFragment extends DialogFragment implements IASBackPressHandler, BaseReaderScreen {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        appearanceSettings = (StoriesReaderAppearanceSettings) getArguments()
                .getSerializable(StoriesReaderAppearanceSettings.SERIALIZABLE_KEY);
        launchData = (StoriesReaderLaunchData) getArguments().
                getSerializable(StoriesReaderLaunchData.SERIALIZABLE_KEY);
        return inflater.inflate(R.layout.cs_mainscreen_stories_draggable, container, false);
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        ScreensManager.getInstance().closeGameReader();
        OldStatisticManager.getInstance().sendStatistic();
        InAppStoryService service = InAppStoryService.getInstance();
        if (service != null) {
            Story story = service.getDownloadManager()
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
                StatisticManager.getInstance().sendCloseStory(story.id, cause, story.lastIndex,
                        story.getSlidesCount(),
                        launchData.getFeed());
            }

        }
        cleanReader();
        super.onDismiss(dialogInterface);
        ScreensManager.getInstance().unsubscribeReaderScreen(this);
    }

    boolean cleaned = false;

    public void cleanReader() {
        if (cleaned) return;
        OldStatisticManager.getInstance().closeStatisticEvent();
        InAppStoryService.useInstance(new UseServiceInstanceCallback() {
            @Override
            public void use(@NonNull InAppStoryService service) throws Exception {
                service.setCurrentIndex(0);
                service.setCurrentId(0);
                service.getDownloadManager().cleanStoriesIndex(type);
                cleaned = true;
            }
        });

    }

    @Override
    public void closeStoryReader(int action) {
        InAppStoryService.getInstance().getListReaderConnector().closeReader();
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

    @Override
    public void disableDrag(boolean disable) {

    }

    @Override
    public void setShowGoodsCallback(ShowGoodsCallback callback) {

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

        Point size = Sizes.getScreenSize();
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
        int dialogWidth = Math.round(dialogHeight / 1.5f);

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

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
        OldStatisticManager.getInstance().sendStatistic();
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cleaned = false;
        int color = getArguments().getInt(AppearanceManager.CS_READER_BACKGROUND_COLOR, Color.BLACK);
        view.setBackgroundColor(color);
        type = launchData.getType();
        if (savedInstanceState == null) {
            storiesContentFragment = new StoriesContentFragment();
            Bundle args = new Bundle();
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