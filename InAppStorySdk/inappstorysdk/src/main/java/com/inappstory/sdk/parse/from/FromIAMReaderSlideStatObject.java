package com.inappstory.sdk.parse.from;

import com.inappstory.sdk.game.reader.logger.GameLog;
import com.inappstory.sdk.inappmessage.domain.reader.IAMReaderSlideStatObject;

import org.json.JSONObject;

public class FromIAMReaderSlideStatObject extends
        ModelToJSONObjectParser<IAMReaderSlideStatObject> {
    public FromIAMReaderSlideStatObject(
            IAMReaderSlideStatObject model
    ) {
        super(model);
    }

    @Override
    protected JSONObject parseToJSONObject() throws Exception {
        JSONObject object = new JSONObject();
        object.put("slideIndex", model.slideIndex);
        object.put("duration", model.duration);
        return object;
    }
}
