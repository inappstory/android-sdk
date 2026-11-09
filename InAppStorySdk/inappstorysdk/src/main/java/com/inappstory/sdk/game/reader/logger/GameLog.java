package com.inappstory.sdk.game.reader.logger;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

public class GameLog {


    @SerializedName(ModelFields.GameLog.message)
    public String message;
    @SerializedName(ModelFields.GameLog.logSession)
    public String logSession;
    @SerializedName(ModelFields.GameLog.webViewVersion)
    public String webViewVersion;
    @SerializedName(ModelFields.GameLog.stacktrace)
    public String stacktrace;
    @SerializedName(ModelFields.GameLog.gameInstanceId)
    public String gameInstanceId;
    @SerializedName(ModelFields.GameLog.type)
    public String type;
    @SerializedName(ModelFields.GameLog.webViewPackage)
    public String webViewPackage;
    @SerializedName(ModelFields.GameLog.gameLoaded)
    public boolean gameLoaded;
    @SerializedName(ModelFields.GameLog.timestamp)
    public Long timestamp;
    @SerializedName(ModelFields.GameLog.launchTryNumber)
    public int launchTryNumber;

    public GameLog() {

    }

    public GameLog(
            String gameInstanceId,
            String logSession,
            String webViewPackage,
            int webViewVersion,
            Long timestamp,
            int launchTryNumber,
            boolean gameLoaded
    ) {
        this.gameInstanceId = gameInstanceId;
        this.timestamp = timestamp;
        this.webViewPackage = webViewPackage;
        this.logSession = logSession;
        this.webViewVersion = Integer.toString(webViewVersion);
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

    GameLog webViewVersion(String webViewVersion) {
        this.webViewVersion = webViewVersion;
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

    String webViewPackage() {
        return webViewPackage;
    }

    Long timestamp() {
        return timestamp;
    }

    String message() {
        return message;
    }

    String webViewVersion() {
        return webViewVersion;
    }

    String logSession() {
        return logSession;
    }

    String stacktrace() {
        return stacktrace;
    }

    public int launchTryNumber() {
        return launchTryNumber;
    }

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


}
