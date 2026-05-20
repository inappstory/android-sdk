package com.inappstory.sdk.refactoring.stories.ui.reader.states;

public class StoryReaderButtonsState {
    @Override
    public String toString() {
        return "StoryReaderButtonsState{" +
                "likeState=" + likeState +
                ", dislikeState=" + dislikeState +
                ", favoriteState=" + favoriteState +
                ", shareState=" + shareState +
                ", soundState=" + soundState +
                '}';
    }

    public StoryReaderButtonsState likeState(StoryReaderButtonState likeState) {
        this.likeState = likeState;
        return this;
    }

    public StoryReaderButtonsState dislikeState(StoryReaderButtonState dislikeState) {
        this.dislikeState = dislikeState;
        return this;
    }

    public StoryReaderButtonsState favoriteState(StoryReaderButtonState favoriteState) {
        this.favoriteState = favoriteState;
        return this;
    }

    public StoryReaderButtonsState shareState(StoryReaderButtonState shareState) {
        this.shareState = shareState;
        return this;
    }

    public StoryReaderButtonsState soundState(StoryReaderButtonState soundState) {
        this.soundState = soundState;
        return this;
    }

    public StoryReaderButtonState likeState() {
        return likeState;
    }

    public StoryReaderButtonState dislikeState() {
        return dislikeState;
    }

    public StoryReaderButtonState favoriteState() {
        return favoriteState;
    }

    public StoryReaderButtonState shareState() {
        return shareState;
    }

    public StoryReaderButtonState soundState() {
        return soundState;
    }

    StoryReaderButtonState likeState = new StoryReaderButtonState();
    StoryReaderButtonState dislikeState = new StoryReaderButtonState();
    StoryReaderButtonState favoriteState = new StoryReaderButtonState();
    StoryReaderButtonState shareState = new StoryReaderButtonState();
    StoryReaderButtonState soundState = new StoryReaderButtonState();


    public StoryReaderButtonsState copy() {
        return new StoryReaderButtonsState()
                .likeState(likeState)
                .dislikeState(dislikeState)
                .shareState(shareState)
                .soundState(soundState)
                .favoriteState(favoriteState);
    }
}
