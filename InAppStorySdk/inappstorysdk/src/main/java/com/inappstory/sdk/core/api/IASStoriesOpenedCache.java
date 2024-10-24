package com.inappstory.sdk.core.api;

import com.inappstory.sdk.core.dataholders.IListItemContent;
import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.Story;

import java.util.List;

public interface IASStoriesOpenedCache {
    String getLocalOpensKey(ContentType type);

    void clearLocalOpensKey();

    void saveStoriesOpened(final ContentType type);

    void saveStoryOpened(final int id, final ContentType type);
}
