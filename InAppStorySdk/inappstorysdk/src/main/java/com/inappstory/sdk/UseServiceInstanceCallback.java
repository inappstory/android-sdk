package com.inappstory.sdk;

import androidx.annotation.NonNull;

public abstract class UseServiceInstanceCallback {
    public abstract void use(@NonNull InAppStoryService service) throws Exception;
    public void error() throws Exception {}
}