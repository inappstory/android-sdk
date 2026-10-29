package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.InAppMessageLimit;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.WebResource;

import org.json.JSONObject;


public final class ToInAppMessageLimit extends ToModelParser<InAppMessageLimit> {
    public ToInAppMessageLimit(JSONObject value) {
        super(value);
    }

    @Override
    protected InAppMessageLimit parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            InAppMessageLimit limit = new InAppMessageLimit();
            limit.messageId = jsonObj.getInt("messageId");
            limit.canOpen = jsonObj.optBoolean("canOpen");
            limit.expire = jsonObj.optLong("expire");
            return limit;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "InAppMessageLimit";
    }
}
