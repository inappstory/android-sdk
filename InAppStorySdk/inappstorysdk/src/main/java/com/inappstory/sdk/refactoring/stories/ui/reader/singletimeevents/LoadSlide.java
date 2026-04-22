package com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents;

import com.inappstory.sdk.refactoring.core.utils.observers.ISTEData;

public class LoadSlide implements ISTEData {
    public String layout() {
        return layout;
    }

    public String slideContent() {
        return slideContent;
    }

    private String slideContent;
    private String layout;

    public LoadSlide slideContent(String result) {
        this.slideContent = slideContent;
        return this;
    }

    public LoadSlide layout(String cb) {
        this.layout = layout;
        return this;
    }
}
