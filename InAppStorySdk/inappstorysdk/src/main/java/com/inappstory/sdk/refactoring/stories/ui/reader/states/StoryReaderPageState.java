package com.inappstory.sdk.refactoring.stories.ui.reader.states;

import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;
import com.inappstory.sdk.stories.api.models.ContentType;

import java.util.List;

public class StoryReaderPageState {
    private final String storyId;
    private final ContentType contentType;
    private StoryDTO storyDTO;
    private StoriesListItemDTO storiesListItemDTO;
    private int slideIndex;
    private final int pageIndex;
    private boolean currentSlideIsLoaded;
    private List<Integer> cachedSlides;


    public StoryReaderPageState(String storyId, int pageIndex, ContentType contentType) {
        this.storyId = storyId;
        this.contentType = contentType;
        this.pageIndex = pageIndex;
    }

    public int slideIndex() {
        return slideIndex;
    }

    public String storyId() {
        return storyId;
    }

    public ContentType contentType() {
        return contentType;
    }

    public StoriesListItemDTO storyListItem() {
        return storiesListItemDTO;
    }

    public StoryDTO story() {
        return storyDTO;
    }

    public int pageIndex() {
        return pageIndex;
    }

    public StoryReaderPageState slideIndex(int slideIndex) {
        this.slideIndex = slideIndex;
        return this;
    }

    public StoryReaderPageState storyListItem(StoriesListItemDTO storiesListItemDTO) {
        this.storiesListItemDTO = storiesListItemDTO;
        return this;
    }

    public StoryReaderPageState story(StoryDTO storyDTO) {
        this.storyDTO = storyDTO;
        return this;
    }

    public StoryReaderPageState copy() {
        return new StoryReaderPageState(storyId, pageIndex, contentType).
                slideIndex(this.slideIndex).
                storyListItem(this.storiesListItemDTO).
                story(this.storyDTO);
    }
}
