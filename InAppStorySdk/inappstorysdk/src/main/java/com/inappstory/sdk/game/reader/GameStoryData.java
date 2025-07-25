package com.inappstory.sdk.game.reader;


import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;
import com.inappstory.sdk.stories.outercallbacks.common.reader.StoryData;

public class GameStoryData {
    public SlideData slideData;

    public StoryData storyData() {
        if (slideData != null)
            return slideData.story();
        return null;
    }

    public GameStoryData(
            SlideData slideData
    ) {
        this.slideData = slideData;
    }

}
