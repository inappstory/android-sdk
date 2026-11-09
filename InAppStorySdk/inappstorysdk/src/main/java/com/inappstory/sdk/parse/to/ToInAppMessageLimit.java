package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.InAppMessageLimit;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
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
            limit.messageId = jsonObj.getInt(
                    ModelFields.InAppMessageLimit.messageId
            );
            limit.canOpen = jsonObj.optBoolean(
                    ModelFields.InAppMessageLimit.canOpen
            );
            limit.expire = jsonObj.optLong(
                    ModelFields.InAppMessageLimit.expire
            );
            return limit;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "InAppMessageLimit";
    }
}
