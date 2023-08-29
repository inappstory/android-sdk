package com.inappstory.sdk.stories.outercallbacks.common.reader;

public interface LikeDislikeStoryCallback {
    void likeStory(SlideData slideData,
                   boolean value);

    void dislikeStory(SlideData slideData,
                      boolean value);
}
