package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.SessionEditor;
import com.inappstory.sdk.network.JsonParser;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
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
            sessionEditor.url = jsonObj.optStringNullable(
                    ModelFields.SessionEditor.url
            );
            sessionEditor.urlTemplate = jsonObj.optStringNullable(
                    ModelFields.SessionEditor.urlTemplate
            );
            sessionEditor.versionTemplate = jsonObj.optStringNullable(
                    ModelFields.SessionEditor.versionTemplate
            );

            sessionEditor.versionsMap =
                    new ToUGCVersionToSDKBuildList(jsonObj.optJSONArray(
                            ModelFields.SessionEditor.versionsMap
                    )).parse();

            sessionEditor.config = new HashMap<>(
                    JsonParser.toObjectMap(jsonObj.optJSONObject(
                            ModelFields.SessionEditor.config
                    ))
            );
            Map<String, String> msgs = JsonParser.toMap(jsonObj.optJSONObject(
                    ModelFields.SessionEditor.messages
            ));
            if (msgs != null)
                sessionEditor.messages = new HashMap<>(msgs);
            return sessionEditor;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "SessionEditor";
    }
}
