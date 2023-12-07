package com.inappstory.sdk.stories.ui.reader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.databinding.IasReaderPagerBinding;
import com.inappstory.sdk.stories.ui.IASUICore;
import com.inappstory.sdk.stories.ui.reader.views.StoriesReaderPagerSwipeListener;
import com.inappstory.sdk.stories.uidomain.reader.StoriesReaderState;

public final class StoriesReaderPagerFragment extends Fragment implements IStoriesReaderPagerScreen,
        StoriesReaderPagerSwipeListener {

    public static final String TAG = "StoriesReaderPagerFragment";

    IasReaderPagerBinding binding;


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        binding = IasReaderPagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        StoriesReaderState state = IASUICore.getInstance().getStoriesReaderVM().getState();

        binding.iasStoriesPager.setPagerSwipeListener(this);
        binding.iasStoriesPager.setParameters(state.appearanceSettings().csStoryReaderAnimation());

    }

    @Override
    public IStoriesReaderScreen getStoriesReaderScreen() {
        Fragment parent = getParentFragment();
        if (parent instanceof IStoriesReaderScreenChild)
            return ((IStoriesReaderScreenChild) parent).getStoriesReaderScreen();
        return null;
    }

    @Override
    public void swipeDown(int index) {

    }

    @Override
    public void swipeUp(int index) {

    }

    @Override
    public void swipeLeft(int index) {

    }

    @Override
    public void swipeRight(int index) {

    }
}