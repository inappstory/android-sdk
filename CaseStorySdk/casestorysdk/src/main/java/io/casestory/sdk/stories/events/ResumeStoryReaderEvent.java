package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class ResumeStoryReaderEvent {

    public boolean isWithBackground() {
        return withBackground;
    }

    boolean withBackground;

    public ResumeStoryReaderEvent(boolean withBackground) {
        this.withBackground = withBackground;
    }


}
