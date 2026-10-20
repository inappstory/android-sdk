package com.inappstory.sdk.parse.from;

public abstract class ModelToStringParser<T> {
    protected final T model;

    public ModelToStringParser(T model) {
        this.model = model;
    }

    public abstract String parse();
}
