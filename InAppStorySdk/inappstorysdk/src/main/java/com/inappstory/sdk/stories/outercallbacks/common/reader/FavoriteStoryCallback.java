package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.core.api.IASCallback;

public interface FavoriteStoryCallback extends IASCallback {
    void favoriteStory(
            SlideData slideData,
            boolean value
    );
}
