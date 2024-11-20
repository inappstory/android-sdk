package com.inappstory.sdk.inappmessage.domain.reader;

public class IAMReaderSlideStatState {

    private long resumedTime = 0;
    private long totalTime = 0;

    public String iterationId() {
        return iterationId;
    }

    private String iterationId;

    void updateTotalTime() {
        this.totalTime =
                (System.currentTimeMillis() - this.resumedTime);
    }

    long totalTime() {
        return this.totalTime;
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
        this.totalTime = 0;
        this.resumedTime = 0;
    }
}
