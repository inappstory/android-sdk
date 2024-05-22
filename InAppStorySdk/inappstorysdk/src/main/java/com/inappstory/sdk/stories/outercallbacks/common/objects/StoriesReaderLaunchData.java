package com.inappstory.sdk.stories.outercallbacks.common.objects;


import com.inappstory.sdk.stories.api.models.Story.StoryType;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;

import java.util.List;
import java.util.UUID;

public class StoriesReaderLaunchData implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "storiesReaderLaunchData";

    public StoriesReaderLaunchData(
            String listUniqueId,
            String feed,
            String sessionId,
            List<Integer> storiesIds,
            int listIndex,
            boolean shownOnlyNewStories,
            int firstAction,
            SourceType sourceType,
            Integer slideIndex,
            StoryType type,
            StoryItemCoordinates initCoordinates
    ) {
        this.listUniqueId = listUniqueId;
        this.sessionId = sessionId;
        this.storiesIds = storiesIds;
        this.listIndex = listIndex;
        this.shownOnlyNewStories = shownOnlyNewStories;
        this.sourceType = sourceType;
        this.firstAction = firstAction;
        this.slideIndex = slideIndex;
        this.feed = feed;
        this.type = type;
        this.readerUniqueId = UUID.randomUUID().toString();
        this.initCoordinates = initCoordinates;
    }

    public String getListUniqueId() {
        return listUniqueId;
    }

    public List<Integer> getStoriesIds() {
        return storiesIds;
    }

    public int getListIndex() {
        return listIndex;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public int getFirstAction() {
        return firstAction;
    }

    public Integer getSlideIndex() {
        return slideIndex;
    }

    public String getFeed() {
        return feed;
    }

    public String getSessionId() {
        return sessionId;
    }

    public StoryType getType() {
        return type;
    }

    private final String listUniqueId;
    private final String sessionId;
    private final List<Integer> storiesIds;
    private final int listIndex;
    private final SourceType sourceType;
    private final int firstAction;
    private final Integer slideIndex;
    private final String feed;
    private final StoryType type;
    private final boolean shownOnlyNewStories;



    public boolean shownOnlyNewStories() {
        return shownOnlyNewStories;
    }

    public StoryItemCoordinates getInitCoordinates() {
        return initCoordinates;
    }

    private final StoryItemCoordinates initCoordinates;

    public String getReaderUniqueId() {
        return readerUniqueId;
    }

    private final String readerUniqueId;

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}
