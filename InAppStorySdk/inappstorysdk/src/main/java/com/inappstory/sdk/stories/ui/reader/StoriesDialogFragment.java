package com.inappstory.sdk.stories.ui.reader;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.inappstory.sdk.R;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.callbacks.CallbackManager;
import com.inappstory.sdk.stories.events.GameCompleteEvent;
import com.inappstory.sdk.stories.outercallbacks.common.reader.CloseReader;
import com.inappstory.sdk.stories.statistic.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.statistic.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.ui.ScreensManager;
import com.inappstory.sdk.stories.utils.BackPressHandler;

import java.util.HashSet;

import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_OVERSCROLL;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static com.inappstory.sdk.AppearanceManager.CS_DISLIKE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_FAVORITE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_FAVORITE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_LIKE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_SHARE;
import static com.inappstory.sdk.AppearanceManager.CS_LIKE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_READER_SETTINGS;
import static com.inappstory.sdk.AppearanceManager.CS_REFRESH_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_SHARE_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_SOUND_ICON;
import static com.inappstory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT;

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
        if (InAppStoryService.isNotNull()) {
            OldStatisticManager.getInstance().sendStatistic();
            Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(InAppStoryService.getInstance().getCurrentId());

            if (story != null) {
                CsEventBus.getDefault().post(new CloseStory(story.id,
                        story.title, story.tags, story.getSlidesCount(),
                        story.lastIndex, CloseStory.CLICK,
                        getArguments().getInt("source", 0)));
                if (CallbackManager.getInstance().getCloseStoryCallback() != null) {
                    CallbackManager.getInstance().getCloseStoryCallback().closeStory(
                            story.id,
                            story.title, story.tags, story.getSlidesCount(),
                            story.lastIndex, CloseReader.CLICK,
                            CallbackManager.getInstance().getSourceFromInt(
                                    getArguments().getInt("source", 0))
                    );
                }
                String cause = StatisticManager.CLICK;
                StatisticManager.getInstance().sendCloseStory(story.id, cause, story.lastIndex,
                        story.getSlidesCount());
            }

        }
        cleanReader();
        removeGameObservables();
        super.onDismiss(dialogInterface);
        if (ScreensManager.getInstance().currentScreen == this)
            ScreensManager.getInstance().currentScreen = null;
    }

    boolean cleaned = false;

    public void cleanReader() {
        if (InAppStoryService.isNull()) return;
        if (cleaned) return;
        OldStatisticManager.getInstance().closeStatisticEvent();
        InAppStoryService.getInstance().setCurrentIndex(0);
        InAppStoryService.getInstance().setCurrentId(0);
        for (Story story : InAppStoryService.getInstance().getDownloadManager().getStories())
            story.setLastIndex(0);
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
    public void closeStoryReader(int action) {
        InAppStoryService.getInstance().getListReaderConnector().closeReader();
        dismiss();
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
    public void shareComplete() {

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
        int dialogWidth = getResources().getDimensionPixelSize(R.dimen.cs_tablet_width);
        int dialogHeight = getResources().getDimensionPixelSize(R.dimen.cs_tablet_height);
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
        dismiss();
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

                dismiss();
                return true;
        }
        return false;
    }


    public void changeStory(int index) {
        getArguments().putInt("index", index);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cleaned = false;
        if (savedInstanceState == null) {
            storiesFragment = new StoriesFragment();
            Bundle args = new Bundle();
            args.putBoolean("isDialogFragment", true);
            args.putInt("index", getArguments().getInt("index", 0));
            args.putInt("source", getArguments().getInt("source", 0));
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
        StoriesReaderSettings storiesReaderSettings = new StoriesReaderSettings(
                getArguments().getBoolean(CS_CLOSE_ON_SWIPE, true),
                getArguments().getBoolean(CS_CLOSE_ON_OVERSCROLL, true),
                getArguments().getInt(CS_CLOSE_POSITION, 1),
                //,
                getArguments().getBoolean(CS_HAS_LIKE, false),
                getArguments().getBoolean(CS_HAS_FAVORITE, false),
                getArguments().getBoolean(CS_HAS_SHARE, false),
                getArguments().getInt(CS_FAVORITE_ICON, R.drawable.ic_stories_status_favorite),
                getArguments().getInt(CS_LIKE_ICON, R.drawable.ic_stories_status_like),
                getArguments().getInt(CS_DISLIKE_ICON, R.drawable.ic_stories_status_dislike),
                getArguments().getInt(CS_SHARE_ICON, R.drawable.ic_share_status),
                getArguments().getInt(CS_CLOSE_ICON, R.drawable.ic_stories_close),
                getArguments().getInt(CS_REFRESH_ICON, R.drawable.ic_refresh),
                getArguments().getInt(CS_SOUND_ICON, R.drawable.ic_stories_status_sound),
                getArguments().getBoolean(CS_TIMER_GRADIENT, true)
        );
        try {
            bundle.putInt(CS_STORY_READER_ANIMATION, getArguments().getInt(CS_STORY_READER_ANIMATION, 0));
            bundle.putString(CS_READER_SETTINGS, JsonParser.getJson(storiesReaderSettings));
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            e.printStackTrace();
        }
    }


}