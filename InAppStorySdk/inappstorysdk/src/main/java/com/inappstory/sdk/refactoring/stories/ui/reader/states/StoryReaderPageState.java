package com.inappstory.sdk.refactoring.stories.ui.reader.states;

public class StoryReaderPageState {
    final String storyId;
    int slideIndex;
    final int pageIndex;

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

    public int pageIndex() {
        return pageIndex;
    }

    public StoryReaderPageState slideIndex(int slideIndex) {
        this.slideIndex = slideIndex;
        return this;
    }

    public StoryReaderPageState copy() {
        return new StoryReaderPageState(storyId, pageIndex).
                slideIndex(this.slideIndex);
    }
}
