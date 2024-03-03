package com.inappstory.sdk.game.reader;

public class GameLoadStatusHolder {
    private final Object gameLoadStatusLock = new Object();
    private boolean gameLoaded;
    private boolean gameLoadFailed;

    boolean hasGameLoadStatus() {
        synchronized (gameLoadStatusLock) {
            return gameLoaded || gameLoadFailed;
        }
    }

    boolean gameLoaded() {
        synchronized (gameLoadStatusLock) {
            return gameLoaded;
        }
    }

    void setGameLoaded() {
        synchronized (gameLoadStatusLock) {
            gameLoaded = true;
        }
    }

    void setGameFailed() {
        synchronized (gameLoadStatusLock) {
            gameLoadFailed = true;
        }
    }



    void clearGameStatus() {
        synchronized (gameLoadStatusLock) {
            gameLoaded = false;
            gameLoadFailed = false;
        }
    }
}
