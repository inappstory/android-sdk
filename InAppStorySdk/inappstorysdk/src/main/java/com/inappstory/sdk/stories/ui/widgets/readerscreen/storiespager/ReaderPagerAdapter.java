package com.inappstory.sdk.stories.ui.widgets.readerscreen.storiespager;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.ui.reader.ReaderManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.inappstory.sdk.AppearanceManager.CS_READER_SETTINGS;
import static com.inappstory.sdk.AppearanceManager.CS_TIMER_GRADIENT;

public class ReaderPagerAdapter extends FragmentStatePagerAdapter {
    private List<Integer> storiesIds = new ArrayList<>();

    public ReaderPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public ReaderPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    String readerSettings;

    ReaderManager manager;

    public ReaderPagerAdapter(@NonNull FragmentManager fm,
                              String readerSettings,
                              Serializable timerGradient,
                              List<Integer> ids,
                              ReaderManager manager) {
        super(fm);
        this.storiesIds.clear();
        this.storiesIds.addAll(ids);
        this.readerSettings = readerSettings;
        this.timerGradient = timerGradient;
        this.manager = manager;
    }

    @Override
    public Parcelable saveState() {
        try {
            Bundle bundle = (Bundle) super.saveState();
            bundle.putParcelableArray("states", null);
            return bundle;
        } catch (Exception e) {
            InAppStoryService.createExceptionLog(e);
            return new Bundle();
        }
    }



    public int getItemId(int position) {
        if (position < storiesIds.size())
            return storiesIds.get(position);
        return -1;
    }


    private HashMap<Integer, ReaderPageFragment> fragMap =
            new HashMap<>();


    Serializable timerGradient;

    @NonNull
    @Override
    public Fragment getItem(int position) {
        ReaderPageFragment frag = new ReaderPageFragment();
        Bundle a = new Bundle();
        a.putInt("story_id", storiesIds.get(position));
        a.putString(CS_READER_SETTINGS, readerSettings);
        a.putSerializable(CS_TIMER_GRADIENT, timerGradient);
        frag.setArguments(a);
        frag.parentManager = manager;
        return frag;
    }

    @Override
    public int getCount() {
        return storiesIds.size();
    }
}
