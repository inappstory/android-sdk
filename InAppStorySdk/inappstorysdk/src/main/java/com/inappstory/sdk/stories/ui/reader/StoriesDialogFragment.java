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

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.inappstory.sdk.AppearanceManager;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.R;
import com.inappstory.sdk.core.IASCoreManager;
import com.inappstory.sdk.core.network.JsonParser;
import com.inappstory.sdk.core.repository.stories.IStoriesRepository;
import com.inappstory.sdk.core.repository.stories.dto.IPreviewStoryDTO;
import com.inappstory.sdk.core.repository.stories.dto.IStoryDTO;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.outercallbacks.common.objects.CloseReader;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryData;
import com.inappstory.sdk.stories.outerevents.ShowStory;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.utils.BackPressHandler;
import com.inappstory.sdk.stories.utils.Sizes;
import com.inappstory.sdk.usecase.callbacks.IUseCaseCallback;
import com.inappstory.sdk.usecase.callbacks.UseCaseCallbackCloseStory;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.HashSet;
import java.util.List;

public class StoriesDialogFragment extends DialogFragment implements BackPressHandler, BaseReaderScreen {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cs_stories_dialog_fragment, null);
    }

    private void removeGameObservables() {
        for (String observableID : observerIDs) {
            MutableLiveData<GameCompleteEvent> observableData =
                    ScreensManager.getInstance().getGameObserver(observableID);
            if (observableData == null) continue;
            observableData.removeObserver(gameCompleteObserver);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        ScreensManager.getInstance().hideGoods();
        ScreensManager.getInstance().closeGameReader();
        OldStatisticManager.getInstance().sendStatistic();
        IStoriesRepository storiesRepository = IASCoreManager.getInstance().getStoriesRepository(type);
        IPreviewStoryDTO story = storiesRepository.getCurrentStory();
        if (story != null) {
            int lastIndex = storiesRepository.getStoryLastIndex(story.getId());
            IUseCaseCallback useCaseCallbackCloseStory = new UseCaseCallbackCloseStory(
                    new SlideData(
                            new StoryData(
                                    story,
                                    getArguments().getString("feedId"),
                                    CallbackManager.getInstance().getSourceFromInt(
                                            getArguments().getInt("source", 0)
                                    )
                            ),
                            lastIndex
                    ),
                    CloseReader.CLICK
            );
            useCaseCallbackCloseStory.invoke();

            String cause = StatisticManager.CLICK;
            StatisticManager.getInstance().sendCloseStory(story.getId(), cause, lastIndex,
                    story.getSlidesCount(),
                    getArguments().getString("feedId"));

        }
        cleanReader();
        removeGameObservables();
        super.onDismiss(dialogInterface);
        if (ScreensManager.getInstance().currentScreen == this)
            ScreensManager.getInstance().currentScreen = null;
    }

    boolean cleaned = false;

    public void cleanReader() {
        if (cleaned) return;
        OldStatisticManager.getInstance().closeStatisticEvent();
        IASCoreManager.getInstance().getStoriesRepository(type).clear();
        IASCoreManager.getInstance().downloadManager.cleanTasks();
        cleaned = true;
    }

    Observer<GameCompleteEvent> gameCompleteObserver = new Observer<GameCompleteEvent>() {
        @Override
        public void onChanged(GameCompleteEvent event) {
            storiesFragment.readerManager.gameComplete(
                    event.getGameState(),
                    event.getStoryId(),
                    event.getSlideIndex()
            );
        }
    };

    HashSet<String> observerIDs = new HashSet<>();

    @Override
    public void closeStoryReader(CloseReader action, String cause) {
        InAppStoryService.getInstance().getListNotifier().closeReader(getArguments().getString("listID"));
        dismissAllowingStateLoss();
    }

    @Override
    public void forceFinish() {
        dismissAllowingStateLoss();
    }

    @Override
    public void observeGameReader(String observableId) {
        final String localObservableId = observableId;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                MutableLiveData<GameCompleteEvent> observableData =
                        ScreensManager.getInstance().getGameObserver(localObservableId);
                if (observableData == null) return;
                observableData.observe(getViewLifecycleOwner(), gameCompleteObserver);
                observerIDs.add(localObservableId);
            }
        });

    }

    @Override
    public void shareComplete(boolean shared) {
        storiesFragment.readerManager.shareComplete(shared);
    }

    @Override
    public void removeStoryFromFavorite(int id) {
        if (storiesFragment != null)
            storiesFragment.removeStoryFromFavorite(id);
    }

    @Override
    public void removeAllStoriesFromFavorite() {
        if (storiesFragment != null)
            storiesFragment.removeAllStoriesFromFavorite();
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
        Fragment frag = getChildFragmentManager().findFragmentById(R.id.dialog_fragment);
        if (frag != null && frag instanceof BackPressHandler) {
            if (((BackPressHandler) frag).onBackPressed())
                return true;
        }
        dismissAllowingStateLoss();
        return true;
    }

    public void onDestroyView() {
        OldStatisticManager.getInstance().sendStatistic();
        ScreensManager.created = System.currentTimeMillis();
        super.onDestroyView();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.dialog_fragment);
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
        getArguments().putInt("index", index);
    }

    Story.StoryType type = Story.StoryType.COMMON;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cleaned = false;
        int color = getArguments().getInt(AppearanceManager.CS_READER_BACKGROUND_COLOR, Color.BLACK);
        view.setBackgroundColor(color);
        String stStoriesType = getArguments().getString("storiesType", Story.StoryType.COMMON.name());
        if (stStoriesType != null) {
            if (stStoriesType.equals(Story.StoryType.UGC.name()))
                type = Story.StoryType.UGC;
        }
        if (savedInstanceState == null) {
            storiesFragment = new StoriesFragment();
            Bundle args = new Bundle();
            args.putBoolean("isDialogFragment", true);
            args.putInt("index", getArguments().getInt("index", 0));

            args.putSerializable("source", getArguments().getSerializable("source"));
            args.putInt("firstAction", getArguments().getInt("firstAction", ShowStory.ACTION_OPEN));
            args.putSerializable("storiesType", getArguments().getSerializable("storiesType"));
            args.putInt("slideIndex", getArguments().getInt("slideIndex", 0));
            setAppearanceSettings(args);
            args.putIntegerArrayList("stories_ids", getArguments().getIntegerArrayList("stories_ids"));
            storiesFragment.setArguments(args);
        } else {
            storiesFragment =
                    (StoriesFragment) getChildFragmentManager().findFragmentByTag("STORIES_FRAGMENT");
        }
        if (storiesFragment != null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction t = fragmentManager.beginTransaction()
                    .replace(R.id.dialog_fragment, storiesFragment);
            t.addToBackStack("STORIES_FRAGMENT");
            t.commitAllowingStateLoss();
        } else {
            dismissAllowingStateLoss();
        }

    }

    StoriesFragment storiesFragment;

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