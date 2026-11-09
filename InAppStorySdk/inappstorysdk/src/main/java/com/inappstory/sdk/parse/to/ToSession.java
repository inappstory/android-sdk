package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.Session;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.WebResource;

import org.json.JSONObject;


public final class ToSession extends ToModelParser<Session> {
    public ToSession(JSONObject value) {
        super(value);
    }

    @Override
    protected Session parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            Session session = new Session();
            session.id = jsonObj.getString(ModelFields.Session.id);
            return session;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "Session";
    }
}
