package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.GameSTIResponse;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameSplashAnimation;

import org.json.JSONObject;

public class FromGameSTIResponse extends ModelToStringParser<GameSTIResponse> {
    public FromGameSTIResponse(GameSTIResponse model) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put(ModelFields.GameSTIResponse.id, model.id);
        object.put(ModelFields.GameSTIResponse.response, model.response);
        return object;
    }
}
