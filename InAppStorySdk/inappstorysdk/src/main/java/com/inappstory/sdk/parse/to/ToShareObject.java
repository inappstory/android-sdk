package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.core.network.content.models.SessionResponse;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.ShareObject;

import org.json.JSONObject;


public final class ToShareObject extends ToModelParser<ShareObject> {
    public ToShareObject(String value) {
        super(value);
    }

    @Override
    protected ShareObject parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            ShareObject shareObject = new ShareObject();
            shareObject.url = jsonObj.getString(
                    ModelFields.ShareObject.url
            );
            shareObject.title = jsonObj.optStringNullable(
                    ModelFields.ShareObject.title
            );
            shareObject.description = jsonObj.optStringNullable(
                    ModelFields.ShareObject.description
            );
            return shareObject;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "ShareObject";
    }
}
