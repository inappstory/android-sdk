package com.inappstory.sdk.stories.ui.reader.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.inappstory.sdk.databinding.IasReaderPageBinding;
import com.inappstory.sdk.stories.ui.IASUICore;
import com.inappstory.sdk.stories.ui.reader.IStoriesReaderPagerScreen;
import com.inappstory.sdk.stories.ui.reader.IStoriesReaderScreen;
import com.inappstory.sdk.stories.uidomain.reader.IStoriesReaderViewModel;
import com.inappstory.sdk.stories.uidomain.reader.page.IStoriesReaderPageViewModel;

public final class StoriesReaderPageFragment extends Fragment implements IStoriesReaderPage {



    IStoriesReaderPageViewModel pageViewModel;
    IStoriesReaderViewModel readerViewModel;

    private static final String INDEX = "index";


    public static StoriesReaderPageFragment newInstance(int index) {
        StoriesReaderPageFragment fragment = new StoriesReaderPageFragment();
        Bundle a = new Bundle();
        a.putInt(INDEX, index);
        fragment.setArguments(a);
        return fragment;
    }


    IasReaderPageBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        int index = getArguments().getInt(INDEX);
        pageViewModel = IASUICore.getInstance().getStoriesReaderVM().getPageViewModel(index);
        binding = IasReaderPageBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IStoriesReaderPagerScreen getStoriesReaderPagerScreen() {
        Fragment parent = getParentFragment();
        if (parent instanceof IStoriesReaderPagerScreen)
            return (IStoriesReaderPagerScreen) parent;
        return null;
    }
}
