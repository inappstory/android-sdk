package com.inappstory.sdk.iasapimodules.games;

import android.content.Context;

import androidx.annotation.NonNull;

public interface IGamesApi {
    void preloadGames();

    void closeGame();

    void openGame(
            String gameId,
            @NonNull Context context
    );
}
