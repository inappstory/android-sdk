package com.inappstory.sdk.inappmessage.domain.stedata;

public class STETypeAndData {
    public STETypeAndData(STEDataType type, STEData data) {
        this.type = type;
        this.data = data;
    }

    public STEDataType type() {
        return type;
    }

    public STEData data() {
        return data;
    }

    private final STEDataType type;
    private final STEData data;
}
