package io.casestory.sdk.stories.ui.widgets.readerscreen;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.casestory.sdk.CaseStoryManager;
import io.casestory.sdk.CaseStoryService;
import io.casestory.sdk.eventbus.EventBus;
import io.casestory.sdk.eventbus.Subscribe;
import io.casestory.sdk.stories.cache.StoryDownloader;
import io.casestory.sdk.stories.events.PageByIndexRefreshEvent;
import io.casestory.sdk.stories.events.PageRefreshEvent;
import io.casestory.sdk.stories.events.PageSelectedEvent;

public class StoriesReaderPagerAdapter extends FragmentStatePagerAdapter {
    private List<Integer> storiesIds = new ArrayList<>();

    public StoriesReaderPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public StoriesReaderPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public StoriesReaderPagerAdapter(@NonNull FragmentManager fm, int closePosition, boolean closeOnSwipe, List<Integer> ids) {
        super(fm);
        this.storiesIds.clear();
        EventBus.getDefault().register(this);
        this.storiesIds.addAll(ids);

        this.closePosition = closePosition;
        this.closeOnSwipe = closeOnSwipe;
        this.hasFavorite = CaseStoryManager.getInstance().hasFavorite();
        this.hasShare = CaseStoryManager.getInstance().hasShare();
        this.hasLike = CaseStoryManager.getInstance().hasLike();
    }

    @Override
    public Parcelable saveState() {
        try {
            Bundle bundle = (Bundle) super.saveState();
            bundle.putParcelableArray("states", null); // Never maintain any states from the base class, just null it out
            return bundle;
        } catch (Exception e) {
            return new Bundle();
        }
    }

    @Subscribe
    public void refreshPageEvent(PageRefreshEvent event) {
        int ind = storiesIds.indexOf(event.getStoryId());
        EventBus.getDefault().post(new PageByIndexRefreshEvent(event.getStoryId()));
        if (ind > 0) {
            EventBus.getDefault().post(new PageByIndexRefreshEvent(storiesIds.get(ind-1)));
        }
        if (ind < storiesIds.size() - 1) {
            EventBus.getDefault().post(new PageByIndexRefreshEvent(storiesIds.get(ind+1)));
        }
    }


    private int closePosition = 0;

    private boolean closeOnSwipe = false;
    private boolean hasFavorite = false;
    private boolean hasShare = false;
    private boolean hasLike = false;


    private HashMap<Integer, StoriesReaderPageFragment> fragMap =
            new HashMap<Integer, StoriesReaderPageFragment>();


    @NonNull
    @Override
    public Fragment getItem(int position) {
        StoriesReaderPageFragment frag = new StoriesReaderPageFragment();
        Bundle a = new Bundle();
        a.putInt("story_id", storiesIds.get(position));
        a.putInt("closePosition", closePosition);
        a.putBoolean("closeOnSwipe", closeOnSwipe);
        a.putBoolean("hasFavorite", hasFavorite);
        a.putBoolean("hasLike", hasLike);
        a.putBoolean("hasShare", hasShare);
        frag.setArguments(a);
        fragMap.put(position, frag);
        return frag;
    }


    public StoriesReaderPageFragment getFragment(int position) {
        return fragMap.get(position);
    }



    @Override
    public int getCount() {
        return storiesIds.size();
    }
}
