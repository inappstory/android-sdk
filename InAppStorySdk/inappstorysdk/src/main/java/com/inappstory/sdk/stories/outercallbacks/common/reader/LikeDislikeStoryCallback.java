package com.inappstory.sdk.stories.outercallbacks.common.reader;

public interface LikeDislikeStoryCallback {
    void likeStory(int id,
                   String title,
                   String tags,
                   int slidesCount,
                   int index,
                   boolean value);

    void dislikeStory(int id,
                      String title,
                      String tags,
                      int slidesCount,
                      int index,
                      boolean value);
}
