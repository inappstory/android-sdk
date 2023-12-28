package com.inappstory.sdk.stories.ui.reader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.inappstory.sdk.databinding.IasReaderPagerBinding;
import com.inappstory.sdk.stories.ui.IASUICore;
import com.inappstory.sdk.stories.ui.reader.views.pager.StoriesReaderPagerAdapter;
import com.inappstory.sdk.stories.ui.reader.views.pager.StoriesReaderPagerSwipeListener;
import com.inappstory.sdk.stories.uidomain.reader.IStoriesReaderViewModel;
import com.inappstory.sdk.stories.uidomain.reader.StoriesReaderState;
import com.inappstory.sdk.stories.uidomain.reader.StoriesReaderViewModel;

public final class StoriesReaderPagerFragment extends Fragment implements IStoriesReaderPagerScreen,
        StoriesReaderPagerSwipeListener, ViewPager.OnPageChangeListener {

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
        IStoriesReaderViewModel viewModel = IASUICore.getInstance().getStoriesReaderVM();
        StoriesReaderState state = viewModel.getState();
        binding.iasStoriesPager.setPagerSwipeListener(this);
        binding.iasStoriesPager.addOnPageChangeListener(this);
        binding.iasStoriesPager.setParameters(state.appearanceSettings().csStoryReaderAnimation());
        binding.iasStoriesPager.setAdapter(new StoriesReaderPagerAdapter(
                getChildFragmentManager(),
                state.launchData().getStoriesIds()
        ));
        binding.iasStoriesPager.setCurrentItem(state.launchData().getListIndex());
        viewModel.currentIndex().observe(getViewLifecycleOwner(), currentPageObserver);
        viewModel.frozen().observe(getViewLifecycleOwner(), freezePagerObserver);
    }

    private final Observer<Integer> currentPageObserver = new Observer<Integer>() {
        @Override
        public void onChanged(Integer index) {
            binding.iasStoriesPager.setCurrentItem(index != null ? index : 0);
        }
    };

    private final Observer<Boolean> freezePagerObserver = new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean frozen) {
            binding.iasStoriesPager.requestDisallowInterceptTouchEvent(frozen);
        }
    };

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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        IASUICore.getInstance().getStoriesReaderVM().currentIndex(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
