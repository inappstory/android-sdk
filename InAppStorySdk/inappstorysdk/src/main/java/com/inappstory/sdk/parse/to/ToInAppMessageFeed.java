package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.InAppMessage;
import com.inappstory.sdk.core.network.content.models.InAppMessageFeed;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToInAppMessageFeed extends ToModelParser<InAppMessageFeed> {
    public ToInAppMessageFeed(String value) {
        super(value);
    }

    @Override
    protected InAppMessageFeed parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            InAppMessageFeed feed = new InAppMessageFeed();
            feed.messages =
                    new ToInAppMessageList(jsonObj.optJSONArray("messages")).parse();
            return feed;
        } catch (Exception e) {
            return null;
        }
    }
}
