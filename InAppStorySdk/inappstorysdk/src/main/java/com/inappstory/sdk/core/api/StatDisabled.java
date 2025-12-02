package com.inappstory.sdk.core.api;

public interface StatDisabled {
    boolean disabled();
    boolean softDisabled();
    void disabled(boolean softDisabled, boolean disabled);
}
