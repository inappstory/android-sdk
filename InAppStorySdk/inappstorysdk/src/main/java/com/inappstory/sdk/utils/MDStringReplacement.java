package com.inappstory.sdk.utils;

public class MDStringReplacement {
    public final String key;
    public final String value;
    public final String link;

    public MDStringReplacement(
            String key,
            String value,
            String link
    ) {
        this.key = key;
        this.value = value;
        this.link = link;
    }

    @Override
    public String toString() {
        return "MDStringReplacement{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
