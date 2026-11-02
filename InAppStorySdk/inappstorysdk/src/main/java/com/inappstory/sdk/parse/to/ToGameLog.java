package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.game.reader.logger.GameLog;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToGameLog extends ToModelParser<GameLog> {
    public ToGameLog(JSONObject value) {
        super(value);
    }

    @Override
    public GameLog parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            GameLog gameLog = new GameLog();
            gameLog.gameInstanceId = jsonObj.optStringNullable("gameInstanceId");
            gameLog.message = jsonObj.optStringNullable("message");
            gameLog.logSession = jsonObj.optStringNullable("logSession");
            gameLog.webViewVersion = jsonObj.optStringNullable("webViewVersion");
            gameLog.stacktrace = jsonObj.optStringNullable("stacktrace");
            gameLog.webViewPackage = jsonObj.optStringNullable("webViewPackage");
            gameLog.type = jsonObj.optStringNullable("type");
            gameLog.gameLoaded = jsonObj.optBoolean("gameLoaded");
            gameLog.timestamp = jsonObj.optLongNullable("timestamp");
            gameLog.launchTryNumber = jsonObj.optInt("launchTryNumber");
            return gameLog;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "GameLog";
    }
}
