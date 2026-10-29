package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.InAppMessageEvent;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToInAppMessageEvent extends ToModelParser<InAppMessageEvent> {
    public ToInAppMessageEvent(JSONObject value) {
        super(value);
    }

    @Override
    public InAppMessageEvent parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            InAppMessageEvent event = new InAppMessageEvent();
            event.name = jsonObj.getString("name");
            event.id = jsonObj.optInt("id");
            event.priority = jsonObj.optInt("priority");
            return event;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "InAppMessageEvent";
    }
}
