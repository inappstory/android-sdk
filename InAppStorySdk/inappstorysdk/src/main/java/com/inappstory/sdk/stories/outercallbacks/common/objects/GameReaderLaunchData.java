package com.inappstory.sdk.stories.outercallbacks.common.objects;


import com.inappstory.sdk.stories.outercallbacks.common.reader.ContentData;


public class GameReaderLaunchData implements SerializableWithKey {
    public static String SERIALIZABLE_KEY = "gameReaderLaunchData";

    public GameReaderLaunchData(
            String gameId,
            String observableUID,
            ContentData slideData
    ) {
        this.gameId = gameId;
        this.observableUID = observableUID;
        this.contentData = slideData;
    }

    public String getGameId() {
        return gameId;
    }

    public String getObservableUID() {
        return observableUID;
    }


    public ContentData getContentData() {
        return contentData;
    }

    private String gameId;
    private String observableUID;
    private ContentData contentData;

    @Override
    public String getSerializableKey() {
        return SERIALIZABLE_KEY;
    }
}
