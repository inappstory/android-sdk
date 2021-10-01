package com.inappstory.sdk.stories.outercallbacks.common.reader;

public interface CloseStoryCallback {

    void closeStory(int id,
                    String title,
                    String tags,
                    int slidesCount,
                    int index,
                    CloseReader action,
                    SourceType source);
}
