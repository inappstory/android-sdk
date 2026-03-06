package com.inappstory.sdk.stories.api.models;

import com.inappstory.sdk.core.IASCore;
import com.inappstory.sdk.stories.api.interfaces.ConditionsResult;
import com.inappstory.sdk.stories.api.interfaces.GLCError;
import com.inappstory.sdk.stories.api.interfaces.GLCSuccess;
import com.inappstory.sdk.stories.api.interfaces.IGameLaunchCondition;
import com.inappstory.sdk.utils.WebViewUtils;

import java.util.List;

public class GameLaunchConditionsChecker {
    private final IASCore core;

    public GameLaunchConditionsChecker(IASCore core) {
        this.core = core;
    }


    public ConditionsResult checkConditions(
            List<IGameLaunchCondition> conditions
    ) {
        for (IGameLaunchCondition condition : conditions) {
            if (condition.conditionType() == GameLaunchConditionType.MIN_WEB_VIEW_VERSION) {
                if (checkWebViewCondition(
                        (GameLCMinWebViewVersion) condition)
                ) {
                    return new GLCSuccess();
                } else {
                    return new GLCError(condition.errorMessage());
                }
            }
        }
        return new GLCSuccess();
    }

    private boolean checkWebViewCondition(
            GameLCMinWebViewVersion webViewVersion
    ) {
        return WebViewUtils.getWebViewVersion(core) > webViewVersion.minVersion;
    }
}
