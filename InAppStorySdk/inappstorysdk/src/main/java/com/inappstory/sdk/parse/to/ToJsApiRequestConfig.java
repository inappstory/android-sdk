package com.inappstory.sdk.parse.to;

import androidx.annotation.NonNull;

import com.inappstory.sdk.network.jsapiclient.JsApiRequestConfig;
import com.inappstory.sdk.parse.ExtendedJsonObject;
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
            config.cb = jsonObj.optStringNullable("cb");
            config.id = jsonObj.optStringNullable("id");
            config.data = jsonObj.optStringNullable("data");
            config.headers = jsonObj.optStringNullable("headers");
            config.profilingKey = jsonObj.optStringNullable("profiling_key");
            config.url = jsonObj.optStringNullable("url");
            config.method = jsonObj.optStringNullable("method");
            config.params = jsonObj.optStringNullable("params");

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
