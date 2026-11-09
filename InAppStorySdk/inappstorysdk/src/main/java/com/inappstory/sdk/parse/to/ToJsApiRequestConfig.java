package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.network.jsapiclient.JsApiRequestConfig;
import com.inappstory.sdk.parse.ExtendedJsonObject;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.models.OnVerticalScrollJSData;

import org.json.JSONObject;


public final class ToJsApiRequestConfig extends ToModelParser<JsApiRequestConfig> {
    public ToJsApiRequestConfig(String value) {
        super(value);
    }



    @Override
    protected JsApiRequestConfig parseString(@NonNull String value) {
        try {
            ExtendedJsonObject jsonObj = new ExtendedJsonObject(
                    new JSONObject(value)
            );
            JsApiRequestConfig config = new JsApiRequestConfig();
            config.cb = jsonObj.optStringNullable(
                    ModelFields.JsApiRequestConfig.cb
            );
            config.id = jsonObj.optStringNullable(
                    ModelFields.JsApiRequestConfig.id
            );
            config.data = jsonObj.optStringNullable(
                    ModelFields.JsApiRequestConfig.data
            );
            config.headers = jsonObj.optStringNullable(
                    ModelFields.JsApiRequestConfig.headers
            );
            config.profilingKey = jsonObj.optStringNullable(
                    ModelFields.JsApiRequestConfig.profilingKey
            );
            config.url = jsonObj.optStringNullable(
                    ModelFields.JsApiRequestConfig.url
            );
            config.method = jsonObj.optStringNullable(
                    ModelFields.JsApiRequestConfig.method
            );
            config.params = jsonObj.optStringNullable(
                    ModelFields.JsApiRequestConfig.params
            );

            return config;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String getModelClassName() {
        return "JsApiRequestConfig";
    }
}
