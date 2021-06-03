package com.inappstory.sdk.stories.ui.reader;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.inappstory.sdk.R;
import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.eventbus.CsEventBus;
import com.inappstory.sdk.eventbus.CsSubscribe;
import com.inappstory.sdk.eventbus.CsThreadMode;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.stories.api.models.StatisticManager;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.events.ChangeStoryEvent;
import com.inappstory.sdk.stories.events.CloseStoryReaderEvent;
import com.inappstory.sdk.stories.managers.OldStatisticManager;
import com.inappstory.sdk.stories.outerevents.CloseStory;
import com.inappstory.sdk.stories.utils.BackPressHandler;

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

public class StoriesDialogFragment extends DialogFragment implements BackPressHandler {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.cs_stories_dialog_fragment, null);
        CsEventBus.getDefault().register(this);
        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (InAppStoryService.isNotNull()) {
            OldStatisticManager.getInstance().sendStatistic();
            Story story = InAppStoryService.getInstance().getDownloadManager()
                    .getStoryById(InAppStoryService.getInstance().getCurrentId());

            if (story == null) return;
            CsEventBus.getDefault().post(new CloseStory(story.id,
                    story.title, story.tags, story.slidesCount,
                    story.lastIndex, CloseStory.CLICK,
                    getArguments().getInt("source", 0)));
            String cause = StatisticManager.CLICK;
            StatisticManager.getInstance().sendCloseStory(story.id, cause, story.lastIndex, story.slidesCount);
        }
        try {
            CsEventBus.getDefault().unregister(this);
        } catch (Exception e) {

        }
        cleanReader();
        super.onDismiss(dialogInterface);
    }

    boolean cleaned = false;

    public void cleanReader() {
        if (InAppStoryService.isNull()) return;
        if (cleaned) return;
        OldStatisticManager.getInstance().closeStatisticEvent();
        InAppStoryService.getInstance().setCurrentIndex(0);
        InAppStoryService.getInstance().setCurrentId(0);
        InAppStoryService.getInstance().isBackgroundPause = false;
        for (Story story : InAppStoryService.getInstance().getDownloadManager().getStories())
            story.lastIndex = 0;
        cleaned = true;
    }


    @Override
    public void onStart()
    {
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
        CsEventBus.getDefault().unregister(this);

        OldStatisticManager.getInstance().sendStatistic();
        StoriesActivity.destroyed = System.currentTimeMillis();
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

    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void closeStoryReaderEvent(CloseStoryReaderEvent event) {
       /* if (InAppStoryService.getInstance() == null) return;
        InAppStoryService.getInstance().closeStatisticEvent();
        InAppStoryService.getInstance().setCurrentIndex(0);
        InAppStoryService.getInstance().setCurrentId(0);
        InAppStoryService.getInstance().isBackgroundPause = false;
        for (Story story : StoryDownloader.getInstance().getStories())
            story.lastIndex = 0;
        CsEventBus.getDefault().unregister(this);*/
        dismiss();
    }



    @CsSubscribe(threadMode = CsThreadMode.MAIN)
    public void changeStoryEvent(ChangeStoryEvent event) {
        getArguments().putInt("index", event.getIndex());
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cleaned = false;
        StoriesActivity.destroyed = -1;
        StoriesFragment fragment = new StoriesFragment();

        Bundle args = new Bundle();
        args.putBoolean("isDialogFragment", true);
        args.putInt("index", getArguments().getInt("index", 0));
        setAppearanceSettings(args);
        args.putIntegerArrayList("stories_ids", getArguments().getIntegerArrayList("stories_ids"));


        fragment.setArguments(args);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction t = fragmentManager.beginTransaction()
                .replace(R.id.dialog_fragment, fragment);
        t.addToBackStack("STORIES_FRAGMENT");
        t.commit();


    }

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
                getArguments().getInt(CS_SOUND_ICON, R.drawable.ic_stories_status_sound)
        );
        try {
            bundle.putInt(CS_STORY_READER_ANIMATION, getArguments().getInt(CS_STORY_READER_ANIMATION, 0));
            bundle.putString(CS_READER_SETTINGS, JsonParser.getJson(storiesReaderSettings));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
