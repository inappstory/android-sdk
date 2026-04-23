package com.inappstory.sdk.refactoring.stories.ui.reader.singletimeevents;


import com.inappstory.sdk.refactoring.core.utils.observers.ISTEData;

public class SetSoundStatus implements ISTEData {
    public boolean soundOn() {
        return soundOn;
    }

    private boolean soundOn;

    public SetSoundStatus soundOn(boolean soundOn) {
        this.soundOn = soundOn;
        return this;
    }
}
