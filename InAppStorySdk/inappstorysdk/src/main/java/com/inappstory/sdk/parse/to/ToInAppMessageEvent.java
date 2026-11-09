package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.InAppMessageEvent;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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
            event.name = jsonObj.getString(ModelFields.InAppMessageEvent.name);
            event.id = jsonObj.optInt(ModelFields.InAppMessageEvent.id);
            event.priority = jsonObj.optInt(ModelFields.InAppMessageEvent.priority);
            return event;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "InAppMessageEvent";
    }
}
