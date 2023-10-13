package com.inappstory.sdk.stories.outercallbacks.common.reader;

import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;

public interface LikeDislikeStoryCallback {
    void likeStory(SlideData slideData,
                   boolean value);

    void dislikeStory(SlideData slideData,
                      boolean value);
}
