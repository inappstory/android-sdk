package com.inappstory.sdk.game.preload;

public interface IGamePreloader {
    void launch();
    void pause();
    void restart();
    void active(boolean active);
}
