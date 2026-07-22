package com.inappstory.sdk.core.api.impl;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.core.api.IASStatisticExceptions;

public class IASStatisticExceptionsImpl implements IASStatisticExceptions {
    private final IASCore core;

    public IASStatisticExceptionsImpl(IASCore core) {
        this.core = core;
    }

    @Override
    public boolean disabled() {
        return disabled;
    }

    @Override
    public boolean softDisabled() {
        return softDisabled || disabled;
    }

    private boolean disabled;
    private boolean softDisabled;

    @Override
    public void disabled(boolean softDisabled, boolean disabled) {
        this.softDisabled = softDisabled;
        this.disabled = disabled;
    }

}
