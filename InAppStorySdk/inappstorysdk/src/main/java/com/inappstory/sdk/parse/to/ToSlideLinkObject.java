package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.SlideLinkObject;
import com.inappstory.sdk.stories.api.models.dialogstructure.DialogStructure;

import org.json.JSONObject;


public final class ToSlideLinkObject extends ToModelParser<SlideLinkObject> {
    public ToSlideLinkObject(String value) {
        super(value);
    }



    @Override
    protected SlideLinkObject parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            SlideLinkObject slideLink = new SlideLinkObject();
            slideLink.elementId = jsonObj.optStringNullable("elementId");
            slideLink.type = jsonObj.optStringNullable("type");
            slideLink.link = new ToStoryLink(jsonObj.optJSONObject("link")).parse();
            return slideLink;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "SlideLinkObject";
    }
}
