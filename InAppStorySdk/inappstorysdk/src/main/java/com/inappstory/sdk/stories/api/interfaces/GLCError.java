package com.inappstory.sdk.stories.api.interfaces;

public final class GLCError implements ConditionsResult {
    public final String message;

    public GLCError(String message) {
        this.message = message;
    }
}