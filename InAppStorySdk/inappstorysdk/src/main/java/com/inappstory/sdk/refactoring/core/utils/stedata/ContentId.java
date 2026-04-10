package com.inappstory.sdk.refactoring.core.utils.stedata;

import com.inappstory.sdk.refactoring.core.utils.observers.ISTEData;

public class ContentId implements ISTEData {
    private final String id;

    public String id() {
        return id;
    }

    public ContentId(String id) {
        this.id = id;
    }
}
