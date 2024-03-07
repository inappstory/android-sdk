package com.inappstory.sdk.game.reader.logger;

import android.util.Log;

public class GameLoggerLvl0 extends AbstractGameLogger {

    public GameLoggerLvl0() {
        super();
    }

    protected GameLoggerLvl0(String gameInstanceId) {
        super(gameInstanceId);
    }

    @Override
    public void sendGameError(String message) {
        Log.d("GameLoggerLvl0", "sendGameError " +message);
    }

    @Override
    public void sendConsoleError(String message) {
        Log.d("GameLoggerLvl0", "sendConsoleError " +message);
    }

    @Override
    public void sendSdkError(String message, String stacktrace) {
        Log.d("GameLoggerLvl0", "sendConsoleWarn " +message);
    }

    @Override
    public void sendConsoleWarn(String message) {
        Log.d("GameLoggerLvl0", "sendConsoleWarn " +message);
    }

    @Override
    public void sendSdkWarn(String message) {
        Log.d("GameLoggerLvl0", "sendSdkWarn " + message);
    }

    @Override
    public void sendConsoleInfo(String message) {
        Log.d("GameLoggerLvl0", "sendConsoleInfo " +message);
    }
}
