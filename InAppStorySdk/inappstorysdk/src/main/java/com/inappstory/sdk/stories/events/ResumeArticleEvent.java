package com.inappstory.sdk.stories.events;

/**
 * Created by Paperrose on 15.06.2018.
 */

public class ResumeArticleEvent {

    public boolean isWithBackground() {
        return withBackground;
    }

    boolean withBackground;

    public ResumeArticleEvent(boolean withBackground) {
        this.withBackground = withBackground;
    }


}
