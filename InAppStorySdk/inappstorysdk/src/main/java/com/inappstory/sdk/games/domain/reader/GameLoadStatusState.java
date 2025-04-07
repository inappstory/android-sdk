package com.inappstory.sdk.games.domain.reader;

public class GameLoadStatusState {
    public int launchTryNumber() {
        return launchTryNumber;
    }

    public int totalReloadTries() {
        return totalReloadTries;
    }

    public boolean isGameLoaded() {
        return gameLoaded;
    }

    public boolean isGameLoadFailed() {
        return gameLoadFailed;
    }

    private int launchTryNumber;
    private int totalReloadTries;
    private boolean gameLoaded;
    private boolean gameLoadFailed;

    public GameLoadStatusState launchTryNumber(int launchTryNumber) {
        this.launchTryNumber = launchTryNumber;
        return this;
    }

    public GameLoadStatusState totalReloadTries(int totalReloadTries) {
        this.totalReloadTries = totalReloadTries;
        return this;
    }

    public GameLoadStatusState gameLoaded(boolean gameLoaded) {
        this.gameLoaded = gameLoaded;
        return this;
    }

    public GameLoadStatusState gameLoadFailed(boolean gameLoadFailed) {
        this.gameLoadFailed = gameLoadFailed;
        return this;
    }

    public GameLoadStatusState copy() {
        return new GameLoadStatusState()
                .gameLoaded(this.gameLoaded)
                .gameLoadFailed(this.gameLoadFailed)
                .launchTryNumber(this.launchTryNumber)
                .totalReloadTries(this.totalReloadTries);
    }
}
