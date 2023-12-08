package com.inappstory.sdk.stories.ui.reader.views.pager;


import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.inappstory.sdk.stories.outercallbacks.common.objects.SourceType;
import com.inappstory.sdk.stories.ui.oldreader.ReaderManager;
import com.inappstory.sdk.stories.ui.reader.page.StoriesReaderPageFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoriesReaderPagerAdapter extends FragmentStatePagerAdapter {
    private List<Integer> storiesIds = new ArrayList<>();

    public StoriesReaderPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public StoriesReaderPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    public StoriesReaderPagerAdapter(@NonNull FragmentManager fm,
                                     List<Integer> ids) {
        super(fm);
        this.storiesIds.clear();
        this.storiesIds.addAll(ids);
    }

    @Override
    public Parcelable saveState() {
        try {
            Bundle bundle = (Bundle) super.saveState();
            bundle.putParcelableArray("states", null);
            return bundle;
        } catch (Exception e) {
            return new Bundle();
        }
    }


    public int getItemId(int position) {
        if (position < storiesIds.size())
            return storiesIds.get(position);
        return -1;
    }


    private HashMap<Integer, StoriesReaderPageFragment> fragMap =
            new HashMap<>();

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return StoriesReaderPageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return storiesIds.size();
    }
}
