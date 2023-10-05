package com.inappstory.sdk.stories.uidomain.list;

import com.inappstory.sdk.InAppStoryService;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;
import com.inappstory.sdk.stories.ui.list.ShownStoriesListItem;
import com.inappstory.sdk.utils.StringsUtils;

import java.util.ArrayList;
import java.util.List;

public class StoriesListPresenter implements IStoriesListPresenter {
    @Override
    public ShownStoriesListItem getShownStoriesListItemByStoryId(
            int storyId,
            int listIndex,
            float currentPercentage,
            String feed,
            SourceType sourceType
    ) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return null;
        Story currentStory = service.getDownloadManager()
                .getStoryById(storyId, Story.StoryType.COMMON);
        if (currentStory != null && currentPercentage > 0) {
            return new ShownStoriesListItem(
                    new StoryData(
                            currentStory.id,
                            StringsUtils.getNonNull(currentStory.statTitle),
                            StringsUtils.getNonNull(currentStory.tags),
                            currentStory.getSlidesCount(),
                            feed,
                            sourceType
                    ),
                    listIndex,
                    currentPercentage
            );
        }
        return null;
    }

    @Override
    public List<Integer> getCachedStoriesPreviews(String cacheId) {
        InAppStoryService service = InAppStoryService.getInstance();
        if (service == null) return null;
        return service.listStoriesIds.get(cacheId);
    }
}
