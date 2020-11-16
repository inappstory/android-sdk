package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 08.07.2018.
 */

public class CloseStoryReaderEvent {
    public boolean isOnboardingEvent() {
        return isOnboardingEvent;
    }

    boolean isOnboardingEvent;

    public int getAction() {
        return action;
    }

    int action;

    public CloseStoryReaderEvent() {

    }


    public CloseStoryReaderEvent(int action) {
        this.action = action;
    }

    public CloseStoryReaderEvent(boolean isOnboardingEvent) {
        this.isOnboardingEvent = isOnboardingEvent;
    }
}
