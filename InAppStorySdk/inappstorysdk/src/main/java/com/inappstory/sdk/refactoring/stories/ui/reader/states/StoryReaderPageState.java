package com.inappstory.sdk.refactoring.stories.ui.reader.states;

import com.inappstory.sdk.refactoring.stories.data.contracts.IStoryItem;
import com.inappstory.sdk.refactoring.stories.data.contracts.IStoryListItem;
import com.inappstory.sdk.refactoring.stories.data.contracts.IStoryReaderItem;
import com.inappstory.sdk.stories.api.models.ContentType;


public class StoryReaderPageState {
    private final String storyId;
    private final ContentType contentType;
    private IStoryReaderItem storyDTO;
    private IStoryListItem storiesListItemDTO;
    private int slideIndex;
    private final int pageIndex;

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

    public IStoryReaderItem story() {
        return storyDTO;
    }

    public IStoryItem storyItem() {
        return storyDTO != null ? storyDTO : storiesListItemDTO;
    }

    public int pageIndex() {
        return pageIndex;
    }

    public StoryReaderPageState slideIndex(int slideIndex) {
        this.slideIndex = slideIndex;
        return this;
    }

    public StoryReaderPageState storyListItem(IStoryListItem storiesListItemDTO) {
        this.storiesListItemDTO = storiesListItemDTO;
        return this;
    }

    public StoryReaderPageState story(IStoryReaderItem storyDTO) {
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
