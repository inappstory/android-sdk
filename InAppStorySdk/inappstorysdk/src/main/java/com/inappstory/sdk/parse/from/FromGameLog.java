package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.logger.GameLog;

import org.json.JSONObject;

public class FromGameLog extends
        ModelToJSONObjectParser<GameLog> {
    public FromGameLog(
            GameLog model
    ) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put("message", model.message);
        object.put("logSession", model.logSession);
        object.put("webViewVersion", model.webViewVersion);
        object.put("stacktrace", model.stacktrace);
        object.put("gameInstanceId", model.gameInstanceId);
        object.put("type", model.type);
        object.put("webViewPackage", model.webViewPackage);
        object.put("gameLoaded", model.gameLoaded);
        object.put("timestamp", model.timestamp);
        object.put("launchTryNumber", model.launchTryNumber);
        return object;
    }
}
