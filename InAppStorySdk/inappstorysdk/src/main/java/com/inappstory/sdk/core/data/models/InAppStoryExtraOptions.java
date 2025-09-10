package com.inappstory.sdk.core.data.models;

import com.inappstory.sdk.core.data.IInAppStoryExtraOptions;

public class InAppStoryExtraOptions implements IInAppStoryExtraOptions {
    private String pos;

    public InAppStoryExtraOptions pos(String posName) {
        this.pos = posName;
        return this;
    }

    @Override
    public String pos() {
        return pos;
    }
}
