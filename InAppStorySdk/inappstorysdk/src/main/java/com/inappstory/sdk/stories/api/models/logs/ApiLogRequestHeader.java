package com.inappstory.sdk.stories.api.models.logs;

public class ApiLogRequestHeader {
    public String key;
    public String value;

    public ApiLogRequestHeader() {

    }

    public ApiLogRequestHeader(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
