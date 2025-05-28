package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.SerializedName;

public class GameLaunchConfigObject {
    @SerializedName("demoMode")
    public boolean demoMode;

    public GameLaunchConfigObject() {
    }

    public GameLaunchConfigObject(boolean demoMode) {
        this.demoMode = demoMode;
    }
}
