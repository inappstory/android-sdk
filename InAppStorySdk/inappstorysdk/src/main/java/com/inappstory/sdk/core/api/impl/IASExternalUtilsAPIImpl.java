package com.inappstory.sdk.core.api.impl;

import com.inappstory.iasutilsconnector.UtilModulesHolder;
import com.inappstory.iasutilsconnector.json.IJsonParser;
import com.inappstory.sdk.core.api.IASExternalUtilsAPI;
import com.inappstory.sdk.network.JsonParser;

public class IASExternalUtilsAPIImpl implements IASExternalUtilsAPI {
    UtilModulesHolder utilModulesHolder;

    @Override
    public UtilModulesHolder getUtilsAPI() {
        return utilModulesHolder;
    }

    @Override
    public boolean hasLottieAnimation() {
        if (utilModulesHolder == null) return false;
        return utilModulesHolder.hasLottieModule();
    }

    @Override
    public void init() {
        utilModulesHolder = UtilModulesHolder.INSTANCE;
        utilModulesHolder.setJsonParser(new IJsonParser() {
            @Override
            public <T> T fromJson(String json, Class<T> typeOfT) {
                return JsonParser.fromJson(json, typeOfT);
            }
        });
    }
}
