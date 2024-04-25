package com.inappstory.sdk.modulesconnector.utils.lottie;

import java.io.InputStream;

public class LottieFileData implements ILottieFileData {


    private LottieFileData(
            String type,
            String cacheKey,
            Object data
    ) {
        this.type = type;
        this.cacheKey = cacheKey;
        this.data = data;
    }

    private String type;
    private String cacheKey;
    private Object data;

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getCacheKey() {
        return null;
    }

    @Override
    public Object getData() {
        return null;
    }
}
