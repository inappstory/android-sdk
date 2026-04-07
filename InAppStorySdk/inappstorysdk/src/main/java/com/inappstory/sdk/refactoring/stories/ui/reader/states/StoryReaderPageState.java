package com.inappstory.sdk.refactoring.stories.ui.reader.states;

import com.inappstory.sdk.refactoring.stories.data.local.StoriesListItemDTO;
import com.inappstory.sdk.refactoring.stories.data.local.StoryDTO;

public class StoryReaderPageState {
    final String storyId;
    StoryDTO storyDTO;
    StoriesListItemDTO storiesListItemDTO;
    int slideIndex;
    final int pageIndex;
    private boolean currentSlideIsLoaded;

    public StoryReaderPageState(String storyId, int pageIndex) {
        this.storyId = storyId;
        this.pageIndex = pageIndex;
    }

    public int slideIndex() {
        return slideIndex;
    }

    public String storyId() {
        return storyId;
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
        return new StoryReaderPageState(storyId, pageIndex).
                slideIndex(this.slideIndex).
                storyListItem(this.storiesListItemDTO).
                story(this.storyDTO);
    }
}
