package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.parse.ExtendedJsonObject;

import org.json.JSONObject;


public final class ToImage extends ToModelParser<Image> {
    public ToImage(JSONObject value) {
        super(value);
    }

    @Override
    public Image parseObject(JSONObject object) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(object);
            Image image = new Image();
            image.url = jsonObj.getString("url");
            image.width = jsonObj.optInt("width");
            image.height = jsonObj.optInt("height");
            image.type = jsonObj.optStringNullable("type");
            image.expire = jsonObj.optInt("expire");
            return image;
        } catch (Exception e) {
            return null;
        }
    }
}
