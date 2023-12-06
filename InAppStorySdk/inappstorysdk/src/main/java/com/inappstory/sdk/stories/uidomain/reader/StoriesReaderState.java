package com.inappstory.sdk.stories.uidomain.reader;

import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderAppearanceSettings;
import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderLaunchData;

public final class StoriesReaderState {

    public StoriesReaderLaunchData launchData() {
        return launchData;
    }

    public StoriesReaderAppearanceSettings appearanceSettings() {
        return appearanceSettings;
    }

    public boolean isDraggable() {
        return isDraggable;
    }

    private StoriesReaderLaunchData launchData;
    private StoriesReaderAppearanceSettings appearanceSettings;
    private boolean isDraggable;


    public StoriesReaderState launchData(StoriesReaderLaunchData launchData) {
        this.launchData = launchData;
        return this;
    }

    public StoriesReaderState appearanceSettings(StoriesReaderAppearanceSettings appearanceSettings) {
        this.appearanceSettings = appearanceSettings;
        return this;
    }

    public StoriesReaderState isDraggable(boolean draggable) {
        isDraggable = this.appearanceSettings.csIsDraggable() && draggable;
        return this;
    }

    public StoriesReaderState() {

    }

    public StoriesReaderState copy() {
        return new StoriesReaderState(
                this.launchData,
                this.appearanceSettings,
                this.isDraggable
        );
    }

    private StoriesReaderState(
            StoriesReaderLaunchData launchData,
            StoriesReaderAppearanceSettings appearanceSettings,
            boolean isDraggable
    ) {
        this.launchData = launchData;
        this.appearanceSettings = appearanceSettings;
        this.isDraggable = isDraggable;
    }
}
