package com.inappstory.sdk.stories.api.models;

import androidx.annotation.NonNull;

import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.stories.api.interfaces.IGameLaunchCondition;
import com.inappstory.sdk.stories.api.interfaces.IGameLaunchConditions;

import java.util.ArrayList;
import java.util.List;

public class GameLaunchConditions implements IGameLaunchConditions {
    @SerializedName("minWebViewVersion")
    GameLCMinWebViewVersion minWebViewVersion;

    @Override
    @NonNull
    public List<IGameLaunchCondition> launchConditions() {
        List<IGameLaunchCondition> conditions = new ArrayList<>();
        if (minWebViewVersion != null) {
            conditions.add(minWebViewVersion);
        }
        return conditions;
    }
}
