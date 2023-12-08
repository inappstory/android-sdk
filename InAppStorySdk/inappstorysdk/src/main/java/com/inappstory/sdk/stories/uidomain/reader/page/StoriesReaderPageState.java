package com.inappstory.sdk.stories.uidomain.reader.page;

import com.inappstory.sdk.core.models.api.Story.StoryType;
import com.inappstory.sdk.stories.outercallbacks.screen.StoriesReaderAppearanceSettings;

public final class StoriesReaderPageState {

    public StoriesReaderPageState(
            StoriesReaderAppearanceSettings appearanceSettings,
            int storyId,
            StoryType storyType,
            boolean hasSwipeUp,
            boolean disableClose
    ) {
        this.appearanceSettings = appearanceSettings;
        this.storyId = storyId;
        this.storyType = storyType;
        this.hasSwipeUp = hasSwipeUp;
        this.disableClose = disableClose;
    }

    public StoriesReaderAppearanceSettings appearanceSettings() {
        return appearanceSettings;
    }

    public StoriesReaderPageState appearanceSettings(
            StoriesReaderAppearanceSettings appearanceSettings
    ) {
        this.appearanceSettings = appearanceSettings;
        return this;
    }

    public int storyId() {
        return storyId;
    }

    public StoriesReaderPageState storyId(int storyId) {
        this.storyId = storyId;
        return this;
    }

    public StoriesReaderPageState storyType(StoryType storyType) {
        this.storyType = storyType;
        return this;
    }

    public boolean hasSwipeUp() {
        return hasSwipeUp;
    }

    public StoriesReaderPageState hasSwipeUp(boolean hasSwipeUp) {
        this.hasSwipeUp = hasSwipeUp;
        return this;
    }

    public boolean disableClose() {
        return disableClose;
    }

    public StoriesReaderPageState disableClose(boolean disableClose) {
        this.disableClose = disableClose;
        return this;
    }

    public boolean isActive() {
        return isActive;
    }

    public StoriesReaderPageState isActive(boolean active) {
        isActive = active;
        return this;
    }

    private StoriesReaderAppearanceSettings appearanceSettings;
    private int storyId;

    public StoryType getStoryType() {
        return storyType;
    }

    private StoryType storyType;
    private boolean hasSwipeUp;
    private boolean disableClose;
    private boolean isActive;

    public StoriesReaderPageState copy() {
        return new StoriesReaderPageState(
                this.appearanceSettings,
                this.storyId,
                this.storyType,
                this.hasSwipeUp,
                this.disableClose
        );
    }

    public StoriesReaderPageState(
            StoriesReaderAppearanceSettings appearanceSettings,
            int storyId,
            StoryType storyType
    ) {
        this.appearanceSettings = appearanceSettings;
        this.storyId = storyId;
    }
}
