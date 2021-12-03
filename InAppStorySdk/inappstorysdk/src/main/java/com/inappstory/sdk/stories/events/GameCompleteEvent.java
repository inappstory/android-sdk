package com.inappstory.sdk.stories.events;

public class GameCompleteEvent {
    String gameState;

    public String getGameState() {
        return gameState;
    }

    public int getStoryId() {
        return storyId;
    }

    public int getSlideIndex() {
        return slideIndex;
    }

    int storyId;

    public GameCompleteEvent(String gameState, int storyId, int slideIndex) {
        this.gameState = gameState;
        this.storyId = storyId;
        this.slideIndex = slideIndex;
    }

    int slideIndex;
}
