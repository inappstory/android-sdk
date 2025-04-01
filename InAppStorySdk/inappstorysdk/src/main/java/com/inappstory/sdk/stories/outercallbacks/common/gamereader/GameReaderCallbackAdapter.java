package com.inappstory.sdk.stories.outercallbacks.common.gamereader;

import com.inappstory.sdk.stories.outercallbacks.common.reader.ContentData;

public class GameReaderCallbackAdapter implements GameReaderCallback {

    @Override
    public void startGame(
            ContentData gameLaunchSourceData,
            String gameId
    ) {

    }

    @Override
    public void finishGame(
            ContentData gameLaunchSourceData,
            String gameId,
            String result) {

    }

    @Override
    public void closeGame(
            ContentData gameLaunchSourceData,
            String gameId) {

    }

    @Override
    public void eventGame(
            ContentData gameLaunchSourceData,
            String gameId,
            String eventName,
            String payload
    ) {

    }

    @Override
    public void gameLoadError(
            ContentData gameLaunchSourceData,
            String gameId
    ) {

    }

    @Override
    public void gameOpenError(
            ContentData gameLaunchSourceData,
            String gameId
    ) {

    }
}
