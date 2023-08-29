package com.inappstory.sdk.game.reader;


import com.inappstory.sdk.stories.api.models.Story;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;

public class GameStoryData {

    public String feed;
    public SlideData slideData;

    public GameStoryData(
            SlideData slideData,
            String feed
    ) {
        this.slideData = slideData;
        this.feed = feed;
    }

}
