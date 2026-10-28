package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Banner;
import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToInAppMessage extends ToModelParser<InAppMessage> {
    public ToInAppMessage(String value) {
        super(value);
    }

    @Override
    protected InAppMessage parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            InAppMessage inAppMessage = new InAppMessage();
            story.id = jsonObj.getInt("id");
            return inAppMessage;
        } catch (Exception e) {
            return null;
        }
    }
}
