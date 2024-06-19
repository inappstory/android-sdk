package com.inappstory.sdk;

import androidx.annotation.NonNull;

public abstract class UseOldManagerInstanceCallback {
    public abstract void use(@NonNull OldInAppStoryManager manager) throws Exception;

    public void error() throws Exception {
    }
}