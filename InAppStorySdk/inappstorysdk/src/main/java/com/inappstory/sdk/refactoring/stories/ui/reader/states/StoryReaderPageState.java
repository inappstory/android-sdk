package com.inappstory.sdk.refactoring.stories.ui.reader.states;

public class StoryReaderPageState {
    public StoryReaderPageLoaderType loaderType() {
        return loaderType;
    }

    public StoryReaderPageState loaderType(StoryReaderPageLoaderType loaderType) {
        this.loaderType = loaderType;
        return this;
    }

    public StoryReaderPageState copy() {
        return new StoryReaderPageState().
                loaderType(this.loaderType);
    }

    private StoryReaderPageLoaderType loaderType = StoryReaderPageLoaderType.HIDDEN;

}
