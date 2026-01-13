package com.inappstory.sdk.core.ui.screens.storyreader;

import com.inappstory.sdk.stories.api.models.ContentType;
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
        return Objects.hash(
                sessionId,
                storiesIds,
                listIndex,
                sourceType,
                slideIndex,
                feed,
                type,
                shownOnlyNewStories
        );
    }

    public LaunchStoryScreenData() {
        this.readerUniqueId = UUID.randomUUID().toString();
    }

    public String listUniqueId() {
        return listUniqueId;
    }

    public List<Integer> storiesIds() {
        return storiesIds;
    }

    public int listIndex() {
        return listIndex;
    }

    public SourceType sourceType() {
        return sourceType;
    }

    public int firstAction() {
        return firstAction;
    }

    public Integer slideIndex() {
        return slideIndex;
    }

    public String feed() {
        return feed;
    }

    public String sessionId() {
        return sessionId;
    }

    public ContentType type() {
        return type;
    }


    public LaunchStoryScreenData listUniqueId(String listUniqueId) {
        this.listUniqueId = listUniqueId;
        return this;
    }

    public LaunchStoryScreenData sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public LaunchStoryScreenData storiesIds(List<Integer> storiesIds) {
        this.storiesIds = storiesIds;
        return this;
    }

    public LaunchStoryScreenData listIndex(int listIndex) {
        this.listIndex = listIndex;
        return this;
    }

    public LaunchStoryScreenData sourceType(SourceType sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public LaunchStoryScreenData firstAction(int firstAction) {
        this.firstAction = firstAction;
        return this;
    }

    public LaunchStoryScreenData slideIndex(Integer slideIndex) {
        this.slideIndex = slideIndex;
        return this;
    }

    public LaunchStoryScreenData feed(String feed) {
        this.feed = feed;
        return this;
    }

    public LaunchStoryScreenData type(ContentType type) {
        this.type = type;
        return this;
    }

    public LaunchStoryScreenData shownOnlyNewStories(boolean shownOnlyNewStories) {
        this.shownOnlyNewStories = shownOnlyNewStories;
        return this;
    }


    private StoryItemCoordinates initCoordinates = null;
    private String listUniqueId = null;
    private String sessionId;
    private List<Integer> storiesIds;
    private int listIndex = 0;
    private SourceType sourceType;
    private int firstAction;
    private Integer slideIndex = 0;
    private String feed = null;
    private ContentType type;
    private boolean shownOnlyNewStories = false;
    private final String readerUniqueId;

    public String cancellationTokenUID() {
        return cancellationTokenUID;
    }

    public LaunchStoryScreenData cancellationTokenUID(String cancellationTokenUID) {
        this.cancellationTokenUID = cancellationTokenUID;
        return this;
    }

    private String cancellationTokenUID = null;

    public LaunchStoryScreenData initCoordinates(StoryItemCoordinates initCoordinates) {
        this.initCoordinates = initCoordinates;
        return this;
    }

    public void clearSingleTimeParameters() {
        firstAction = 0;
        slideIndex = null;
        listIndex = 0;
    }


    public boolean shownOnlyNewStories() {
        return shownOnlyNewStories;
    }

    public StoryItemCoordinates getInitCoordinates() {
        return initCoordinates;
    }

    public String getReaderUniqueId() {
        return readerUniqueId;
    }

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}