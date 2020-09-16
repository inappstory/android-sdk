package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class PauseStoryReaderEvent {

    public boolean isWithBackground() {
        return withBackground;
    }

    boolean withBackground;

    public PauseStoryReaderEvent(boolean withBackground) {
        this.withBackground = withBackground;
    }


}
