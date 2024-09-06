package com.inappstory.sdk.core.ui.screens;

import androidx.fragment.app.FragmentManager;

import com.inappstory.sdk.core.ui.screens.storyreader.BaseStoryScreen;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.ui.OverlapFragmentObserver;

public interface IOverlapContainerHolder {
    void openShareOverlapContainer(
            IOverlapContainerData data,
            FragmentManager fragmentManager,
            OverlapFragmentObserver observer
    );

    void openGoodsOverlapContainer(
            String skusString,
            final String widgetId,
            final SlideData slideData
    );
}
