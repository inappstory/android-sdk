package com.inappstory.sdk.game.reader.logger;

public interface IGameLogSender {
    void stop();
    void start(boolean gameLaunched);
}
