package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.inner.share.InnerShareData;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.stories.api.models.UrlObject;

import org.json.JSONObject;


public final class ToInnerShareData extends ToModelParser<InnerShareData> {
    public ToInnerShareData(String value) {
        super(value);
    }



    @Override
    protected InnerShareData parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            InnerShareData shareData = new InnerShareData();
            shareData.text = jsonObj.optStringNullable("text");
            shareData.url = jsonObj.optStringNullable("url");
            shareData.title = jsonObj.optStringNullable("title");
            shareData.payload = jsonObj.optStringNullable("payload");

            return shareData;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "InnerShareData";
    }
}
