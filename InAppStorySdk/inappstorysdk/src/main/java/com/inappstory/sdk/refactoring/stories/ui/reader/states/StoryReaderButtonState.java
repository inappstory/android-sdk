package com.inappstory.sdk.refactoring.stories.ui.reader.states;

import java.util.Objects;

public class StoryReaderButtonState {
    private boolean enabled;
    private boolean active;
    private boolean visible;

    public boolean enabled() {
        return enabled;
    }

    public StoryReaderButtonState enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public boolean active() {
        return active;
    }

    public StoryReaderButtonState active(boolean active) {
        this.active = active;
        return this;
    }

    public boolean visible() {
        return visible;
    }

    public StoryReaderButtonState visible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public StoryReaderButtonState copy() {
        return new StoryReaderButtonState()
                .active(active)
                .enabled(enabled)
                .visible(visible);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StoryReaderButtonState)) return false;
        StoryReaderButtonState that = (StoryReaderButtonState) o;
        return enabled == that.enabled &&
                active == that.active &&
                visible == that.visible;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, active, visible);
    }

    @Override
    public String toString() {
        return "StoryReaderButtonState{" +
                "enabled=" + enabled +
                ", active=" + active +
                ", visible=" + visible +
                '}';
    }
}
