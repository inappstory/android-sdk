package com.inappstory.sdk.parse.to;

import com.inappstory.sdk.core.network.content.models.Image;
import com.inappstory.sdk.core.network.content.models.Story;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;

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
            image.url = jsonObj.getString(
                    ModelFields.Image.url
            );
            image.width = jsonObj.optInt(
                    ModelFields.Image.width
            );
            image.height = jsonObj.optInt(
                    ModelFields.Image.height
            );
            image.type = jsonObj.optStringNullable(
                    ModelFields.Image.type
            );
            image.expire = jsonObj.optInt(
                    ModelFields.Image.expire
            );
            return image;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "Image";
    }
}
