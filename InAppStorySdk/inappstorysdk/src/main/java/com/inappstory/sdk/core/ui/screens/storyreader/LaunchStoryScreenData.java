package com.inappstory.sdk.core.ui.screens.storyreader;

import com.inappstory.sdk.stories.api.models.ContentType;
import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.outercallbacks.common.objects.SerializableWithKey;
import com.inappstory.sdk.stories.outercallbacks.common.objects.StoryItemCoordinates;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SourceType;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LaunchStoryScreenData implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "storiesReaderLaunchData";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LaunchStoryScreenData that = (LaunchStoryScreenData) o;
        return listIndex == that.listIndex &&
                shownOnlyNewStories == that.shownOnlyNewStories &&
                Objects.equals(sessionId, that.sessionId) &&
                Objects.equals(storiesIds, that.storiesIds) &&
                sourceType == that.sourceType &&
                Objects.equals(slideIndex, that.slideIndex) &&
                Objects.equals(feed, that.feed) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, storiesIds, listIndex, sourceType, slideIndex, feed, type, shownOnlyNewStories);
    }

    public LaunchStoryScreenData(
            String listUniqueId,
            String feed,
            String sessionId,
            List<Integer> storiesIds,
            int listIndex,
            boolean shownOnlyNewStories,
            int firstAction,
            SourceType sourceType,
            Integer slideIndex,
            ContentType type,
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

    public ContentType getType() {
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
    private final ContentType type;
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