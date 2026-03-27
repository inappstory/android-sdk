package com.inappstory.sdk.refactoring.stories.ui.reader.states;

public class StoryReaderPageLoaderState {
    public StoryReaderPageLoaderType loaderType() {
        return loaderType;
    }

    public StoryReaderPageLoaderState loaderType(StoryReaderPageLoaderType loaderType) {
        this.loaderType = loaderType;
        return this;
    }

    public StoryReaderPageLoaderState copy() {
        return new StoryReaderPageLoaderState().
                loaderType(this.loaderType);
    }

    private StoryReaderPageLoaderType loaderType = StoryReaderPageLoaderType.HIDDEN;

}
