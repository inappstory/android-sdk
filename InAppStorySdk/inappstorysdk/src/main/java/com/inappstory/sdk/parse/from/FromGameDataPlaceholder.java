package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.GameDataPlaceholder;

import org.json.JSONObject;

public class FromGameDataPlaceholder extends
        ModelToJSONObjectParser<GameDataPlaceholder> {
    public FromGameDataPlaceholder(
            GameDataPlaceholder model
    ) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put("name", model.name);
        object.put("type", model.type);
        object.put("value", model.value);
        return object;
    }
}
