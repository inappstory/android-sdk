package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;

public interface FavoriteStoryCallback {
    void favoriteStory(
            SlideData slideData,
            boolean value
    );
}
