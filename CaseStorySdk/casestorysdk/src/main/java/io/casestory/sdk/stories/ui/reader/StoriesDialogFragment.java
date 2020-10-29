package io.casestory.sdk.stories.ui.reader;

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

import io.casestory.casestorysdk.R;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.eventbus.CsSubscribe;
import io.casestory.sdk.eventbus.ThreadMode;
import io.casestory.sdk.stories.api.models.Story;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.ChangeStoryEvent;
import io.casestory.sdk.stories.events.CloseStoriesReaderEvent;
import io.casestory.sdk.stories.events.CloseStoryReaderEvent;
import io.casestory.sdk.stories.utils.BackPressHandler;

import static io.casestory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static io.casestory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static io.casestory.sdk.AppearanceManager.CS_STORY_READER_ANIMATION;

public class StoriesDialogFragment extends DialogFragment implements BackPressHandler {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.cs_stories_dialog_fragment, null);
        EventBus.getDefault().register(this);
        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        EventBus.getDefault().post(new CloseStoriesReaderEvent());
        super.onDismiss(dialogInterface);
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
        EventBus.getDefault().unregister(this);
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

    @CsSubscribe(threadMode = ThreadMode.MAIN)
    public void closeStoryReaderEvent(CloseStoryReaderEvent event) {
        CaseStoryService.getInstance().closeStatisticEvent();
        CaseStoryService.getInstance().setCurrentIndex(0);
        CaseStoryService.getInstance().setCurrentId(0);
        CaseStoryService.getInstance().isBackgroundPause = false;
        for (Story story : StoryDownloader.getInstance().getStories())
            story.lastIndex = 0;
        dismiss();
    }



    @CsSubscribe(threadMode = ThreadMode.MAIN)
    public void changeStoryEvent(ChangeStoryEvent event) {
        getArguments().putInt("index", event.getIndex());
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StoriesFragment fragment = new StoriesFragment();

        StoriesActivity.destroyed = -1;
        fragment = new StoriesFragment();

        Bundle args = new Bundle();
        args.putBoolean("isDialogFragment", true);
        args.putInt("index", getArguments().getInt("index", 0));
        args.putInt(CS_CLOSE_POSITION, getArguments().getInt(CS_CLOSE_POSITION, 1));
        args.putInt(CS_STORY_READER_ANIMATION, getArguments().getInt(CS_STORY_READER_ANIMATION, 0));
        args.putBoolean(CS_CLOSE_ON_SWIPE, getArguments().getBoolean(CS_CLOSE_ON_SWIPE, false));
        args.putBoolean("onboarding", getArguments().getBoolean("onboarding", false));

        fragment.setArguments(args);
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.fragments_layout);
        //     if (f != null && f.getFragmentTag().equals(newFragment.getFragmentTag())) return;
        FragmentTransaction t = fragmentManager.beginTransaction()
                .replace(R.id.fragments_layout, fragment);
        t.addToBackStack("STORIES_FRAGMENT");
        t.commit();


    }


}
