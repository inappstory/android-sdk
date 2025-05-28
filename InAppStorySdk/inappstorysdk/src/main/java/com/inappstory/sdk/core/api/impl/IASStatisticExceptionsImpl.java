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
        return true;
    }

    @Override
    public void disabled(boolean disabled) {

    }
}
