package com.inappstory.sdk.stories.stackfeed;

public interface IStackFeedResult {
    void success(
            IStackStoryData stackStoryData,
            IStackFeedActions actions
    );

    void update(IStackStoryData stackStoryData);

    void error();
}
