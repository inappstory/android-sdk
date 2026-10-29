package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.ContentResource;
import com.inappstory.sdk.core.network.content.models.StorySlide;
import com.inappstory.sdk.parse.ExtendedJsonObject;

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
            resource.url = jsonObj.getString("url");
            resource.key = jsonObj.optString("key");
            resource.type = jsonObj.optString("type");
            resource.purpose = jsonObj.optString("purpose");
            resource.filename = jsonObj.optString("filename");
            resource.index = jsonObj.optIntNullable("slide_index");
            resource.rangeStart = jsonObj.optLongNullable("range_start");
            resource.rangeEnd = jsonObj.optLongNullable("range_end");
            return resource;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getClassName() {
        return "ContentResource";
    }
}
