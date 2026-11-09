package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.network.annotations.models.Required;
import com.inappstory.sdk.network.annotations.models.SerializedName;
import com.inappstory.sdk.parse.fieldkeys.ModelFields;
import com.inappstory.sdk.stories.api.interfaces.IGameLaunchCondition;

public class GameLCMinWebViewVersion implements IGameLaunchCondition {
    @Required
    @SerializedName(ModelFields.GameLCMinWebViewVersion.minVersion)
    public int minVersion;
    @SerializedName(ModelFields.GameLCMinWebViewVersion.message)
    public String message;
    @SerializedName(ModelFields.GameLCMinWebViewVersion.loggerMessage)
    public String loggerMessage;

    @Override
    public GameLaunchConditionType conditionType() {
        return GameLaunchConditionType.MIN_WEB_VIEW_VERSION;
    }

    @Override
    public String errorMessage() {
        return message;
    }

    @Override
    public String loggerErrorMessage() {
        return loggerMessage;
    }
}
