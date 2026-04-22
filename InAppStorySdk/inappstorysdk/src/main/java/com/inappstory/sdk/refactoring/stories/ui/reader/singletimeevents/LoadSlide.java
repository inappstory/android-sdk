package com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents;

import com.inappstory.sdk.refactoring.core.utils.observers.ISTEData;

public class LoadSlide implements ISTEData {
    public String layout() {
        return layout;
    }

    public String slide() {
        return slide;
    }

    private String slide;
    private String layout;

    public LoadSlide slide(String slide) {
        this.slide = slide;
        return this;
    }

    public LoadSlide layout(String layout) {
        this.layout = layout;
        return this;
    }
}
