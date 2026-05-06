package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderPageViewModel;
import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StoryReaderPagerAdapter extends PagerAdapter {
    private final StoryReaderViewModel readerViewModel;
    private final StoryReaderPageAppearance pageAppearance;

    public StoryReaderPagerAdapter(
            StoryReaderViewModel readerViewModel,
            StoryReaderPageAppearance pageAppearance
    ) {
        this.readerViewModel = readerViewModel;
        this.pageAppearance = pageAppearance;
    }

    static String getTagByPosition(int position) {
        return "StoryReaderPage_" + position;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        StoryReaderPage page = new StoryReaderPage(
                container.getContext(),
                pageAppearance
        );
        page.setTag(getTagByPosition(position));
        page.viewModel(
                readerViewModel.getOrCreatePageViewModel(
                        readerViewModel.readerImmutableState().storiesIds().get(position)
                )
        );
        page.measureViews();
        container.addView(page);
        return page;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        readerViewModel.destroyPage(position);
        if (object instanceof StoryReaderPage) {
            container.removeView((StoryReaderPage) object);
        }
    }

    @Override
    public int getCount() {
        return readerViewModel != null ?
                readerViewModel.readerImmutableState().storiesIds().size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
