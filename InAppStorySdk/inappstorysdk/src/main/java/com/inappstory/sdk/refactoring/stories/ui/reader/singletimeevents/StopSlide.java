package com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents;


import com.inappstory.sdk.refactoring.core.utils.observers.ISTEData;

public class StopSlide implements ISTEData {
    public boolean prepareForRestart() {
        return prepareForRestart;
    }

    private boolean prepareForRestart;

    public StopSlide prepareForRestart(boolean prepareForRestart) {
        this.prepareForRestart = prepareForRestart;
        return this;
    }
}
