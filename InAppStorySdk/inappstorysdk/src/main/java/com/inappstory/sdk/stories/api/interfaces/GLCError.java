package com.inappstory.sdk.stories.api.interfaces;

public final class GLCError implements ConditionsResult {
    public final String logMessage;
    public final String uiMessage;

    public GLCError(String uiMessage, String logMessage) {
        this.uiMessage = uiMessage;
        this.logMessage = logMessage;
    }
}