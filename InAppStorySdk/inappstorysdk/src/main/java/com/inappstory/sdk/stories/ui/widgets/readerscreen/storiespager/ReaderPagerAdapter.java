package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.inappstory.sdk.InAppStoryManager;
import com.inappstory.sdk.stories.ui.widgets.readerscreen.StoriesReaderPageFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_ON_SWIPE;
import static com.inappstory.sdk.AppearanceManager.CS_CLOSE_POSITION;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_FAVORITE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_LIKE;
import static com.inappstory.sdk.AppearanceManager.CS_HAS_SHARE;

public class ReaderPagerAdapter extends FragmentStatePagerAdapter {
    private List<Integer> storiesIds = new ArrayList<>();

    public ReaderPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public ReaderPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public ReaderPagerAdapter(@NonNull FragmentManager fm, int closePosition, boolean closeOnSwipe, List<Integer> ids) {
        super(fm);
        this.storiesIds.clear();
        this.storiesIds.addAll(ids);

        this.closePosition = closePosition;
        this.closeOnSwipe = closeOnSwipe;
        this.hasFavorite = InAppStoryManager.getInstance().hasFavorite();
        this.hasShare = InAppStoryManager.getInstance().hasShare();
        this.hasLike = InAppStoryManager.getInstance().hasLike();
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


    private int closePosition = 0;

    private boolean closeOnSwipe = false;
    private boolean hasFavorite = false;
    private boolean hasShare = false;
    private boolean hasLike = false;


    private HashMap<Integer, ReaderPageFragment> fragMap =
            new HashMap<Integer, ReaderPageFragment>();


    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (fragMap.get(position) == null) {
            ReaderPageFragment frag = new ReaderPageFragment();
            Bundle a = new Bundle();
            a.putInt("story_id", storiesIds.get(position));
            a.putInt(CS_CLOSE_POSITION, closePosition);
            a.putBoolean(CS_CLOSE_ON_SWIPE, closeOnSwipe);
            a.putBoolean(CS_HAS_FAVORITE, hasFavorite);
            a.putBoolean(CS_HAS_LIKE, hasLike);
            a.putBoolean(CS_HAS_SHARE, hasShare);
            frag.setArguments(a);
            fragMap.put(position, frag);
        }

        return fragMap.get(position);
    }


    public ReaderPageFragment getFragment(int position) {
        return fragMap.get(position);
    }


    @Override
    public int getCount() {
        return storiesIds.size();
    }
}
