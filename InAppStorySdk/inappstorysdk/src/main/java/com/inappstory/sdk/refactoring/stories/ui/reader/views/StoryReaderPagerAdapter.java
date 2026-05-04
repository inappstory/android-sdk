package com.inappstory.sdk.refactoring.stories.ui.reader.views;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderPageViewModel;
import com.inappstory.sdk.refactoring.stories.ui.reader.viewmodels.StoryReaderViewModel;


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
                new StoryReaderPageViewModel(
                        readerViewModel,
                        readerViewModel.readerImmutableState().storiesIds().get(position),
                        position
                )
        );
        page.measureViews();
        container.addView(page);
        return page;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof StoryReaderPage) {
            ((StoryReaderPage) object).destroyView();
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
