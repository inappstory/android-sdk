package com.inappstory.sdk.stories.outercallbacks.common.reader;

public interface FavoriteStoryCallback {
    void favoriteStory(int id,
                       String title,
                       String tags,
                       int slidesCount,
                       int index,
                       boolean value);
}
