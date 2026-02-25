package com.inappstory.sdk.refactoring.session.data.local;

public class SessionPlaceholderDTO {
    private final String name;
    
    private final String defaultVal;

    public SessionPlaceholderDTO(String name, String defaultVal) {
        this.name = name;
        this.defaultVal = defaultVal;
    }

    public String name() {
        return name;
    }

    public String defaultVal() {
        return defaultVal;
    }
}
