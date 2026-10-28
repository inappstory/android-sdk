package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.SessionEditor;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.ShareObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


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
            sessionEditor.url = jsonObj.optStringNullable("url");
            sessionEditor.urlTemplate = jsonObj.optStringNullable("urlTemplate");
            sessionEditor.versionTemplate = jsonObj.optStringNullable("versionTemplate");

            sessionEditor.versionsMap =
                    new ToUGCVersionToSDKBuildList(jsonObj.optJSONArray("versionsMap")).parse();

            sessionEditor.config = new HashMap<>(
                    JsonParser.toObjectMap(jsonObj.optJSONObject("config"))
            );
            Map<String, String> msgs = JsonParser.toMap(jsonObj.optJSONObject("messages"));
            if (msgs != null)
                sessionEditor.messages = new HashMap<>(msgs);
            return sessionEditor;
        } catch (Exception e) {
            return null;
        }
    }
}
