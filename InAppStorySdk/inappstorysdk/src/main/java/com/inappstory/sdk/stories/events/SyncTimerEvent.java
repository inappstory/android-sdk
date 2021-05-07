package com.inappstory.sdk.stories.events;

public class SyncTimerEvent {
    public long getEventTimer() {
        return eventTimer;
    }

    long eventTimer;

    public SyncTimerEvent(long currentTimeLeft) {
        this.currentTimeLeft = currentTimeLeft;
        this.eventTimer = System.currentTimeMillis();
    }

    public long getCurrentTimeLeft() {
        return currentTimeLeft;
    }

    long currentTimeLeft;
}
