package com.inappstory.sdk.inappmessage.domain.reader;

public class IAMReaderSlideStatState {

    private long resumedTime = 0;

    public String iterationId() {
        return iterationId;
    }

    private String iterationId;

    long totalTime() {
        return (System.currentTimeMillis() - this.resumedTime);
    }

    void create(String iterationId) {
        this.iterationId = iterationId;
        this.resumedTime = System.currentTimeMillis();
    }

    void resume() {
        this.resumedTime = System.currentTimeMillis();
    }

    void clear() {
        this.iterationId = null;
        this.resumedTime = 0;
    }
}
