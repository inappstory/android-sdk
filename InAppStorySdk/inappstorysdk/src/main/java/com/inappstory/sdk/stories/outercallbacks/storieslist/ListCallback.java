package com.inappstory.sdk.stories.outercallbacks.storieslist;

public interface ListCallback {
    void storiesLoaded(int size, String feedId);

    void loadError(String feedId);

    void itemClick(int id,
                   int listIndex,
                   String title,
                   String tags,
                   int slidesCount,
                   boolean isFavoriteList,
                   String feedId);
}
