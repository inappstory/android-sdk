package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.ContentResource;
import com.inappstory.sdk.core.network.content.models.StorySlide;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

import org.json.JSONObject;


public final class ToContentResource extends ToModelParser<ContentResource> {
    public ToContentResource(JSONObject value) {
        super(value);
    }

    @Override
    public ContentResource parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            ContentResource resource = new ContentResource();
            resource.url = jsonObj.getString(
                    ModelFields.ContentResource.url
            );
            resource.key = jsonObj.optString(
                    ModelFields.ContentResource.key
            );
            resource.type = jsonObj.optString(
                    ModelFields.ContentResource.type
            );
            resource.purpose = jsonObj.optString(
                    ModelFields.ContentResource.purpose
            );
            resource.filename = jsonObj.optString(
                    ModelFields.ContentResource.filename
            );
            resource.index = jsonObj.optIntNullable(
                    ModelFields.ContentResource.index
            );
            resource.rangeStart = jsonObj.optLongNullable(
                    ModelFields.ContentResource.rangeStart
            );
            resource.rangeEnd = jsonObj.optLongNullable(
                    ModelFields.ContentResource.rangeEnd
            );
            return resource;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "ContentResource";
    }
}
