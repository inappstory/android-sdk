package com.inappstory.sdk.core.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.game.cache.InGameResourceDownloadManager;
import com.inappstory.sdk.stories.outercallbacks.common.gamereader.GameReaderCallback;

public interface IASGames {
    void close();

    void open(@NonNull Context context, String gameId);

    void callback(GameReaderCallback gameReaderCallback);

    InGameResourceDownloadManager inGameResourceDownloadManager();

    void preloadGames();

    boolean gameCanBeOpened(String gameId);
}
