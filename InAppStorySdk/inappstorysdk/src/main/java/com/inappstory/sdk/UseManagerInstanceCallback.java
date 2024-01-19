package com.inappstory.sdk;

import androidx.annotation.NonNull;

public abstract class UseManagerInstanceCallback {
    public abstract void use(@NonNull InAppStoryManager manager) throws Exception;

    public void error() throws Exception {
    }
}