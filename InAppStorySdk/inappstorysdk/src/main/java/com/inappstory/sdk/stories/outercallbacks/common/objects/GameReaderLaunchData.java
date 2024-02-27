package com.inappstory.sdk.stories.outercallbacks.common.objects;


import com.inappstory.sdk.game.reader.GameScreenOptions;
import com.inappstory.sdk.stories.api.models.WebResource;
import com.inappstory.sdk.stories.outercallbacks.common.reader.SlideData;

import java.util.List;

public class GameReaderLaunchData implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "gameReaderLaunchData";

    public GameReaderLaunchData(
            String gameId,
            String observableUID,
            SlideData slideData
    ) {
        this.gameId = gameId;
        this.observableUID = observableUID;
        this.slideData = slideData;
    }

    public String getGameId() {
        return gameId;
    }

    public String getObservableUID() {
        return observableUID;
    }


    public SlideData getSlideData() {
        return slideData;
    }

    private String gameId;
    private String observableUID;
    private SlideData slideData;

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}
