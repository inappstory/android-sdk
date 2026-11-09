package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.logger.GameLog;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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
        object.put(ModelFields.GameLog.message, model.message);
        object.put(ModelFields.GameLog.logSession, model.logSession);
        object.put(ModelFields.GameLog.webViewVersion, model.webViewVersion);
        object.put(ModelFields.GameLog.stacktrace, model.stacktrace);
        object.put(ModelFields.GameLog.gameInstanceId, model.gameInstanceId);
        object.put(ModelFields.GameLog.type, model.type);
        object.put(ModelFields.GameLog.webViewPackage, model.webViewPackage);
        object.put(ModelFields.GameLog.gameLoaded, model.gameLoaded);
        object.put(ModelFields.GameLog.timestamp, model.timestamp);
        object.put(ModelFields.GameLog.launchTryNumber, model.launchTryNumber);
        return object;
    }
}
