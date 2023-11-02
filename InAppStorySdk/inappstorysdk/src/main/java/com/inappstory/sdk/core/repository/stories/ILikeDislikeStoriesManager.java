package com.inappstory.sdk.core.repository.stories;

public interface ILikeDislikeStoriesManager {
    void likeStory(int storyId);

    void dislikeStory(int storyId);

    void clearLikeDislikeStoryStatus(int storyId);
}
