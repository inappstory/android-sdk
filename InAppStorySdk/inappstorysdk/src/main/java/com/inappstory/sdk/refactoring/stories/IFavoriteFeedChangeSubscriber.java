package com.inappstory.sdk.refactoring.stories;

public interface IFavoriteFeedChangeSubscriber {
    void onChange(String storyId, boolean add);
}
