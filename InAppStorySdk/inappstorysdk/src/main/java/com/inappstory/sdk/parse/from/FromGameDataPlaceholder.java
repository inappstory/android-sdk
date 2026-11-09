package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.GameDataPlaceholder;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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
        object.put(ModelFields.GameDataPlaceholder.name, model.name);
        object.put(ModelFields.GameDataPlaceholder.type, model.type);
        object.put(ModelFields.GameDataPlaceholder.value, model.value);
        return object;
    }
}
