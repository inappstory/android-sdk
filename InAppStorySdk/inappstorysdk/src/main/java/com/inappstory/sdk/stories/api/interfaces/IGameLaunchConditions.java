package com.inappstory.sdk.stories.api.interfaces;

import androidx.annotation.NonNull;

import java.util.List;

public interface IGameLaunchConditions {
    @NonNull
    List<IGameLaunchCondition> launchConditions();
}
