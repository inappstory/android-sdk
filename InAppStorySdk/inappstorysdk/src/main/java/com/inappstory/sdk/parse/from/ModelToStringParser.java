package com.inappstory.sdk.parse.from;

public abstract class ModelToStringParser<T> extends ModelToJSONObjectParser<T> {

    public ModelToStringParser(T model) {
        super(model);
    }

    public String parse() {
        try {
            return parseToJSONObject().toString();
        } catch (Exception e) {
            return null;
        }
    }
}
