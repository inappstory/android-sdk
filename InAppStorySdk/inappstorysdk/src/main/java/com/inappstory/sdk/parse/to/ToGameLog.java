package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.game.reader.logger.GameLog;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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
            gameLog.gameInstanceId = jsonObj.optStringNullable(
                    ModelFields.GameLog.gameInstanceId
            );
            gameLog.message = jsonObj.optStringNullable(
                    ModelFields.GameLog.message
            );
            gameLog.logSession = jsonObj.optStringNullable(
                    ModelFields.GameLog.logSession
            );
            gameLog.webViewVersion = jsonObj.optStringNullable(
                    ModelFields.GameLog.webViewVersion
            );
            gameLog.stacktrace = jsonObj.optStringNullable(
                    ModelFields.GameLog.stacktrace
            );
            gameLog.webViewPackage = jsonObj.optStringNullable(
                    ModelFields.GameLog.webViewPackage
            );
            gameLog.type = jsonObj.optStringNullable(
                    ModelFields.GameLog.type
            );
            gameLog.gameLoaded = jsonObj.optBoolean(
                    ModelFields.GameLog.gameLoaded
            );
            gameLog.timestamp = jsonObj.optLongNullable(
                    ModelFields.GameLog.timestamp
            );
            gameLog.launchTryNumber = jsonObj.optInt(
                    ModelFields.GameLog.launchTryNumber
            );
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
