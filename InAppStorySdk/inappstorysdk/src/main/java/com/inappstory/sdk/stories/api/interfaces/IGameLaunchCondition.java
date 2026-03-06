package com.inappstory.sdk.stories.api.interfaces;

import com.inappstory.sdk.stories.api.models.GameLaunchConditionType;

public interface IGameLaunchCondition {
    GameLaunchConditionType conditionType();
    String errorMessage();
}
