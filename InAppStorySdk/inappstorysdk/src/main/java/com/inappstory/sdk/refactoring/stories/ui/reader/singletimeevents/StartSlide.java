package com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents;


import com.inappstory.sdk.refactoring.core.utils.observers.ISTEData;

public class StartSlide implements ISTEData {
    public boolean soundOn() {
        return soundOn;
    }

    private boolean soundOn;

    public StartSlide soundOn(boolean soundOn) {
        this.soundOn = soundOn;
        return this;
    }
}
