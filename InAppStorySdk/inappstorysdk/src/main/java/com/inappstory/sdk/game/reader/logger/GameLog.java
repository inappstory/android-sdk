package com.inappstory.sdk.game.reader.logger;

public class GameLog {
    public GameLog() {

    }

    public GameLog(
            String gameInstanceId,
            String logSession,
            Long timestamp,
            int launchTryNumber,
            boolean gameLoaded
    ) {
        this.gameInstanceId = gameInstanceId;
        this.timestamp = timestamp;
        this.logSession = logSession;
        this.gameLoaded = gameLoaded;
        this.launchTryNumber = launchTryNumber;
    }

    GameLog type(String type) {
        this.type = type;
        return this;
    }

    GameLog message(String message) {
        this.message = message;
        return this;
    }

    GameLog stacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
        return this;
    }


    String gameInstanceId() {
        return gameInstanceId;
    }


    boolean gameLoaded() {
        return gameLoaded;
    }

    String type() {
        return type;
    }

    Long timestamp() {
        return timestamp;
    }

    String message() {
        return message;
    }

    String logSession() {
        return logSession;
    }

    String stacktrace() {
        return stacktrace;
    }

    public String gameInstanceId;
    public String type;
    public Long timestamp;

    public int launchTryNumber() {
        return launchTryNumber;
    }

    public int launchTryNumber;

    public GameLog(
            String gameInstanceId,
            String type,
            Long timestamp,
            String message,
            String logSession,
            String stacktrace,
            boolean gameLoaded
    ) {
        this.gameInstanceId = gameInstanceId;
        this.type = type;
        this.timestamp = timestamp;
        this.message = message;
        this.logSession = logSession;
        this.stacktrace = stacktrace;
        this.gameLoaded = gameLoaded;
    }

    public String message;
    public String logSession;
    public String stacktrace;
    public boolean gameLoaded;

}
