package com.inappstory.sdk.core.ui.widgets.customicons;

import com.inappstory.sdk.ICustomIconState;

public class CustomIconState implements ICustomIconState {

    private final boolean active;
    private final boolean enabled;

    public CustomIconState(boolean active, boolean enabled) {
        this.active = active;
        this.enabled = enabled;
    }

    @Override
    public boolean active() {
        return active;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }
}
