package com.inappstory.sdk.game.reader;


import com.inappstory.sdk.stories.outercallbacks.common.objects.SlideData;

import java.io.Serializable;

public class GameStoryData implements Serializable {
    public SlideData slideData;

    public GameStoryData(
            SlideData slideData
    ) {
        this.slideData = slideData;
    }

}
