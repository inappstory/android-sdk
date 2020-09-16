package io.casestory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class PauseArticleEvent {

    public boolean isWithBackground() {
        return withBackground;
    }

    boolean withBackground;

    public PauseArticleEvent(boolean withBackground) {
        this.withBackground = withBackground;
    }


}
