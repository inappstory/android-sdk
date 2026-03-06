package com.inappstory.sdk.game.cache;

public final class UIUseCaseError extends SimpleUseCaseError {
    private final String uiMessage;

    public UIUseCaseError(String uiMessage, String logMessage) {
        super(logMessage != null ? logMessage : "");
        this.uiMessage = uiMessage;
    }

    public String uiMessage() {
        return uiMessage != null ? uiMessage : "";
    }
}
