package com.inappstory.sdk.parse.from;


import org.json.JSONObject;

public abstract class ModelToJSONObjectParser<T> {
    protected final T model;

    public ModelToJSONObjectParser(T model) {
        this.model = model;
    }

    protected abstract JSONObject parseToJSONObject() throws Exception;
}
