package com.inappstory.sdk.refactoring.core.utils.observers;

public class STETypeAndData {
    public STETypeAndData(ISTEDataType type, ISTEData data) {
        this.type = type;
        this.data = data;
    }

    public ISTEDataType type() {
        return type;
    }

    public ISTEData data() {
        return data;
    }

    private final ISTEDataType type;
    private final ISTEData data;
}
