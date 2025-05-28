package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.core.api.IASCallback;

public interface LikeDislikeStoryCallback extends IASCallback {
    void likeStory(SlideData slideData,
                   boolean value);

    void dislikeStory(SlideData slideData,
                      boolean value);
}
