package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.Feed;
import com.inappstory.sdk.core.network.content.models.SessionResponse;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToSessionResponse extends ToModelParser<SessionResponse> {
    public ToSessionResponse(String value) {
        super(value);
    }

    @Override
    protected SessionResponse parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            SessionResponse sessionResponse = new SessionResponse();
            story.id = jsonObj.getInt("id");
            return sessionResponse;
        } catch (Exception e) {
            return null;
        }
    }
}
