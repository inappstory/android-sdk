package com.inappstory.sdk.refactoring.stories;

public interface IStoriesFavoriteFeedChangeSubscriber {
    void onChange(String storyId, boolean add);
}
