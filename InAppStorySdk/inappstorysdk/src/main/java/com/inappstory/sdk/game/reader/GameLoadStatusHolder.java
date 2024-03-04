package com.inappstory.sdk.game.reader;

public class GameLoadStatusHolder {
    private final Object gameLoadStatusLock = new Object();
    private boolean gameLoaded;
    private boolean gameLoadFailed;
    private int currentReloadTry;

    private int totalReloadTries;

    public void setTotalReloadTries(int totalReloadTries) {

        synchronized (gameLoadStatusLock) {
            this.totalReloadTries = totalReloadTries;
        }
    }

    public boolean updateCurrentReloadTry() {
        synchronized (gameLoadStatusLock) {
            if (currentReloadTry < totalReloadTries) {
                currentReloadTry++;
                return true;
            }
            return false;
        }
    }


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
            currentReloadTry = 0;
        }
    }

    void setGameFailed() {
        synchronized (gameLoadStatusLock) {
            gameLoadFailed = true;
        }
    }

    void clearGameLoadTries() {
        synchronized (gameLoadStatusLock) {
            currentReloadTry = 0;
        }
    }

    void clearGameStatus() {
        synchronized (gameLoadStatusLock) {
            gameLoaded = false;
            gameLoadFailed = false;
        }
    }
}
