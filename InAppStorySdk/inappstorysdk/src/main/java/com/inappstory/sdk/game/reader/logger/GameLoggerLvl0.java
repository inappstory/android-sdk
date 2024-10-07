package com.inappstory.sdk.game.reader.logger;

import android.util.Log;

import com.inappstory.sdk.core.IASCore;

public class GameLoggerLvl0 extends AbstractGameLogger {

    public GameLoggerLvl0(IASCore core) {
        super(core);
    }

    protected GameLoggerLvl0(IASCore core, String gameInstanceId) {
        super(core, gameInstanceId);
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
