package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.SlideLinkObject;
import com.inappstory.sdk.stories.api.models.UrlObject;

import org.json.JSONObject;


public final class ToUrlObject extends ToModelParser<UrlObject> {
    public ToUrlObject(String value) {
        super(value);
    }



    @Override
    protected UrlObject parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            UrlObject urlObject = new UrlObject();
            urlObject.url = jsonObj.optStringNullable("url");
            return urlObject;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "UrlObject";
    }
}
