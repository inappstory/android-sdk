package com.inappstory.sdk.stories.events;

public class DebugEvent {
    public String getMessage() {
        return message;
    }

    String message;

    public DebugEvent(String message) {
        this.message = message;
    }
}
