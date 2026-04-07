package com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents;

import com.inappstory.sdk.inappmessage.domain.stedata.STEData;

public class StartSlide implements STEData {
    public boolean soundOn() {
        return soundOn;
    }

    private boolean soundOn;

    public StartSlide soundOn(boolean soundOn) {
        this.soundOn = soundOn;
        return this;
    }
}
