package com.inappstory.sdk.game.cache;

public class SimpleUseCaseError implements UseCaseError{
    private final String message;

    public SimpleUseCaseError(String message) {
        this.message = message;
    }

    @Override
    public String message() {
        return message;
    }
}
