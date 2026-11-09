package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.GameCenterData;
import com.inappstory.sdk.stories.api.models.WebResource;

import org.json.JSONObject;


public final class ToWebResource extends ToModelParser<WebResource> {
    public ToWebResource(JSONObject value) {
        super(value);
    }

    @Override
    protected WebResource parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            WebResource webResource = new WebResource();
            webResource.url = jsonObj.getString(
                    ModelFields.WebResource.url
            );
            webResource.key = jsonObj.getString(
                    ModelFields.WebResource.key
            );
            webResource.sha1 = jsonObj.optStringNullable(
                    ModelFields.WebResource.sha1
            );
            webResource.size = jsonObj.optLong(
                    ModelFields.WebResource.size
            );
            return webResource;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "WebResource";
    }
}
