package com.inappstory.sdk.parse.to;

public abstract class StringToModelParser<T> {
    protected final String value;

    public StringToModelParser(String value) {
        this.value = value;
    }

    public abstract T parse();

    protected String parseError(String field) {
        return "Field " + field + " has incorrect format.";
    }
}
