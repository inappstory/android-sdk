package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 08.07.2018.
 */

public class CloseStoryReaderEvent {
    public boolean isOnboardingEvent() {
        return isOnboardingEvent;
    }

    boolean isOnboardingEvent;

    public CloseStoryReaderEvent() {

    }

    public CloseStoryReaderEvent(boolean isOnboardingEvent) {
        this.isOnboardingEvent = isOnboardingEvent;
    }
}
