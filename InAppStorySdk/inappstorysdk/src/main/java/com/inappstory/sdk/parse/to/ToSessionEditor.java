package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.SessionEditor;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.ShareObject;

import org.json.JSONObject;


public final class ToSessionEditor extends ToModelParser<SessionEditor> {
    public ToSessionEditor(String value) {
        super(value);
    }

    @Override
    protected SessionEditor parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            SessionEditor sessionEditor = new SessionEditor();
            story.id = jsonObj.getInt("id");
            return sessionEditor;
        } catch (Exception e) {
            return null;
        }
    }
}
