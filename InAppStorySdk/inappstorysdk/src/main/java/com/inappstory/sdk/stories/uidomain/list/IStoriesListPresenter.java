package com.inappstory.sdk.stories.uidomain.list;

import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.ui.list.ShownStoriesListItem;

import java.util.List;

public interface IStoriesListPresenter {
    ShownStoriesListItem getShownStoriesListItemByStoryId(
            int storyId,
            int listIndex,
            float currentPercentage,
            String feed,
            SourceType sourceType
    );

    List<Integer> getCachedStoriesPreviews(String cacheId);
}
