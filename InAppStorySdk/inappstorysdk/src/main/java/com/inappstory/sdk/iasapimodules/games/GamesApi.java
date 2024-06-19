package com.inappstory.sdk.iasapimodules.games;

import android.content.Context;

import androidx.annotation.NonNull;

import com.inappstory.sdk.iasapimodules.NotImplementedYetException;

public class GamesApi implements IGamesApi{

    @Override
    public void preloadGames() {
        throw new NotImplementedYetException();
    }

    @Override
    public void closeGame() {
        throw new NotImplementedYetException();
    }

    @Override
    public void openGame(String gameId, @NonNull Context context) {
        throw new NotImplementedYetException();
    }
}
